// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductStateChangeEventProducerImpl;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.model.RelatedProductOrderItemEntity;
import com.orange.discobole.productinventory.repository.ProductRepository;
import com.orange.discobole.productinventory.service.PatchProductService;
import com.orange.discobole.productinventory.service.ProductService;
import com.orange.discobole.productinventory.util.ProductEntityUtil;
import com.orange.discobole.productinventory.validation.ProductDTOValidator;
import com.orange.discobole.productinventory.validation.ProductDatesChecker;
import com.orange.discobole.productinventory.validation.status.StatusChecker;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INTERNAL_ERROR;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PHYSICAL_PRODUCT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.SHIPMENT_PRODUCT;
import static com.orange.discobole.productinventory.enumerate.PublishEventEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.ProductEntityUtil.getProductRelationshipsByType;
import static com.orange.discobole.productinventory.validation.ProductAttributesValidator.validateForUnpatchableAttributes;
import static com.orange.discobole.productinventory.validation.ProductOrderAttributesValidator.validateProductOrderItem;
import static com.orange.discobole.productinventory.validation.ProductRelationshipValidator.*;
import static com.orange.discobole.productinventory.validation.status.StatusCheckerContext.getStatusChecker;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class PatchProductServiceImpl implements PatchProductService {
    private final ObjectMapper mapper;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final Validator validator;

    private final ProductStateChangeEventProducerImpl productStateChangeEventProducer;
    private final ProductRepository productRepository;
    private final List<ProductStatusType> terminalStatuses = List.of(ProductStatusType.TERMINATED, ProductStatusType.CANCELLED, ProductStatusType.SOLD, ProductStatusType.ABORTED);
    private final List<ProductStatusType> nonSoldTerminalStatuses = List.of(ProductStatusType.TERMINATED, ProductStatusType.CANCELLED, ProductStatusType.ABORTED);
    private final List<ProductStatusType> activeStatuses = List.of(ProductStatusType.ACTIVE, ProductStatusType.SOLD);
    private final ProductTerminationEventService productTerminationEventService;
    private static boolean isProductType(ProductEntity patchedProduct, ProductTypeEnum productTypeEnum) {
        return patchedProduct.getAtType().equals(productTypeEnum.getValue());
    }

    private void checkRelationshipForSoldStatus(ProductEntity patchedProduct) {
        if (!isProductType(patchedProduct, SHIPMENT_PRODUCT) &&
                !isProductType(patchedProduct, PHYSICAL_PRODUCT)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(INVALID_STATUS_SOLD_FOR_PRODUCT_TYPE, patchedProduct.getAtType()));
        }

        if (isProductType(patchedProduct, SHIPMENT_PRODUCT)
                && !isShipmentLinkedToPhysicalProduct(patchedProduct)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(SHIPMENT_PRODUCT_MISSING_RELIES_ON_RELATIONSHIP_WITH_PHYSICAL_PRODUCT, patchedProduct.getId()));
        }

    }

    /**
     * check if the product in param is linked to a physical product (relationship relies on) in the same contract.
     *
     * @param patchedProduct product patched
     * @return boolean
     */
    private boolean isShipmentLinkedToPhysicalProduct(ProductEntity patchedProduct) {
        return Optional.ofNullable(patchedProduct.getProductRelationship())
                .stream()
                .flatMap(Collection::stream)
                .filter(productRelationship ->
                        ProductEntityUtil.isRelationShipTypeOneOf(productRelationship, ProductRelationshipType.RELIESON))
                .anyMatch(relationshipEntity -> {
                    ProductEntity product = productRepository.findById(relationshipEntity.getProduct().getId().toString())
                            .orElseThrow();
                    return isProductType(product, PHYSICAL_PRODUCT);
                });
    }

    @Override
    public Map<String, ArrayNode> groupProductOperations(List<ProductPatch> productPatches) {
        checkValuePresenceForNonRemoveOps(productPatches);
        Collector<? super ObjectNode, Object, ArrayNode> arrayNodeCollector = Collectors.collectingAndThen(Collectors.toList(), ops -> mapper.convertValue(ops, ArrayNode.class));
        return productPatches.stream().collect(Collectors.groupingBy(patch -> extractProductId(patch.getPath()), Collectors.mapping(this::createOperationObject, arrayNodeCollector)));
    }

    private void checkValuePresenceForNonRemoveOps(List<ProductPatch> productPatches) {
        for (ProductPatch patch : productPatches) {
            if (patch.getOp() != PatchOperationType.REMOVE && patch.getValue() == null) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(),  INVALID_INPUT.getStatus(),
                        String.format(VALUE_REQUIRED_FOR_NON_REMOVE_OPERATION, patch.getOp(), patch.getPath())
                );
            }
        }
    }
    private String extractProductId(String path) {
        String[] parts = path.split("/");
        return parts.length >= 5 ? parts[4] : "";
    }

    private ObjectNode createOperationObject(ProductPatch patch) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("op", patch.getOp().getValue());
        String[] path = patch.getPath().split("/");
        objectNode.put("path", "/" + String.join("/", Arrays.copyOfRange(path, 5, path.length)));
        try {
            String jsonString = mapper.writeValueAsString(patch.getValue());
            objectNode.set(VALUE, mapper.readTree(jsonString));

        } catch (JsonProcessingException e) {
            throw new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(),  "Failed to process export job: " + e.getMessage());
        }

        return objectNode;
    }

    public boolean checkContainsStatusModification(ArrayNode arrayNode) {
        for (JsonNode node : arrayNode) {
            if (isPath(node, "/status") || isPath(node, "/operationalStatus")) {
                return true;
            }
        }
        return false;
    }

    public void checkPatchProduct(Map<String, ArrayNode> productPatchNodes, List<ProductEntity> products) {
        List<RelatedProductOrderItemEntity> productOrderItems = new ArrayList<>();
        List<ProductEntity> patchProductRelationships = productService.getListOfProductEntityBy(getProductRelationshipIdsFrom(productPatchNodes));

        productPatchNodes.forEach((productId, arrayNode) -> {
            ProductEntity productEntity = ProductEntityUtil.findBy(products, productId).orElseThrow(() ->
                    new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, productId))
            );
            Set<String> uniquePatchRelationshipProductIds = new HashSet<>();
            for (JsonNode node : arrayNode) {
                try {
                    if (isPath(node, "/productOrderItem/-")) {
                        RelatedProductOrderItemEntity productOrderItem = mapper.treeToValue(node.get(VALUE), RelatedProductOrderItemEntity.class);
                        productOrderItems.add(productOrderItem);
                    }
                    if (isPath(node, PRODUCT_RELATIONSHIP_PATH)) {
                        ProductRelationshipEntity productRelationship = mapper.treeToValue(node.get(VALUE), ProductRelationshipEntity.class);
                        checkPatchWithProductRelationshipPath(productEntity, uniquePatchRelationshipProductIds, productRelationship, patchProductRelationships);
                    }
                    if (isPath(node, "/startDate") && Objects.nonNull(productEntity.getStartDate())) {
                        throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ALREADY_HAVE_START_DATE);
                    }
                } catch (JsonProcessingException e) {
                    throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ONE_OR_MULTIPLE_ISSUES_ARE_EXIST_IN_THE_PATH_OR_THE_VALUE_FIELDS);
                }
            }
        });
        validateProductOrderItem(productOrderItems);
    }

    private Set<String> getProductRelationshipIdsFrom(Map<String, ArrayNode> productPatchNodes) {
        Set<String> patchProductRelationshipIds = new HashSet<>();
        productPatchNodes.forEach((productId, arrayNode) -> {
            for (JsonNode node : arrayNode) {
                try {
                    if (isPath(node, PRODUCT_RELATIONSHIP_PATH)) {
                        ProductRelationshipEntity productRelationship = mapper.treeToValue(node.get(VALUE), ProductRelationshipEntity.class);
                        patchProductRelationshipIds.add(productRelationship.getProduct().getId().toString());
                    }
                } catch (JsonProcessingException e) {
                    throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ONE_OR_MULTIPLE_ISSUES_ARE_EXIST_IN_THE_PATH_OR_THE_VALUE_FIELDS);
                }
            }
        });
        return patchProductRelationshipIds;
    }

    private void checkPatchWithProductRelationshipPath(ProductEntity parent, Set<String> productIdSet, ProductRelationshipEntity productRelationship, List<ProductEntity> products) {
        String id = productRelationship.getProduct().getId().toString();
        isSameProductDuplicatedRelationship(productIdSet, id);
        isProductOnRelationshipWithSameProduct(parent.getId(), id);
        isProductWithRelationshipExists(parent.getProductRelationship(), id);
        ProductEntity child = ProductEntityUtil.findBy(products, id).orElseThrow(() ->
                new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, productRelationship.getProduct().getId()))
        );
        productRelationship.setProduct(new ProductRefEntity(child.getId()));
    }

    private boolean isPath(JsonNode node, String path) {
        return path.equals(node.get("path").asText());
    }

    @Override
    public List<ProductEntity> applyPatchToProductsWithValidation(Map<String, ArrayNode> productPatchNodes, List<ProductEntity> products) {
        List<ProductEntity> patchedProducts = new ArrayList<>();
        List<ProductEntity> prePatchedProducts = new ArrayList<>();
        productPatchNodes.forEach((productId, arrayNode) -> {
            try {
                ProductEntity productEntity = ProductEntityUtil.findBy(products, productId).orElseThrow(() ->
                        new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, productId))
                );
                prePatchedProducts.add(productEntity);

                ArrayNode replaceOps = filterPatchOperations(arrayNode, "replace");
                ArrayNode addOps = filterPatchOperations(arrayNode, "add");
                ArrayNode removeOps = filterPatchOperations(arrayNode, "remove");
                Product tempProduct = applyPatchToProduct(replaceOps, productEntity);
                tempProduct = applyPatchToProduct(addOps, productMapper.toEntity(tempProduct));
                Product finalProduct = applyPatchToProduct(removeOps, productMapper.toEntity(tempProduct));

                ProductDTOValidator.validateProduct(finalProduct, validator);
                ProductEntity patchedProductEntity = productMapper.toEntity(finalProduct);
                patchedProductEntity.setIsRootProduct(productEntity.getIsRootProduct());
                validateForUnpatchableAttributes(patchedProductEntity, productEntity);
                if (checkContainsStatusModification(arrayNode)) {
                    validProductStatusTransition(patchedProductEntity, productEntity);
                    productStateChangeEventProducer.publishEvent(StateChangeProduct.fromProduct(finalProduct, productEntity.getStatus()), PRODUCT_STATE_CHANGE.name(), PRODUCT_STATE_CHANGE.getDomain());
                }
                patchedProducts.add(patchedProductEntity);
            } catch (InvalidTypeIdException e) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ONE_OR_MORE_OPERATION_FIELDS_OP_MUST_BE_VERIFIED);
            } catch (IOException | JsonPatchException e) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ONE_OR_MULTIPLE_ISSUES_ARE_EXIST_IN_THE_PATH_OR_THE_VALUE_FIELDS);
            }

        });
        checkPatchProduct(productPatchNodes, products);
        checkInnerProductRelationShipStatus(productPatchNodes, patchedProducts);
        productTerminationEventService.sendEventOnTerminationDateChange(prePatchedProducts, patchedProducts);
        return patchedProducts;
    }
    private ArrayNode filterPatchOperations(ArrayNode source, String opType) {
        ArrayNode filtered = mapper.createArrayNode();
        for (JsonNode node : source) {
            if (node.has("op") && opType.equalsIgnoreCase(node.get("op").asText())) {
                filtered.add(node);
            }
        }
        return filtered;
    }


    private Product applyPatchToProduct(ArrayNode arrayNode, ProductEntity product) throws JsonPatchException, IOException {
        Product productDto = productMapper.toDtoWithFullMapping(product);
        handleNullLists(productDto, arrayNode);
        JsonNode productJson = mapper.convertValue(productDto, JsonNode.class);
        validatePatchOperations(arrayNode, productJson);
        sortRemoveOperations(arrayNode);
        JsonPatch patch = JsonPatch.fromJson(arrayNode);
        JsonNode patched = patch.apply(productJson);
        return mapper.treeToValue(patched, Product.class);
    }
    private void validatePatchOperations(ArrayNode arrayNode, JsonNode productJson) throws JsonPatchException {
        List<String> invalidPaths = StreamSupport.stream(arrayNode.spliterator(), false)
                .filter(op -> "remove".equals(op.get("op").asText()))
                .map(op -> op.get("path").asText())
                .filter(path -> !jsonPathExists(productJson, path))
                .toList();

        if (!invalidPaths.isEmpty()) {
            throw new JsonPatchException("Invalid paths: " + String.join(", ", invalidPaths));
        }
    }
    private void sortRemoveOperations(ArrayNode arrayNode) {
        List<JsonNode> sortedOperations = StreamSupport.stream(arrayNode.spliterator(), false)
                .sorted((a, b) -> Integer.compare(
                        extractLastIndexFromPath(b.get("path").asText()),
                        extractLastIndexFromPath(a.get("path").asText())
                ))
                .toList();

        arrayNode.removeAll();
        sortedOperations.forEach(arrayNode::add);
    }
    private int extractLastIndexFromPath(String path) {
        Matcher matcher = Pattern.compile("/(\\d+)$").matcher(path);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }
    private boolean jsonPathExists(JsonNode node, String path) {
        return !node.at(path).isMissingNode();
    }

    private void checkInnerProductRelationShipStatus(Map<String, ArrayNode> productPatchNodes, List<ProductEntity> patchedProducts) {
        List<ProductEntity> allPatchedProducts = new ArrayList<>();
        List<ProductEntity> activeProductEntities = new ArrayList<>();
        List<ProductEntity> soldProductEntities = new ArrayList<>();
        List<ProductEntity> terminatedProductEntities = new ArrayList<>();
        productPatchNodes.forEach((productId, arrayNode) -> {
            for (JsonNode node : arrayNode) {
                if (isPath(node, "/status")) {
                    ProductEntity patchedProduct = ProductEntityUtil.findBy(patchedProducts, productId).orElseThrow(() ->
                            new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, productId))
                    );
                    allPatchedProducts.add(patchedProduct);
                    try {
                        ProductStatusType status = mapper.treeToValue(node.get(VALUE), ProductStatusType.class);
                        if (status.equals(ProductStatusType.SOLD)) {
                            checkRelationshipForSoldStatus(patchedProduct);
                            soldProductEntities.add(patchedProduct);
                        }
                        if (status.equals(ProductStatusType.ACTIVE)) {
                            activeProductEntities.add(patchedProduct);
                            ProductDatesChecker.setTerminationDate(patchedProduct);
                        } else if (nonSoldTerminalStatuses.contains(status)) {
                            terminatedProductEntities.add(patchedProduct);
                        }
                    } catch (JsonProcessingException e) {
                        throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ONE_OR_MULTIPLE_ISSUES_ARE_EXIST_IN_THE_PATH_OR_THE_VALUE_FIELDS);
                    }
                }
            }
        });
        checkInnerProductRelationShipStatusActive(activeProductEntities, allPatchedProducts);
        checkInnerProductRelationShipStatusSold(soldProductEntities, allPatchedProducts);
        checkInnerProductRelationShipForFinalStatus(terminatedProductEntities, allPatchedProducts);
    }



    private void validProductStatusTransition(ProductEntity productPatched, ProductEntity product) {
        getStatusChecker(product.getAtType()).validateProductStatus(product.getStatus(), product.getOperationalStatus(), productPatched.getStatus(), productPatched.getOperationalStatus());
        ProductDatesChecker.validateProductDates(productPatched);
        StatusChecker.addStatusChange(productPatched);
    }

    private void checkInnerProductRelationShipForFinalStatus(List<ProductEntity> patchedTerminatedProducts, List<ProductEntity> allPatchedProducts) {
        Map<String, String> productsNotPresentInPatchToBeCheckedForTerminalStatus = new HashMap<>();
        patchedTerminatedProducts.forEach(patchedTerminatedProduct -> {
            if (!nonSoldTerminalStatuses.contains(patchedTerminatedProduct.getStatus())) {
                return;
            }
            if (patchedTerminatedProduct.getProductRelationship() != null) {
                for (ProductRelationshipEntity productRelationship : patchedTerminatedProduct.getProductRelationship()) {
                    if (ProductEntityUtil.isRelationShipTypeOneOf(productRelationship, ProductRelationshipType.SELLS, ProductRelationshipType.BUNDLES)) {
                        Optional<ProductEntity> optionalPatchedProduct = allPatchedProducts.stream().filter(product -> product.getId().equals(productRelationship.getProduct().getId().toString())).findFirst();
                        if (optionalPatchedProduct.isPresent() && !terminalStatuses.contains(optionalPatchedProduct.get().getStatus())) {
                            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(PRODUCT_STATUS_CANNOT_BE_TERMINATED, patchedTerminatedProduct.getId(), optionalPatchedProduct.get().getId()));
                        } else if (optionalPatchedProduct.isEmpty()) {
                            productsNotPresentInPatchToBeCheckedForTerminalStatus.put(productRelationship.getProduct().getId().toString(), patchedTerminatedProduct.getId());
                        }
                    }
                }
                Optional<ProductEntity> byIdInAndStatusNotIn = productRepository.findFirstByIdInAndStatusNotIn(productsNotPresentInPatchToBeCheckedForTerminalStatus.keySet(), terminalStatuses);
                if (byIdInAndStatusNotIn.isPresent()) {
                    throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(PRODUCT_STATUS_CANNOT_BE_TERMINATED, productsNotPresentInPatchToBeCheckedForTerminalStatus.get(byIdInAndStatusNotIn.get().getId()), byIdInAndStatusNotIn.get().getId()));
                }
            }

        });
    }

    private void checkInnerProductRelationShipStatusActive(List<ProductEntity> patchedActiveProducts, List<ProductEntity> allPatchedProducts) {
        Set<String> productsChildNotPresentInPatchToBeCheckedForActiveStatus = new HashSet<>();
        patchedActiveProducts.forEach(patchedActiveProduct -> {
            if (!patchedActiveProduct.getStatus().equals(ProductStatusType.ACTIVE)) {
                return;
            }
            boolean anyChildIsActive = false;
            boolean productHasChildrenToBeChecked = false;
            if (patchedActiveProduct.getProductRelationship() != null) {

                for (ProductRelationshipEntity productRelationship : patchedActiveProduct.getProductRelationship()) {
                    if (ProductEntityUtil.isRelationShipTypeOneOf(productRelationship, ProductRelationshipType.SELLS, ProductRelationshipType.BUNDLES)) {
                        productHasChildrenToBeChecked = true;
                        Optional<ProductEntity> optionalPatchedProduct = allPatchedProducts.stream().filter(product -> product.getId().equals(productRelationship.getProduct().getId().toString())).findFirst();
                        if (optionalPatchedProduct.isPresent() && activeStatuses.contains(optionalPatchedProduct.get().getStatus())) {
                            anyChildIsActive = true;
                            break;
                        } else if (optionalPatchedProduct.isEmpty()) {
                            productsChildNotPresentInPatchToBeCheckedForActiveStatus.add(productRelationship.getProduct().getId().toString());
                        }
                    }
                }


                if (
                        productHasChildrenToBeChecked
                                && !anyChildIsActive
                                && !productsChildNotPresentInPatchToBeCheckedForActiveStatus.isEmpty()
                                && productRepository
                                .findFirstByIdInAndStatusIn(productsChildNotPresentInPatchToBeCheckedForActiveStatus, activeStatuses)
                                .isPresent()
                ) {
                    anyChildIsActive = true;

                }
                if (productHasChildrenToBeChecked && !anyChildIsActive) {
                    throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(PRODUCT_STATUS_CANNOT_BE_ACTIVE, patchedActiveProduct.getId()));
                }
            }
        });
    }

    private void checkInnerProductRelationShipStatusSold(List<ProductEntity> soldProducts, List<ProductEntity> allPatchedProducts) {
        Set<String> productsChildNotPresentInPatchToBeCheckedForSoldStatus = new HashSet<>();

        soldProducts.stream()
                .filter(product -> product.getProductSpecification() == null && product.getStatus().equals(ProductStatusType.SOLD))
                .forEach(soldProduct -> {
                    boolean anyChildProductIsSold = false;
                    boolean productHasChildrenToBeChecked = false;
                    if (soldProduct.getProductRelationship() != null) {
                        for (ProductRelationshipEntity productRelationship : soldProduct.getProductRelationship()) {
                            if (ProductEntityUtil.isRelationShipTypeOneOf(productRelationship, ProductRelationshipType.SELLS)) {
                                productHasChildrenToBeChecked = true;
                                Optional<ProductEntity> optionalPatchedProduct = allPatchedProducts.stream().filter(product -> product.getId().equals(productRelationship.getProduct().getId().toString())).findFirst();
                                if (optionalPatchedProduct.isPresent() && optionalPatchedProduct.get().getStatus().equals(ProductStatusType.SOLD)) {
                                    anyChildProductIsSold = true;
                                    break;
                                } else if (optionalPatchedProduct.isEmpty()) {
                                    productsChildNotPresentInPatchToBeCheckedForSoldStatus.add(productRelationship.getProduct().getId().toString());
                                }
                            }

                        }
                        if (productHasChildrenToBeChecked
                                && !anyChildProductIsSold
                                && !productsChildNotPresentInPatchToBeCheckedForSoldStatus.isEmpty()
                                && productRepository.findFirstByIdInAndStatusIn(productsChildNotPresentInPatchToBeCheckedForSoldStatus, List.of(ProductStatusType.SOLD))
                                .isPresent()
                        ) {
                            anyChildProductIsSold = true;
                        }
                        if (productHasChildrenToBeChecked && !anyChildProductIsSold) {
                            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(PRODUCT_STATUS_CANNOT_BE_SOLD, soldProduct.getId()));
                        }
                    }

                });
    }

    @Override
    public List<ProductEntity> abortProductsByOrderId(String productOrderId) {
        List<ProductEntity> patchedProducts = new ArrayList<>();
        Set<StateChangeProduct> productToPublish = new HashSet<>();
        List<ProductEntity> products = productRepository.findByProductOrderItemProductOrderIdAndStatus(productOrderId, ProductStatusType.CREATED);
        products.forEach(patchedProduct -> {
            final ProductStatusType oldStatus = patchedProduct.getStatus();
            patchedProduct.setStatus(ProductStatusType.ABORTED);
            patchedProduct.setOperationalStatus(ProductOperationalStatusType.ABORTED);
            StatusChecker.addStatusChange(patchedProduct);
            patchedProducts.add(patchedProduct);
            // @formatter:off
            productToPublish.add(StateChangeProduct
                    .builder()
                    .id(patchedProduct.getId())
                    .operationalStatus(patchedProduct.getOperationalStatus())
                    .status(patchedProduct.getStatus())
                    .atType(patchedProduct.getAtType())
                    .oldStatus(oldStatus)
                    .build()
            );
            // @formatter:on
        });
        productRepository.saveAll(patchedProducts);
        productStateChangeEventProducer.publishEvents(productToPublish, ORDER_STATE_CHANGE.getTitle(), ORDER_STATE_CHANGE.getDomain());
        return patchedProducts;
    }

    public boolean isUpdatedByPatch(ArrayNode arrayNode, String path) {
        for (JsonNode node : arrayNode) {
            if (isPath(node, path)) {
                return true;
            }
        }
        return false;
    }

    private void handleNullLists(Product product, ArrayNode arrayNode) {
        if (Objects.isNull(product.getProductRelationship()) && isUpdatedByPatch(arrayNode, PRODUCT_RELATIONSHIP_PATH)) {
            product.setProductRelationship(new ArrayList<>());
        }
        if (Objects.isNull(product.getProductCharacteristic()) && isUpdatedByPatch(arrayNode, PRODUCT_CHARACTERISTIC_PATH)) {
            product.setProductCharacteristic(new ArrayList<>());
        }
        if (Objects.isNull(product.getProductPrice()) && isUpdatedByPatch(arrayNode, PRODUCT_PRICE_PATH)) {
            product.setProductPrice(new ArrayList<>());
        }
    }

}

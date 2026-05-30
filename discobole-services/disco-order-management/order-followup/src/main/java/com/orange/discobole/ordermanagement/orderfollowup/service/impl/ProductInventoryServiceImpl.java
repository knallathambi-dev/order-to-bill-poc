// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductDateHelper;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.orderfollowup.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productinventory.dto.v1.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage.*;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.ServiceConstants.PRODUCT_INVENTORY_URI;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class ProductInventoryServiceImpl implements ProductInventoryService {
    public static final String PRODUCT_REF = "ProductRef";

    private final DiscoServiceUrl discoServiceUrl;
    private final ProductDateHelper productDateHelper;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductInventoryServiceImpl(DiscoServiceUrl discoServiceUrl, ProductDateHelper productDateHelper, WebClient webClient, ObjectMapper objectMapper) {
        this.discoServiceUrl = discoServiceUrl;
        this.productDateHelper = productDateHelper;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    private static boolean isContractProductUpdated(List<Product> updatedProducts, String contractProductId) {
        return updatedProducts
                .stream()
                .anyMatch(product -> product.getId().equals(contractProductId));
    }

    private static void setContractOperationStatus(PatchDTOList patchDTOList, String contractProductId) {
        PatchDTO patch = PatchDTO.builder()
                .op(PatchOperationType.REPLACE)
                .path(PRODUCT_INVENTORY_URI + contractProductId + ServiceConstants.OPERATIONAL_STATUS_URI)
                .value(ProductOperationalStatusType.ACTIVE.getValue())
                .build();
        if (CollectionUtils.isEmpty(patchDTOList.getList())) {
            patchDTOList.setList(List.of(patch));
        } else {
            List<PatchDTO> patches = patchDTOList.getList();
            List<PatchDTO> newPatches = new ArrayList<>(patches);
            newPatches.add(patch);
            patchDTOList.setList(newPatches);
        }
    }

    @Override
    public List<Product> getProductsByProductOrderById(String productOrderId) {
        log.info("Fetching products from inventory for productOrderId: {}", productOrderId);
        String productInventoryUrl = discoServiceUrl.getProductInventoryUrl();
        log.debug("Product inventory URL: {}", productInventoryUrl);
        return webClient.get()
                .uri(productInventoryUrl, uri -> uri
                        .queryParam("productOrderItem.productOrderId", productOrderId)
                        .build())
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(response -> {
                    HttpStatusCode statusCode = response.getStatusCode();
                    log.debug("GET products by productOrderId: {} - Response status: {}", productOrderId, statusCode);
                    if (statusCode == HttpStatus.OK) {
                        List<Product> products = response.getBody();
                        if (products != null) {
                            log.info("Successfully fetched {} product(s) for productOrderId: {}", products.size(), productOrderId);
                            return Mono.just(products);
                        } else {
                            log.error("Received null response body while fetching products for productOrderId: {}", productOrderId);
                            return Mono.error(new DiscoException(RECEIVED_NULL_RESPONSE_BODY));
                        }

                    } else {
                        log.error("Failed to fetch products for productOrderId: {} - Unexpected status code: {}", productOrderId, statusCode);
                        return Mono.error(new DiscoException("Products cannot be found. Status code: " + response.getStatusCode()));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error fetching products for productOrderId: {}", productOrderId, error);
                    return Mono.error(new DiscoException("Product cannot be found."));
                })
                .block();
    }

    @Override
    public List<Product> updateProducts(String jsonPatch) {
        String productInventoryUrl = discoServiceUrl.getProductInventoryUrl();
        log.info("Updating products in inventory via PATCH request");
        log.debug("PATCH request body: {}", jsonPatch);
        return webClient.patch()
                .uri(productInventoryUrl)
                .header("Content-Type", ServiceConstants.APPLICATION_JSON_PATCH_JSON)
                .bodyValue(jsonPatch)
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(response -> {
                    HttpStatusCode statusCode = response.getStatusCode();
                    log.debug("PATCH products - Response status: {}", statusCode);
                    if (statusCode == HttpStatus.OK) {
                        List<Product> updatedProducts = response.getBody();
                        if (updatedProducts != null) {
                            log.info("Successfully updated {} product(s) in inventory", updatedProducts.size());
                            return Mono.just(updatedProducts);
                        } else {
                            log.error("Received null response body after PATCH update");
                            return Mono.error(new DiscoException(ERROR_UPDATING_PRODUCTS_IN_INVENTORY));
                        }
                    }
                    log.error("PATCH products failed with unexpected status code: {}", statusCode);
                    return Mono.error(new DiscoException(PRODUCTS_UPDATE_FAILED_IN_INVENTORY));
                })
                .onErrorResume(error -> {
                    if (error instanceof DiscoException discoException) {
                        return Mono.error(discoException);
                    } else if (error instanceof WebClientResponseException ex) {
                        HttpStatusCode status = ex.getStatusCode();
                        log.error("PATCH products - WebClient error response - status: {}, body: {}", status.value(), ex.getResponseBodyAsString());
                        if (status == HttpStatus.BAD_REQUEST || status == HttpStatus.INTERNAL_SERVER_ERROR) {
                            return Mono.error(new DiscoException(ERROR_UPDATING_PRODUCTS_IN_INVENTORY));
                        }
                    }
                    log.error("PATCH products - Unexpected error during update", error);
                    return Mono.error(new DiscoException(PRODUCTS_UPDATE_FAILED_IN_INVENTORY));
                })
                .block();
    }

    @Override
    public Product getProductById(String productId) {
        if (isBlank(productId)) {
            log.warn("getProductById called with blank productId - rejecting request");
            throw new InvalidParameterException(INVALID_INPUT);
        }
        log.info("Fetching product by id: {}", productId);
        String productInventoryByIdUrl = discoServiceUrl.getProductInventoryByIdUrl(productId);
        log.debug("Product inventory by-id URL: {}", productInventoryByIdUrl);

        return webClient.get()
                .uri(productInventoryByIdUrl)
                .retrieve()
                .toEntity(Product.class)
                .flatMap(response -> {
                    HttpStatusCode statusCode = response.getStatusCode();
                    log.debug("GET product by id: {} - Response status: {}", productId, statusCode);
                    if (statusCode == HttpStatus.OK) {
                        Product product = response.getBody();
                        if (product != null) {
                            log.info("Successfully fetched product id: {} - status: {}", productId, product.getStatus());
                            return Mono.just(product);
                        } else {
                            log.error("Received null response body for productId: {}", productId);
                            return Mono.error(new DiscoException(RECEIVED_NULL_RESPONSE_BODY));
                        }
                    } else {
                        log.error("Failed to fetch product id: {} - Unexpected status code: {}", productId, statusCode);
                        return Mono.error(new DiscoException("Product cannot be found. Status code: " + response.getStatusCode()));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error fetching product by id: {}", productId, error);
                    return Mono.error(new DiscoException("Product cannot be found"));
                })
                .block();
    }

    @Override
    public void updateProductsHierarchy(String productOrderId, String deliveredProductOrderItemId, ProductOrderStateType productOrderState) {
        log.info("Updating products hierarchy - productOrderId: {}, deliveredProductOrderItemId: {}, productOrderState: {}",
                productOrderId, deliveredProductOrderItemId, productOrderState);
        if (productOrderId == null || deliveredProductOrderItemId == null) {
            log.warn("Skipping hierarchy update - productOrderId or deliveredProductOrderItemId is null");
            return;
        }
        List<Product> products = getProductsByProductOrderById(productOrderId);
        if (CollectionUtils.isEmpty(products)) {
            log.warn("No products found for productOrderId: {} - skipping hierarchy update", productOrderId);
            return;
        }
        log.debug("Found {} product(s) for hierarchy update - productOrderId: {}", products.size(), productOrderId);

        Product deliveredProduct = getDeliveredProduct(products, deliveredProductOrderItemId);
        if (Objects.isNull(deliveredProduct)) {
            log.warn("Delivered product not found for orderItemId: {} - skipping hierarchy update", deliveredProductOrderItemId);
            return;
        }
        log.debug("Delivered product identified: {} for orderItemId: {}", deliveredProduct.getId(), deliveredProductOrderItemId);

        //process the update products status, product price dates
        processUpdateProducts(products, deliveredProduct, productOrderState, productOrderId);

        //process the update of some relationship and status for the migration use case
        processUpdatesForMigration(products, productOrderState, productOrderId);
        log.info("Products hierarchy update completed - productOrderId: {}", productOrderId);
    }

    private Product getDeliveredProduct(List<Product> products, String deliveredProductOrderItemId) {
        return products.stream()
                .filter(product -> product.getProductOrderItem().stream()
                        .anyMatch(relatedProductOrderItem -> deliveredProductOrderItemId.equals(relatedProductOrderItem.getOrderItemId())))
                .findFirst()
                .orElse(null);
    }

    private void processUpdateProducts(List<Product> products, Product deliveredProduct, ProductOrderStateType productOrderState, String productOrderId) {
        log.debug("Processing product updates for deliveredProduct: {}, productOrderId: {}", deliveredProduct.getId(), productOrderId);
        List<Product> updatedProducts = new ArrayList<>();
        PatchDTOList patchDTOList = PatchDTOList.builder().build();

        updateProductHierarchyStatus(products, deliveredProduct, updatedProducts, patchDTOList);
        updateContractProductOperationStatus(deliveredProduct, productOrderState, updatedProducts, patchDTOList);

        updateProductPriceDate(deliveredProduct, productOrderId, patchDTOList);
        updateProductTermDate(deliveredProduct, productOrderId, patchDTOList);

        if (CollectionUtils.isEmpty(patchDTOList.getList())) {
            log.debug("No patches generated for deliveredProduct: {} - skipping update", deliveredProduct.getId());
            return;
        }

        log.debug("Applying {} patch(es) for deliveredProduct: {}", patchDTOList.getList().size(), deliveredProduct.getId());
        List<Product> patchedProducts = processProductUpdate(patchDTOList);
        patchedProducts = removeProductById(patchedProducts, deliveredProduct.getId());
        if (CollectionUtils.isEmpty(patchedProducts)) {
            log.debug("No additional products to update after removing deliveredProduct: {}", deliveredProduct.getId());
            return;
        }
        log.debug("Updating term and price dates for {} remaining product(s)", patchedProducts.size());
        updateProductsTermAndPriceDate(patchedProducts, productOrderId);
    }

    private List<Product> removeProductById(List<Product> products, String id) {
        return products.stream()
                .filter(product -> !product.getId().equals(id))
                .toList();
    }

    private void updateProductTermDate(Product deliveredProduct, String productOrderId, PatchDTOList patchDTOList) {
        List<PatchDTO> productTermPatches = productDateHelper.updateProductTermDate(deliveredProduct, productOrderId);
        updatePatchDTOList(patchDTOList, productTermPatches);
    }

    private void updateProductsTermAndPriceDate(List<Product> products, String productOrderId) {

        List<PatchDTO> patches = Stream.of(
                        getProductTermsPatches(products, productOrderId),
                        getProductPricesPatches(products, productOrderId)
                )
                .flatMap(List::stream)
                .toList();

        if (patches.isEmpty()) {
            return;
        }

        processProductUpdate(
                PatchDTOList.builder()
                        .list(patches)
                        .build()
        );
    }

    private List<PatchDTO> getProductTermsPatches(List<Product> patchedProducts, String productOrderId) {
        return patchedProducts.stream()
                .map(product -> productDateHelper.updateProductTermDate(product, productOrderId))
                .filter(patchDTOS -> !CollectionUtils.isEmpty(patchDTOS))
                .flatMap(List::stream)
                .toList();
    }

    private List<PatchDTO> getProductPricesPatches(List<Product> patchedProducts, String productOrderId) {
        return patchedProducts.stream()
                .map(product -> productDateHelper.updateProductPriceDate(product, productOrderId))
                .filter(patchDTOS -> !CollectionUtils.isEmpty(patchDTOS))
                .flatMap(List::stream)
                .toList();
    }

    private void updateProductHierarchyStatus(List<Product> products, Product deliveredProduct, List<Product> updatedProducts, PatchDTOList patchDTOList) {
        log.debug("Traversing product hierarchy upward from deliveredProduct: {}", deliveredProduct.getId());
        Product nextLevelProduct = getParentProduct(products, deliveredProduct);
        int level = 0;
        while (nextLevelProduct != null) {
            level++;
            ProductStatusType oldProductStatus = nextLevelProduct.getStatus();
            List<Product> childProducts = getChildProducts(products, nextLevelProduct);
            setProductStatus(nextLevelProduct, childProducts);
            if (!oldProductStatus.equals(nextLevelProduct.getStatus())) {
                log.debug("Hierarchy level {} - Product {} status changed: {} -> {}",
                        level, nextLevelProduct.getId(), oldProductStatus, nextLevelProduct.getStatus());
                updatedProducts.add(nextLevelProduct);
            } else {
                log.trace("Hierarchy level {} - Product {} status unchanged: {}", level, nextLevelProduct.getId(), oldProductStatus);
            }
            nextLevelProduct = getParentProduct(products, nextLevelProduct);
        }
        log.debug("Hierarchy traversal completed - {} level(s) traversed, {} product(s) with status change", level, updatedProducts.size());

        if (!CollectionUtils.isEmpty(updatedProducts)) {
            createPatchWithHierarchy(updatedProducts, patchDTOList);
        }
    }

    private void updateContractProductOperationStatus(Product deliveredProduct, ProductOrderStateType productOrderState, List<Product> updatedProducts, PatchDTOList patchDTOList) {
        boolean isAllProductProceeded = isAllProductProceeded(productOrderState);
        if (isAllProductProceeded) {
            log.debug("All products proceeded (state: {}) - checking contract product operational status", productOrderState);
            Optional<String> contractProductId = findContractProductId(deliveredProduct);
            if (contractProductId.isPresent()) {
                boolean isContractProductUpdated = isContractProductUpdated(updatedProducts, contractProductId.get());
                if (!isContractProductUpdated) {
                    Product contractProduct = getProductById(contractProductId.get());
                    if (contractProduct.getOperationalStatus().equals(ProductOperationalStatusType.PENDINGMODIFICATION)) {
                        log.info("Setting contract product {} operational status from PendingModification to ACTIVE", contractProductId.get());
                        setContractOperationStatus(patchDTOList, contractProductId.get());
                    } else {
                        log.debug("Contract product {} operational status is {} - no update needed",
                                contractProductId.get(), contractProduct.getOperationalStatus());
                    }
                } else {
                    log.debug("Contract product {} already updated in hierarchy - skipping", contractProductId.get());
                }
            } else {
                log.debug("No contract product (rootProduct) relationship found for deliveredProduct: {}", deliveredProduct.getId());
            }
        } else {
            log.debug("Product order state {} does not require contract operational status update", productOrderState);
        }
    }

    private void updateProductPriceDate(Product deliveredProduct, String productOrderId, PatchDTOList patchDTOList) {
        List<PatchDTO> orderPricesPatches = productDateHelper.updateProductPriceDate(deliveredProduct, productOrderId);
        updatePatchDTOList(patchDTOList, orderPricesPatches);
    }

    private void updatePatchDTOList(PatchDTOList patchDTOList, List<PatchDTO> patches) {
        if (!patches.isEmpty()) {
            if (CollectionUtils.isEmpty(patchDTOList.getList())) {
                patchDTOList.setList(patches);
            } else {
                List<PatchDTO> oldPatches = patchDTOList.getList();
                List<PatchDTO> newPatches = new ArrayList<>(oldPatches);
                newPatches.addAll(patches);
                patchDTOList.setList(newPatches);
            }
        }
    }

    private Optional<String> findContractProductId(Product product) {
        return Optional.ofNullable(product.getProductRelationship())
                .stream()
                .flatMap(Collection::stream)
                .filter(r -> ServiceConstants.ROOT_PRODUCT.equals(r.getRelationshipType()))
                .map(r -> ((ProductRef) r.getProduct()).getId())
                .findFirst();
    }

    private boolean isAllProductProceeded(ProductOrderStateType productOrderState) {
        return productOrderState != null && (productOrderState.equals(ProductOrderStateType.FAILED) || productOrderState.equals(ProductOrderStateType.PARTIAL) || productOrderState.equals(ProductOrderStateType.COMPLETED));
    }

    private List<Product> processProductUpdate(PatchDTOList patchDTOList) {
        try {
            String patchJson = objectMapper.writeValueAsString(patchDTOList.getList());
            log.debug("Processing product update with {} patch operation(s)", patchDTOList.getList().size());
            return updateProducts(patchJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize patch operations to JSON", e);
            throw new DiscoException(UNABLE_TO_UPDATE_PRODUCTS);
        }
    }

    public void createPatchWithHierarchy(List<Product> updatedProductList, PatchDTOList patchDTOList) {
        List<PatchDTO> patches = updatedProductList.stream()
                .map(product -> buildProductPatchRequest(product.getId(), product.getStatus(), product.getOperationalStatus()))
                .flatMap(List::stream)
                .toList();

        patchDTOList.setList(patches);
    }

    private List<PatchDTO> buildProductPatchRequest(String productId, ProductStatusType
            status, ProductOperationalStatusType operationalStatus) {
        String productUri = PRODUCT_INVENTORY_URI + productId;
        List<PatchDTO> patches = new ArrayList<>();
        PatchDTO patchStatus = createReplacePatchRequest(productUri + ServiceConstants.STATUS_URI, status.getValue());
        PatchDTO patchOperationalStatus = createReplacePatchRequest(productUri + ServiceConstants.OPERATIONAL_STATUS_URI, operationalStatus.getValue());
        patches.add(patchStatus);
        patches.add(patchOperationalStatus);

        return patches;
    }

    private PatchDTO createReplacePatchRequest(String path, String value) {
        return PatchDTO.builder()
                .op(PatchOperationType.REPLACE)
                .path(path)
                .value(value)
                .build();
    }

    private void setProductStatus(Product parentProduct, List<Product> childProducts) {
        if (hasStatusAfterIgnoringSoldStatus(childProducts, ProductStatusType.ABORTED)) {
            log.trace("Product {} - child has ABORTED status, setting parent to ABORTED", parentProduct.getId());
            parentProduct.setStatus(ProductStatusType.ABORTED);
            parentProduct.setOperationalStatus(ProductOperationalStatusType.ABORTED);
        } else if (hasStatusAfterIgnoringSoldStatus(childProducts, ProductStatusType.CREATED)) {
            log.trace("Product {} - child has CREATED status, setting parent to CREATED", parentProduct.getId());
            parentProduct.setStatus(ProductStatusType.CREATED);
        } else if (allHaveSoldStatus(childProducts) || allHaveActiveStatusAfterIgnoringSoldStatus(childProducts)) {
            log.trace("Product {} - all children ACTIVE/SOLD, setting parent to ACTIVE", parentProduct.getId());
            parentProduct.setStatus(ProductStatusType.ACTIVE);
            parentProduct.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        } else if (allHaveTerminatedStatusAfterIgnoringSoldStatus(childProducts)) {
            log.trace("Product {} - all children TERMINATED, setting parent to TERMINATED", parentProduct.getId());
            parentProduct.setStatus(ProductStatusType.TERMINATED);
            parentProduct.setOperationalStatus(ProductOperationalStatusType.TERMINATED);
        }
    }

    private boolean hasStatusAfterIgnoringSoldStatus(List<Product> products, ProductStatusType...
            statusTypes) {
        return products.stream()
                .map(Product::getStatus)
                .filter(status -> status != ProductStatusType.SOLD)
                .anyMatch(status -> Arrays.asList(statusTypes).contains(status));
    }

    private boolean allHaveActiveStatusAfterIgnoringSoldStatus(List<Product> products) {
        return products.stream().filter(product -> product.getStatus() != ProductStatusType.SOLD)
                .allMatch(product -> product.getStatus() == ProductStatusType.ACTIVE);
    }

    private boolean allHaveTerminatedStatusAfterIgnoringSoldStatus(List<Product> products) {
        return products.stream().filter(product -> product.getStatus() != ProductStatusType.SOLD)
                .allMatch(product -> product.getStatus() == ProductStatusType.TERMINATED);
    }

    private boolean allHaveSoldStatus(List<Product> products) {
        return products.stream()
                .allMatch(product -> product.getStatus() == ProductStatusType.SOLD);
    }

    private List<Product> getChildProducts(List<Product> products, Product parentProduct) {
        List<String> childIds = getProductIdsWithBundlesRelationType(parentProduct);
        return getProductsByIds(childIds, products);
    }

    private List<String> getProductIdsWithBundlesRelationType(Product product) {
        return product.getProductRelationship().stream()
                .filter(Objects::nonNull)
                .filter(productRelationship -> ServiceConstants.BUNDLES.equals(productRelationship.getRelationshipType()) || ServiceConstants.BUNDLES_MIGRATE.equals(productRelationship.getRelationshipType()))
                .map(ProductRelationship::getProduct)
                .filter(ProductRef.class::isInstance)
                .map(ProductRef.class::cast)
                .map(ProductRef::getId)
                .toList();
    }

    private List<Product> getProductsByIds(List<String> childIds, List<Product> products) {
        return products.stream()
                .filter(product -> childIds.contains(product.getId()))
                .toList();
    }


    private Product getParentProduct(List<Product> products, Product product) {
        return findParentByRelationshipType(products, product, ServiceConstants.BUNDLES_MIGRATE)
                .or(() -> findParentByRelationshipType(products, product, ServiceConstants.BUNDLES))
                .orElse(null);
    }

    private Optional<Product> findParentByRelationshipType(List<Product> products,
                                                           Product targetProduct,
                                                           String relationshipType) {
        String targetProductId = targetProduct.getId();

        return products.stream()
                .filter(p -> hasRelationshipTo(p, targetProductId, relationshipType))
                .findFirst();
    }

    private boolean hasRelationshipTo(
            Product product,
            String targetProductId,
            String relationshipType) {

        List<ProductRelationship> relationships = product.getProductRelationship();
        if (relationships == null) {
            return false;
        }

        return relationships.stream()
                .filter(Objects::nonNull)
                .filter(relationship -> relationshipType.equals(relationship.getRelationshipType()))
                .map(ProductRelationship::getProduct)
                .filter(ProductRef.class::isInstance)
                .map(ProductRef.class::cast)
                .anyMatch(productRef -> targetProductId.equals(productRef.getId()));
    }

    private void processUpdatesForMigration(List<Product> products, ProductOrderStateType productOrderState, String productOrderId) {
        if (ProductOrderStateType.COMPLETED.equals(productOrderState) && isMigrationUseCase(products, productOrderId)) {
            log.info("Migration use case detected for productOrderId: {} - processing migration updates", productOrderId);
            List<PatchDTO> patches = new ArrayList<>();

            //remove the bundlesMigrate relationship
            log.debug("Step 1/5 - Processing bundlesMigrate relationship removal for productOrderId: {}", productOrderId);
            processBundlesMigrateRelationship(products, productOrderId, patches);

            //process multiple relationship
            log.debug("Step 2/5 - Processing migrated atomic products for productOrderId: {}", productOrderId);
            processMigratedAtomicProducts(products, productOrderId, patches);

            //process terminate migrated item hierarchy
            log.debug("Step 3/5 - Processing terminate hierarchy for productOrderId: {}", productOrderId);
            processTerminateHierarchy(products, productOrderId, patches);

            //remove the reliesOn between the migrated atomic and existing product
            log.debug("Step 4/5 - Removing reliesOn relationships for migrated products - productOrderId: {}", productOrderId);
            removeReliesOnRelationshipForMigratedProduct(products, productOrderId, patches);

            //update the root product for the noChange/modify item
            log.debug("Step 5/5 - Updating root product identifiers for productOrderId: {}", productOrderId);
            updateRootProductIdentifier(products, productOrderId, patches);

            if (!CollectionUtils.isEmpty(patches)) {
                log.info("Applying {} migration patch(es) for productOrderId: {}", patches.size(), productOrderId);
                PatchDTOList patchDTOList = PatchDTOList.builder().build();
                patchDTOList.setList(patches);
                try {
                    updateProducts(patchDTOList.toJsonString());
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize migration patches to JSON for productOrderId: {}", productOrderId, e);
                    throw new DiscoException(UNABLE_TO_UPDATE_PRODUCTS);
                }
                log.info("Migration updates completed successfully for productOrderId: {}", productOrderId);
            } else {
                log.debug("No migration patches generated for productOrderId: {}", productOrderId);
            }
        } else {
            log.debug("No migration processing required - productOrderState: {}, productOrderId: {}", productOrderState, productOrderId);
        }
    }

    private void updateRootProductIdentifier(List<Product> products, String productOrderId, List<PatchDTO> patches) {
        String rootProductIdentifier = getRootProductId(products);
        List<Product> noChangeOrModifyProducts = products
                .stream()
                .filter(getNoChangeOrModifyProductPredicate(productOrderId))
                .toList();
        if (!CollectionUtils.isEmpty(noChangeOrModifyProducts) && !isBlank(rootProductIdentifier)) {
            log.debug("Updating root product identifier to {} for {} noChange/modify product(s)",
                    rootProductIdentifier, noChangeOrModifyProducts.size());
            noChangeOrModifyProducts.forEach(noChangeOrModifyProduct -> processUpdateRootProductIdentifier(noChangeOrModifyProduct, rootProductIdentifier, patches));
        } else {
            log.debug("No root product identifier update needed - rootProductId: {}, noChange/modify products count: {}",
                    rootProductIdentifier, noChangeOrModifyProducts.size());
        }
    }

    private void processUpdateRootProductIdentifier(Product noChangeModifyProduct, String rootProductIdentifier, List<PatchDTO> patches) {
        ProductRelationship rootProductRelationship = getFirstProductRelationship(noChangeModifyProduct, ServiceConstants.ROOT_PRODUCT);
        if (Objects.nonNull(rootProductRelationship)) {
            log.trace("Patching rootProduct identifier for product {} -> rootProductId: {}", noChangeModifyProduct.getId(), rootProductIdentifier);
            String path = PRODUCT_INVENTORY_URI + noChangeModifyProduct.getId()
                    + FollowUpConstants.PRODUCT_RELATIONSHIP_URI
                    + noChangeModifyProduct.getProductRelationship().indexOf(rootProductRelationship)
                    + FollowUpConstants.PRODUCT_ID;
            PatchDTO patchDTO = createReplacePatchRequest(path, rootProductIdentifier);
            patches.add(patchDTO);
        }
    }

    private String getRootProductId(List<Product> products) {
        return products.stream()
                .filter(product -> isMigrateContractType(product, products))
                .map(Product::getId)
                .findFirst()
                .orElse(null);
    }

    private boolean isMigrateContractType(Product product, List<Product> products) {
        if (!isContractType(product)) {
            return false;
        }

        if (product.getProductRelationship() == null) {
            return false;
        }

        return product.getProductRelationship().stream()
                .anyMatch(rel -> hasMigrationFromProduct(rel, products));
    }

    private boolean isContractType(Product product) {
        return product.getProductOffering() != null
                && ServiceConstants.CONTRACT_TYPE.equals(product.getProductOffering().getAtType());
    }

    private boolean hasMigrationFromProduct(ProductRelationship productRelationship, List<Product> products) {
        if (!ServiceConstants.MIGRATE_FROM.equals(productRelationship.getRelationshipType())) {
            return false;
        }

        if (!(productRelationship.getProduct() instanceof ProductRef productRef)) {
            return false;
        }

        return productExists(products, productRef.getId());
    }

    private void removeReliesOnRelationshipForMigratedProduct(List<Product> products, String productOrderId, List<PatchDTO> patches) {
        List<Product> noChangeOrModifyProducts = products
                .stream()
                .filter(getNoChangeOrModifyProductPredicate(productOrderId))
                .toList();

        noChangeOrModifyProducts.forEach(noChangeOrModifyProduct -> {
            List<ProductRelationship> productRelationships = getProductRelationshipsByType(noChangeOrModifyProduct, ServiceConstants.RELIES_ON, ServiceConstants.RELIES_FROM);
            removeReliesOnReliesFromForNoChangeModifyItem(productRelationships, noChangeOrModifyProduct, products, productOrderId, patches);
        });
    }

    private void removeReliesOnReliesFromForNoChangeModifyItem(List<ProductRelationship> productRelationships, Product noChangeOrModifyProduct, List<Product> products, String productOrderId, List<PatchDTO> patches) {
        productRelationships.forEach(relationship -> {
            if (relationship.getProduct() instanceof ProductRef product) {
                Optional<Product> optionalRelatedProduct = products.stream()
                        .filter(p -> product.getId().equals(p.getId()))
                        .filter(p -> isaMigrateFromItem(p, products, productOrderId) || isDeletedItem(p, productOrderId))
                        .findFirst();

                optionalRelatedProduct.ifPresent(relatedProduct -> removeReliesRelationships(noChangeOrModifyProduct, relationship, relatedProduct, patches));
            }
        });
    }

    private boolean isDeletedItem(Product product, String productOrderId) {
        return product.getProductOrderItem().stream()
                .anyMatch(getRelatedProductOrderItemPredicate(productOrderId, ServiceConstants.DELETE));
    }

    private boolean isaMigrateFromItem(Product product, List<Product> products, String productOrderId) {
        return isaMigrateItem(product, productOrderId)
                && (product.getProductRelationship().stream().noneMatch(productRelationship -> ServiceConstants.MIGRATE_FROM.equals(productRelationship.getRelationshipType()))
                || hasMissingMigratedFromProduct(product, products));
    }

    private void removeReliesRelationships(Product noChangeOrModifyProduct, ProductRelationship relationship, Product relatedProduct, List<PatchDTO> patches) {
        // remove the reliesOn/reliesFrom relationship
        addRemovePatchToPatchList(noChangeOrModifyProduct, noChangeOrModifyProduct.getProductRelationship().indexOf(relationship), patches);

        // remove the reliesOn/reliesFrom relationship from the related noChangeOrModifyProduct
        Optional<ProductRelationship> reliesProductRelationship = relatedProduct.getProductRelationship()
                .stream()
                .filter(productRelationship ->
                        ServiceConstants.RELIES_FROM.equals(productRelationship.getRelationshipType()) || ServiceConstants.RELIES_ON.equals(productRelationship.getRelationshipType()))
                .filter(productRelationship -> productRelationship.getProduct() instanceof ProductRef relatedFromProduct
                        && noChangeOrModifyProduct.getId().equals(relatedFromProduct.getId()))
                .findFirst();

        reliesProductRelationship.ifPresent(productRelationship -> addRemovePatchToPatchList(relatedProduct, relatedProduct.getProductRelationship().indexOf(productRelationship), patches));

    }

    private Predicate<Product> getNoChangeOrModifyProductPredicate(String productOrderId) {
        return product -> product.getProductOrderItem().stream()
                .anyMatch(relatedProductOrderItem -> productOrderId.equals(relatedProductOrderItem.getProductOrderId())
                        && (ServiceConstants.NO_CHANGE.equals(relatedProductOrderItem.getOrderItemAction()) || ServiceConstants.MODIFY.equals(relatedProductOrderItem.getOrderItemAction())));
    }

    private boolean isMigrationUseCase(List<Product> products, String productOrderId) {
        return products.stream()
                .filter(product -> ServiceConstants.CONTRACT_TYPE.equals(product.getProductOffering().getAtType()))
                .filter(product -> Objects.nonNull(product.getProductOrderItem()))
                .anyMatch(product -> isaMigrateItem(product, productOrderId));
    }

    private void processMigratedAtomicProducts(List<Product> products, String productOrderId, List<PatchDTO> patches) {
        List<Product> migratedProducts = getContractsAndBundlesToMigrate(products, productOrderId);

        migratedProducts.forEach(migratedProduct -> {
            List<ProductRelationship> bundlesProductRelationships = getProductRelationshipsByType(migratedProduct, ServiceConstants.BUNDLES);

            bundlesProductRelationships.forEach(productRelationship -> {
                if (productRelationship.getProduct() instanceof ProductRef productRef) {
                    Optional<Product> extractedProduct = products
                            .stream()
                            .filter(product -> productRef.getId().equals(product.getId()))
                            .findFirst();

                    if (extractedProduct.isPresent()) {
                        //remove the relationship bundles between the noChange or modify product  and the old bundled product
                        removeBundlesRelationship(extractedProduct.get(), migratedProduct, productOrderId, migratedProduct.getProductRelationship().indexOf(productRelationship), patches);
                        //replace migrated product status to Terminated in the current product
                        terminateMigratedProduct(extractedProduct.get(), productOrderId, patches);
                        //replace the reliesOnMigrate by reliesOn
                        replaceReliesOnMigrateToReliesOn(extractedProduct.get(), patches);
                    }
                }
            });
        });
    }

    private boolean isaContractOrBundleProduct(Product product) {
        return ServiceConstants.CONTRACT_TYPE.equals(product.getProductOffering().getAtType())
                || ServiceConstants.BUNDLE_PRODUCT_OFFERING.equals(product.getProductOffering().getAtType());
    }

    private boolean hasMissingMigratedFromProduct(Product product, List<Product> products) {
        ProductRelationship migrateFromRelationship =
                getFirstProductRelationship(product, ServiceConstants.MIGRATE_FROM);

        if (Objects.isNull(migrateFromRelationship)) {
            return false;
        }

        if (!(migrateFromRelationship.getProduct() instanceof ProductRef migratedProduct)) {
            return false;
        }

        return !productExists(products, migratedProduct.getId());
    }

    private boolean productExists(List<Product> products, String productId) {
        return products.stream()
                .anyMatch(product -> productId.equals(product.getId()));
    }

    private ProductRelationship getFirstProductRelationship(Product product, String relationshipType) {
        return product.getProductRelationship()
                .stream()
                .filter(productRelationship -> relationshipType.equals(productRelationship.getRelationshipType()))
                .findFirst()
                .orElse(null);
    }

    private void replaceReliesOnMigrateToReliesOn(Product product, List<PatchDTO> patches) {
        if (ServiceConstants.ATOMIC_PRODUCT_OFFERING.equals(product.getProductOffering().getAtType())) {
            List<ProductRelationship> relationshipsReliesOnMigrate = getProductRelationshipsByType(product, ServiceConstants.RELIES_ON_MIGRATE);

            relationshipsReliesOnMigrate.forEach(productRelationship -> {
                if (productRelationship.getProduct() instanceof ProductRef reliesOnProduct) {
                    String path = PRODUCT_INVENTORY_URI + product.getId() + FollowUpConstants.PRODUCT_RELATIONSHIP_URI + product.getProductRelationship().indexOf(productRelationship) + FollowUpConstants.RELATIONSHIP_TYPE_URI;
                    PatchDTO reliesOnPatch = createReplacePatchRequest(path, ServiceConstants.RELIES_ON);
                    PatchDTO reliesFromPatch = createProductRelationshipReliesFromPatch(product.getId(), reliesOnProduct.getId());
                    patches.add(reliesOnPatch);
                    patches.add(reliesFromPatch);
                }
            });
        }
    }

    private PatchDTO createProductRelationshipReliesFromPatch(String relatedProductId, String productId) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(FollowUpConstants.STRING_RELATIONSHIP_TYPE, ServiceConstants.RELIES_FROM);
        HashMap<String, Object> product = new HashMap<>();
        product.put(FollowUpConstants.ID, relatedProductId);
        product.put(FollowUpConstants.TYPE, PRODUCT_REF);
        values.put(FollowUpConstants.PRODUCT, product);
        return PatchDTO.builder()
                .op(PatchOperationType.ADD)
                .path(PRODUCT_INVENTORY_URI + productId + FollowUpConstants.PRODUCT_RELATIONSHIP_URI + "-")
                .value(values)
                .build();
    }

    private void terminateMigratedProduct(Product product, String productOrderId, List<PatchDTO> patches) {
        if (ServiceConstants.ATOMIC_PRODUCT_OFFERING.equals(product.getProductOffering().getAtType())
                && isaMigrateItem(product, productOrderId)) {
            log.debug("Terminating migrated atomic product: {} for productOrderId: {}", product.getId(), productOrderId);
            String productUri = PRODUCT_INVENTORY_URI + product.getId();
            PatchDTO patchStatus = createReplacePatchRequest(productUri + ServiceConstants.STATUS_URI, ProductStatusType.TERMINATED.getValue());
            PatchDTO patchOperationalStatus = createReplacePatchRequest(productUri + ServiceConstants.OPERATIONAL_STATUS_URI, ProductOperationalStatusType.TERMINATED.getValue());
            patches.add(patchStatus);
            patches.add(patchOperationalStatus);
        }
    }

    private void processTerminateHierarchy(List<Product> products, String productOrderId, List<PatchDTO> patches) {
        List<Product> migratedBundlesProducts = getContractsAndBundlesToMigrate(products, productOrderId);
        log.debug("Terminating {} migrated contract/bundle product(s) for productOrderId: {}", migratedBundlesProducts.size(), productOrderId);

        migratedBundlesProducts.forEach(product -> {
            log.trace("Setting product {} to TERMINATED status", product.getId());
            product.setStatus(ProductStatusType.TERMINATED);
            product.setOperationalStatus(ProductOperationalStatusType.TERMINATED);
        });

        List<PatchDTO> patchDTOList = migratedBundlesProducts.stream()
                .map(product -> buildProductPatchRequest(product.getId(), product.getStatus(), product.getOperationalStatus()))
                .flatMap(List::stream)
                .toList();
        patches.addAll(patchDTOList);
    }

    private List<Product> getContractsAndBundlesToMigrate(List<Product> products, String productOrderId) {
        return products
                .stream()
                .filter(this::isaContractOrBundleProduct)
                .filter(product -> isaMigrateFromItem(product, products, productOrderId) || isDeletedItem(product, productOrderId))
                .toList();
    }

    private boolean isaMigrateItem(Product product, String productOrderId) {
        return product.getProductOrderItem()
                .stream()
                .anyMatch(getRelatedProductOrderItemPredicate(productOrderId, ServiceConstants.MIGRATE));
    }

    private void removeBundlesRelationship(Product atomicProduct, Product migratedBundleProduct, String productOrderId, int productRelationshipIndex, List<PatchDTO> patches) {
        if (isaNoChangeOrModifyItem(atomicProduct, productOrderId)) {
            addRemovePatchToPatchList(migratedBundleProduct, productRelationshipIndex, patches);
        }
    }

    private void addRemovePatchToPatchList(Product product, int productRelationshipIndex, List<PatchDTO> patches) {
        String path = PRODUCT_INVENTORY_URI + product.getId() + FollowUpConstants.PRODUCT_RELATIONSHIP_URI + productRelationshipIndex;
        PatchDTO patch = createRemovePatchRequest(path);
        patches.add(patch);
    }

    private PatchDTO createRemovePatchRequest(String path) {
        return PatchDTO.builder()
                .op(PatchOperationType.REMOVE)
                .path(path)
                .value(StringUtils.EMPTY)
                .build();
    }

    private boolean isaNoChangeOrModifyItem(Product product, String productOrderId) {
        return product.getProductOrderItem()
                .stream()
                .anyMatch(relatedProductOrderItem -> productOrderId.equals(relatedProductOrderItem.getProductOrderId())
                        && (ServiceConstants.NO_CHANGE.equals(relatedProductOrderItem.getOrderItemAction()) || ServiceConstants.MODIFY.equals(relatedProductOrderItem.getOrderItemAction())));
    }

    private void processBundlesMigrateRelationship(List<Product> products, String productOrderId, List<PatchDTO> patches) {
        List<Product> migratedBundlesProduct = products
                .stream()
                .filter(this::isaContractOrBundleProduct)
                .filter(product -> product.getProductRelationship()
                        .stream()
                        .anyMatch(productRelationship -> ServiceConstants.MIGRATE_FROM.equals(productRelationship.getRelationshipType()))
                        || product.getProductOrderItem()
                        .stream()
                        .anyMatch(getRelatedProductOrderItemPredicate(productOrderId, ServiceConstants.ADD)))
                .toList();

        migratedBundlesProduct.forEach(product -> updateBundlesMigrateToBundles(product, patches));

    }

    private static Predicate<RelatedProductOrderItem> getRelatedProductOrderItemPredicate(String productOrderId, String itemAction) {
        return relatedProductOrderItem -> productOrderId.equals(relatedProductOrderItem.getProductOrderId())
                && itemAction.equals(relatedProductOrderItem.getOrderItemAction());
    }

    private void updateBundlesMigrateToBundles(Product product, List<PatchDTO> patches) {
        List<ProductRelationship> bundlesMigrateRelationship = getProductRelationshipsByType(product, ServiceConstants.BUNDLES_MIGRATE);
        bundlesMigrateRelationship.forEach(productRelationship -> {
            String path = PRODUCT_INVENTORY_URI + product.getId() + FollowUpConstants.PRODUCT_RELATIONSHIP_URI + product.getProductRelationship().indexOf(productRelationship) + FollowUpConstants.RELATIONSHIP_TYPE_URI;
            PatchDTO patch = createReplacePatchRequest(path, ServiceConstants.BUNDLES);
            patches.add(patch);
        });
    }

    private List<ProductRelationship> getProductRelationshipsByType(Product product, String... relationTypes) {
        return product.getProductRelationship()
                .stream()
                .filter(productRelationship -> Arrays.asList(relationTypes).contains(productRelationship.getRelationshipType()))
                .toList();
    }
}
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.event.om.Command.EventType;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.mapper.product.order.ProductOrderItemMapper;
import com.orange.discobole.ordermanagement.ordercapture.producer.ProductOrderCommandProducer;
import com.orange.discobole.ordermanagement.ordercapture.service.OrderInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.BillingAccountRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ResourceRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
import com.orange.discobole.productinventory.dto.v1.ProductRelationship;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.event.om.Command.EventType.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.*;
import static java.lang.String.format;

@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ProductOrderCommandProducer productOrderCommandProducer;
    private final OrderInventoryService orderInventoryService;
    private final ProductInventoryService productInventoryService;
    private final ProductOrderItemMapper productOrderItemMapper;

    public ProductOrderServiceImpl(ProductOrderCommandProducer productOrderCommandProducer, OrderInventoryService orderInventoryService, ProductInventoryService productInventoryService, ProductOrderItemMapper productOrderItemMapper) {
        this.productOrderCommandProducer = productOrderCommandProducer;
        this.orderInventoryService = orderInventoryService;
        this.productInventoryService = productInventoryService;
        this.productOrderItemMapper = productOrderItemMapper;
    }

    private static void updateProductOrderState(ProductOrder productOrder, ProductOrderStateType state) {
        productOrder.setState(state);
        productOrder
                .getProductOrderItem()
                .forEach(productOrderItem -> {
                    ProductOrderItemStateType itemState = mapOrderStateToItemState(state);
                    productOrderItem.setState(itemState);
                });
    }

    private static ProductOrderItemStateType mapOrderStateToItemState(ProductOrderStateType orderState) {
        return switch (orderState) {
            case DRAFT -> ProductOrderItemStateType.DRAFT;
            case ACKNOWLEDGED -> ProductOrderItemStateType.ACKNOWLEDGED;
            case ACCEPTED -> ProductOrderItemStateType.ACCEPTED;
            case INPROGRESS -> ProductOrderItemStateType.INPROGRESS;
            case HELD -> ProductOrderItemStateType.HELD;
            case PENDING -> ProductOrderItemStateType.PENDING;
            case PENDINGCANCELLATION -> ProductOrderItemStateType.PENDINGCANCELLATION;
            case ASSESSINGCANCELLATION -> ProductOrderItemStateType.ASSESSINGCANCELLATION;
            case CANCELLED -> ProductOrderItemStateType.CANCELLED;
            case REJECTED -> ProductOrderItemStateType.REJECTED;
            case COMPLETED -> ProductOrderItemStateType.COMPLETED;
            case FAILED -> ProductOrderItemStateType.FAILED;
            default -> throw new IllegalArgumentException("Unhandled ProductOrderStateType: " + orderState);
        };
    }

    private static boolean isShipmentProductItem(ProductOrderItem productOrderItem) {
        return productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                && product.getProductSpecification() != null
                && SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE.equals(product.getProductSpecification().getAtBaseType());
    }

    @Override
    public ProductOrder createProductOrder(ProductOrder productOrder) {
        log.debug("Create product order: {}", productOrder);
        return orderInventoryService.createProductOrder(productOrder);
    }

    @Override
    public void updateOrderItemsAndOrderTotalPrice(ProductOrder productOrder, List<ProductOrderItem> productOrderItems, List<OrderPrice> orderTotalPrices) {
        log.debug("Update order items and order total price: {}", productOrderItems);
        setProductOrderItemId(productOrderItems);

        ProductOrder result = ProductOrder.builder()
                .id(productOrder.getId())
                .productOrderItem(productOrderItems)
                .orderTotalPrice(orderTotalPrices)
                .build();

        updateProductOrderInventory(result, ORDER_ITEMS_AND_ORDER_TOTAL_PRICE_VALUE_CHANGE_COMMAND);
    }

    @Override
    public void updateProductOrderInventoryState(ProductOrder productOrder, ProductOrderStateType productOrderState) {
        log.debug("Update product order state: {}", productOrder);
        checkParameter(productOrder, productOrderState);
        updateProductOrderState(productOrder, productOrderState);
        productOrderCommandProducer.publishCommand(productOrder, PRODUCT_ORDER_STATE_CHANGE_COMMAND);
    }

    private void updateProductOrderInventory(ProductOrder productOrder, EventType eventType) {
        log.debug("Update product order: {}", productOrder);
        productOrderCommandProducer.publishCommand(productOrder, eventType);
    }

    @Override
    public void addResourceRef(ProductOrder productOrder, Map<String, List<ResourceRef>> productOrderItemResourcesMap) {
        ProductOrder result = ProductOrder.builder()
                .id(productOrder.getId())
                .build();

        List<ProductOrderItem> productOrderItemsWithResourceRef = new ArrayList<>();

        productOrder.getProductOrderItem()
                .forEach(productOrderItem -> {
                    if (productOrderItemResourcesMap.containsKey(productOrderItem.getId())) {
                        List<ResourceRef> realizingResource = productOrderItemResourcesMap.get(productOrderItem.getId());

                        if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
                            product.setRealizingResource(realizingResource);

                            ProductOrderItem modifiedItem = ProductOrderItem.builder()
                                    .id(productOrderItem.getId())
                                    .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                                            .realizingResource(realizingResource)
                                            .build())
                                    .build();
                            productOrderItemsWithResourceRef.add(modifiedItem);
                        } else {
                            log.warn("ProductOrderItem {} has an incompatible product type.", productOrderItem.getId());
                        }
                    }
                });

        result.setProductOrderItem(productOrderItemsWithResourceRef);
        updateProductOrderInventory(result, REALIZING_RESOURCE_VALUE_CHANGE_COMMAND);
    }

    @Override
    public void addPaymentRef(ProductOrder productOrder, Map<String, List<String>> orderItemPaymentRefList) {
        ProductOrder result = ProductOrder.builder()
                .id(productOrder.getId())
                .build();

        List<ProductOrderItem> productOrderItemsWithPaymentRef = new ArrayList<>();

        productOrder.getProductOrderItem().forEach(productOrderItem -> {
            if (orderItemPaymentRefList.containsKey(productOrderItem.getId())) {
                List<String> paymentRefIds = orderItemPaymentRefList.get(productOrderItem.getId());
                List<PaymentRef> paymentRefs = createPaymentRefs(paymentRefIds);
                productOrderItem.setPayment(paymentRefs);

                ProductOrderItem modifiedItem = ProductOrderItem.builder()
                        .id(productOrderItem.getId())
                        .payment(paymentRefs)
                        .build();
                productOrderItemsWithPaymentRef.add(modifiedItem);
            }
        });

        result.setProductOrderItem(productOrderItemsWithPaymentRef);

        updateProductOrderInventory(result, PAYMENT_VALUE_CHANGE_COMMAND);
    }

    @Override
    public void addBillingAccountRef(ProductOrder productOrder, Map<String, String> orderItemBillingAccountRef) {
        ProductOrder updatedProductOrder = ProductOrder.builder()
                .id(productOrder.getId())
                .build();

        List<ProductOrderItem> productOrderItemWithBillingAccountRefList = new ArrayList<>();

        productOrder.getProductOrderItem().forEach(productOrderItem -> {
            if (orderItemBillingAccountRef.containsKey(productOrderItem.getId())) {
                String billingAccountRefId = orderItemBillingAccountRef.get(productOrderItem.getId());
                BillingAccountRef billingAccountRef = createBillingAccountRef(billingAccountRefId);
                productOrderItem.setBillingAccount(billingAccountRef);

                ProductOrderItem modifiedItem = ProductOrderItem.builder()
                        .id(productOrderItem.getId())
                        .billingAccount(billingAccountRef)
                        .build();

                productOrderItemWithBillingAccountRefList.add(modifiedItem);
            }
        });

        updatedProductOrder.setProductOrderItem(productOrderItemWithBillingAccountRefList);
        updateProductOrderInventory(updatedProductOrder, BILLING_ACCOUNT_VALUE_CHANGE_COMMAND);
    }

    @Override
    public void addAppointmentRef(ProductOrder productOrder, Map<String, String> orderItemAppointmentRef) {
        ProductOrder updatedProductOrder = ProductOrder.builder()
                .id(productOrder.getId())
                .build();

        List<ProductOrderItem> productOrderItemWithAppointmentRefList = new ArrayList<>();
        productOrder.getProductOrderItem().forEach(productOrderItem -> {
            if (orderItemAppointmentRef.containsKey(productOrderItem.getId())) {
                String appointmentRefId = orderItemAppointmentRef.get(productOrderItem.getId());
                AppointmentRef appointmentRef = createAppointmentRef(appointmentRefId);
                productOrderItem.setAppointment(appointmentRef);

                ProductOrderItem modifiedItem = ProductOrderItem.builder()
                        .id(productOrderItem.getId())
                        .appointment(appointmentRef)
                        .build();

                productOrderItemWithAppointmentRefList.add(modifiedItem);
            }
        });
        updatedProductOrder.setProductOrderItem(productOrderItemWithAppointmentRefList);
        updateProductOrderInventory(updatedProductOrder, APPOINTMENT_VALUE_CHANGE_COMMAND);
    }

    private AppointmentRef createAppointmentRef(String appointmentRefId) {
        return AppointmentRef.builder()
                .id(appointmentRefId)
                .atType(APPOINTMENT_TYPE)
                .build();
    }

    @Override
    public void updateProduct(final ProductOrder productOrder) {
        final ProductOrder modifiedProductOrder = ProductOrder.builder()
                .id(productOrder.getId())
                .build();

        final List<ProductOrderItem> itemsToBeUpdated = productOrder.getProductOrderItem().stream()
                .filter(orderItem -> ItemActionType.ADD.equals(orderItem.getAction()) || isMigratedItem(orderItem))
                .map(orderItem -> ProductOrderItem.builder()
                        .id(orderItem.getId())
                        .product(orderItem.getProduct())
                        .build())
                .collect(Collectors.toList());

        if (!itemsToBeUpdated.isEmpty()) {
            modifiedProductOrder.setProductOrderItem(itemsToBeUpdated);
            updateProductOrderInventory(modifiedProductOrder, PRODUCT_VALUE_CHANGE_COMMAND);
        }
    }

    private boolean isMigratedItem(ProductOrderItem orderItem) {
        return ItemActionType.MIGRATE.equals(orderItem.getAction()) &&
                Objects.nonNull(orderItem.getProductOrderItemRelationship()) &&
                orderItem.getProductOrderItemRelationship()
                        .stream()
                        .anyMatch(orderItemRelationship ->
                                RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType())
                        );
    }

    private void setProductOrderItemId(List<ProductOrderItem> productOrderItems) {
        log.debug("Set product order item(s) id(s) {} ", productOrderItems);
        Map<String, String> productOrderItemIdMap = new HashMap<>();
        productOrderItems.forEach(productOrderItem -> {
            String itemId = String.valueOf(UUID.randomUUID());
            if (Objects.nonNull(productOrderItem.getId())) {
                productOrderItemIdMap.put(productOrderItem.getId(), itemId);
            }
            productOrderItem.setId(itemId);
            productOrderItem.setState(ProductOrderItemStateType.DRAFT);
        });
        setProductOrderItemRelationshipId(productOrderItems, productOrderItemIdMap);
    }

    @Override
    public void updateProductOrderRelatedParties(ProductOrder productOrder) {
        log.debug("Inside update order related party : {}", productOrder);
        ProductOrder updatedProductOrder = ProductOrder.builder()
                .id(productOrder.getId())
                .relatedParty(productOrder.getRelatedParty())
                .build();
        productOrderCommandProducer.publishCommand(updatedProductOrder, RELATED_PARTIES_VALUE_CHANGE_COMMAND);
    }

    @Override
    public List<String> getPhysicalProductOrderItems(Map<String, String> productOrderItemProductSpecIdMap, List<ProductSpecification> productSpecifications) {
        log.debug("Getting tangible resources id(s) for product order");
        if (CollectionUtils.isEmpty(productOrderItemProductSpecIdMap)) {
            return Collections.emptyList();
        }
        Map<String, ProductSpecification> productSpecMap = productSpecifications.stream()
                .collect(Collectors.toMap(ProductSpecification::getId, spec -> spec));
        return extractPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecMap);
    }

    private List<String> extractPhysicalProductOrderItems(Map<String, String> productOrderItemProductSpecIdMap, Map<String, ProductSpecification> productSpecMap) {
        List<String> prodOrderItemRelatedResIds = new ArrayList<>();
        for (Map.Entry<String, String> entry : productOrderItemProductSpecIdMap.entrySet()) {
            ProductSpecification productSpec = productSpecMap.get(entry.getValue());
            if (Objects.nonNull(productSpec)
                    && StringUtils.isNotBlank(productSpec.getSupportEntity())
                    && productSpec.getSupportEntity().equalsIgnoreCase(STOCK_ITEM_TYPE)) {
                prodOrderItemRelatedResIds.add(entry.getKey());
            }
        }
        return prodOrderItemRelatedResIds;
    }

    @Override
    public Map<String, List<String>> getProductOrderItemLogicalResourcesIds(Map<String, String> productOrderItemProdSpecMap, List<ProductSpecification> productSpecifications) {
        log.debug("Getting related resources id(s) for product order");
        if (productOrderItemProdSpecMap == null) {
            return Collections.emptyMap();
        }
        Map<String, ProductSpecification> productSpecMap = productSpecifications.stream()
                .collect(Collectors.toMap(ProductSpecification::getId, spec -> spec));
        return extractLogicalResourcesIds(productOrderItemProdSpecMap, productSpecMap);
    }


    private Map<String, List<String>> extractLogicalResourcesIds(Map<String, String> productOrderItemProdSpecMap, Map<String, ProductSpecification> productSpecMap) {
        Map<String, List<String>> prodOrderItemRelatedResIds = new HashMap<>();
        for (Map.Entry<String, String> entry : productOrderItemProdSpecMap.entrySet()) {
            ProductSpecification productSpec = productSpecMap.get(entry.getValue());
            if (Objects.nonNull(productSpec) && !CollectionUtils.isEmpty(productSpec.getProductSpecCharacteristic())) {
                List<String> relatedResourcesIdList = productSpec.getProductSpecCharacteristic()
                        .stream()
                        .map(com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecCharacteristic::getName)
                        .filter(Objects::nonNull)
                        .toList();
                if (!CollectionUtils.isEmpty(relatedResourcesIdList)) {
                    prodOrderItemRelatedResIds.put(entry.getKey(), relatedResourcesIdList);
                }
            }
        }
        return prodOrderItemRelatedResIds;
    }


    @Override
    public List<Product> createProducts(final ProductOrder productOrder,
                                        Map<String, String> physicalProductOrderItemSerialNumberMap,
                                        String requestedConfigurationAction, String contractProductId) {
        try {
            List<ProductOrderItem> allProductOrderItems = productOrder.getProductOrderItem();
            List<ProductOrderItem> installableProductOrderItems = productOrder.getProductOrderItem().stream()
                    .filter(productOrderItem -> Objects.nonNull(productOrderItem.getIsInstallable()) && productOrderItem.getIsInstallable())
                    .toList();

            productOrder.setProductOrderItem(installableProductOrderItems);

            List<Product> products = convertToProducts(productOrder, physicalProductOrderItemSerialNumberMap, requestedConfigurationAction);

            List<Product> createdProducts = products.stream()
                    .map(productInventoryService::createProducts)
                    .toList();
            List<Product> productList = createdProducts.stream()
                    .flatMap(productDTO -> getProductFromProductRelationship(productDTO, new ArrayList<>())
                            .stream())
                    .toList();

            // create patch for relies on relationship
            PatchDTOList productPatch = createPatchToAddRelationshipRelies(productOrder, productList);

            //create patch for modification and delete products
            productOrder.setProductOrderItem(allProductOrderItems);
            createPatchToAddModifyOrDeleteAction(productOrder, productPatch);

            //modify contract operational status
            updateContractOperationalStatus(contractProductId, requestedConfigurationAction, productPatch);

            //patch products
            if (!CollectionUtils.isEmpty(productPatch.getList())) {
                productInventoryService.updateProducts(productPatch.toJsonString());
            }

            return productList;
        } catch (JsonProcessingException e) {
            throw new DiscoException("Invalid patch for Relationship");
        }
    }

    @Override
    public void updateValidityCharacteristic(ProductOrder productOrder, Map<ProductOrderItem, ValidityCharacteristic> validityMap) {
        Map<String, ValidityCharacteristic> updatedValidityMap = validityMap.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getKey().getId()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId(),
                        Map.Entry::getValue
                ));

        ProductOrder result = ProductOrder.builder()
                .id(productOrder.getId())
                .build();

        List<ProductOrderItem> updatedItems = new ArrayList<>();
        for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
            if (updatedValidityMap.containsKey(productOrderItem.getId())) {
                ValidityCharacteristic updatedValidityCharacteristic = updatedValidityMap.get(productOrderItem.getId());
                updateProductCharacteristics(productOrderItem, updatedValidityCharacteristic);
                updatedItems.add(productOrderItem);
            }
        }

        result.setProductOrderItem(updatedItems);
        updateProductOrderInventory(result, PRODUCT_VALUE_CHANGE_COMMAND);
    }

    @Override
    public void updateRequestedCompletionDate(ProductOrder productOrder) {
        ProductOrder result = ProductOrder.builder()
                .id(productOrder.getId())
                .requestedCompletionDate(productOrder.getRequestedCompletionDate())
                .build();
        updateProductOrderInventory(result, REQUESTED_COMPLETION_DATE_VALUE_CHANGE_COMMAND);


    }

    private void updateProductCharacteristics(ProductOrderItem productOrderItem,
                                              ValidityCharacteristic validityCharacteristic) {
        if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            List<Characteristic> characteristics = new ArrayList<>(product.getProductCharacteristic());
            characteristics.removeIf(ValidityCharacteristic.class::isInstance);
            characteristics.add(validityCharacteristic);
            product.setProductCharacteristic(characteristics);
        }
    }

    private void updateContractOperationalStatus(String contractProductId, String requestedConfigurationAction, PatchDTOList productPatch) {
        if (Objects.nonNull(contractProductId)) {
            switch (requestedConfigurationAction) {
                case MODIFICATION ->
                        modifyContractOperationalStatus(contractProductId, productPatch, ProductOperationalStatusType.PENDINGMODIFICATION.getValue());
                case TERMINATION ->
                        modifyContractOperationalStatus(contractProductId, productPatch, ProductOperationalStatusType.PENDINGTERMINATE.getValue());
                case MIGRATE ->
                        modifyContractOperationalStatus(contractProductId, productPatch, ProductOperationalStatusType.PENDINGMIGRATE.getValue());
                default -> {
                    break;
                }
            }
        }
    }

    private List<ProductOrderItem> getShipmentProductOrderItem(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream().filter(ProductOrderServiceImpl::isShipmentProductItem).toList();
    }

    private List<Product> convertShipmentToProduct(ProductOrder productOrder, List<ProductOrderItem> productOrderItems) {
        if (productOrder.getProductOrderItem().isEmpty()) {
            return List.of();
        }
        return productOrderItems.stream()
                .map(productOrderItem -> createShipmentProduct(productOrderItem, productOrder))
                .toList();
    }

    private List<Product> getProductFromProductRelationship(Product productDTO, List<Product> products) {
        if (!CollectionUtils.isEmpty(productDTO.getProductRelationship())) {
            for (ProductRelationship productRelationshipDTO : productDTO.getProductRelationship()) {
                Object relatedProduct = productRelationshipDTO.getProduct();

                if (relatedProduct instanceof Product product) {
                    getProductFromProductRelationship(product, products);
                }
                if (relatedProduct instanceof PhysicalProduct physicalProduct) {
                    getProductFromProductRelationship(physicalProduct, products);
                }
            }
        }
        products.add(productDTO);
        return products;
    }

    private PatchDTOList createPatchToAddRelationshipRelies(ProductOrder productOrder, List<Product> products) {
        List<PatchDTO> patchList = new ArrayList<>();
        if (CollectionUtils.isEmpty(products)) {
            return PatchDTOList.builder()
                    .list(patchList)
                    .build();
        }

        productOrder.getProductOrderItem()
                .stream()
                .filter(productOrderItem -> ATOMIC_PRODUCT_OFFERING_TYPE.equals(productOrderItem.getProductOffering().getAtType()))
                .forEach(productOrderItem -> {
                    addOrderItemRelationshipToPatch(patchList, productOrderItem, products);
                    addProductRelationshipToPatch(patchList, productOrderItem, products);
                });
        return PatchDTOList.builder()
                .list(patchList)
                .build();
    }

    private void addProductRelationshipToPatch(List<PatchDTO> patchList, ProductOrderItem productOrderItem, List<Product> productDTOList) {
        if (notHavingMigratedToRel(productOrderItem)
                && !ItemActionType.DELETE.equals(productOrderItem.getAction())
                && productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                && Objects.nonNull(product.getProductRelationship()) && !product.getProductRelationship().isEmpty()) {

            product.getProductRelationship().forEach(productRelationship -> {
                if (Objects.nonNull(productRelationship.getRelationshipType()) &&
                        RELIES_ON.equals(productRelationship.getRelationshipType()) &&
                        productRelationship.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef productRef) {

                    Product currentProduct = getProductByProductOrderItemId(productOrderItem.getId(), productDTOList)
                            .orElseThrow(() -> new DiscoException(PRODUCT_NOT_FOUND + productOrderItem.getId()));

                    patchList.add(createProductRelationshipPatch(productRelationship.getRelationshipType(), productRef.getId(), currentProduct.getId()));
                    patchList.add(createProductRelationshipPatch(RELIES_FROM, currentProduct.getId(), productRef.getId()));
                }
            });
        }
    }

    private void addOrderItemRelationshipToPatch(List<PatchDTO> patchList, ProductOrderItem productOrderItem, List<Product> products) {
        if (CollectionUtils.isEmpty(productOrderItem.getProductOrderItemRelationship())) {
            return;
        }
        if (notHavingMigratedToRel(productOrderItem) && !ItemActionType.MODIFY.equals(productOrderItem.getAction()) && !ItemActionType.DELETE.equals(productOrderItem.getAction())) {
            productOrderItem.getProductOrderItemRelationship().stream()
                    .filter(orderItemRelationship -> Objects.nonNull(orderItemRelationship.getRelationshipType())
                            && RelationshipType.RELIESON.equals(orderItemRelationship.getRelationshipType()))
                    .forEach(orderItemRelationship -> {
                        Product currentProduct = getProductByProductOrderItemId(productOrderItem.getId(), products)
                                .orElseThrow(() -> new DiscoException(format(PRODUCT_NOT_FOUND, productOrderItem.getId())));
                        Product relatedProduct = getProductByProductOrderItemId(orderItemRelationship.getId(), products)
                                .orElseThrow(() -> new DiscoException(format(PRODUCT_NOT_FOUND, productOrderItem.getId())));
                        patchList.add(createProductRelationshipPatch(RELIES_ON, relatedProduct.getId(), currentProduct.getId()));
                        patchList.add(createProductRelationshipPatch(RELIES_FROM, currentProduct.getId(), relatedProduct.getId()));
                    });
        }
    }

    private boolean notHavingMigratedToRel(ProductOrderItem productOrderItem) {
        return Optional.ofNullable(productOrderItem.getProductOrderItemRelationship())
                .map(relationships -> relationships
                        .stream()
                        .noneMatch(orderItemRelationship -> RelationshipType.MIGRATETO.equals(orderItemRelationship.getRelationshipType())))
                .orElse(true);
    }

    private PatchDTO createProductRelationshipPatch(String relationshipType, String relatedProductId, String currentProductId) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(RELATIONSHIP_TYPE, relationshipType);
        HashMap<String, Object> product = new HashMap<>();
        product.put(ID, relatedProductId);
        product.put(TYPE, PRODUCT_REF);
        values.put(PRODUCT, product);
        return PatchDTO.builder()
                .op(PatchOperationType.ADD)
                .path(PRODUCT_INVENTORY_URI + currentProductId + PRODUCT_RELATIONSHIP_URI)
                .value(values)
                .build();
    }

    private Optional<Product> getProductByProductOrderItemId(String productOrderItemId, List<Product> products) {
        return products.stream()
                .filter(product -> product.getProductOrderItem().stream()
                        .anyMatch(relatedProductOrderItemDTO -> relatedProductOrderItemDTO.getOrderItemId().equals(productOrderItemId)))
                .findFirst();
    }

    private void createPatchToAddModifyOrDeleteAction(ProductOrder productOrder, PatchDTOList patchList) {
        productOrder.getProductOrderItem().stream()
                .filter(productOrderItem -> productOrderItem.getAction().equals(ItemActionType.MODIFY)
                        || productOrderItem.getAction().equals(ItemActionType.DELETE) || productOrderItem.getAction().equals(ItemActionType.NOCHANGE) || havingMigrateToRel(productOrderItem))
                .forEach(productOrderItem -> {
                    validateProductOrderItem(productOrderItem);
                    addOrderItemPatch(productOrder, productOrderItem, patchList);
                    addOrderPricePatch(patchList, productOrderItem);
                });
    }

    private boolean havingMigrateToRel(ProductOrderItem productOrderItem) {
        return !CollectionUtils.isEmpty(productOrderItem.getProductOrderItemRelationship()) && productOrderItem.getProductOrderItemRelationship().stream()
                .anyMatch(orderItemRelationship -> RelationshipType.MIGRATETO.equals(orderItemRelationship.getRelationshipType()));
    }

    private void addOrderPricePatch(PatchDTOList patchList, ProductOrderItem productOrderItem) {
        if ((productOrderItem.getAction().equals(ItemActionType.MODIFY) || productOrderItem.getAction().equals(ItemActionType.DELETE))
                && !CollectionUtils.isEmpty(productOrderItem.getItemPrice())
                && productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {

            Product mappedProduct = productOrderItemMapper.mapProductOrderItemToProduct(productOrderItem);
            mappedProduct.getProductPrice().forEach(productPrice ->
                    addPricePatch(product.getId(), productPrice, patchList)
            );
        }
    }

    private void validateProductOrderItem(ProductOrderItem productOrderItem) {
        if (!(productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) || product.getId() == null) {
            throw new DiscoException(PRODUCT_NOT_FOUND + productOrderItem.getId());
        }
    }

    private void addOrderItemPatch(ProductOrder productOrder, ProductOrderItem productOrderItem, PatchDTOList patchList) {
        if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            String path = format(URL_FORMAT, PRODUCT_INVENTORY_URI, product.getId(), PRODUCT_ORDER_ITEM_URI);

            Map<String, Object> values = Map.of(
                    PRODUCT_ORDER_ID, productOrder.getId(),
                    ORDER_ITEM_ID, productOrderItem.getId(),
                    ORDER_ITEM_ACTION, productOrderItem.getAction()
            );

            patchList.getList().add(createPatchDTO(path, values, PatchOperationType.ADD));
            log.info("Update order item with modify or delete action");
        }
    }

    private void addPricePatch(String productId, ProductPrice productPrice, PatchDTOList patchList) {
        String pathPrice = format(URL_FORMAT, PRODUCT_INVENTORY_URI, productId, PRODUCT_PRICE_URI);
        JsonNode jsonNode = OBJECT_MAPPER.valueToTree(productPrice);
        patchList.getList().add(createPatchDTO(pathPrice, jsonNode, PatchOperationType.ADD));
    }

    private void modifyContractOperationalStatus(String productId, PatchDTOList patchList, String operationalStatus) {
        String pathOperationalStatus = format(URL_FORMAT, PRODUCT_INVENTORY_URI, productId, OPERATIONAL_STATUS_URI);
        patchList.getList().add(createPatchDTO(pathOperationalStatus, operationalStatus, PatchOperationType.REPLACE));
    }

    private PatchDTO createPatchDTO(String path, Object values, PatchOperationType patchOperationType) {
        return PatchDTO.builder()
                .op(patchOperationType)
                .path(path)
                .value(values)
                .build();
    }

    private List<Product> convertToProducts(ProductOrder productOrder,
                                            Map<String, String> physicalProductOrderItemSerialNumberMap,
                                            String requestedConfigurationAction) {
        if (productOrder.getProductOrderItem().isEmpty()) {
            return List.of();
        }
        //Acquisition use case
        if (ADD.equals(requestedConfigurationAction)) {
            List<ProductOrderItem> parentProductOrderItems = getParentProductOrderItem(productOrder.getProductOrderItem());
            if (!CollectionUtils.isEmpty(parentProductOrderItems)) {
                return parentProductOrderItems.stream()
                        .map(productOrderItem -> createProductRecursive(productOrderItem, productOrder, physicalProductOrderItemSerialNumberMap))
                        .toList();
            }
        } else if (MIGRATE.equals(requestedConfigurationAction)) {
            List<ProductOrderItem> targetParent = getMigratedParentOrderItems(productOrder.getProductOrderItem());
            if (!CollectionUtils.isEmpty(targetParent)) {
                return targetParent.stream().map(productOrderItem -> createProductRecursive(productOrderItem, productOrder, physicalProductOrderItemSerialNumberMap))
                        .toList();
            }
        } else {
            List<ProductOrderItem> hasParentProductOrderItem = getHasParentProductOrderItem(productOrder.getProductOrderItem());
            if (!CollectionUtils.isEmpty(hasParentProductOrderItem)) {
                //create list of product
                List<Product> hasParentProducts = hasParentProductOrderItem.stream()
                        .map(productOrderItem -> {
                            Product productDTO = createProductRecursive(productOrderItem, productOrder, physicalProductOrderItemSerialNumberMap);
                            return addRelationshipHasParent(productDTO, productOrderItem);
                        })
                        .toList();

                List<Product> products = new ArrayList<>(hasParentProducts);
                List<ProductOrderItem> productOrderItemShipment = getShipmentProductOrderItem(productOrder.getProductOrderItem());
                if (!CollectionUtils.isEmpty(productOrderItemShipment)) {
                    List<Product> shipmentProducts = convertShipmentToProduct(productOrder, productOrderItemShipment);
                    products.addAll(shipmentProducts);
                }
                return products;
            }
        }
        return List.of();
    }

    private List<ProductOrderItem> getMigratedParentOrderItems(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(productOrderItem -> ItemActionType.MIGRATE.equals(productOrderItem.getAction()))
                .filter(this::isParentProductOrderItem)
                .filter(this::hasRelationshipTypeMigrateFrom)
                .toList();
    }

    private boolean hasRelationshipTypeMigrateFrom(ProductOrderItem productOrderItem) {
        return productOrderItem.getProductOrderItemRelationship().stream()
                .filter(Objects::nonNull)
                .anyMatch(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()));
    }

    private Product createProductRecursive(ProductOrderItem productOrderItem,
                                           ProductOrder productOrder,
                                           Map<String, String> physicalProductOrderItemSerialNumberMap) {
        log.debug("Inside create product {}", productOrderItem);
        Product productDTO = setProductProperties(productOrderItem, productOrder, physicalProductOrderItemSerialNumberMap);
        if (!isALeafProduct(productOrderItem)) {
            setProductRelationship(productDTO, productOrderItem, productOrder, physicalProductOrderItemSerialNumberMap);
        }
        return productDTO;
    }

    private void setProductRelationship(Product productDTO,
                                        ProductOrderItem productOrderItem,
                                        ProductOrder productOrder,
                                        Map<String, String> physicalProductOrderItemSerialNumberMap) {
        List<ProductOrderItem> productOrderItems = productOrder.getProductOrderItem();
        List<String> bundlesChildIds = getProductItemIdsByRelationType(productOrderItem);
        List<ProductOrderItem> bundlesChildProductItems = getProductItemById(bundlesChildIds, productOrderItems);
        List<ProductRelationship> productRelationshipDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productDTO.getProductRelationship())) {
            productRelationshipDTOS = new ArrayList<>(productDTO.getProductRelationship());
        }
        processChildRelationship(productDTO, productRelationshipDTOS, productOrder, physicalProductOrderItemSerialNumberMap, bundlesChildProductItems);
    }

    private void processChildRelationship(Product productDTO, List<ProductRelationship> productRelationshipDTOS, ProductOrder productOrder, Map<String, String> physicalProductOrderItemSerialNumberMap, List<ProductOrderItem> bundlesChildProductItems) {
        setChildRelationship(bundlesChildProductItems,
                productOrder,
                productRelationshipDTOS,
                physicalProductOrderItemSerialNumberMap);
        productDTO.setProductRelationship(productRelationshipDTOS);
    }

    private void setChildRelationship(List<ProductOrderItem> productOrderItems,
                                      ProductOrder productOrder,
                                      List<ProductRelationship> productRelationshipDTOS,
                                      Map<String, String> productOrderItemSerialNumberMap) {
        for (ProductOrderItem productOrderItem : productOrderItems) {
            ProductRelationship productRelationshipDTO = ProductRelationship
                    .builder()
                    .relationshipType(RelationshipType.BUNDLES.getValue())
                    .build();
            Product product = createProductRecursive(productOrderItem, productOrder, productOrderItemSerialNumberMap);
            productRelationshipDTO.setProduct(product);
            productRelationshipDTOS.add(productRelationshipDTO);
        }
    }

    private Product setProductProperties(ProductOrderItem productOrderItem,
                                         ProductOrder productOrder,
                                         Map<String, String> physicalProductOrderItemSerialNumberMap) {
        Product productDTO;
        if (physicalProductOrderItemSerialNumberMap.containsKey(productOrderItem.getId())) {
            productDTO = productOrderItemMapper.mapProductOrderItemToPhysicalProduct(productOrderItem);
            productDTO.setProductSerialNumber(physicalProductOrderItemSerialNumberMap.get(productOrderItem.getId()));
        } else if (isShipmentProductItem(productOrderItem)) {
            productDTO = productOrderItemMapper.mapProductOrderItemToShipmentProduct(productOrderItem);
        } else {
            productDTO = productOrderItemMapper.mapProductOrderItemToProduct(productOrderItem);
        }
        productDTO.setStatus(ProductStatusType.CREATED);
        productDTO.setOperationalStatus(ProductOperationalStatusType.CREATED);
        RelatedProductOrderItem relatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .orderItemId(productOrderItem.getId())
                .productOrderId(productOrder.getId())
                .orderItemAction(productOrderItem.getAction().getValue())
                .build();

        productDTO.setProductOrderItem(Collections.singletonList(relatedProductOrderItem));
        if (!CollectionUtils.isEmpty(productOrder.getRelatedParty())) {
            productDTO.setRelatedParty(List.copyOf(productOrderItemMapper.mapRelatedPartyRefsToRelatedParties(productOrder.getRelatedParty())));
        }
        return productDTO;
    }

    private Product createShipmentProduct(ProductOrderItem productOrderItem, ProductOrder productOrder) {
        Product productDTO;
        productDTO = productOrderItemMapper.mapProductOrderItemToShipmentProduct(productOrderItem);
        productDTO.setStatus(ProductStatusType.CREATED);
        productDTO.setOperationalStatus(ProductOperationalStatusType.CREATED);
        RelatedProductOrderItem relatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .orderItemId(productOrderItem.getId())
                .productOrderId(productOrder.getId())
                .orderItemAction(ItemActionType.ADD.getValue())
                .build();

        productDTO.setProductOrderItem(Collections.singletonList(relatedProductOrderItem));
        if (!CollectionUtils.isEmpty(productOrder.getRelatedParty())) {
            productDTO.setRelatedParty(List.copyOf(productOrderItemMapper.mapRelatedPartyRefsToRelatedParties(productOrder.getRelatedParty())));
        }
        return productDTO;
    }

    private List<String> getProductItemIdsByRelationType(ProductOrderItem productOrderItem) {
        return productOrderItem.getProductOrderItemRelationship().stream()
                .filter(Objects::nonNull)
                .filter(orderItemRelationshipDTO -> RelationshipType.BUNDLES.equals(orderItemRelationshipDTO.getRelationshipType()))
                .map(OrderItemRelationship::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<ProductOrderItem> getProductItemById(List<String> childIds, List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(productOrderItem -> childIds.contains(productOrderItem.getId()))
                .filter(productOrderItem -> ADD.equals(productOrderItem.getAction().getValue()) || ItemActionType.MIGRATE.equals(productOrderItem.getAction()))
                .toList();
    }

    private List<ProductOrderItem> getParentProductOrderItem(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(productOrderItem -> productOrderItem.getAction().equals(ItemActionType.ADD)
                        && productOrderItem.getProductOffering() != null
                        && productOrderItem.getProductOffering().getAtType() != null)
                .filter(this::isParentProductOrderItem)
                .toList();
    }

    private boolean isParentProductOrderItem(ProductOrderItem productOrderItem) {
        return Objects.isNull(productOrderItem.getProductOrderItemRelationship()) || productOrderItem.getProductOrderItemRelationship()
                .stream()
                .filter(Objects::nonNull)
                .noneMatch(orderItemRelationship -> RelationshipType.ISCHILD.equals(orderItemRelationship.getRelationshipType()));
    }

    private List<ProductOrderItem> getHasParentProductOrderItem(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(productOrderItem -> productOrderItem.getAction() == ItemActionType.ADD)
                .filter(productOrderItem -> productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                        && !CollectionUtils.isEmpty(product.getProductRelationship())
                        && product.getProductRelationship().stream()
                        .anyMatch(relationship -> relationship.getRelationshipType().equals(HAS_PARENT)))
                .toList();
    }

    private Product addRelationshipHasParent(Product product, ProductOrderItem productOrderItem) {
        if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product orderProduct) {
            List<ProductRelationship> relationships = orderProduct.getProductRelationship()
                    .stream()
                    .filter(relationship -> HAS_PARENT.equals(relationship.getRelationshipType()))
                    .map(relationship -> {
                        var productRef = (com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef) relationship.getProduct();
                        return ProductRelationship.builder()
                                .relationshipType(HAS_PARENT)
                                .product(ProductRef.builder()
                                        .id(productRef.getId())
                                        .href(productRef.getHref())
                                        .build())
                                .build();
                    })
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(product.getProductRelationship())) {
                product.setProductRelationship(relationships);
            } else {
                product.getProductRelationship().addAll(relationships);
            }
        }
        return product;
    }

    private void setProductOrderItemRelationshipId(List<ProductOrderItem> productOrderItems, Map<String, String> productOrderItemIdMap) {
        productOrderItems.forEach(productOrderItem -> {
            if (productOrderItem.getProductOrderItemRelationship() != null) {
                productOrderItem.getProductOrderItemRelationship()
                        .forEach(orderItemRelationshipDTO -> {
                            if (orderItemRelationshipDTO.getId() != null
                                    && productOrderItemIdMap.containsKey(orderItemRelationshipDTO.getId())) {
                                orderItemRelationshipDTO.setId(productOrderItemIdMap.get(orderItemRelationshipDTO.getId()));
                            }
                        });
            }
        });
    }

    private void checkParameter(ProductOrder productOrder, ProductOrderStateType productOrderState) {
        Assert.notNull(productOrder, ExceptionMessage.PRODUCT_ORDER_MAY_NOT_BE_NULL);
        Assert.notNull(productOrderState, ExceptionMessage.PRODUCT_ORDER_STATE_MAY_NOT_BE_NULL);
        Assert.notNull(productOrder.getId(), ExceptionMessage.PRODUCT_ORDER_ID_MAY_NOT_BE_NULL);
        Assert.notNull(productOrder.getProductOrderItem(), ExceptionMessage.PRODUCT_ORDER_ITEM_MAY_NOT_BE_NULL);
    }

    private List<PaymentRef> createPaymentRefs(List<String> paymentRefIds) {
        return paymentRefIds.stream()
                .map(paymentRefId -> PaymentRef.builder()
                        .id(paymentRefId)
                        .atType(PAYMENT_TYPE)
                        .build())
                .collect(Collectors.toList());
    }

    private BillingAccountRef createBillingAccountRef(String billingAccountRefId) {
        return BillingAccountRef.builder()
                .id(billingAccountRefId)
                .atType(BILLING_ACCOUNT_TYPE)
                .build();
    }

    private boolean isALeafProduct(ProductOrderItem racine) {
        if (Objects.isNull(racine.getProductOrderItemRelationship())) {
            return true;
        } else {
            return racine.getProductOrderItemRelationship().stream()
                    .noneMatch(orderItemRelationshipDTO -> RelationshipType.BUNDLES.equals(orderItemRelationshipDTO.getRelationshipType()));
        }
    }
}
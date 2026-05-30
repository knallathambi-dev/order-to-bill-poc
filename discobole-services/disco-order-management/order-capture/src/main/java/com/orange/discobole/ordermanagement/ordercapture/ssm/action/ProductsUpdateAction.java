// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecificationRelationship;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductSpecificationService;
import com.orange.discobole.ordermanagement.ordercapture.util.ProductOrderUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.ProductUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import com.orange.discobole.productinventory.dto.v1.Product;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Predicate;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.MIGRATE_FROM;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("updateProductsAction")
@Slf4j
public class ProductsUpdateAction implements StateMachineStateAction<String, String> {
    private final ProductInventoryService productInventoryService;
    private final ProductOrderService productOrderService;
    private final ProductSpecificationService productSpecificationService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductsUpdateAction(ProductInventoryService productInventoryService, ProductOrderService productOrderService, ProductSpecificationService productSpecificationService) {
        this.productInventoryService = productInventoryService;
        this.productOrderService = productOrderService;
        this.productSpecificationService = productSpecificationService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        try {
            log.info("Inside update products action");
            assert productOrder != null;
            String configurationAction = StateMachineUtil.getStringValue(context, OrderCaptureConstants.REQUESTED_CONFIGURATION_ACTION);
            List<Product> products = productInventoryService.getProductsByProductOrderId(productOrder.getId());
            processConfirmProducts(productOrder, products, configurationAction);
            processTerminateProducts(productOrder, products);
            processMigrateProducts(productOrder, products, configurationAction);
            setContextVariables(context, TRUE);
            return Mono.empty();
        } catch (Exception e) {
            return updateContextVariables(e, context, productOrder);
        }
    }

    private void processConfirmProducts(ProductOrder productOrder, List<Product> products, String configurationAction) {
        if (OrderCaptureConstants.ADD.equals(configurationAction) || OrderCaptureConstants.MODIFICATION.equals(configurationAction)) {
            List<String> addedOrderItemIds = ProductOrderUtil.getOrderItemsIdBy(productOrder, ItemActionType.ADD);
            confirmProducts(addedOrderItemIds, products);
        }

        if (OrderCaptureConstants.MIGRATE.equals(configurationAction)) {
            List<String> addedMigratedOrderItemIds = productOrder.getProductOrderItem()
                    .stream()
                    .filter(productOrderItem -> ItemActionType.ADD.equals(productOrderItem.getAction())
                            || isMigrateItem(productOrderItem))
                    .map(ProductOrderItem::getId)
                    .toList();

            confirmProducts(addedMigratedOrderItemIds, products);
        }
    }

    private void processTerminateProducts(ProductOrder productOrder, List<Product> products) {
        List<String> deletedOrderItemIds = ProductOrderUtil.getOrderItemsIdBy(productOrder, ItemActionType.DELETE);
        Predicate<Product> deleteFilter = productDTO -> ProductUtil.atLeastOneOrderItemExistInProduct(productDTO, deletedOrderItemIds);
        List<Product> terminatedProductsIds = products.stream().filter(deleteFilter).toList();
        if (!CollectionUtils.isEmpty(terminatedProductsIds)) {
            productInventoryService.terminateProducts(terminatedProductsIds, products, productOrder.getId());
        }
    }

    private void processMigrateProducts(ProductOrder productOrder, List<Product> products, String configurationAction) throws JsonProcessingException {
        if (OrderCaptureConstants.MIGRATE.equals(configurationAction)) {
            List<ProductOrderItem> noChangeModifyItems = productOrder.getProductOrderItem()
                    .stream()
                    .filter(productOrderItem -> ItemActionType.MODIFY.equals(productOrderItem.getAction())
                            || ItemActionType.NOCHANGE.equals(productOrderItem.getAction()))
                    .toList();

            if (!CollectionUtils.isEmpty(noChangeModifyItems)) {
                List<PatchDTO> patchList = new ArrayList<>();
                processBundlesMigrationRelationship(noChangeModifyItems, productOrder, products, patchList);
                processReliesOnMigrateRelationship(noChangeModifyItems, products, patchList, productOrder.getId());

                if (!CollectionUtils.isEmpty(patchList)) {
                    PatchDTOList productPatch = PatchDTOList
                            .builder()
                            .list(patchList)
                            .build();
                    productInventoryService.updateProducts(productPatch.toJsonString());
                }
            }
        }
    }

    private Optional<ProductOrderItem> getBundleMigrateItem(ProductOrder productOrder, List<String> parentRelationshipIds) {
        return productOrder.getProductOrderItem()
                .stream()
                .filter(productOrderItem -> parentRelationshipIds.contains(productOrderItem.getId()) &&
                        (hasMigrateFromRelationship(productOrderItem) || ItemActionType.ADD.equals(productOrderItem.getAction())))
                .findFirst();
    }

    private boolean hasMigrateFromRelationship(ProductOrderItem productOrderItem) {
        List<OrderItemRelationship> relationships = productOrderItem.getProductOrderItemRelationship();

        if (CollectionUtils.isEmpty(relationships)) {
            return false;
        }

        return relationships.stream()
                .anyMatch(orderItemRelationship ->
                        RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()));
    }

    private boolean isMigrateItem(ProductOrderItem productOrderItem) {
        return ItemActionType.MIGRATE.equals(productOrderItem.getAction())
                && productOrderItem.getProductOrderItemRelationship()
                .stream()
                .anyMatch(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()));
    }

    private void confirmProducts(List<String> filteredOrderItemIds, List<Product> products) {
        Predicate<Product> addFilter = productDTO -> ProductUtil.atLeastOneOrderItemExistInProduct(productDTO, filteredOrderItemIds);
        List<String> productsIds = products.stream().filter(addFilter).map(Product::getId).toList();
        if (!CollectionUtils.isEmpty(productsIds)) {
            productInventoryService.confirmProducts(productsIds);
        }
    }

    private void processReliesOnMigrateRelationship(List<ProductOrderItem> noChangeModifyItems, List<Product> products, List<PatchDTO> patchList, String productOrderId) {
        List<String> productSpecIds = extractProductSpecificationIds(noChangeModifyItems);
        if (!CollectionUtils.isEmpty(productSpecIds)) {
            List<Product> atomicMigrateProducts = getAtomicMigrateProducts(products, productOrderId);
            if (!CollectionUtils.isEmpty(atomicMigrateProducts)) {
                List<ProductSpecification> productSpecifications = productSpecificationService.fetchProductSpecifications(productSpecIds);
                productSpecifications.forEach(productSpecification -> processProductSpecification(productSpecification, atomicMigrateProducts, noChangeModifyItems, patchList));
            }
        }
    }

    private List<Product> getAtomicMigrateProducts(List<Product> products, String productOrderId) {
        return products.stream()
                .filter(product -> ATOMIC_PRODUCT_OFFERING_TYPE.equals(product.getProductOffering().getAtType()))
                .filter(product -> product.getProductOrderItem()
                        .stream()
                        .anyMatch(relatedProductOrderItem -> productOrderId.equals(relatedProductOrderItem.getProductOrderId())
                                && OrderCaptureConstants.MIGRATE.equals(relatedProductOrderItem.getOrderItemAction())))
                .filter(product -> hasMigratedFromProduct(product, products))
                .toList();
    }

    private boolean hasMigratedFromProduct(Product product, List<Product> products) {
        com.orange.discobole.productinventory.dto.v1.ProductRelationship migrateFromRelationship = getFirstMigrateFromProductRelationship(product);

        if (Objects.isNull(migrateFromRelationship)) {
            return false;
        }

        if (!(migrateFromRelationship.getProduct() instanceof com.orange.discobole.productinventory.dto.v1.ProductRef migratedProduct)) {
            return false;
        }

        return productExists(products, migratedProduct.getId());
    }

    private com.orange.discobole.productinventory.dto.v1.ProductRelationship getFirstMigrateFromProductRelationship(Product product) {
        if (CollectionUtils.isEmpty(product.getProductRelationship())) {
            return null;
        }
        return product.getProductRelationship()
                .stream()
                .filter(productRelationship -> MIGRATE_FROM.equals(productRelationship.getRelationshipType()))
                .findFirst()
                .orElse(null);

    }

    private boolean productExists(List<Product> products, String productId) {
        return products.stream()
                .anyMatch(product -> productId.equals(product.getId()));
    }

    private void processBundlesMigrationRelationship(List<ProductOrderItem> noChangeModifyItems, ProductOrder productOrder, List<Product> products, List<PatchDTO> patchList) {
        noChangeModifyItems.forEach(productOrderItem -> createBundlesMigrateRelationship(productOrderItem, productOrder, products, patchList));
    }

    private void processProductSpecification(ProductSpecification productSpecification, List<Product> atomicMigrateProducts, List<ProductOrderItem> noChangeModifyItems, List<PatchDTO> patchList) {
        if (!CollectionUtils.isEmpty(productSpecification.getProductSpecificationRelationship())) {
            List<ProductSpecificationRelationship> reliesOnRelationships = productSpecification.getProductSpecificationRelationship()
                    .stream()
                    .filter(rel -> StringUtils.isNotBlank(rel.getRelationshipType()) && OrderCaptureConstants.RELIES_ON.equals(rel.getRelationshipType()))
                    .toList();
            for (ProductSpecificationRelationship reliesOnRelationship : reliesOnRelationships) {
                List<ProductOrderItem> matchingProductOrderItems = findMatchingItem(productSpecification, noChangeModifyItems);
                if (!CollectionUtils.isEmpty(matchingProductOrderItems)) {
                    matchingProductOrderItems.forEach(matchingProductOrderItem -> addRelationshipsToMatchingItem(reliesOnRelationship, atomicMigrateProducts, matchingProductOrderItem, patchList));
                }
            }
        }
    }

    private void addRelationshipsToMatchingItem(ProductSpecificationRelationship reliesOnRelationship, List<Product> atomicMigrateProducts, ProductOrderItem reliesOnItem, List<PatchDTO> patchList) {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product = (com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product) reliesOnItem.getProduct();
        Optional<Product> optionalMatchingProduct = atomicMigrateProducts.stream()
                .filter(p -> p.getProductSpecification().getId().equals(reliesOnRelationship.getId()))
                .findFirst();
        optionalMatchingProduct.ifPresent(matchinhProduct -> patchList.add(createProductRelationshipPatch(OrderCaptureConstants.RELIES_ON_MIGRATE, matchinhProduct.getId(), product.getId())));
    }

    private List<ProductOrderItem> findMatchingItem(ProductSpecification productSpecification, List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(item -> item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                        && Objects.nonNull(product.getProductSpecification())
                        && productSpecification.getId().equals(product.getProductSpecification().getId()))
                .toList();
    }

    private List<String> extractProductSpecificationIds(List<ProductOrderItem> filteredItems) {
        return filteredItems.stream()
                .map(ProductOrderItem::getProduct)
                .filter(Objects::nonNull)
                .filter(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.class::isInstance)
                .map(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.class::cast)
                .map(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product::getProductSpecification)
                .filter(Objects::nonNull)
                .map(ProductSpecificationRef::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void createBundlesMigrateRelationship(ProductOrderItem
                                                          productOrderItem, ProductOrder productOrder, List<Product> products, List<PatchDTO> patchList) {
        List<String> parentRelationshipIds = productOrderItem.getProductOrderItemRelationship()
                .stream()
                .filter(orderItemRelationship -> RelationshipType.ISCHILD.equals(orderItemRelationship.getRelationshipType()))
                .map(OrderItemRelationship::getId)
                .toList();

        Optional<ProductOrderItem> bundleMigrateItem = getBundleMigrateItem(productOrder, parentRelationshipIds);

        if (bundleMigrateItem.isPresent()) {
            Predicate<Product> migrateFromFilter = productDTo -> ProductUtil.atLeastOneOrderItemExistInProduct(productDTo, List.of(bundleMigrateItem.get().getId()));
            Optional<Product> parentProduct = products.stream().filter(migrateFromFilter).findFirst();

            Predicate<Product> migrateToFilter = productDTo -> ProductUtil.atLeastOneOrderItemExistInProduct(productDTo, List.of(productOrderItem.getId()));
            Optional<Product> childProduct = products.stream().filter(migrateToFilter).findFirst();

            if (parentProduct.isPresent()
                    && childProduct.isPresent()) {
                patchList.add(createProductRelationshipPatch(OrderCaptureConstants.BUNDLES_MIGRATE, childProduct.get().getId(), parentProduct.get().getId()));
            }
        }
    }

    private PatchDTO createProductRelationshipPatch(String relationshipType, String relatedProductId, String
            currentProductId) {
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

    private Mono<Void> updateContextVariables(Exception e, StateContext<String, String> context, ProductOrder productOrder) {
        log.error("Unable to update products [{}]:", e.getMessage(), e);
        setContextVariables(context, FALSE);
        context.getExtendedState().getVariables().put(StateMachineUtil.DESCRIPTION, DescriptionConstants.PRODUCT_UPDATE_ERROR);
        productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.HELD);
        return Mono.empty();
    }

    private void setContextVariables(StateContext<String, String> context, boolean areProductsUpdated) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.ARE_PRODUCTS_UPDATED, areProductsUpdated);
    }
}
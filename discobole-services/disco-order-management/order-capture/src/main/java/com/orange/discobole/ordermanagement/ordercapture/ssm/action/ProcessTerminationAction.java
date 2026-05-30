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
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.service.ResourceInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.RESOURCES_CANCELLATION_ERROR;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("terminateProcessAction")
@Slf4j
public class ProcessTerminationAction implements StateMachineStateAction<String, String> {
    private final ProductOrderService productOrderService;
    private final ProductInventoryService productInventoryService;
    private final ResourceInventoryService resourceInventoryService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProcessTerminationAction(ProductOrderService productOrderService, ProductInventoryService productInventoryService, ResourceInventoryService resourceInventoryService) {
        this.productOrderService = productOrderService;
        this.productInventoryService = productInventoryService;
        this.resourceInventoryService = resourceInventoryService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        log.info("Inside terminate process action");
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        ProductOrder productOrder = StateMachineUtil.getObjectValue(context, CREATED_PRODUCT_ORDER, ProductOrder.class);
        String requestedAction = StateMachineUtil.getStringValue(context, REQUESTED_CONFIGURATION_ACTION);

        try {
            if (Objects.nonNull(productOrder)) {
                List<String> reservedResourcesIdList = getProductOrderReservedResourcesIds(productOrder);
                if (!reservedResourcesIdList.isEmpty()) {
                    resourceInventoryService.rollBackReservedResource(reservedResourcesIdList);
                }
                productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.CANCELLED);
                boolean areProductCreated = StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_CREATED, FALSE);
                if (TRUE.equals(areProductCreated)) {
                    List<com.orange.discobole.productinventory.dto.v1.Product> products = productInventoryService.getProductsByProductOrderId(productOrder.getId());
                    if (!CollectionUtils.isEmpty(products)) {
                        List<String> cancellableProductIds = extractCancellableProductIds(products);
                        if (!CollectionUtils.isEmpty(cancellableProductIds)) {
                            productInventoryService.cancelProducts(cancellableProductIds);
                            revertRelationships(requestedAction, productOrder, products);
                        }
                    }
                    revertContractProductOperationStatus(context);
                }
            }
            context.getStateMachine().getTransitions().clear();
            return context.getStateMachine().stopReactively();
        } catch (DiscoException discoException) {
            setContextVariables(context, discoException);
            setProductOrderStatusToHeld(productOrder, context);
        } catch (Exception e) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.INTERNAL_SERVER_ERROR);
            setProductOrderStatusToHeld(productOrder, context);
            log.error("Unable to terminate process [{}]:", e.getMessage(), e);
        }
        return null;
    }

    public void revertRelationships(String requestedConfigurationAction, ProductOrder productOrder, List<com.orange.discobole.productinventory.dto.v1.Product> products) {
        List<PatchDTO> patches = new ArrayList<>();

        //remove reliesOn reliesFrom relationships
        revertReliesOnRelationships(requestedConfigurationAction, products, productOrder.getId(), patches);


        //remove new bundles relationships
        revertBundlesRelationships(requestedConfigurationAction, productOrder, patches);

        if (!CollectionUtils.isEmpty(patches)) {
            PatchDTOList patchDTOList = PatchDTOList.builder().build();
            patchDTOList.setList(patches);
            try {
                productInventoryService.updateProducts(patchDTOList.toJsonString());
            } catch (JsonProcessingException e) {
                throw new DiscoException(UNABLE_TO_UPDATE_PRODUCTS);
            }
        }
    }

    private void revertReliesOnRelationships(String requestedConfigurationAction, List<com.orange.discobole.productinventory.dto.v1.Product> products, String productOrderId, List<PatchDTO> patches) {
        if (MIGRATE.equals(requestedConfigurationAction) || MODIFICATION.equals(requestedConfigurationAction)) {
            List<com.orange.discobole.productinventory.dto.v1.Product> addedMigratedProducts = getAddedMigratedProducts(products, productOrderId);
            addedMigratedProducts.forEach(product -> removeReliesOnReliesFromRelationships(product, products, patches));
        }
    }

    private List<com.orange.discobole.productinventory.dto.v1.Product> getAddedMigratedProducts(List<com.orange.discobole.productinventory.dto.v1.Product> products, String productOrderId) {
        return products
                .stream()
                .filter(product -> ATOMIC_PRODUCT_OFFERING_TYPE.equals(product.getProductOffering().getAtType()))
                .filter(product -> !com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE.equals(product.getStatus()))
                .filter(product -> product.getProductOrderItem().stream().anyMatch(relatedProductOrderItem ->
                        productOrderId.equals(relatedProductOrderItem.getProductOrderId()) && (ADD.equals(relatedProductOrderItem.getOrderItemAction()) ||
                                isMigrateFromItem(relatedProductOrderItem, product)))
                )
                .toList();
    }

    private boolean isMigrateFromItem(RelatedProductOrderItem relatedProductOrderItem, com.orange.discobole.productinventory.dto.v1.Product product) {
        return MIGRATE.equals(relatedProductOrderItem.getOrderItemAction())
                && product.getProductRelationship()
                .stream()
                .anyMatch(productRelationship -> MIGRATE_FROM.equals(productRelationship.getRelationshipType()));
    }

    private void removeReliesOnReliesFromRelationships(com.orange.discobole.productinventory.dto.v1.Product product, List<com.orange.discobole.productinventory.dto.v1.Product> products, List<PatchDTO> patches) {
        List<com.orange.discobole.productinventory.dto.v1.ProductRelationship> productRelationships = getReliesOnReliesFromRelationships(product);
        if (!CollectionUtils.isEmpty(productRelationships)) {
            productRelationships.forEach(relationship -> createPatchForReliesRelationships(relationship, product, products, patches)
            );
        }
    }

    private void createPatchForReliesRelationships(com.orange.discobole.productinventory.dto.v1.ProductRelationship reliesProductRelationship, com.orange.discobole.productinventory.dto.v1.Product product, List<com.orange.discobole.productinventory.dto.v1.Product> products, List<PatchDTO> patches) {
        //patch for reliesOn/reliesFrom
        addRemovePatchToPatchList(product, product.getProductRelationship().indexOf(reliesProductRelationship), patches);

        //patch for the reliesFrom
        removeReliesFromRelationship(product, reliesProductRelationship, products, patches);
    }

    private void removeReliesFromRelationship(com.orange.discobole.productinventory.dto.v1.Product product, com.orange.discobole.productinventory.dto.v1.ProductRelationship reliesProductRelationship, List<com.orange.discobole.productinventory.dto.v1.Product> products, List<PatchDTO> patches) {
        if (OrderCaptureConstants.RELIES_ON.equals(reliesProductRelationship.getRelationshipType())
                && reliesProductRelationship.getProduct() instanceof com.orange.discobole.productinventory.dto.v1.ProductRef productRef
        ) {
            if (isProductIdExist(products, productRef.getId())) {
                Optional<com.orange.discobole.productinventory.dto.v1.Product> optionalReliesFromProduct = getActiveProductById(products, productRef.getId());
                if (optionalReliesFromProduct.isPresent()) {
                    Optional<com.orange.discobole.productinventory.dto.v1.ProductRelationship> optionalProductRelationship = getReliesFromProduct(optionalReliesFromProduct.get(), product.getId());
                    optionalProductRelationship.ifPresent(productRelationship -> addRemovePatchToPatchList(optionalReliesFromProduct.get(), optionalReliesFromProduct.get().getProductRelationship().indexOf(productRelationship), patches));
                }
            } else {
                com.orange.discobole.productinventory.dto.v1.Product reliesFromProduct = productInventoryService.getProductById(productRef.getId());
                Optional<com.orange.discobole.productinventory.dto.v1.ProductRelationship> optionalProductRelationship = getReliesFromProduct(reliesFromProduct, product.getId());
                optionalProductRelationship.ifPresent(productRelationship -> addRemovePatchToPatchList(reliesFromProduct, reliesFromProduct.getProductRelationship().indexOf(productRelationship), patches));
            }
        }
    }

    private Optional<com.orange.discobole.productinventory.dto.v1.Product> getActiveProductById(List<com.orange.discobole.productinventory.dto.v1.Product> products, String productId) {
        return products
                .stream()
                .filter(product -> productId.equals(product.getId()))
                .filter(product -> com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE.equals(product.getStatus())
                        || com.orange.discobole.productinventory.dto.v1.ProductStatusType.SOLD.equals(product.getStatus()))
                .findFirst();
    }

    private Optional<com.orange.discobole.productinventory.dto.v1.ProductRelationship> getReliesFromProduct(com.orange.discobole.productinventory.dto.v1.Product reliesFromProduct, String productId) {
        return reliesFromProduct.getProductRelationship()
                .stream()
                .filter(productRelationship -> RELIES_FROM.equals(productRelationship.getRelationshipType()))
                .filter(productRelationship -> productRelationship.getProduct() instanceof com.orange.discobole.productinventory.dto.v1.ProductRef productRef
                        && productId.equals(productRef.getId()))
                .findFirst();
    }

    private boolean isProductIdExist(List<com.orange.discobole.productinventory.dto.v1.Product> products, String productId) {
        return products
                .stream()
                .anyMatch(product -> productId.equals(product.getId()));
    }

    private List<com.orange.discobole.productinventory.dto.v1.ProductRelationship> getReliesOnReliesFromRelationships(com.orange.discobole.productinventory.dto.v1.Product product) {
        List<String> reliesOnReliesFromRelationships = List.of(RELIES_ON, RELIES_FROM);
        return product.getProductRelationship()
                .stream()
                .filter(productRelationship -> reliesOnReliesFromRelationships.contains(productRelationship.getRelationshipType()))
                .toList();
    }

    private void addRemovePatchToPatchList(com.orange.discobole.productinventory.dto.v1.Product product, int productRelationshipIndex, List<PatchDTO> patches) {
        String path = PRODUCT_INVENTORY_URI + product.getId() + PRODUCT_RELATIONSHIP_DELETE_URI + productRelationshipIndex;
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

    private void revertBundlesRelationships(String requestedConfigurationAction, ProductOrder productOrder, List<PatchDTO> patches) {
        if (MODIFICATION.equals(requestedConfigurationAction)) {
            removeBundlesRelationships(productOrder, patches);
        }
    }

    private void removeBundlesRelationships(ProductOrder productOrder, List<PatchDTO> patches) {
        List<ProductOrderItem> productOrderItems = productOrder.getProductOrderItem()
                .stream()
                .filter(this::isHavingHasParentRelationship)
                .toList();

        Set<String> bundleIds = new HashSet<>();
        Set<String> childIds = new HashSet<>();
        extractBundleChildRelationships(productOrderItems, bundleIds, childIds);

        List<com.orange.discobole.productinventory.dto.v1.Product> bundledProducts = productInventoryService.getProductByIds(List.copyOf(bundleIds));

        Map<com.orange.discobole.productinventory.dto.v1.Product, List<com.orange.discobole.productinventory.dto.v1.ProductRelationship>> bundleToChildRelationships =
                createBundleToChildMapping(bundledProducts, childIds);


        bundleToChildRelationships.forEach((bundleProduct, childRelationships) ->
                childRelationships.forEach(relationship ->
                        addRemovePatchToPatchList(bundleProduct,
                                bundleProduct.getProductRelationship().indexOf(relationship), patches)
                )
        );
    }

    private void extractBundleChildRelationships(List<ProductOrderItem> productOrderItems, Set<String> bundleIds, Set<String> childIds) {
        for (ProductOrderItem item : productOrderItems) {
            if (item.getProduct() instanceof Product product) {
                extractHasParentRelationship(product)
                        .ifPresent(relationship -> {
                            if (relationship.getProduct() instanceof ProductRef productRef) {
                                String bundleId = productRef.getId();
                                bundleIds.add(bundleId);
                                childIds.add(product.getId());
                            }
                        });
            }
        }
    }

    private Map<com.orange.discobole.productinventory.dto.v1.Product, List<com.orange.discobole.productinventory.dto.v1.ProductRelationship>> createBundleToChildMapping(List<com.orange.discobole.productinventory.dto.v1.Product> bundledProducts, Set<String> childIds) {
        return bundledProducts.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        bundledProduct -> bundledProduct.getProductRelationship()
                                .stream()
                                .filter(relationship -> BUNDLES.equals(relationship.getRelationshipType()))
                                .filter(relationship -> isProductExistInChildSet(relationship, childIds))
                                .toList()
                ));
    }

    private boolean isProductExistInChildSet(com.orange.discobole.productinventory.dto.v1.ProductRelationship
                                                     relationship, Set<String> childIds) {
        if (relationship.getProduct() instanceof com.orange.discobole.productinventory.dto.v1.ProductRef productRef) {
            return childIds.contains(productRef.getId());
        }
        return false;
    }

    private Optional<ProductRelationship> extractHasParentRelationship(Product product) {
        return product.getProductRelationship()
                .stream()
                .filter(productRelationship -> HAS_PARENT.equals(productRelationship.getRelationshipType()))
                .findFirst();
    }

    private boolean isHavingHasParentRelationship(ProductOrderItem productOrderItem) {
        if (productOrderItem.getProduct() instanceof Product product
                && !CollectionUtils.isEmpty(product.getProductRelationship())) {
            return product.getProductRelationship()
                    .stream().anyMatch(productRelationship -> HAS_PARENT.equals(productRelationship.getRelationshipType()));
        }
        return false;
    }

    private List<String> extractCancellableProductIds(List<com.orange.discobole.productinventory.dto.v1.Product> products) {
        return products.stream()
                .filter(this::isNeitherActiveNorSoldProduct)
                .map(com.orange.discobole.productinventory.dto.v1.Product::getId)
                .toList();
    }

    private void revertContractProductOperationStatus(StateContext<String, String> context) {
        String contractProductId = StateMachineUtil.getStringValue(context, CONTRACT_PRODUCT_ID);
        if (contractProductId != null) {
            activateContractProduct(contractProductId);
        }
    }

    private void activateContractProduct(String contractProductId) {
        PatchDTO patch = PatchDTO.builder()
                .op(PatchOperationType.REPLACE)
                .path(PRODUCT_INVENTORY_URI + contractProductId + OPERATIONAL_STATUS_URI)
                .value(ProductOperationalStatusType.ACTIVE.getValue())
                .build();
        PatchDTOList productPatch = PatchDTOList.builder()
                .list(List.of(patch))
                .build();
        try {
            productInventoryService.updateProducts(productPatch.toJsonString());
        } catch (Exception e) {
            throw new DiscoException(ERROR_UPDATING_PRODUCTS, e);
        }
    }

    private boolean isNeitherActiveNorSoldProduct(com.orange.discobole.productinventory.dto.v1.Product product) {
        return !Objects.equals(product.getStatus(), com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE)
                && !Objects.equals(product.getStatus(), com.orange.discobole.productinventory.dto.v1.ProductStatusType.SOLD);
    }

    private List<String> getProductOrderReservedResourcesIds(ProductOrder productOrder) {
        return productOrder.getProductOrderItem().stream()
                .map(ProductOrderItem::getProduct)
                .filter(Objects::nonNull)
                .filter(Product.class::isInstance)
                .map(Product.class::cast)
                .map(Product::getRealizingResource)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(resourceRef -> LOGICAL_RESOURCE.equals(resourceRef.getAtType()))
                .map(ResourceRef::getId)
                .toList();
    }

    private void setContextVariables(StateContext<String, String> context, DiscoException exception) {
        if (!exception.getReason().isEmpty() && ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE.equals(exception.getReason())) {
            StateMachineUtil.setDescriptionContext(context, RESOURCES_CANCELLATION_ERROR);
        } else {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PRODUCT_UPDATE_ERROR);
        }
    }

    private void setProductOrderStatusToHeld(ProductOrder productOrder, StateContext<String, String> context) {
        productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.HELD);
        context.getExtendedState().getVariables().put(CREATED_PRODUCT_ORDER, productOrder);
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationRelationship;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductSpecificationNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderItemValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedOrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.MaintainOrchestrationPlanNodeRelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanNodeRelationshipService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.dto.helper.ProductOrderItemHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.*;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationBaseType.SHIPPING_PRODUCT_SPECIFICATION;

/**
 * Responsible for relationship of the Orchestration plan nodes
 */
@Slf4j
@Service
@AllArgsConstructor
public class OrchestrationPlanNodeRelationshipServiceImpl implements OrchestrationPlanNodeRelationshipService {
    private final MaintainOrchestrationPlanNodeRelatedProduct maintainOrchestrationPlanNodeRelatedProduct;

    /**
     * create related orchestration plan node with delivers after status
     *
     * @param orchestrationPlan            initialized orchestration plan
     * @param relatedProductOrderItemDTOId related product order item id that matches delivering after
     * @return RelatedOrchestrationPlanNode which holds related node id and DELIVER_AFTER relationship
     */

    private static RelatedOrchestrationPlanNode createRelatedOrchestrationPlanNode(OrchestrationPlan orchestrationPlan, String relatedProductOrderItemDTOId) {
        // TODO check if we need to handle list here instead of findFirst
        String relatedNodeID = orchestrationPlan.getOrchestrationPlanNodeIDBy(relatedProductOrderItemDTOId)
                .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(new OrchestrationPlanNodeNotFoundException(NODE_NOT_FOUND_BY_ITEM_ID, relatedProductOrderItemDTOId)));
        return new RelatedOrchestrationPlanNode(relatedNodeID, RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER);
    }

    @Override
    public void maintainRelationshipsBetweenNodes(List<ProductOrderItem> productOrderItems, OrchestrationPlan orchestrationPlan,
                                                  Map<String, ProductSpecification> productSpecificationsMappedByIds) {
        log.info("Building relationship between nodes for orchestration plan id: {}", orchestrationPlan.getId());

        productOrderItems.stream()
                .filter(productOrderItem -> {
                    ProductRefOrValue product = productOrderItem.getProduct();
                    // Check if product is an instance of Product
                    if (product instanceof Product) {
                        return !SHIPPING_PRODUCT_SPECIFICATION.getValue().equals(
                                ((Product) product).getProductSpecification().getAtBaseType());
                    }
                    // Exclude items where product is not a Product
                    return false;
                })
                .forEach(productOrderItem -> {
                    String productSpecificationId = ProductOrderItemHelper.getProductSpecificationId(productOrderItem)
                            .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(
                                    new CoodTechnicalException(PRODUCT_SPEC_ID_NOT_FOUND, productOrderItem.getId())));

                    if (productSpecificationId == null) {
                        throw new CoodRecoverableAndNonRetryableException(
                                new ProductOrderItemValidationException(PRODUCT_ORDER_INVALID_EVENT_NO_PRODUCT_SPEC, productOrderItem.getId()));
                    }

                    ProductSpecification productSpecification = productSpecificationsMappedByIds.get(productSpecificationId);
                    if (productSpecification == null) {
                        throw new CoodRecoverableAndNonRetryableException(
                                new ProductSpecificationNotFoundException(PRODUCT_SPEC_BY_ID_NOT_FOUND, productSpecificationId, productOrderItem.getId()));
                    }

                    ItemActionType actionType = productOrderItem.getAction();
                    log.info("Orchestration plan id: {}, with action type {}", orchestrationPlan.getId(), actionType);
                    log.debug("Orchestration plan {}, action type {}, productSpecification {}, productOrderItem {}", orchestrationPlan, actionType, productSpecification, productOrderItem);

                    switch (actionType) {
                        case ADD, MODIFY, MIGRATE -> {
                            List<RelatedOrchestrationPlanNode> relatedOrchestrationPlanNodes = getRelatedOrchestrationPlanNodesToOrchestrationPlanNodeBasedOnAction(productOrderItems, orchestrationPlan, productSpecification, productOrderItem);
                            OrchestrationPlanNode orchestrationPlanNode = getOrchestrationPlanNodeByOrderItemIdAndNodes(orchestrationPlan.getOrchestrationPlanNodes(), productOrderItem.getId());
                            orchestrationPlanNode.setRelatedOrchestrationPlanNode(relatedOrchestrationPlanNodes);
                        }
                        case DELETE ->
                                addDeliversAfterRelatedNodesToOrchestrationPlanNodeHaveDeleteAction(productOrderItems, orchestrationPlan, productSpecification, productOrderItem);
                        default -> {
                            log.error("Invalid product order action: {} for item with ID: {}", actionType, productOrderItem.getId());
                            throw new CoodNonRecoverableAndNonRetryableException(new ProductOrderValidationException(PRODUCT_ORDER_ITEM_ACTION_INVALID, actionType));
                        }
                    }

                    log.info("Finished for node: {}", orchestrationPlan.getId());
                    log.debug("Finished for node: {}", orchestrationPlan);
                });

        log.info("Finished building relationship between nodes for orchestration plan id: {}", orchestrationPlan.getId());
    }

    private List<RelatedOrchestrationPlanNode> getRelatedOrchestrationPlanNodesToOrchestrationPlanNodeBasedOnAction(List<ProductOrderItem> productOrderItems, OrchestrationPlan orchestrationPlan, ProductSpecification productSpecification,
                                                                                                                    ProductOrderItem productOrderItem) {
        log.info("Start with Node: {}", orchestrationPlan.getId());
        log.debug("Orchestration plan {}, productSpecification {}, productOrderItem {}", orchestrationPlan, productSpecification, productOrderItem);

        List<RelatedOrchestrationPlanNode> relatedOrchestrationPlanNodeList = new ArrayList<>();

        if (productSpecification.getProductSpecificationRelationship() == null) {
            return List.of();
        }

        for (ProductSpecificationRelationship productSpecificationRelationship : productSpecification.getProductSpecificationRelationship()) {
            if (productSpecificationRelationship.getRelationshipType().equals(ProductSpecificationRelationshipType.RELIES_ON)) {
                // If relies on products exist in the same product order add them as related nodes
                getRelatedProductOrderItemIdByProductSpecificationIdThenProductOrderRelations(
                        productOrderItems,
                        productSpecificationRelationship.getId(),
                        productOrderItem
                )
                        .ifPresent(relatedProductOrderItemDTOId -> {
                            RelatedOrchestrationPlanNode relatedOrchestrationPlanNode = createRelatedOrchestrationPlanNode(orchestrationPlan, relatedProductOrderItemDTOId);
                            relatedOrchestrationPlanNodeList.add(relatedOrchestrationPlanNode);

                        });
                maintainOrchestrationPlanNodeRelatedProduct.maintainRelatedProductSpec(orchestrationPlan.getOrchestrationPlanNodes(),
                        productOrderItem, productSpecificationRelationship);
            }
        }

        return relatedOrchestrationPlanNodeList;
    }

    private void addDeliversAfterRelatedNodesToOrchestrationPlanNodeHaveDeleteAction(List<ProductOrderItem> productOrderItems, OrchestrationPlan orchestrationPlan, ProductSpecification productSpecification,
                                                                                     ProductOrderItem productOrderItemDTO) {
        log.info("Orchestration plan node Delete Action: {}", orchestrationPlan.getId());
        log.debug("Orchestration plan {}, productSpecification {}, productOrderItems {} productOrderItemDTO {}", orchestrationPlan, productSpecification, productOrderItems, productOrderItemDTO);

        RelatedOrchestrationPlanNode relatedOrchestrationPlanNode = createRelatedOrchestrationPlanNode(orchestrationPlan, productOrderItemDTO.getId());

        if (Objects.isNull(productSpecification.getProductSpecificationRelationship())) {
            //tODo need to throw error ?
            log.error("ProductSpecification does not have a product relationship: {}", productSpecification.getProductSpecificationRelationship());
            return;
        }
        productSpecification.getProductSpecificationRelationship().stream()
                .filter(relationship -> Set.of(ProductSpecificationRelationshipType.REQUIRES, ProductSpecificationRelationshipType.RELIES_ON)
                        .contains(relationship.getRelationshipType()))
                .forEach(relationship -> processRelatedProductOrderItem(productOrderItems, orchestrationPlan, productOrderItemDTO, relatedOrchestrationPlanNode, relationship));
        log.info("Finished orchestration plan node Delete Action: {}", orchestrationPlan.getId());
    }

    private void processRelatedProductOrderItem(List<ProductOrderItem> productOrderItems, OrchestrationPlan orchestrationPlan, ProductOrderItem productOrderItem,
                                                RelatedOrchestrationPlanNode relatedOrchestrationPlanNode, ProductSpecificationRelationship relationship) {
        //TODO id not found need to throw ?
        getRelatedProductOrderItemIdByProductSpecificationIdThenProductOrderRelations(productOrderItems, relationship.getId(), productOrderItem)
                .ifPresent(productOrderItemId -> {
                    getOrchestrationPlanNodeByOrderItemIdAndNodes(orchestrationPlan.getOrchestrationPlanNodes(), productOrderItemId).addRelatedOrchestrationPlanNode(relatedOrchestrationPlanNode);
                    maintainOrchestrationPlanNodeRelatedProduct.maintainRelatedProductSpec(orchestrationPlan.getOrchestrationPlanNodes(),
                            productOrderItem, relationship);
                });
    }

    private Optional<String> getRelatedProductOrderItemIdByProductSpecificationIdThenProductOrderRelations(List<ProductOrderItem> productOrderItems, String productSpecificationId, ProductOrderItem productOrderItem) {

        List<ProductOrderItem> filteredItems = productOrderItems.stream()
                // Multiple products could have the same specification id, so we need to get all of them.
                .filter(poi -> {
                    ProductRefOrValue product = poi.getProduct();
                    // Check if product is an instance of Product and has a non-null ProductSpecification
                    return product instanceof Product &&
                            Objects.nonNull(((Product) product).getProductSpecification());
                })
                .filter(poi -> {
                    Product product = (Product) poi.getProduct();
                    return product.getProductSpecification().getId().equals(productSpecificationId);
                })
                .toList();

        // COOD is a catalog driven architecture, so if the catalog provides all the info needed we use it directly.
        if (filteredItems.size() == 1) {
            return Optional.of(filteredItems.get(0).getId());
        }

        // If the current product is marked for DELETE and there are exactly two
        // related items with MIGRATE actions, this means the deleted product
        // relies on a migrated product from an old plan. In this case, link the
        // deleted product to the product order item that has a MIGRATE-TO relationship.
        if (filteredItems.size() == 2
                && ItemActionType.DELETE.equals(productOrderItem.getAction())
                && filteredItems.stream().allMatch(poi -> poi.getAction().equals(ItemActionType.MIGRATE))) {
            return filteredItems.stream()
                    .filter(poi -> poi.getProductOrderItemRelationship().stream()
                            .anyMatch(pr -> RelationshipType.MIGRATETO.equals(pr.getRelationshipType())))
                    .map(ProductOrderItem::getId)
                    .findFirst();
        }

        // Corner case in multi-purchase scenario:
        // A "MODIFY" order item has no relationships defined at the order level,
        // but the Product Catalog provides specification relationships for this item type.
        // Additionally, multiple "ADD" order items exist for the same type.
        // In this case, the item should be delivered independently,
        // without applying any prerequisites.
        if (filteredItems.size() > 1
                && ItemActionType.MODIFY.equals(productOrderItem.getAction())
                && CollectionUtils.isEmpty(productOrderItem.getProductOrderItemRelationship())) {
            return Optional.empty();
        }

        // In case that order item has relies on relationship with another that catalog doesn't have
        List<String> allReliesOnIds = Objects.nonNull(productOrderItem.getProductOrderItemRelationship()) ? productOrderItem.getProductOrderItemRelationship().stream()
                .filter(relationship -> Objects.nonNull(relationship) && Objects.nonNull(relationship.getRelationshipType()) && relationship.getRelationshipType().equals(RelationshipType.RELIESON))
                .map(OrderItemRelationship::getId)
                .toList() : Collections.emptyList();

        // If multiple products have the same specification id in the relationship,
        // then we complement the Catalog info by searching in the OM info.
        List<String> orderItemsMatchingIds = filteredItems.stream()
                .map(ProductOrderItem::getId)
                .filter(id -> Objects.nonNull(productOrderItem.getProductOrderItemRelationship()) && productOrderItem.getProductOrderItemRelationship().stream()
                        .anyMatch(relationship -> Objects.nonNull(relationship) && Objects.nonNull(relationship.getRelationshipType()) && relationship.getRelationshipType().equals(RelationshipType.RELIESON)
                                && relationship.getId().equals(id)))
                .toList();

        // Missing relationship scenario
        // If there are matching order items for spec id, but there are no reliesOn relationship for selected order item
        if (!filteredItems.isEmpty() && orderItemsMatchingIds.isEmpty() && allReliesOnIds.isEmpty()) {
            boolean allMigrate = filteredItems.stream().allMatch(poi -> ItemActionType.MIGRATE.equals(poi.getAction()));
            throw new CoodRecoverableAndNonRetryableException(new ProductOrderValidationException(allMigrate ?
                    MISSING_RELIES_ON_RELATIONSHIP_IN_MIGRATE_SCENARIO : MISSING_RELIES_ON_RELATIONSHIP_IN_MULTI_PURCHASE_SCENARIO, productOrderItem.getId()));
        }

        // wrong relationship scenario
        // throw exception if there is wrong reliesOn relation that doesn't match catalog relations
        if (!filteredItems.isEmpty() && !allReliesOnIds.isEmpty() && filteredItems.stream().map(ProductOrderItem::getId).noneMatch(allReliesOnIds::contains)) {
            boolean allMigrate = filteredItems.stream().allMatch(poi -> ItemActionType.MIGRATE.equals(poi.getAction()));
            throw new CoodRecoverableAndNonRetryableException(new ProductOrderValidationException(allMigrate ?
                    WRONG_RELIES_ON_RELATIONSHIP_IN_MIGRATE_SCENARIO : WRONG_RELIES_ON_RELATIONSHIP_IN_MULTI_PURCHASE_SCENARIO, productOrderItem.getId(), allReliesOnIds));
        }

        /*
            Return the ID wrapped in Optional,
            this should be exactly one id in the product order, corner cases are out of scoop for now.
        */
        return orderItemsMatchingIds.stream().findFirst();
    }

    private OrchestrationPlanNode getOrchestrationPlanNodeByOrderItemIdAndNodes(Set<OrchestrationPlanNode> orchestrationPlanNodes, String productOrderItemId) {
        return orchestrationPlanNodes.stream()
                .filter(node -> node.getRelatedProductOrderItem().stream()
                        .anyMatch(orderItem -> orderItem.getId().equals(productOrderItemId)))
                .findFirst()
                .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(new OrchestrationPlanNodeNotFoundException(NODE_NOT_FOUND_BY_ORDER_ITEM_ID, productOrderItemId)));

    }

}


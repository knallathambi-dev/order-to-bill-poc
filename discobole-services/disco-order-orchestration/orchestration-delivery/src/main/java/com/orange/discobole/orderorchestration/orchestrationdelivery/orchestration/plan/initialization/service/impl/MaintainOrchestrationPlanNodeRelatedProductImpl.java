// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.OrderItemRelationship;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelationshipType;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationRelationship;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.MaintainOrchestrationPlanNodeRelatedProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct.relatedProductWithProductSpecificationRelationshipReliesOnBuilder;


@Component
@Slf4j
public class MaintainOrchestrationPlanNodeRelatedProductImpl implements MaintainOrchestrationPlanNodeRelatedProduct {

    public void maintainRelatedProductSpec(Set<OrchestrationPlanNode> orchestrationPlanNodes, ProductOrderItem productOrderItem,
                                           ProductSpecificationRelationship productSpecificationRelationship) {

        log.info("Maintaining related product specification of nodes {} for product Order item ID: {}", orchestrationPlanNodes.stream()
                .map(OrchestrationPlanNode::getId).toList(), productOrderItem);

        List<OrderItemRelationship> productOrderItemRelationships = productOrderItem.getProductOrderItemRelationship();

        boolean containsMigratedTo = productOrderItemRelationships != null && productOrderItemRelationships.stream().anyMatch(orderItemRelationship -> orderItemRelationship.getRelationshipType() == RelationshipType.MIGRATETO);

        if (containsMigratedTo) {
            return;
        }


        RelatedProduct relatedProductRef = relatedProductWithProductSpecificationRelationshipReliesOnBuilder(productSpecificationRelationship, productOrderItem.getIsInstallable()).build();

        OrchestrationPlanNode orchestrationPlanNodeForRelatedProductRef = getOrchestrationPlanNodeByItemId(orchestrationPlanNodes, productOrderItem);

        orchestrationPlanNodeForRelatedProductRef.addRelatedProduct(relatedProductRef);

        log.info("Finished Maintaining related product specification of nodes {} for product Order item ID: {}", orchestrationPlanNodes.stream().map(OrchestrationPlanNode::getId).toList(), productOrderItem);

        log.debug("orchestration plan nodes : {}", orchestrationPlanNodes);
    }

    private OrchestrationPlanNode getOrchestrationPlanNodeByItemId(Set<OrchestrationPlanNode> orchestrationPlanNodes, ProductOrderItem productOrderItem) {
        return orchestrationPlanNodes.stream()
                .filter(node -> node.getRelatedProductOrderItem().stream()
                        .anyMatch(relatedProductOrderItem -> relatedProductOrderItem.getId().equals(productOrderItem.getId())))
                .findFirst()
                .orElseThrow(() -> new CoodTechnicalException("Cannot find orchestration node for the order item ID: " + productOrderItem.getId()));
    }

}

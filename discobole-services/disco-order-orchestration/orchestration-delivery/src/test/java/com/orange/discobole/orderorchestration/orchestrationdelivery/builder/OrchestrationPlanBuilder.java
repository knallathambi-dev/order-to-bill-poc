// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.builder;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType.DELIVERS;

public class OrchestrationPlanBuilder {
    public static OrchestrationPlan.OrchestrationPlanBuilder getOrchestrationPlanBuilder() {
        return OrchestrationPlan.builder()
                .id("id")
                .relatedContractName(null)
                .relatedProductOrder(new RelatedProductOrder("1"))
                .relatedParty(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty("id")))
                .state(State.IN_PROGRESS)
                .receivedDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC))
                .requestedDeliveryDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC))
                .orchestrationPlanNodes(Set.of(
                        new OrchestrationPlanNode("id1", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder(), getProductOrderBuilder().build(), List.of(new RelatedProductOrderItem("id1", "add", 1)), new RelatedSupplyChainOrder("id1", "1d1"), List.of(RelatedProduct.builder().isInstallable(true).productOrderItemId("id1").id("id1").type(RelatedProductType.CFS).relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "112", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("id2", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false),
                        new OrchestrationPlanNode("id2", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder(), getProductOrderBuilder().build(), List.of(new RelatedProductOrderItem("id2", "add", 1)), new RelatedSupplyChainOrder("id2", "id2"), List.of(RelatedProduct.builder().isInstallable(true).productOrderItemId("id2").id("id2").type(RelatedProductType.CFS).relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id2", "name", "version"))).build()).build(), RelatedProduct.builder()
                                .id("id1")
                                .isInstallable(true)
                                .relationshipType(RelatedProductRelationType.RELIES_ON)
                                .productOrderItemId("id2")
                                .productSpecification(ProductSpecification.builder().id("id").build()).build()), List.of(), null, null, null, false),
                        new OrchestrationPlanNode("id3", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder(), getProductOrderBuilder().build(), List.of(new RelatedProductOrderItem("id3", "add", 1)), new RelatedSupplyChainOrder("id3", "id3"), List.of(RelatedProduct.builder().isInstallable(true).id("id3").productOrderItemId("id3").type(RelatedProductType.CFS).relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id3", "name", "version"))).build()).build()), null, null, null, null, false)
                ));

    }

    private static RelatedProductOrder.RelatedProductOrderBuilder getProductOrderBuilder() {
        return RelatedProductOrder.builder().id("1");
    }
}

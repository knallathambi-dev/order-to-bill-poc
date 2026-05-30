// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.builder;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.builder.RelatedProductBuilder.getRelatedProductBuilder;

public class OrchestrationPlanNodeBuilder {

    public static OrchestrationPlanNode.OrchestrationPlanNodeBuilder getCFSOrchestrationPlanNodeBuilder() {
        return OrchestrationPlanNode.builder()
                .id("id1")
                .state(OrchestrationPlanNodeState.IN_PROGRESS)
                .relatedServiceOrder(new RelatedServiceOrder())
                .relatedProductOrder(RelatedProductOrder.builder().id("1").build())
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .relatedSupplyChainOrder(new RelatedSupplyChainOrder("id1", "id1"))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder().productOrderItemId("id1").type(RelatedProductType.CFS).isInstallable(true).build())))
                .relatedOrchestrationPlanNode(
                        List.of(new RelatedOrchestrationPlanNode("id2", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)));
    }

    public static OrchestrationPlanNode.OrchestrationPlanNodeBuilder getStockItemOrchestrationPlanNodeBuilder() {
        return OrchestrationPlanNode.builder()
                .id("id1")
                .state(OrchestrationPlanNodeState.IN_PROGRESS)
                .relatedServiceOrder(new RelatedServiceOrder())
                .relatedProductOrder(RelatedProductOrder.builder().id("1").build())
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .relatedSupplyChainOrder(new RelatedSupplyChainOrder("id1", "id1"))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder().build())))
                .relatedOrchestrationPlanNode(
                        List.of(new RelatedOrchestrationPlanNode("id2", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)));
    }

}

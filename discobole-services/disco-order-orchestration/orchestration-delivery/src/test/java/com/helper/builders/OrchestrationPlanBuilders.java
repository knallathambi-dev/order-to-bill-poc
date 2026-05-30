// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.helper.builders;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OrchestrationPlanBuilders {

    public static OrchestrationPlan.OrchestrationPlanBuilder orchestrationPlanBuilder() {
        return OrchestrationPlan.builder()
                .id(UUID.randomUUID().toString())
                .relatedProductOrder(RelatedProductOrder.builder().id(UUID.randomUUID().toString()).build())
                .relatedParty(List.of(RelatedParty.builder().id("231-mf4").role("customer").name("Abir").href(null).build()))
                .state(State.INITIALIZED)
                .requestedDeliveryDate(Instant.parse("2023-06-23T08:44:32.157581800Z"))
                .orchestrationPlanNodes(Set.of(
                        OrchestrationPlanNode.builder().id(UUID.randomUUID().toString())
                                .state(OrchestrationPlanNodeState.INITIALIZED)
                                .relatedServiceOrder(null)
                                .relatedProductOrderItem(List.of(RelatedProductOrderItem.builder().id("8").build()))
                                .relatedProduct(null)
                                .relatedOrchestrationPlanNode(null)
                                .build()
                ));
    }
}

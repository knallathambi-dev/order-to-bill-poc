// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.builder;


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrder;

import java.time.Instant;
import java.util.List;

public class OrchestrationPlanBuilders {

    public static OrchestrationPlan.OrchestrationPlanBuilder orchestrationPlanInProgressWithoutOrchestrationPlanNodesBuilder(String relatedProductOrderId) {
        return OrchestrationPlan.builder()
                .relatedProductOrder(RelatedProductOrder.builder().id(relatedProductOrderId).build())
                .relatedParty(List.of(RelatedParty.builder().id("231-mf4").role("customer").name("A1").build()))
                .state(State.IN_PROGRESS)
                .receivedDate(Instant.parse("2023-10-23T12:33:37.910Z"));

    }
}

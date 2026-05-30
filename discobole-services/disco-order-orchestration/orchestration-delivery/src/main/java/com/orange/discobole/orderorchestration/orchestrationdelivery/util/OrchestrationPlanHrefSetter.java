// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util;

import com.orange.discobole.orderorchestration.orchestrationdelivery.api.v1.OrchestrationPlanApi;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

public class OrchestrationPlanHrefSetter {

    private OrchestrationPlanHrefSetter() {
    }

    public static String generateHref(String id, String fields) {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrchestrationPlanApi.class)
                        .getOrchestrationPlanById(id, fields)).toUriComponentsBuilder()
                .build().toUriString();
    }

    public static void setHrefForOrchestrationPlans(List<OrchestrationPlan> orchestrationPlanList, String fields) {
        orchestrationPlanList.forEach(orchestrationPlan -> orchestrationPlan.setHref(generateHref(orchestrationPlan.getId(), fields)));
    }
}

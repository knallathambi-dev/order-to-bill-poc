// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.OrchestrationPlanResponse;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;

import java.util.List;

public interface OrchestrationManagementService {
    OrchestrationPlan getOrchestrationPlanById(String id, String fields) throws CoodNotFoundException;

    OrchestrationPlanResponse getOrchestrationPlans(OrchestrationPlanFilter orchestrationPlanFilter, List<String> sorts);
}

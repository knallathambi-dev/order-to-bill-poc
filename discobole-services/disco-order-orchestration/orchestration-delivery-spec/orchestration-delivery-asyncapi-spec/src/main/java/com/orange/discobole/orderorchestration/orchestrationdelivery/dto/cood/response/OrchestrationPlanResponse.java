// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class OrchestrationPlanResponse {
    private HttpHeaders responseHeaders;
    private HttpStatus httpStatus;
    private List<OrchestrationPlan> orchestrationPlans;
}

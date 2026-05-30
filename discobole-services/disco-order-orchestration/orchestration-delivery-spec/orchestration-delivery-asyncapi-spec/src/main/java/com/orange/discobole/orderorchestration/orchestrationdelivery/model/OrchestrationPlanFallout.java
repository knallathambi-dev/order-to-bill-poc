// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class OrchestrationPlanFallout {

    private String planId;

    private String orderId;

    private Instant receivedDate;

    private State state;
}

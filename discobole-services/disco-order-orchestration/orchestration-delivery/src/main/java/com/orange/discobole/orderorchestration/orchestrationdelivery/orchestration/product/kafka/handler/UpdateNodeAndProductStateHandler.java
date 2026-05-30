// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;

import java.util.Map;

public interface UpdateNodeAndProductStateHandler {
    void update(OrchestrationPlanNode node);

    void update(OrchestrationPlanNode node, Map<String, Object> headers);
}

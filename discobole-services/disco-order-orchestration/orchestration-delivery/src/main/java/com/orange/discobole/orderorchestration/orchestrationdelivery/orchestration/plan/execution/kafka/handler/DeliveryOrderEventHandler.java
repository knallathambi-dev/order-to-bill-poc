// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler;


import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;

public interface DeliveryOrderEventHandler {
    void handleEvent(OrchestrationPlanNodeStateChangeEvent message);

    void handleDeadLetter(OrchestrationPlanNodeStateChangeEvent payload, FalloutCharacteristicWrapper characteristicWrapper);
}

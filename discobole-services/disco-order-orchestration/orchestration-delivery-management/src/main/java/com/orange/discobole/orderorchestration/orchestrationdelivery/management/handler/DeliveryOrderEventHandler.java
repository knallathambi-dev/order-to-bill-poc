// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler;

import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;

public interface DeliveryOrderEventHandler {
    void handleEvent(DeliveryOrderEvent deliveryOrderEventEvent);

    void handleDeadLetter(DeliveryOrderEvent deliveryOrderEvent, CoodError coodError, String traceParent, String topicName);
}

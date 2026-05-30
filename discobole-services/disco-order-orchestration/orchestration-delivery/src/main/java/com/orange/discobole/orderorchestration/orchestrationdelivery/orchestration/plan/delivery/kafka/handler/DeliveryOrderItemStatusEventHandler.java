// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.handler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;

import java.util.Map;

public interface DeliveryOrderItemStatusEventHandler {

    void handleEvent(DeliveryOrderItemStatusEvent serviceEvent, Map<String, String> headers);

    void handleDeadLetter(DeliveryOrderItemStatusEvent message, FalloutCharacteristicWrapper characteristicWrapper, Map<String, String> headers);
}

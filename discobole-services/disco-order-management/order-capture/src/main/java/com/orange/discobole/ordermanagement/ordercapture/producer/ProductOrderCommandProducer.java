// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.producer;

import com.orange.discobole.ordermanagement.event.om.Command.EventType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;

public interface ProductOrderCommandProducer {

    void publishCommand(ProductOrder productOrder, EventType eventType);
}
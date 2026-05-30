// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer;

import com.orange.discobole.ordermanagement.commons.enumeration.EventType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;


public interface ProductOrderEventProducer {
    void publishEvent(ProductOrder productOrder, EventType eventType);
}
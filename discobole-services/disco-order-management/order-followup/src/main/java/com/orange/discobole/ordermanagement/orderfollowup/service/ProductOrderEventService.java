// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service;

import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemStateChangedEvent;
import com.orange.discobole.ordermanagement.orderfollowup.enums.OfupStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProductOrderEventService {

    ProductOrderItemStateChangedEvent save(ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent);

    Optional<ProductOrderItemStateChangedEvent> findByProductOrderId(String productOrderId);

    void deleteByProductOrderId(String productOrderId);

    ProductOrderItemStateChangedEvent createProductOrderItemStateChangedEvent(ProductOrder productOrder, Instant eventTime);

    void addProductOrderItem(ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent);

    void updateProductItemOfupState(String productOrderId, String eventId, OfupStateType ofupStateType);

    void reinitializeProductItemOfupState();

    List<ProductOrderItemStateChangedEvent> getNewProductOrderItemEvents();

    void executeReceiveNewProductStateChangeEventTaskFlow(ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent);
}
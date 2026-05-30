// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderEntity;
import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.ProductOrderResponse;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import org.springframework.util.MultiValueMap;

/**
 * Service Interface for managing {@link ProductOrderEntity}.
 */
public interface ProductOrderService {
    ProductOrder saveProductOrder(ProductOrder productOrder) throws ProductOrderInventoryException;

    ProductOrder getProductOrderById(String id, String fields);

    ProductOrderResponse getProductOrders(MultiValueMap<String, Object> attributes);

    void updateProductOrderPayment(ProductOrder productOrder);

    void updateProductOrderBillingAccount(ProductOrder productOrder);

    void updateProductOrderAppointment(ProductOrder productOrder);

    void updateProductOrderProduct(ProductOrder productOrder);

    void updateProductOrderRealizingResource(ProductOrder productOrder);

    void updateOrderItemsAndOrderTotalPrice(ProductOrder productOrder);

    void updateState(ProductOrder productOrder);

    void updateProductOrderRelatedParties(ProductOrder productOrder);

    void updateProductOrderHierarchy(String productOrderId, String productOrderItemId, ProductOrderItemStateType nodeStatus);
    void updateRequestedCompletionDate(ProductOrder productOrder);
}
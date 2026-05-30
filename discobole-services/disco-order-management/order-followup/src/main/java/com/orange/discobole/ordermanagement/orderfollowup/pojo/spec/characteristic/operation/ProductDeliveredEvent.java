// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.pojo.spec.characteristic.operation;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDeliveredEvent {
    private String productOrderId;
    private String productOrderItemId;
    private String productOrderItemEventId;
    private ProductOrderStateType relatedProductOrderState;
    private ProductOrderItemStateType productState;
}
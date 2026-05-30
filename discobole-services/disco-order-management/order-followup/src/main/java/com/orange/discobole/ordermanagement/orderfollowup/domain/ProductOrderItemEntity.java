// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.domain;

import com.orange.discobole.ordermanagement.orderfollowup.enums.OfupStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * A ProductOrderItem.
 */
@Data
@Builder
public class ProductOrderItemEntity implements Serializable {

    private String eventId;
    private Instant eventTime;
    private String productOrderItemId;
    private ProductOrderStateType relatedProductOrderState;
    private ProductOrderItemStateType state;
    private OfupStateType ofupState;
}
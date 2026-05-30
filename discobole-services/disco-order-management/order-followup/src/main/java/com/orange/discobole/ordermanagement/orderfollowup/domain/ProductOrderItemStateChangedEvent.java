// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.domain;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_ORDER_ITEM_EVENT_COLLECTION;

@Getter
@Setter
@Builder
@Document(collection = PRODUCT_ORDER_ITEM_EVENT_COLLECTION)
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProductOrderItemStateChangedEvent implements Serializable {
    @Id
    private String id;
    @Indexed(unique = true)
    private String productOrderId;
    private Instant eventTime;
    private String processId;
    private String nextTaskToBePerformed;
    private ProductOrderStateType productOrderState;
    private List<ProductOrderItemEntity> productOrderItems;
}
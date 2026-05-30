// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.springframework.statemachine.StateContext;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ProductOrderUtil {

    private ProductOrderUtil() {
    }

    public static List<String> getOrderItemsIdBy(ProductOrder productOrder, ItemActionType... actionTypes) {
        if (Objects.isNull(productOrder)) {
            throw new IllegalArgumentException("product order should not be null");
        }
        if (Objects.isNull(actionTypes)) {
            return List.of();
        }
        return productOrder.getProductOrderItem().stream()
                .filter(productOrderItem -> Arrays.asList(actionTypes).contains(productOrderItem.getAction()))
                .map(ProductOrderItem::getId).toList();
    }

    public static RelatedParty getRelatedParty(StateContext<String, String> context) {
        List<RelatedParty> relatedPartyList = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.TASK_RELATED_PARTY, RelatedParty.class);
        return !CollectionUtils.isEmpty(relatedPartyList) ? relatedPartyList.get(0) : null;
    }
}
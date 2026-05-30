// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.constant;

import com.orange.discobole.ordermanagement.orderfollowup.pojo.spec.characteristic.operation.ChooseOperation;
import com.orange.discobole.ordermanagement.orderfollowup.pojo.spec.characteristic.operation.ProductDeliveredEvent;

public class FollowUpConstants {
    public static final String PRODUCTS_CACHE = "products";
    public static final String PRODUCT_ORDER_ITEM_OFUP_STATE_TYPE = "productOrderItems.ofupState";
    public static final String PRODUCT_ORDER_ITEMS_EVENT_ID = "productOrderItems.eventId";
    public static final String DEFAULT_ID = "1";
    public static final int DEFAULT_MIN_CARDINALITY = 1;
    public static final Integer DEFAULT_MAX_CARDINALITY = 1;
    public static final String ORDER_FOLLOW_UP = "OrderFollowUp";
    public static final String NEW_PRODUCT_ORDER_ITEM_STATE_CHANGE_EVENT_TITLE = "OrderFollowUp.receiveNewProductStateChangeEvent";
    public static final String PRODUCT_ORDER_ITEM_EVENT_COLLECTION = "productOrderItemEvent";
    public static final String PRODUCT_DELIVERED_EVENT_CLASS = ProductDeliveredEvent.class.getSimpleName();
    public static final String CHOOSE_OPERATION_CLASS = ChooseOperation.class.getSimpleName();
    public static final String PRODUCT_ORDER_ID = "productOrderId";
    public static final String PRODUCT_ORDER_ITEM_ID = "productOrderItemId";
    public static final String PRODUCT_ORDER_ITEM_EVENT_ID = "productOrderItemEventId";
    public static final String PRODUCT_ORDER_STATE = "productOrderState";
    public static final String OBJECT_NAME = "Object";
    public static final String IS_PRODUCT_UPDATED = "isProductUpdated";
    public static final String OBJECT_CHARACTERISTIC = "ObjectCharacteristic";
    public static final String OBJECT = "Object";
    public static final String PRODUCT_ORDER_ITEMS = "productOrderItems";
    public static final String STRING_RELATIONSHIP_TYPE = "relationshipType";
    public static final String RELATIONSHIP_TYPE_URI = "/relationshipType";
    public static final String PRODUCT_RELATIONSHIP_URI = "/productRelationship/";
    public static final String PRODUCT_ID = "/product/id";
    public static final String ID = "id";
    public static final String TYPE = "@type";
    public static final String PRODUCT = "product";
    public static final String DISCOUNT_PRICE_ALTERATION = "discountPriceAlteration";
    public static final String RECURRING_DISCOUNT = "recurringDiscount";
    public static final String NON_RECURRING_DISCOUNT = "nonRecurringDiscount";

    private FollowUpConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}
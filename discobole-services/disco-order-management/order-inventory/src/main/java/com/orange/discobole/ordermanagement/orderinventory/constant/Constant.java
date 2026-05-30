// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.constant;

public class Constant {
    public static final String FIELDS = "fields";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String SORT = "sort";
    public static final String NONE = "none";
    public static final String NULL = "null";
    public static final String HREF_FIELD = "href";
    public static final String TYPE_FIELD = "atType";
    public static final String ID_FIELD = "id";
    public static final String PRODUCT_ORDER_ITEM_FIELD = "productOrderItem";
    public static final String ORDER_TOTAL_PRICE = "orderTotalPrice";
    public static final String RELATED_PARTY_FIELD = "relatedParty";
    public static final String PRODUCT_ORDER_STATE_FIELD = "state";
    public static final String PRODUCT_ORDER_ITEM_STATE_FIELD = "productOrderItem.$[].state";
    public static final String PRODUCT_ORDER_ITEM_ID_FIELD = "productOrderItem.id";
    public static final String PAYMENT_FIELD = "productOrderItem.$.payment";
    public static final String BILLING_ACCOUNT_FIELD = "productOrderItem.$.billingAccount";
    public static final String APPOINTMENT_FIELD = "productOrderItem.$.appointment";
    public static final String PRODUCT_FIELD = "productOrderItem.$.product";
    public static final String REALIZING_RESOURCE_FIELD = "productOrderItem.$.product.realizingResource";
    public static final String SPACE_REGEX = ".*\\s,|,\\s.*|.*\\s;|;\\s.*|^\\s.*|.*\\s$";
    public static final String X_TOTAL_COUNT = "X-Total-Count";
    public static final String X_RESULT_COUNT = "X-Result-Count";
    public static final String DATE_TYPE_REGEX = "^((2000|2400|2800|(19|2[0-9])(0[48]|[2468][048]|[13579][26]))-02-29)$"
            + "|^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$"
            + "|^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$"
            + "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$";
    public static final String OAUTH_SCHEME_NAME = "Keycloak";
    public static final String ATOMIC_PRODUCT_OFFERING = "AtomicProductOffering";
    public static final String REQUESTED_COMPLETION_DATE = "requestedCompletionDate";

    private Constant() {
    }
}
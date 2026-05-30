// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.constant;

public class GuardNameConstants {
    public static final String PRODUCTS_PROCEEDED_GUARD = "isAllProductsProceededUpdated";
    public static final String PRODUCT_ORDER_STATE_GUARD = "isProductOrderStatePartialGuard";
    public static final String PRODUCT_UPDATE_GUARD = "isProductUpdateGuard";

    private GuardNameConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}
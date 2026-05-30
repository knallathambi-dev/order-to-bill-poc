// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.constant;

public class PreAuthorizeExpressions {


    public static final String HAS_ROLE_ENTITLEMENT = "@securityService.hasRoleEntitlement('";
    public static final String CLOSING_BRACKET = "')";
    public static final String CAN_READ_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.READ_PRODUCT_ORDER + CLOSING_BRACKET;
    public static final String CAN_CREATE_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.CREATE_PRODUCT_ORDER + CLOSING_BRACKET;


    private PreAuthorizeExpressions() {
        throw new IllegalStateException("Utility class");
    }
}

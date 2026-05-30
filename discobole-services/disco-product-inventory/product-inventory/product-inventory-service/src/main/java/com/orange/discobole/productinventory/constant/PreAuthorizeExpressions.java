// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.constant;

public class PreAuthorizeExpressions {


    public static final String HAS_ROLE_ENTITLEMENT = "@securityService.hasRoleEntitlement('";
    public static final String CLOSING_BRACKET = "')";
    public static final String CAN_READ_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.READ_PRODUCT + CLOSING_BRACKET;
    public static final String CAN_EXPORT_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.EXPORT_PRODUCT + CLOSING_BRACKET;
    public static final String CAN_CREATE_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.CREATE_PRODUCT + CLOSING_BRACKET;
    public static final String CAN_UPDATE_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.UPDATE_PRODUCT + CLOSING_BRACKET;
    public static final String CAN_DELETE_PRODUCT = HAS_ROLE_ENTITLEMENT + Roles.DELETE_PRODUCT + CLOSING_BRACKET;


    private PreAuthorizeExpressions() {
        throw new IllegalStateException("Utility class");
    }
}

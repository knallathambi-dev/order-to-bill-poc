// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.constant;

public class ExceptionMessage {


    public static final String COULD_NOT_GET_PRODUCT_SPECIFICATION = "Error getting product Specification from catalog";
    public static final String COULD_NOT_GET_PRODUCT_SPECIFICATION_IDS = "Error retrieving product specifications: IDs list is empty";
    public static final String PRODUCT_SPECIFICATION_NOT_FOUND = "Error getting product Specification is not found";
    public static final String UTILITY_CLASS = "Utility class";
    public static final String INVALID_PRODUCTS_PARAMETERS = "Invalid product parameters";
    public static final String ORCHESTRATION_PLAN_MAY_NOT_BE_NULL = "orchestrationPlan may not be null";
    public static final String ORCHESTRATION_PLAN_ID_MAY_NOT_BE_NULL = "orchestrationPlan id may not be null";
    public static final String EVENT_TYPE_MAY_NOT_BE_NULL = "eventType id may not be null";

    private ExceptionMessage() {
        throw new IllegalStateException(UTILITY_CLASS);
    }

}


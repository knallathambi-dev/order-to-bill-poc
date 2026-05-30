// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.constant;

public final class Constant {
    public static final String PRODUCT_INVENTORY_MANAGEMENT_BASE_URL = "productInventoryManagement/v1";
    public static final String REPORTS_SUB_PATH = "reports";

    public static final String PRODUCT_DELETE_EVENT_TOPIC = "disco.product-inventory.productDeleteEvent-event";

    public static final String PRODUCT_CREATE_EVENT_TOPIC = "disco.product-inventory.productCreateEvent-event";
    public static final String PRODUCT_STATE_CHANGE_EVENT_TOPIC = "disco.product-inventory.productStateChangeEvent-event";
    public static final String SPECIFY_EITHER_COLLECT_DATE_OR_COLLECT_DATE_LTE_COLLECT_DATE_GTE_NOT_BOTH = "Specify either 'collectDate' or 'collectDate.lte'/'collectDate.gte', not both.";
    public static final String COLLECT_DATE_GTE_MUST_BE_AFTER_COLLECT_DATE_LTE = "'collectDate.gte' must be after 'collectDate.lte'";
    public static final String AT_LEAST_ONE_OF_COLLECT_DATE_COLLECT_DATE_LTE_OR_COLLECT_DATE_GTE_MUST_BE_PROVIDED = "At least one of 'collectDate', 'collectDate.lte', or 'collectDate.gte' must be provided.";
    public static final String THE_REPORT_WITH_ID_S_AND_TYPE_DOES_NOT_EXIST =
            "The Report with id %s and type %s does not exist";

}

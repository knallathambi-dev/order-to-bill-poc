// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.status;

import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PHYSICAL_PRODUCT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.SHIPMENT_PRODUCT;

public class StatusCheckerContext {

    private static final ProductStatusChecker PRODUCT_STATUS_CHECKER = new ProductStatusChecker();

    private static final TangibleProductStatusChecker TANGIBLE_PRODUCT_STATUS_CHECKER = new TangibleProductStatusChecker();

    private StatusCheckerContext() {

    }

    public static StatusChecker getStatusChecker(String type) {
        if (type.equals(PHYSICAL_PRODUCT.getValue()) || type.equals(SHIPMENT_PRODUCT.getValue())) {
            return TANGIBLE_PRODUCT_STATUS_CHECKER;
        } else {
            return PRODUCT_STATUS_CHECKER;
        }
    }
}

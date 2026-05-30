// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.status;


import lombok.extern.slf4j.Slf4j;

import static com.orange.discobole.productinventory.validation.status.ProductStatusLifecycle.*;


@Slf4j
class ProductStatusChecker extends StatusChecker {
    public ProductStatusChecker() {
        super(MAP_PRODUCT_STATUS_MAIN_TO_OPERATION, MAP_PRODUCT_STATUS_MAIN, MAP_PRODUCT_STATUS_OPERATIONAL);
    }

}


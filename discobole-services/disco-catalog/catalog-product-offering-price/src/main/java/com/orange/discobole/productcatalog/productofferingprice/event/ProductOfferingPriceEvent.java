// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.productofferingprice.constant.OdacaConstants;

public interface ProductOfferingPriceEvent extends Event {

    @Override
    default String aggregateName() {
        return OdacaConstants.PRODUCT_OFFERING_PRICE;
    }

}

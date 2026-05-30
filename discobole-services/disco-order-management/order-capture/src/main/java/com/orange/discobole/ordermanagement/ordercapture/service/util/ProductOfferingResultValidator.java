// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.util;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;

import java.util.Objects;

public interface ProductOfferingResultValidator {
    ResponseResult isValid(ProductOffering productOfferingResult);

    default boolean isProductOfferingSellable(ProductOffering productOffering) {
        return Boolean.TRUE.equals(productOffering.getIsSellable());
    }

    default boolean isProductOfferingCurrentlyAvailable(ProductOffering productOffering) {
        return Objects.nonNull(productOffering.getLifecycleStatus()) &&
                (ServiceConstants.LAUNCHED.equalsIgnoreCase(productOffering.getLifecycleStatus()) ||
                        ServiceConstants.ACTIVE.equalsIgnoreCase(productOffering.getLifecycleStatus()));
    }
}
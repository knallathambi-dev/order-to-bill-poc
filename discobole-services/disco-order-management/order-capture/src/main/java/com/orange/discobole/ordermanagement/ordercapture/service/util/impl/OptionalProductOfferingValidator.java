// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.util.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.ProductOfferingResultValidator;

public class OptionalProductOfferingValidator implements ProductOfferingResultValidator {

    @Override
    public ResponseResult isValid(ProductOffering productOffering) {
        if (productOffering == null || productOffering.getType() == null) {
            return buildResponse(false, DescriptionConstants.SELECTED_OFFER_NOT_VALID);
        }

        boolean isAtomicOrBundle = productOffering.getType().equalsIgnoreCase(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE) ||
                productOffering.getType().equalsIgnoreCase(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE);

        if (!isProductOfferingCurrentlyAvailable(productOffering) || !isAtomicOrBundle) {
            return buildResponse(false, DescriptionConstants.SELECTED_OFFER_NOT_VALID);
        }

        if (isProductOfferingSellable(productOffering)) {
            return buildResponse(true, DescriptionConstants.ACCESSORY_SELECTED_OFFER);
        } else {
            return buildResponse(true, DescriptionConstants.LOGICAL_SELECTED_OFFER);
        }
    }

    private ResponseResult buildResponse(boolean result, String description) {
        return ResponseResult.builder()
                .result(result)
                .description(description)
                .build();
    }
}
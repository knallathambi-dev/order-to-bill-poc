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

import java.util.Objects;

public class AcquisitionProductOfferingValidator implements ProductOfferingResultValidator {

    @Override
    public ResponseResult isValid(ProductOffering productOffering) {
        if (isContractProductOfferingValid(productOffering)) {
            return buildSuccessResponse(DescriptionConstants.CONTRACT_SELECTED_OFFER);
        } else if (isAccessoryProductOfferingValid(productOffering)) {
            return buildSuccessResponse(DescriptionConstants.ACCESSORY_SELECTED_OFFER);
        } else {
            return buildFailureResponse();
        }
    }

    private boolean isContractProductOfferingValid(ProductOffering productOffering) {
        return Objects.nonNull(productOffering)
                && isProductOfferingCurrentlyAvailable(productOffering)
                && hasProductOfferingType(productOffering, ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE);
    }

    private boolean isAccessoryProductOfferingValid(ProductOffering productOffering) {
        return Objects.nonNull(productOffering)
                && isProductOfferingCurrentlyAvailable(productOffering)
                && (hasProductOfferingType(productOffering, ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE)
                || hasProductOfferingType(productOffering, ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE))
                && isProductOfferingSellable(productOffering);
    }

    private boolean hasProductOfferingType(ProductOffering productOffering, String productOfferingType) {
        return Objects.nonNull(productOffering.getType())
                && productOffering.getType().equalsIgnoreCase(productOfferingType);
    }

    private ResponseResult buildSuccessResponse(String description) {
        return ResponseResult.builder()
                .result(true)
                .description(description)
                .build();
    }

    private ResponseResult buildFailureResponse() {
        return ResponseResult.builder()
                .result(false)
                .description(DescriptionConstants.SELECTED_OFFER_NOT_VALID)
                .build();
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.impl;

import com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.ProductEntityUtil;
import com.orange.discobole.productinventory.validation.ProductEntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.SHIPMENT_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL;

@Component
@Slf4j
@Order(2)
public class ShipmentProductValidator implements ProductEntityValidator {

    @Override
    public void validate(ProductEntity product) {
        if (product.getAtType().equals(ProductTypeEnum.SHIPMENT_PRODUCT.getValue())) {
            boolean isContract = ProductEntityUtil.isProductOfferingType(product, ProductOfferingTypeEnum.CONTRACT);
            boolean isBundleProductOffering = ProductEntityUtil.isProductOfferingType(product, ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING);
            if (isContract || isBundleProductOffering) {
                log.error("Shipment Product can not be instantiated at this level: " + (isContract ? ProductOfferingTypeEnum.CONTRACT.getValue() : ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()));
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(SHIPMENT_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL, isContract ? ProductOfferingTypeEnum.CONTRACT.getValue() : ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()));
            }

        }
    }

}

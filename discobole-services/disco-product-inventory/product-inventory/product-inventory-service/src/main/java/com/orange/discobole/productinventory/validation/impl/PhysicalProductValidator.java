// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.impl;

import com.orange.discobole.productinventory.config.ApplicationConfigProperties;
import com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.service.ResourceInventoryService;
import com.orange.discobole.productinventory.util.ProductEntityUtil;
import com.orange.discobole.productinventory.validation.ProductEntityValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.MISSING_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Component
@Slf4j
@Order(1)
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class PhysicalProductValidator implements ProductEntityValidator {
    private final ResourceInventoryService resourceInventoryService;
    private final ApplicationConfigProperties applicationConfigProperties;

    public PhysicalProductValidator(ResourceInventoryService resourceInventoryService, ApplicationConfigProperties applicationConfigProperties) {
        this.resourceInventoryService = resourceInventoryService;
        this.applicationConfigProperties = applicationConfigProperties;
    }


    @Override
    public void validate(ProductEntity product) {
        if (product.getAtType().equals(ProductTypeEnum.PHYSICAL_PRODUCT.getValue())) {
            boolean isContract = ProductEntityUtil.isProductOfferingType(product, ProductOfferingTypeEnum.CONTRACT);
            boolean isBundleProductOffering = ProductEntityUtil.isProductOfferingType(product, ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING);
            if (isContract || isBundleProductOffering) {
                log.error("Tangible Product can not be instantiated at this level: " + (isContract ? ProductOfferingTypeEnum.CONTRACT.getValue() : ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()));
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(TANGIBLE_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL, isContract ? ProductOfferingTypeEnum.CONTRACT.getValue() : ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()));
            }
            if (applicationConfigProperties.isEnableResourceInventoryManagementCheck() && ProductEntityUtil.isProductSpecification(product)) {
                    checkPhysicalProductSpecification(product);
                }        
        }
    }

    private void checkPhysicalProductSpecification(ProductEntity product) {
        if (Objects.isNull(product.getProductSerialNumber()) || product.getProductSerialNumber().isEmpty()) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, MISSING_INPUT.getCode(), MISSING_INPUT.getStatus(), EMPTY_PRODUCT_SERIAL_NUMBER);
        }
        if (product.getRealizingResource() == null || product.getRealizingResource().isEmpty()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    TANGIBLE_PRODUCT_REALIZING_RESOURCE_CANNOT_BE_NULL);
        }
        if (product.getRealizingResource().stream().anyMatch(serviceRefEntity -> !StringUtils.hasText(serviceRefEntity.getId()))) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    TANGIBLE_PRODUCT_REALIZING_RESOURCE_ID_CANNOT_BE_NULL);
        }
        resourceInventoryService.checkResourceInventory(product);
    }
}

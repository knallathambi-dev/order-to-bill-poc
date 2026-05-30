// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.enumerate.UnitEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductPriceEntity;
import com.orange.discobole.productinventory.model.TimePeriodEntity;
import com.orange.discobole.productinventory.model.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.orange.discobole.productinventory.constant.Constant.PRICE_TYPE_RECURRING;
import static com.orange.discobole.productinventory.constant.Constant.VALIDITY_CHARACTERISTIC;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.INVALID_TERMINATION_DATE;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_START_DATE_NOT_ADDED_IN_POST_REQUEST;

@Slf4j
public class ProductDatesChecker {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private ProductDatesChecker() {

    }

    public static void validateProductDates(ProductEntity productPatched) {
        ProductDatesChecker.checkStartDate(productPatched);
        ProductDatesChecker.checkTerminationDate(productPatched);
    }

    private static void checkStartDate(ProductEntity productPatched) {
        if ((productPatched.getStatus() == ProductStatusType.ACTIVE || productPatched.getStatus() == ProductStatusType.SOLD) && Objects.isNull(productPatched.getStartDate())) {
            productPatched.setStartDate(OffsetDateTime.now());
        }
    }
    private static void checkTerminationDate(ProductEntity productPatched) {
        ProductStatusType patchedStatus = productPatched.getStatus();
        if (patchedStatus == ProductStatusType.TERMINATED) {
            OffsetDateTime currentDateTime = OffsetDateTime.now();
            OffsetDateTime startDate = productPatched.getStartDate();
            OffsetDateTime terminationDate = productPatched.getTerminationDate();
            if ((terminationDate == null && currentDateTime.isBefore(startDate)) ||
                    (terminationDate != null && terminationDate.isBefore(startDate))) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), INVALID_TERMINATION_DATE);
            }
            if (terminationDate == null) {
                productPatched.setTerminationDate(currentDateTime);
            }
            updateRecurringPriceEndDates(productPatched, currentDateTime);

        }

    }
    private static void updateRecurringPriceEndDates(ProductEntity product, OffsetDateTime terminationDate) {
        List<ProductPriceEntity> prices = product.getProductPrice();

        if (prices == null) {
            return;
        }
        for (ProductPriceEntity price : prices) {
            if (PRICE_TYPE_RECURRING.equalsIgnoreCase(price.getPriceType())) {
                TimePeriodEntity validFor = price.getValidFor();
                if (validFor == null) {
                    validFor = new TimePeriodEntity();
                    price.setValidFor(validFor);
                }
                validFor.setEndDateTime(terminationDate);
            }
        }
    }
    public static void validateProductStartDate(OffsetDateTime startDate) {
        if (Objects.nonNull(startDate)) {
            log.error("Invalid product start date: {}", startDate);
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_START_DATE_NOT_ADDED_IN_POST_REQUEST);
        }
    }

    public static void setTerminationDate(ProductEntity patchedProduct) {
        if (Objects.isNull(patchedProduct) || patchedProduct.getProductCharacteristic() == null || patchedProduct.getProductCharacteristic().isEmpty()) {
            return;
        }
        patchedProduct.getProductCharacteristic().stream()
                .filter(characteristic -> VALIDITY_CHARACTERISTIC.equals(characteristic.getAtType()))
                .findFirst()
                .ifPresent(characteristicEntity -> {
                    Value value = MAPPER.convertValue(characteristicEntity.getValue(), Value.class);

                    if (Objects.nonNull(value.getValidTo())) {
                        patchedProduct.setTerminationDate(value.getValidTo());
                    } else if (Objects.nonNull(patchedProduct.getStartDate()) && Objects.nonNull(value.getValue()) && UnitEnum.isValidUnit(value.getUnitOfMeasure())) {
                        switch (UnitEnum.fromValue(value.getUnitOfMeasure())) {
                            case DAY -> patchedProduct.setTerminationDate(patchedProduct.getStartDate().plusDays(value.getValue()));
                            case MONTH -> patchedProduct.setTerminationDate(patchedProduct.getStartDate().plusMonths(value.getValue()));
                            case HOUR -> patchedProduct.setTerminationDate(patchedProduct.getStartDate().plusHours(value.getValue()));
                            default -> log.error("Invalid unit: {}", value.getUnitOfMeasure());
                        }
                    } else {
                        log.error("Invalid data: startDate={}, value={}, unit={}",
                                patchedProduct.getStartDate(), value.getValue(), value.getUnitOfMeasure());
                    }
                });
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.TimeRange;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.ProductCatalogCustomMapper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ProductCatalogCustomMapperImpl implements ProductCatalogCustomMapper {

    @Override
    public Set<Characteristic> toProductCharacteristic(List<ProductSpecificationCharacteristic> productSpecificationCharacteristics) throws JsonProcessingException {
        final String ROLLOVER_CHARACTERISTICS = "rollover";
        final String QUANTITY_CHARACTERISTICS = "Quantity";
        final String VALIDITY_CHARACTERISTICS = "Validity";
        final String DATE_CHARACTERISTICS = "DateCharacteristic";

        if (Objects.isNull(productSpecificationCharacteristics)) {
            return Set.of();
        }
        Set<Characteristic> productCharacteristics = new HashSet<>();
        for (ProductSpecificationCharacteristic productSpecificationCharacteristic : productSpecificationCharacteristics) {
            if (Objects.nonNull(productSpecificationCharacteristic.getProductSpecCharacteristicValue()) && !productSpecificationCharacteristic.getProductSpecCharacteristicValue().isEmpty()) {
                if (productSpecificationCharacteristic.getName().toLowerCase().contains(ROLLOVER_CHARACTERISTICS.toLowerCase())) {
                    productCharacteristics.add(mapRolloverCharacteristicToProductCharacteristic(productSpecificationCharacteristic));
                } else if ((productSpecificationCharacteristic.getName().toLowerCase().contains(QUANTITY_CHARACTERISTICS.toLowerCase()))) {
                    productCharacteristics.add(mapQuantityCharacteristicToProductCharacteristic(productSpecificationCharacteristic));
                } else if ((productSpecificationCharacteristic.getName().toLowerCase().contains(VALIDITY_CHARACTERISTICS.toLowerCase()))) {
                    productCharacteristics.add(mapValidityCharacteristicToProductCharacteristic(productSpecificationCharacteristic));
                } else if (productSpecificationCharacteristic.getType() != null && (productSpecificationCharacteristic.getType().equals(DATE_CHARACTERISTICS))) {
                    productCharacteristics.add(mapDateCharacteristicToProductCharacteristic(productSpecificationCharacteristic));
                } else {
                    productCharacteristics.add(buildDefaultProductCharacteristic(productSpecificationCharacteristic));
                }
            }
        }
        return productCharacteristics;
    }

    public Characteristic mapRolloverCharacteristicToProductCharacteristic(ProductSpecificationCharacteristic productSpecificationCharacteristic) {
        final String ROLLOVER_VALIDITY_CHARACTERISTICS = "Validity of rollovers";
        if (productSpecificationCharacteristic.getName().equalsIgnoreCase(ROLLOVER_VALIDITY_CHARACTERISTICS)) {
            return StringCharacteristic.builder()
                    .atType(StringCharacteristic.class.getSimpleName())
                    .name(productSpecificationCharacteristic.getName())
                    .value(productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getValue()
                            .concat(productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getUnitOfMeasure()))
                    .valueType(productSpecificationCharacteristic.getValueType())
                    .build();
        }
        return buildDefaultProductCharacteristic(productSpecificationCharacteristic);
    }

    @Override
    public Characteristic buildDefaultProductCharacteristic(ProductSpecificationCharacteristic productSpecificationCharacteristic) {
        return StringCharacteristic.builder()
                .atType(StringCharacteristic.class.getSimpleName())
                .name(productSpecificationCharacteristic.getName())
                .value(productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getValue())
                .valueType(productSpecificationCharacteristic.getValueType())
                .build();
    }

    private static Characteristic mapQuantityCharacteristicToProductCharacteristic(ProductSpecificationCharacteristic productSpecificationCharacteristic) {
        Map<String, Object> quantityCharacteristicValue = Map.of(
                "value", productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getValue(),
                "unitOfMeasure", productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getUnitOfMeasure()
        );
        return ObjectCharacteristic.builder()
                .atType(ObjectCharacteristic.class.getSimpleName())
                .name(productSpecificationCharacteristic.getName())
                .value(quantityCharacteristicValue)
                .valueType(productSpecificationCharacteristic.getValueType())
                .build();
    }

    private static Characteristic mapValidityCharacteristicToProductCharacteristic(ProductSpecificationCharacteristic productSpecificationCharacteristic) {
        String validFrom = null;
        String validTo = null;
        TimeRange timeRange = productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getTimeRange();

        if (timeRange != null) {
             validFrom = timeRange.getValidFrom();
             validTo = timeRange.getValidTo();
        }

        String value = productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getValue();
        return ValidityCharacteristic.builder()
                .atType(ValidityCharacteristic.class.getSimpleName())
                .name(productSpecificationCharacteristic.getName())
                .valueType(productSpecificationCharacteristic.getValueType())
                .value(ValidityValue.builder()
                        .value((Objects.nonNull(value) && !value.isEmpty()) ? Integer.valueOf(value) : null)
                        .unitOfMeasure(productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getUnitOfMeasure())
                        .validFrom(Objects.nonNull(validFrom) ? OffsetDateTime.parse(validFrom) : null)
                        .validTo(Objects.nonNull(validTo) ? OffsetDateTime.parse(validTo) : null)
                        .build())
                .build();
    }

    private static Characteristic mapDateCharacteristicToProductCharacteristic(ProductSpecificationCharacteristic productSpecificationCharacteristic) {
       String value = productSpecificationCharacteristic.getProductSpecCharacteristicValue().get(0).getValue();
        return DateCharacteristic.builder()
                .atType(DateCharacteristic.class.getSimpleName())
                .name(productSpecificationCharacteristic.getName())
                .valueType(productSpecificationCharacteristic.getValueType())
                .value((Objects.nonNull(value) && !value.isEmpty()) ? OffsetDateTime.parse(value) : null)
                .build();
    }
}

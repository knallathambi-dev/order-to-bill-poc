// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValueUse;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.AtomicProductOfferingCharacteristicsDefinedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


class AtomicProductOfferingCharacteristicsDefinedEventTest extends ProductOfferingApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(AtomicProductOfferingCharacteristicsDefinedEvent.class);

    private AtomicProductOfferingCharacteristicsDefinedEvent atomicProductOfferingCharacteristicsDefinedEvent;
    private final List<ProductSpecificationCharacteristicValueUse> characteristicList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        String productOffId = "PROD1";
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValueList=new ArrayList<>();
        ProductSpecificationCharacteristicValue prodSpecCharacteristicValue=new ProductSpecificationCharacteristicValue();
        prodSpecCharacteristicValue.value("SIM");
        productSpecificationCharacteristicValueList.add(prodSpecCharacteristicValue);
        ProductSpecificationCharacteristicValueUse productSpecCharValueUse = new ProductSpecificationCharacteristicValueUse();
        productSpecCharValueUse.maxCardinality(1).maxCardinality(10).description("desc")
                .name("usage").productSpecCharacteristicValue(productSpecificationCharacteristicValueList).
                validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
        characteristicList.add(productSpecCharValueUse);
        atomicProductOfferingCharacteristicsDefinedEvent = new AtomicProductOfferingCharacteristicsDefinedEvent
                (productOffId,characteristicList,lastUpdate);
    }

    @Test
    void productSpecUsageSelectedEventTest() {
        Assertions.assertAll("productSpecUsageSelectedEvent",
                () -> Assertions.assertEquals("PROD1", atomicProductOfferingCharacteristicsDefinedEvent.getproductOfferingId()),
                () -> Assertions.assertEquals(atomicProductOfferingCharacteristicsDefinedEvent.getProductSpecificationCharacteristicValueUse(),characteristicList));
        LOGGER.info("ProductOfferingCharacteristicsDefinedEvent: {}", atomicProductOfferingCharacteristicsDefinedEvent);
    }
}

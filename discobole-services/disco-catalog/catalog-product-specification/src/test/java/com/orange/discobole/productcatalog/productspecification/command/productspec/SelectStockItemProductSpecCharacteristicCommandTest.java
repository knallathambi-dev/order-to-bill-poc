// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectStockItemProductSpecCharacteristicCommandTest extends ProductSpecificationApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(SelectStockItemProductSpecCharacteristicCommandTest.class);

    List<ProductSpecificationCharacteristic> productSpecCharacteristics = new ArrayList<>();
    private final String aggregateId = "1";
    @BeforeEach
    public void setUp() {
        ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic().id("stockItem1");
        productSpecCharacteristics.add(productSpecificationCharacteristic);
    }

    @Test
    public void selectProductSpecCharacteristicCommandTest() {
        SelectStockItemProductSpecCharacteristicCommand selectStockItemProductSpecCharacteristicCommand = new SelectStockItemProductSpecCharacteristicCommand(aggregateId,
                productSpecCharacteristics);
        Assertions.assertAll("StockItemProductSpecCharacteristicProcessing",
                () -> assertEquals(selectStockItemProductSpecCharacteristicCommand.getProductSpecCharacteristics().get(0).getId(),
                        "stockItem1"));
        LOGGER.info("selectStockItemProductSpecCharacteristicCommand: {}", selectStockItemProductSpecCharacteristicCommand);
    }

}
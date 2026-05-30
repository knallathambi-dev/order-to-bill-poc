// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.stockitem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;

import java.time.OffsetDateTime;
import java.util.UUID;

public class StockItemAttributeValueChangeCommandTest {

    private static final Logger LOGGER = LogManager.getLogger(StockItemAttributeValueChangeCommandTest.class);

    private Event event = null;
    private StockItem stockItem = null;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("StockItemAttributeValueChange");
        event.setType("stockItem");
        event.setTimeOcurred(OffsetDateTime.now());
        stockItem = new StockItem();
        stockItem.setId(UUID.randomUUID().toString());
        stockItem.setState("active");
        stockItem.setLastUpdate(OffsetDateTime.now());
        event.setEvent(stockItem);
    }

    @Test
    void attributeValueChangeCommandTest() {
        StockItemAttributeValueChangeCommand attributeValueChangeCommand = new StockItemAttributeValueChangeCommand(
                "1",event);
        Assertions.assertEquals(event, attributeValueChangeCommand.getEvent());
        LOGGER.info("Stock Item Attribute Change Command: {}", attributeValueChangeCommand);
    }
}

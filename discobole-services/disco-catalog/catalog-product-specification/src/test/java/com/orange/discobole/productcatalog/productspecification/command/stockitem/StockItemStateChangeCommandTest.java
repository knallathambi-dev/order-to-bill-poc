// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.stockitem;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StockItemStateChangeCommandTest extends ProductSpecificationApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(StockItemStateChangeCommand.class);
    private final String eventType = "stockItemEventType";
    private final String state = "active";
    private Event event;
    private StockItem stockItem;

    @BeforeEach
    public void setUp() {
        event = new Event();
        stockItem = new StockItem();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("stockItemEventType");
        event.setEvent(stockItem);
        stockItem.setId(UUID.randomUUID().toString());
        stockItem.setState("active");
    }

    @Test
    public void stockItemStateChangeCommandTest() throws Exception {
        StockItemStateChangeCommand stateChangeCommand = new StockItemStateChangeCommand("1",event);
        Assertions.assertAll("StockItemEventProcessing",
                () -> assertEquals(stateChangeCommand.getEvent().getEventType(), eventType),
                () -> assertEquals(
                        ((StockItem) stateChangeCommand.getEvent().getEvent()).getState(),
                        state));
        LOGGER.info("Stock Item Command: {}", stateChangeCommand);
    }
}

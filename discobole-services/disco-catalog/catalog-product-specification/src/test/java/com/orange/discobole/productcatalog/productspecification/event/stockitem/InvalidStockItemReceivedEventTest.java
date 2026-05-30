// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.event.stockitem.InvalidStockItemReceivedEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Class InvalidStockItemReceivedEvent.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
class InvalidStockItemReceivedEventTest {
    private static final Logger LOGGER = LogManager.getLogger(InvalidStockItemReceivedEventTest.class);

    private InvalidStockItemReceivedEvent invalidStockItemReceivedEvent;

    @BeforeEach
    void setUp() {
        String eventId = "1";
        com.orange.discobole.productcatalog.productspecification.dto.generated.Event event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
        event.setEventId(eventId);
        invalidStockItemReceivedEvent = new InvalidStockItemReceivedEvent(event, "wrong lifecyclestatus");
    }

    @Test
    void invalidStructureReceivedEventTest() {
        Assertions.assertAll("InvalidStructureReceivedEvent",
                () -> Assertions.assertEquals("1",invalidStockItemReceivedEvent.getEvent().getEventId()),
                () -> Assertions.assertEquals( "wrong lifecyclestatus",invalidStockItemReceivedEvent.getError()));
        LOGGER.info("invalidStockItemReceivedEvent: {}", invalidStockItemReceivedEvent);
    }
}
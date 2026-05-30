// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.listener;

import static org.mockito.Mockito.doNothing;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.handler.StockItemEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.StockItemListener;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemListenerTest extends CatalogApplicationTests {

    private StockItemListener stockItemListener;

    @Mock
    private StockItemEventHandler eventHandler;

    private StockItem stockItem;

    @BeforeEach
    public void setup() {
        stockItemListener = new StockItemListener();
        ReflectionTestUtils.setField(stockItemListener, "eventHandler", eventHandler);
        stockItem = new StockItem();
        stockItem.setState(UUID.randomUUID().toString());
        stockItem.setState("active");
        stockItem.setLastUpdate(OffsetDateTime.now());
    }

    @Test
    void listenConfiguratorEventTest() {
        StockItemReplicatedEvent event = new StockItemReplicatedEvent("1",stockItem);
        stockItemListener.register(StockItemReplicatedEvent.class, eventHandler::handle);
        doNothing().when(eventHandler).handle(event);
        Assertions.assertNotNull(stockItemListener.stockItem());
    }
}
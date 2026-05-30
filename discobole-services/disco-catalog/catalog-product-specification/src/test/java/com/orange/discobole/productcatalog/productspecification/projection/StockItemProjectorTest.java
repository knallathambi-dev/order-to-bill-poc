// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.projection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.*;
import com.orange.discobole.productcatalog.productspecification.exception.BosInvalidEventException;


import java.time.OffsetDateTime;

@ActiveProfiles("test")
public class StockItemProjectorTest {

    private String stockItemId;
    private String lifeCycleStatus;
    private StockItemProjector stockItemProjector = null;
    private StockItem stockItem = null;


    @MockBean
    private LinkDiscoverers linkDiscoverers;

    private StreamBridge bridge;

    @BeforeEach
    void setUp() {
        stockItemId = "1";
        lifeCycleStatus = "active";
        stockItem = new StockItem();
        stockItem.setId(stockItemId);
        stockItem.setState(lifeCycleStatus);
        stockItem.setLastUpdate(OffsetDateTime.now());
        stockItemProjector = new StockItemProjector();
        bridge = Mockito.mock(StreamBridge.class);
        ReflectionTestUtils.setField(stockItemProjector, "bridge", bridge);
    }

    @Test
    void handleStockItemReplicatedEvent() {
        stockItemProjector.handle(new StockItemReplicatedEvent("1", stockItem));
        Mockito.verify(bridge, Mockito.times(1))
        .send(Mockito.anyString(), Mockito.any());
    }

    @Test
    void handleStockItemNotificationSentEvent() {
        stockItemProjector.handle(new StockItemNotificationSentEvent("1", stockItem));
        Mockito.verify(bridge, Mockito.times(1))
        .send(Mockito.anyString(), Mockito.any());
    }

    @Test
    void handleStockItemDuplicatedEvent() {
        Assertions.assertThrows(BosInvalidEventException.class,
                () -> stockItemProjector.handle(new StockItemDuplicatedEvent(stockItem)));
    }

    @Test
    void handleCurrentStockItemNotAlreadyExistedEvent() {
        Assertions.assertThrows(BosInvalidEventException.class,
                () -> stockItemProjector.handle(new CurrentStockItemNotAlreadyExistedEvent(stockItem)));

    }

    @Test
    void handleStockItemEarlyTimeRejectedEvent() {
        Assertions.assertThrows(BosInvalidEventException.class,
                () -> stockItemProjector.handle(new StockItemEarlyTimeRejectedEvent(stockItemId,
                        OffsetDateTime.now().minusHours(1L), OffsetDateTime.now())));
    }

    @Test
    void handleInvalidStatusReceivedStockItemEvent() {
        Assertions.assertThrows(BosInvalidEventException.class,
                () -> stockItemProjector.handle(new InvalidStatusReceivedStockItemEvent(stockItemId,
                        StockItemLifeCycleEnum.from(lifeCycleStatus), StockItemLifeCycleEnum.from("active"))));
    }

    @Test
    void handleStockItemStatusUpdatedEvent() {
        stockItemProjector.handle(new StockItemStatusUpdatedEvent("1", stockItemId,
                StockItemLifeCycleEnum.from(lifeCycleStatus), OffsetDateTime.now()));
        Mockito.verify(bridge, Mockito.times(1))
        .send(Mockito.anyString(), Mockito.any());
    }

    @Test
    void handleStockItemAttributeUpdatedEvent() {
        stockItemProjector.handle(new StockItemAttributeUpdatedEvent("1", stockItem));
        Mockito.verify(bridge, Mockito.times(1))
        .send(Mockito.anyString(), Mockito.any());
    }
}

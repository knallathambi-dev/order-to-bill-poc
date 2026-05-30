// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;




import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemExpurgedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemNotificationSentEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemStatusUpdatedEvent;




@ExtendWith(MockitoExtension.class)
public class StockitemEventHandlerSeviceTest {



   @InjectMocks
    StockitemEventHandlerService stockitemEventHandlerService;
    StockItem stockItem;



   private static String aggregateId = "1";
    private static String lifeCycle = "active";
    private static String stockItemId = "1";



   @Mock
    Publisher publisher;



   @BeforeEach
    void setup() {
        stockItem = new StockItem();
        stockItem.setId("1");
    }



   @Test
    void stockItemExpurgedEventHandlerTest() {
        StockItemExpurgedEvent event = new StockItemExpurgedEvent(aggregateId, stockItem);



       Mockito.doNothing().when(publisher).project(List.of(event));
        stockitemEventHandlerService.handle(event);
        assertEquals("1", event.getAggregrateId());
    }



   @Test
    void stockItemReplicatedEventHandlerTest() {
        StockItemReplicatedEvent event = new StockItemReplicatedEvent(aggregateId, stockItem);



       Mockito.doNothing().when(publisher).project(List.of(event));
        stockitemEventHandlerService.handle(event);
        assertEquals("1", event.getaggregateId());
    }



   @Test
    void stockItemNotificationSentEventHandlerTest() {
        StockItemNotificationSentEvent event = new StockItemNotificationSentEvent(aggregateId, stockItem);



       Mockito.doNothing().when(publisher).project(List.of(event));
        stockitemEventHandlerService.handle(event);
        assertEquals("1", event.getAggregrateId());
    }



   @Test
    void stockItemAttributeUpdatedEventHandlerTest() {
        StockItemAttributeUpdatedEvent event = new StockItemAttributeUpdatedEvent(aggregateId, stockItem);



       Mockito.doNothing().when(publisher).project(List.of(event));
        stockitemEventHandlerService.handle(event);
        assertEquals("1", event.getAggregrateId());
    }



   @Test
    void stockItemStatusUpdatedEventHandlerTest() {
        StockItemStatusUpdatedEvent event = new StockItemStatusUpdatedEvent(aggregateId, stockItemId,
                StockItemLifeCycleEnum.from(lifeCycle), OffsetDateTime.now());



       Mockito.doNothing().when(publisher).project(List.of(event));
        stockitemEventHandlerService.handle(event);
        assertEquals("1", event.getAggregrateId());
    }



}
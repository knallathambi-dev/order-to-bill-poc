// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.catalog.common.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.handler.StockItemEventHandler;
import com.orange.discobole.productcatalog.catalog.service.StockItemService;
import com.orange.discobole.productcatalog.catalog.service.StockItemTypeService;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemStatusUpdatedEvent;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemEventHandlerTest {

    private StockItemEventHandler stockItemEventHandler;

    private StockItemService stockItemService;
    
    private StockItemTypeService stockItemTypeService;

    private StockItemEvent event;

    private StockItem stockItem;
    private StockItemType stockItemType;
    private String stockItemId;
    private String stockItemTypeId;
    private String lifeCycleStatus;
    private OffsetDateTime timeNow;

    @BeforeEach
    void setUp() {
        stockItemId = "1";
        stockItemTypeId = "SIT1";
        lifeCycleStatus = "active";
        timeNow = OffsetDateTime.now();
        stockItemEventHandler = new StockItemEventHandler();
        stockItemService = Mockito.mock(StockItemService.class);
        stockItemTypeService = Mockito.mock(StockItemTypeService.class);
        ReflectionTestUtils.setField(stockItemEventHandler, "stockItemService", stockItemService);
        ReflectionTestUtils.setField(stockItemEventHandler, "stockItemTypeService", stockItemTypeService);
        stockItem = new StockItem();
        stockItem.setState(lifeCycleStatus);
        stockItem.setLastUpdate(OffsetDateTime.now());
        TimePeriod validFor = new TimePeriod().startDateTime(timeNow)
    			.endDateTime(timeNow.plusDays(10));
        stockItem.setValidFor(validFor);
        stockItemType = new StockItemType();
        stockItemType.setName("stockItemType");
        stockItemType.setId(stockItemTypeId);
        stockItem.setStockItemType(stockItemType);
        stockItem.setId(stockItemId);
        event = new StockItemEvent() {
        };
        event.aggregateName();
    }

    @Test
    void handleStockItemReplicatedEvent() {
    	Mockito.when(stockItemTypeService.fetchStockItemTypeById(stockItemId)).thenReturn(stockItemType);
    	Mockito.doNothing().when(stockItemService).saveStockItem(stockItem);
        stockItemEventHandler.handle(new StockItemReplicatedEvent("1",stockItem));
        Mockito.verify(stockItemTypeService, Mockito.times(1)).saveStockItemType(stockItemType);
        Mockito.verify(stockItemService, Mockito.times(1)).saveStockItem(stockItem);
    }

    @Test
    void handleStockItemStatusUpdatedEvent() {
        Mockito.when(stockItemService.fetchStockItemById(stockItemId)).thenReturn(stockItem);
        Mockito.doNothing().when(stockItemService).saveStockItem(stockItem);
        stockItemEventHandler.handle(new StockItemStatusUpdatedEvent("1",stockItemId,
                StockItemLifeCycleEnum.from(lifeCycleStatus), OffsetDateTime.now()));
        Mockito.verify(stockItemService, Mockito.times(1)).saveStockItem(stockItem);
    }

    @Test
    void handleStockItemAttributeUpdatedEvent() {
        Mockito.doNothing().when(stockItemService).saveStockItem(stockItem);
        stockItemEventHandler.handle(new StockItemAttributeUpdatedEvent("1",stockItem));
        Mockito.verify(stockItemService, Mockito.times(1)).saveStockItem(stockItem);
    }
    
    @Test
    void handleStockItemGreaterEndDateReplicatedEvent() {
    	Mockito.when(stockItemTypeService.fetchStockItemTypeById(stockItemTypeId)).thenReturn(stockItemType);
        TimePeriod validFor = new TimePeriod().startDateTime(timeNow)
    			.endDateTime(timeNow.plusDays(9));
    	stockItemType.setValidFor(validFor);
    	Mockito.doNothing().when(stockItemService).saveStockItem(stockItem);
    	Mockito.doNothing().when(stockItemTypeService).saveStockItemType(stockItemType);
        stockItemEventHandler.handle(new StockItemReplicatedEvent("1",stockItem));
        Mockito.verify(stockItemTypeService, Mockito.times(1)).saveStockItemType(stockItemType);
        Mockito.verify(stockItemService, Mockito.times(1)).saveStockItem(stockItem);
        Assertions.assertEquals(stockItemType.getValidFor().getEndDateTime(), stockItem.getValidFor().getEndDateTime());
    }
    
    @Test
    void handleStockItemSmallerStartDateValidForInReplicatedEvent() {
    	Mockito.when(stockItemTypeService.fetchStockItemTypeById(stockItemTypeId)).thenReturn(stockItemType);
        TimePeriod validFor = new TimePeriod().startDateTime(timeNow.plusDays(10))
    			.endDateTime(timeNow.plusDays(10));
    	stockItemType.setValidFor(validFor);
    	Mockito.doNothing().when(stockItemService).saveStockItem(stockItem);
    	Mockito.doNothing().when(stockItemTypeService).saveStockItemType(stockItemType);
        stockItemEventHandler.handle(new StockItemReplicatedEvent("1",stockItem));
        Mockito.verify(stockItemTypeService, Mockito.times(1)).saveStockItemType(stockItemType);
        Mockito.verify(stockItemService, Mockito.times(1)).saveStockItem(stockItem);
        Assertions.assertEquals(stockItemType.getValidFor().getStartDateTime(), stockItem.getValidFor().getStartDateTime());
    }
}

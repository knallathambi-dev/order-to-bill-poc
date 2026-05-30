// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.service.StockItemService;
import com.orange.discobole.productcatalog.catalog.service.StockItemTypeService;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemStatusUpdatedEvent;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class StockItemEventHandler {
    @Resource
    private StockItemService stockItemService;
    
    @Resource
    private StockItemTypeService stockItemTypeService;

    /**
     * Handle the {@link StockItemReplicatedEvent}.
     *
     * @param event the event
     */
    public void handle(final StockItemReplicatedEvent event) {
        StockItem stockItem = event.getStockItem();
        StockItemType stockItemTypeFromEvent = event.getStockItem().getStockItemType();
        StockItemType stockItemTypeFromQuery = stockItemTypeService.fetchStockItemTypeById(stockItemTypeFromEvent.getId());
        if(null == stockItemTypeFromQuery || (null == stockItemTypeFromQuery.getValidFor() && null != stockItem.getValidFor())) {
        	stockItemTypeFromEvent.setValidFor(stockItem.getValidFor());
        	stockItemTypeService.saveStockItemType(stockItemTypeFromEvent);
        }else if(null != stockItem.getValidFor() && null != stockItemTypeFromQuery.getValidFor()){
			if (null != stockItem.getValidFor().getStartDateTime()
					&& (null == stockItemTypeFromQuery.getValidFor().getStartDateTime() || stockItem.getValidFor()
							.getStartDateTime().isBefore(stockItemTypeFromQuery.getValidFor().getStartDateTime()))) {
				stockItemTypeFromQuery.getValidFor().setStartDateTime(stockItem.getValidFor().getStartDateTime());
				stockItemTypeService.saveStockItemType(stockItemTypeFromQuery);
			}
			if (null != stockItem.getValidFor().getEndDateTime()
					&& (null == stockItemTypeFromQuery.getValidFor().getEndDateTime() || stockItem.getValidFor()
							.getEndDateTime().isAfter(stockItemTypeFromQuery.getValidFor().getEndDateTime()))) {
				stockItemTypeFromQuery.getValidFor().setEndDateTime(stockItem.getValidFor().getEndDateTime());
				stockItemTypeService.saveStockItemType(stockItemTypeFromQuery);
			}
        }   
        stockItemService.saveStockItem(stockItem);   
    }

    /**
     * Handle the {@link StockItemStatusUpdatedEvent}.
     *
     * @param event the event
     */
    public void handle(final StockItemStatusUpdatedEvent event) {
        StockItem stockItem = stockItemService.fetchStockItemById(event.getStockItemId());
        stockItem.setState(event.getStatus().toString());
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setStartDateTime(event.getStockItemTimeOccurred());
        stockItem.setValidFor(timePeriod);
        stockItemService.saveStockItem(stockItem);
    }

    /**
     * Handle the {@link StockItemAttributeUpdatedEvent}.
     *
     * @param event the event
     */
    public void handle(StockItemAttributeUpdatedEvent event) {
        StockItem stockItem = event.getStockItem();
        stockItemService.saveStockItem(stockItem);
    }
}

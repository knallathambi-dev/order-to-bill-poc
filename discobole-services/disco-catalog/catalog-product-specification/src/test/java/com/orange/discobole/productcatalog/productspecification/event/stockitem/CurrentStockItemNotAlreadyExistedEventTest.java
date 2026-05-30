// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.CurrentServiceSpecNotAlreadyExistedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.CurrentStockItemNotAlreadyExistedEvent;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The CurrentStockItemNotAlreadyExistedEvent consist event, if stock item is not already present in the system.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
class CurrentStockItemNotAlreadyExistedEventTest {

    private StockItem stockItem;

    @BeforeEach
    void setUp(){
        stockItem = new StockItem();
        stockItem.setId("1");
        stockItem.setState("active");
        stockItem.setLastUpdate(OffsetDateTime.now());
    }

    @Test
    void serviceSpecNotAlreadyExistTest(){
        CurrentStockItemNotAlreadyExistedEvent currentStockItemNotAlreadyExistedEvent = new CurrentStockItemNotAlreadyExistedEvent(
                stockItem);
        Assertions.assertEquals(stockItem, currentStockItemNotAlreadyExistedEvent.getStockItem());
    }
}
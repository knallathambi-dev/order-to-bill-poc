// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemServiceImplTest extends CatalogApplicationTests {

    private String stockItemId;
    private String lifecycleStatus;
    private String name;
    private StockItem stockItem;

    @InjectMocks
    private StockItemServiceImpl stockItemService;

    @Mock
    MongoTemplate mongoTemplate;

    private Map<String, Object> requestParams = new HashMap<>();

    @BeforeEach
    void setUp() {
        stockItemId = "1";
        lifecycleStatus = "active";
        name = "BOS Mobile line";
        stockItem = new StockItem();
        stockItem.setId(stockItemId);
        stockItem.setState(lifecycleStatus);
        stockItem.setName(name);
        requestParams.put("name", name);
        stockItemService.saveStockItem(stockItem);
    }

    @Test
    void findStockItemTest() throws UnsupportedEncodingException {
        List<StockItem> stockItems = new ArrayList<>();
        stockItems.add(stockItem);
        requestParams.put("lifecycleStatus", "launched");
        Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

        Query query = new Query();

        for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
            query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
        }
        when(mongoTemplate.find(query, StockItem.class)).thenReturn(stockItems);
        List<StockItem> cfsSpecs = stockItemService.fetchStockItem(requestParams);
        Assertions.assertThat(cfsSpecs).hasSize(1);

    }

    @Test
    void findStockItemByIdTest() {
        when(mongoTemplate.findById("1", StockItem.class)).thenReturn(stockItem);
        StockItem stockItm = stockItemService.fetchStockItemById(stockItemId);
        Assertions.assertThat(stockItm.getId()).isEqualTo(stockItemId);
        stockItemId = "4";
        stockItm = stockItemService.fetchStockItemById(stockItemId);
        Assertions.assertThat(stockItm).isNull();
    }

    @Test
    void fetchStockItemByIdAndFieldListTest() {
        List<String> fieldList = List.of("validFor");
        OffsetDateTime startDateTime = OffsetDateTime.now();
        StockItem stockItm = new StockItem();
        stockItm.setId("stockItem1");
        stockItm.setValidFor(new TimePeriod().startDateTime(startDateTime).endDateTime(startDateTime.plusDays(7)));
        when(mongoTemplate.findOne(any(Query.class), eq(StockItem.class))).thenReturn(stockItm);
        StockItem returnedServiceSpec = stockItemService
                .fetchStockItemById("stockItem1", fieldList);
        assertEquals(stockItm.getId(),returnedServiceSpec.getId());
        assertNotNull(returnedServiceSpec.getValidFor());
    }

    @Test
    void updateStockItemTest() {
        StockItem stockItm = new StockItem();
        stockItemId = "1";
        stockItm.setId(stockItemId);
        lifecycleStatus = "launched";
        stockItm.setState(lifecycleStatus);
        stockItemService.saveStockItem(stockItm);
        when(mongoTemplate.findById("1", StockItem.class)).thenReturn(stockItm);
        StockItem cfsSpecs = stockItemService.fetchStockItemById(stockItemId);
        Assertions.assertThat(cfsSpecs.getState()).isEqualTo("launched");
    }
}

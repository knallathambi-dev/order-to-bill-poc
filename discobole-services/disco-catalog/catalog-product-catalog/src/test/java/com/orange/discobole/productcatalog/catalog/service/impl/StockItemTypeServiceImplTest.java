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
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

/**
 * @author Pankaj Gautam
 * @since 1.0
 */
class StockItemTypeServiceImplTest extends CatalogApplicationTests {

    private String stockItemId;
    private String stockItemTypeId;
    private String lifecycleStatus;
    private String name;
    private StockItem stockItem;
    private StockItemType stockItemType;

    @InjectMocks
    private StockItemTypeServiceImpl stockItemTypeService;

    @Mock
    MongoTemplate mongoTemplate;

    private Map<String, Object> requestParams = new HashMap<>();

    @BeforeEach
    void setUp() {
        stockItemId = "1";
        stockItemTypeId = "SIT1";
        lifecycleStatus = "active";
        name = "BOS Mobile line";
        stockItem = new StockItem();
        stockItem.setId(stockItemId);
        stockItem.setState(lifecycleStatus);
        stockItem.setName(name);
        stockItemType = new StockItemType();
        stockItemType.setName("stockItemType");
        stockItemType.setId(stockItemTypeId);
        stockItem.setStockItemType(stockItemType);
        requestParams.put("name", name);
        stockItemTypeService.saveStockItemType(stockItemType);
    }

    @Test
    void findStockItemTypeTest() throws UnsupportedEncodingException {
        List<StockItemType> stockItemTypes = new ArrayList<>();
        stockItemTypes.add(stockItemType);
        requestParams.put("lifecycleStatus", "launched");
        Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

        Query query = new Query();

        for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
            query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
        }
        when(mongoTemplate.find(query, StockItemType.class)).thenReturn(stockItemTypes);
        List<StockItemType>  fetchedStockItemTypes = stockItemTypeService.fetchStockItemType(requestParams);
        Assertions.assertThat(fetchedStockItemTypes).hasSize(1);

    }

    @Test
    void findStockItemByIdTest() {
        when(mongoTemplate.findById("SIT1", StockItemType.class)).thenReturn(stockItemType);
        StockItemType stockItmType = stockItemTypeService.fetchStockItemTypeById(stockItemTypeId);
        Assertions.assertThat(stockItmType.getId()).isEqualTo(stockItemTypeId);
        stockItemTypeId = "SIT4";
        stockItmType = stockItemTypeService.fetchStockItemTypeById(stockItemTypeId);
        Assertions.assertThat(stockItmType).isNull();
    }

    @Test
    void fetchStockItemByIdAndFieldListTest() {
        List<String> fieldList = List.of("validFor");
        OffsetDateTime startDateTime = OffsetDateTime.now();
        StockItemType stockItmType = new StockItemType();
        stockItmType.setId("SIT1");
        stockItmType.setValidFor(new TimePeriod().startDateTime(startDateTime).endDateTime(startDateTime.plusDays(7)));
        when(mongoTemplate.findOne(any(Query.class), eq(StockItemType.class))).thenReturn(stockItmType);
        StockItemType returnedStockItemType = stockItemTypeService
                .fetchStockItemTypeById("stockItem1", fieldList);
        assertEquals(stockItmType.getId(),returnedStockItemType.getId());
        assertNotNull(returnedStockItemType.getValidFor());
    }

    @Test
    void updateStockItemTest() {
    	StockItemType stockItmType = new StockItemType();
    	stockItemTypeId = "SIT1";
        stockItmType.setId(stockItemTypeId);
        lifecycleStatus = "launched";
        stockItmType.setState(lifecycleStatus);
        stockItemTypeService.saveStockItemType(stockItmType);
        when(mongoTemplate.findById("SIT1", StockItemType.class)).thenReturn(stockItmType);
        StockItemType fetchedStockItemType = stockItemTypeService.fetchStockItemTypeById(stockItemTypeId);
        Assertions.assertThat(fetchedStockItemType.getState()).isEqualTo("launched");
    }
}

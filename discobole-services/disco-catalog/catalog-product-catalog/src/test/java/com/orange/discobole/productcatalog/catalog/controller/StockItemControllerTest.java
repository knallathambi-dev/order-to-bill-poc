// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.controller.StockItemController;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.service.StockItemService;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * StockItemControllerTest tests the endpoint to fetch stock item
 * based on different params or specifically by id.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
@WebMvcTest(controllers = StockItemController.class)
@WithMockUser(username = "BOS", roles = { "CatalogAdministrator" })
class StockItemControllerTest extends CatalogApplicationTests {

    private static final String BASE_URL = "/serviceCatalogManagement/v1";
    private static final String SEPARATOR = "/";

    @MockBean
    private StockItemService stockItemService;

    @Resource
    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

//    @Test
//    void findStockItemTest() throws Exception {
//        List<StockItem> stockItems = new ArrayList<>();
//        when(stockItemService.fetchStockItem(captor.capture(),any(), any())).thenReturn(stockItems);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItem"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//        Assertions.assertEquals(10, captor.getValue().size());
//    }

//    @Test
//    void findStockItemNullTest() throws Exception {
//        when(stockItemService.fetchStockItem(captor.capture(),any(), any())).thenReturn(null);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItem"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//        Assertions.assertEquals(10, captor.getValue().size());
//    }

    @Test
    void findStockItemByIdTest() throws Exception {
        StockItem stockItem = new StockItem();
        stockItem.setId("stockItem1");
        when(stockItemService.fetchStockItemById("stockItem1")).thenReturn(stockItem);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItem" + SEPARATOR + "stockItem1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void findStockItemByIdNullTest() throws Exception {
        StockItem stockItem = null;
        when(stockItemService.fetchStockItemById("stockItem1")).thenReturn(stockItem);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItem" + SEPARATOR + "stockItem1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void findStockItemByIdUnauthorizedTest() throws Exception {
        StockItem stockItem = new StockItem();
        stockItem.setId("stockItem1");
        when(stockItemService.fetchStockItemById("stockItem1")).thenReturn(stockItem);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItem" + SEPARATOR + "stockItem1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    
    @Test
    void findStockItemByStockItemTypeIdTest() throws Exception {
        StockItemType stockItemtype = new StockItemType();
        List<StockItem> stockItems = new ArrayList<>();
        stockItemtype.setId("SIT1");
        when(stockItemService.fetchStockItemByStockItemTypeId("SIT1")).thenReturn(stockItems);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItem" + SEPARATOR + "stockItemType" + SEPARATOR + "SIT1" ))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
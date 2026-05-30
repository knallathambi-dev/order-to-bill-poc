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
import com.orange.discobole.productcatalog.catalog.controller.StockItemTypeController;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.service.StockItemTypeService;

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
 * StockItemTypeControllerTest tests the endpoint to fetch stock item
 * based on different params or specifically by id.
 *
 * @author Pankaj Gautam
 * @since 1.0
 */
@WebMvcTest(controllers = StockItemTypeController.class)
@WithMockUser(username = "BOS", roles = { "CatalogAdministrator" })
class StockItemTypeControllerTest extends CatalogApplicationTests {

    private static final String BASE_URL = "/serviceCatalogManagement/v1";
    private static final String SEPARATOR = "/";

    @MockBean
    private StockItemTypeService stockItemTypeService;

    @Resource
    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

//    @Test
//    void findStockItemTest() throws Exception {
//        List<StockItemType> stockItemTypes = new ArrayList<>();
//        when(stockItemTypeService.fetchStockItemType(captor.capture(),any(), any())).thenReturn(stockItemTypes);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItemType"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//        Assertions.assertEquals(7, captor.getValue().size());
//    }

//    @Test
//    void findStockItemNullTest() throws Exception {
//        when(stockItemTypeService.fetchStockItemType(captor.capture(),any(), any())).thenReturn(null);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItemType"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//        Assertions.assertEquals(7, captor.getValue().size());
//    }

    @Test
    void findStockItemByIdTest() throws Exception {
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("SIT1");
        when(stockItemTypeService.fetchStockItemTypeById("SIT1")).thenReturn(stockItemType);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItemType" + SEPARATOR + "SIT1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void findStockItemByIdNullTest() throws Exception {
    	StockItemType stockItemType  = null;
        when(stockItemTypeService.fetchStockItemTypeById("SIT1")).thenReturn(stockItemType);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItemType" + SEPARATOR + "SIT1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void findStockItemByIdUnauthorizedTest() throws Exception {
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("SIT1");
        when(stockItemTypeService.fetchStockItemTypeById("SIT1")).thenReturn(stockItemType);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "stockItemType" + SEPARATOR + "SIT1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    
}
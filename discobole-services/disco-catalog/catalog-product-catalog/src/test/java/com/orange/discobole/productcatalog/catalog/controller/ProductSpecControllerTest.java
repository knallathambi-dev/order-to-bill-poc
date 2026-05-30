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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.service.ProductSpecService;

import jakarta.annotation.Resource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = ProductSpecController.class)
@WithMockUser(username = "BOS", roles = {"CatalogAdministrator"})
class ProductSpecControllerTest extends CatalogApplicationTests {

    @MockBean
    ProductSpecService productSpecService;

    @Resource
    protected MockMvc mvc;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    private static final String BASE_URL = "/productCatalogManagement/v1";
    private static final String SEPARATOR = "/";

    @Test
    void findProductSpecificationTest() throws Exception {
        List<ProductSpecification> productSpecifications = new ArrayList<>();
        ProductSpecification productSpecification = new ProductSpecification();
        productSpecification.setLifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
        productSpecifications.add(productSpecification);
        Map<String, Object> map = new HashMap<>();
        map.put("data", List.of(productSpecification));
        map.put("count", 1L);
        given(productSpecService.fetchProductSpecificationWithCount(captor.capture(), any(), any(), any()))
                .willReturn(map);
        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productSpecification").param("state", "active"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(23, captor.getValue().size());
    }

//    @Test
//    void findProductSpecificationNullTest() throws Exception {
//        given(productSpecService.fetchProductSpecification(captor.capture(), any(), any(), any())).willReturn(
//                List.of());
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productSpecification").param("state", "active"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//        assertEquals(23, captor.getValue().size());
//    }

    @Test
    void findProductSpecificationByIdTest() throws Exception {
        ProductSpecification productSpecification = new ProductSpecification().id("id123");
        given(productSpecService.fetchProductSpecificationById("id123")).willReturn(productSpecification);
        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productSpecification" + SEPARATOR + "id123"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void findProductSpecificationByInvalidIdTest() throws Exception {
        given(productSpecService.fetchProductSpecificationById("id123")).willReturn(null);
        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productSpecification" + SEPARATOR + "id124"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void findProductSpecificationByIdUnauthorizedTest() throws Exception {
        ProductSpecification productSpecification = new ProductSpecification().id("id123");
        given(productSpecService.fetchProductSpecificationById("id123")).willReturn(productSpecification);
        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productSpecification" + SEPARATOR + "id123"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

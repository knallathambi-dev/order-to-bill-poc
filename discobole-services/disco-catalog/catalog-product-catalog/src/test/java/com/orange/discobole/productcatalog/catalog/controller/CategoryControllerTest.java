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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The CategoryControllerTest represents test class for Category Controller
 *
 * @author Varshika Choudhary
 */

@WebMvcTest(controllers = CategoryController.class)
@WithMockUser
class CategoryControllerTest extends CatalogApplicationTests {

    private static final String BASE_URL = "/productCatalogManagement/v1";
    private static final String SEPARATOR = "/";

    @MockBean
    private CategoryService categoryService;
    
    @MockBean
    private CategoryEntityRelationshipService categoryEntityService;

    @MockBean
    private ProductOfferingService productOfferingService;
    @Resource
    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

//    @Test
//    void findCategoriesByLifeCycleStatusTest() throws Exception {
//        List<Category> categories = new ArrayList<>();
//        given(categoryService.fetchCategory(captor.capture(),any(), any(), any())).willReturn(categories);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "category").param("lifecycleStatus", "active"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//        Assertions.assertEquals(12, captor.getValue().size());
//    }

//    @Test
//    void findCategoriesNullTest() throws Exception {
//        given(categoryService.fetchCategory(captor.capture(),any(), any(), any())).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "category").param("lifecycleStatus", "active"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//        Assertions.assertEquals(12, captor.getValue().size());
//    }

    @Test
    void findCategoriesByIdTest() throws Exception {
        Category category = new Category();
        category.id("category1");
        CategoryEntityRelationship entity=new CategoryEntityRelationship();
        entity.id("category1");
        Set<ProductOfferingRef> productOfferings=new HashSet<ProductOfferingRef>();
        productOfferings.add(new ProductOfferingRef().id("p01"));
        entity.productOfferings(productOfferings);
        given(categoryService.fetchCategoryById("category1")).willReturn(category);
        given(categoryEntityService.fetchEntityById("category1")).willReturn(entity);
       
        given(productOfferingService.fetchProductOfferingById("p01")).willReturn(new ProductOffering().id("p01").name("pname"));
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "category" + SEPARATOR + "category1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void findCategoryByIdNullTest() throws Exception {
        given(categoryService.fetchCategoryById("category1")).willReturn(null);
        given(categoryEntityService.fetchEntityById("category1")).willReturn(null);
        mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "category" + SEPARATOR + "category1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

//    @Test
//    void findCategoriesByTypeTest() throws Exception {
//        List<Category> categories = new ArrayList<>();
//        given(categoryService.fetchCategory(captor.capture(),any(), any(), any())).willReturn(categories);
//        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "category").param("type", "ProductOfferingCategory"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//        Assertions.assertEquals(12, captor.getValue().size());
//    }
}

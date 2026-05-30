// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CategoryServiceImplTest represents test class for Category Service Impl
 *
 * @author Varshika Choudhary
 */

class CategoryServiceImplTest extends CatalogApplicationTests {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    MongoTemplate mongoTemplate;

    Query query;
    Category category;

    @BeforeEach
    public void init() {
        category = new Category();
        ReflectionTestUtils.setField(categoryService, "mongoTemplate", mongoTemplate);
    }

    @Test
    void saveProductOfferingTest() {
        categoryService.saveCategory(category);
        Mockito.verify(mongoTemplate, Mockito.times(1)).save(category);
    }

    @Test
    void fetchCategoryByIdTest() {
        category.id("category1");
        when(mongoTemplate.findById("category1", Category.class)).thenReturn(category);
        Category returnedCategory = categoryService.fetchCategoryById("category1");
        assertEquals(category.getId(),(returnedCategory.getId()));
    }

    @Test
    void fetchCategoryLifecycleStatusFilterTest() throws UnsupportedEncodingException {
        Long offset = 0L;
        Long limit = 1L;
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("lifecycleStatus", "active");

        List<Category> categories = new ArrayList<>();
        Category categoryLocal = new Category();
        categoryLocal.id("category1");
        categoryLocal.lifecycleStatus("active");
        categories.add(categoryLocal);

        AggregationResults aggregationResults = mock(AggregationResults.class);
        when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(Category.class)))
                .thenReturn(aggregationResults);
        when(aggregationResults.getMappedResults()).thenReturn(categories);

        List<Category> returnedCategoryList = categoryService.fetchCategory(requestParams, offset, limit, null);

        assertNotNull(returnedCategoryList);
        assertEquals(1, returnedCategoryList.size());
        assertEquals("category1", returnedCategoryList.get(0).getId());
    }

    @Test
    void fetchCategoryTypeFilterTest() throws UnsupportedEncodingException {
        Long offset = 0L;
        Long limit = 1L;
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("type", "ProductOfferingCategory");

        List<Category> categories = new ArrayList<>();
        Category categoryLocal = new Category();
        categoryLocal.id("category1");
        categoryLocal.type("ProductOfferingCategory");
        categories.add(categoryLocal);

        AggregationResults aggregationResults = mock(AggregationResults.class);
        when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(Category.class)))
                .thenReturn(aggregationResults);
        when(aggregationResults.getMappedResults()).thenReturn(categories);

        List<Category> returnedCategoryList = categoryService.fetchCategory(requestParams, offset, limit, null);

        assertNotNull(returnedCategoryList);
        assertEquals(1, returnedCategoryList.size());
        assertEquals("category1", returnedCategoryList.get(0).getId());
    }
    @Test
    void removeProductOfferingTest() {
        categoryService.removeCategory(category.getId());
		verify(mongoTemplate, times(1)).remove(any(Query.class), eq(Category.class));
    }
}

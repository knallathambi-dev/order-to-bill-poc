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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingDto;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.service.impl.ProductOfferingServiceImpl;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

class ProductOfferingServiceImplTest extends CatalogApplicationTests {

	@InjectMocks
	ProductOfferingServiceImpl productOfferingService;

	@Mock
	MongoTemplate mongoTemplate;

	ProductOffering productOffering;

	@BeforeEach
	void init() {
		productOffering = new ProductOffering();
		ReflectionTestUtils.setField(productOfferingService, "mongoTemplate", mongoTemplate);
	}

	@Test
	void saveProductOfferingTest() {
		productOffering.setLifecycleStatus(ProductOfferingLifecycle.INSTUDY);
		productOfferingService.saveProductOffering(productOffering);
		ProductOfferingDto savePO = new ProductOfferingDto().lifecycleStatus(ProductOfferingLifecycle.INSTUDY);
		Mockito.verify(mongoTemplate, Mockito.times(1)).save(savePO);
	}

	@Test
	void fetchProductOfferingTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields="description,lifecycleStatus";
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("param1", "value1");
		List<ProductOffering> productOfferingList = new ArrayList<>();
		ProductOffering productOfferingLocal = new ProductOffering();
		productOfferingLocal.setId("offerId1");
		productOfferingList.add(productOfferingLocal);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductOffering.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productOfferingList);
		List<ProductOffering> returnedProductOfferingList = productOfferingService.fetchProductOffering(requestParams,
				offset, limit,fields);
		assertNotNull(returnedProductOfferingList);
		assertEquals(1, returnedProductOfferingList.size());
		assertEquals("offerId1", returnedProductOfferingList.get(0).getId());
	}

	@Test
	void fetchSortedProductOfferingTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields="description,lifecycleStatus";
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("name", "brand,-brand,+brand");
		List<ProductOffering> productOfferingList = new ArrayList<>();
		ProductOffering productOfferingLocal = new ProductOffering();
		productOfferingLocal.setId("offerId1");
		productOfferingList.add(productOfferingLocal);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductOffering.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productOfferingList);
		List<ProductOffering> returnedProductOfferingList = productOfferingService.fetchProductOffering(requestParams,
				offset, limit,fields);
		assertNotNull(returnedProductOfferingList);
		assertEquals(1, returnedProductOfferingList.size());
		assertEquals("offerId1", returnedProductOfferingList.get(0).getId());
	}

	@Test
	void sortedProductOfferingInvalidKeyTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields="description,lifecycleStatus";
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("param1", "value1");
		List<ProductOffering> productOfferingList = new ArrayList<>();
		ProductOffering productOfferingLocal = new ProductOffering();
		productOfferingLocal.setId("offerId1");
		productOfferingList.add(productOfferingLocal);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductOffering.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productOfferingList);
		assertFalse(QueryParamUtil.isValid(ProductOffering.class, "param1"));
		List<ProductOffering> returnedProductOfferingList = productOfferingService.fetchProductOffering(requestParams,
				offset, limit,fields);
		assertNotNull(returnedProductOfferingList);
	}

	@Test
	void fetchProductOfferingByIdTest() {
		productOffering.setId("product_id123");
		when(mongoTemplate.findById("product_id123", ProductOffering.class)).thenReturn(productOffering);
		ProductOffering returnedProductOffering = productOfferingService.fetchProductOfferingById("product_id123");
		assertEquals(productOffering.getId(), returnedProductOffering.getId());
	}

	@Test
	void fetchProductOfferingByIdAndFieldListTest() {
		List<String> fieldList = List.of("category", "validFor");
		OffsetDateTime startDateTime = OffsetDateTime.now();
		productOffering.id("productOff_id123").addCategoryItem(new CategoryRef().id("category1"))
				.validFor(new TimePeriod().startDateTime(startDateTime).endDateTime(startDateTime.plusDays(7)));
		when(mongoTemplate.findOne(any(Query.class), eq(ProductOffering.class))).thenReturn(productOffering);
		ProductOffering returnedProductOffering = productOfferingService.fetchProductOfferingById("productOff_id123",
				fieldList);
		assertEquals(productOffering.getId(), returnedProductOffering.getId());
		assertNotNull(returnedProductOffering.getCategory());
		assertNotNull(returnedProductOffering.getValidFor());
	}

	@Test
	void removeProductOfferingTest() {
		productOfferingService.removeProductOffering("productOffId");
		verify(mongoTemplate, times(1)).remove(any(Query.class), eq(ProductOffering.class));
	}

}

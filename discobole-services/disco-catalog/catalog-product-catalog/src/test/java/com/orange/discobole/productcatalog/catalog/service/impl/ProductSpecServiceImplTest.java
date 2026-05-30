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
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;
import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ServiceSpecificationRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

class ProductSpecServiceImplTest extends CatalogApplicationTests {

	private String productSpecId;
	private ProductSpecificationLifecycle lifecycleStatus;
	private String name;
	private ProductSpecification productSpecification;

	@InjectMocks
	private ProductSpecServiceImpl productSpecService;

	@Mock
	MongoTemplate mongoTemplate;
	@BeforeEach
	void setUp() {
		productSpecId = "1";
		name = "BOS Mobile line";
		lifecycleStatus = ProductSpecificationLifecycle.ACTIVE;
		productSpecification = new ProductSpecification();
		productSpecification.setId(productSpecId);
		productSpecification.setLifecycleStatus(lifecycleStatus);
		productSpecification.setName(name);

	}

	@Test
	void saveProductSpecificationTest() {
		productSpecService.saveProductSpecification(productSpecification);
		Mockito.verify(mongoTemplate, times(1)).save(productSpecification);
	}

	@Test
	void updateProductSpecificationTest() {
		String prodSpecId = UUID.randomUUID().toString();
		Update update = mock(Update.class);
		UpdateResult updateResult = mock(UpdateResult.class);
		Mockito.when(updateResult.getModifiedCount()).thenReturn(1L);
		Mockito.when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(ProductSpecification.class)))
				.thenReturn(updateResult);
		productSpecService.updateProductSpecification(prodSpecId, update);
		Mockito.verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(ProductSpecification.class));
		

	}

	@Test
	void fetchSortedProductSpecificationWithFilterCriteriaTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields=null;
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("name", "brand,-brand,+brand");
		List<ProductSpecification> productSpecifications = new ArrayList<>();
		ProductSpecification productSpec = new ProductSpecification();
		productSpec.setId("productSpecification1");
		productSpecifications.add(productSpec);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductSpecification.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productSpecifications);
		List<ProductSpecification> returnedProductSpecificationList = productSpecService
				.fetchProductSpecification(requestParams, offset, limit,fields);
		assertNotNull(returnedProductSpecificationList);
		assertEquals(1, returnedProductSpecificationList.size());
		assertEquals("productSpecification1", returnedProductSpecificationList.get(0).getId());
	}

	@Test
	void fetchProductSpecificationWithFilterCriteriaTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields=null;
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("param1", "value1");
		List<ProductSpecification> productSpecifications = new ArrayList<>();
		ProductSpecification productSpec = new ProductSpecification();
		productSpec.setId("productSpecification1");
		productSpecifications.add(productSpec);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductSpecification.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productSpecifications);
		List<ProductSpecification> returnedProductSpecificationList = productSpecService
				.fetchProductSpecification(requestParams, offset, limit,fields);
		assertNotNull(returnedProductSpecificationList);
		assertEquals(1, returnedProductSpecificationList.size());
		assertEquals("productSpecification1", returnedProductSpecificationList.get(0).getId());
	}

	@Test
	void fetchSortedProductSpecificationTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields=null;
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("name", "brand,-brand,+brand");
		List<ProductSpecification> productSpecifications = new ArrayList<>();
		ProductSpecification prodSpec = new ProductSpecification();
		prodSpec.setId("productSpecification1");
		productSpecifications.add(prodSpec);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductSpecification.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productSpecifications);
		List<ProductSpecification> returnedProductSpecificationList = productSpecService
				.fetchProductSpecification(requestParams, offset, limit,fields);
		assertNotNull(returnedProductSpecificationList);
		assertEquals(1, returnedProductSpecificationList.size());
		assertEquals("productSpecification1", returnedProductSpecificationList.get(0).getId());
	}

	@Test
	void sortedProductSpecInvalidKeyTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		String fields=null;
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("param1", "value1");
		List<ProductSpecification> productSpecifications = new ArrayList<>();
		ProductSpecification productSpec = new ProductSpecification();
		productSpec.setId("productSpecification1");
		productSpecifications.add(productSpec);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductSpecification.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productSpecifications);
		assertFalse(QueryParamUtil.isValid(ProductSpecification.class, "param1"));
		List<ProductSpecification> returnedProductSpecificationList = productSpecService
				.fetchProductSpecification(requestParams, offset, limit,fields);
		assertNotNull(returnedProductSpecificationList);
	}

	@Test
	void fetchProductSpecificationByIdTest() {
		productSpecification.setId("product_id123");
		when(mongoTemplate.findById("product_id123", ProductSpecification.class)).thenReturn(productSpecification);
		ProductSpecification returnedProductSpecification = productSpecService
				.fetchProductSpecificationById("product_id123");
		assertEquals(productSpecification.getId(),returnedProductSpecification.getId());
	}

	@Test
	void fetchProductSpecificationByIdAndFieldListTest() {
		List<String> fieldList = List.of("serviceSpecification", "validFor");
		OffsetDateTime startDateTime = OffsetDateTime.now();
		ProductSpecification productSpec = new ProductSpecification();
		productSpec.id("product_id123")
				.serviceSpecification(List.of(new ServiceSpecificationRef().id("cfs1").name("mobilePass").type("CFS")))
				.validFor(new TimePeriod().startDateTime(startDateTime).endDateTime(startDateTime.plusDays(7)));
		when(mongoTemplate.findOne(any(Query.class), eq(ProductSpecification.class))).thenReturn(productSpec);
		ProductSpecification returnedProductSpecification = productSpecService
				.fetchProductSpecificationById("product_id123", fieldList);
		assertEquals(productSpec.getId(),returnedProductSpecification.getId());
		assertNotNull(returnedProductSpecification.getServiceSpecification());
		assertNotNull(returnedProductSpecification.getValidFor());
	}

	@Test
	void removeProductSpecificationTest() {
		productSpecService.removeProductSpecification(productSpecId);
		verify(mongoTemplate, times(1)).remove(any(Query.class), eq(ProductSpecification.class));
	}
}
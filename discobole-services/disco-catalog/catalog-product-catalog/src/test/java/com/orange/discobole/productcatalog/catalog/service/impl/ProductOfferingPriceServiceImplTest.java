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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPriceDto;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.service.impl.ProductOfferingPriceServiceImpl;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductOfferingPriceServiceImplTest extends CatalogApplicationTests {

	@InjectMocks
	ProductOfferingPriceServiceImpl productOfferingPriceService;

	@Mock
	MongoTemplate mongoTemplate;

	ProductOfferingPrice productOfferingPrice;

	@Mock
	private MongoConverter mongoConverter;



	@BeforeEach
	void setUp() {
		productOfferingPrice = new ProductOfferingPrice();
		ReflectionTestUtils.setField(productOfferingPriceService, "mongoTemplate", mongoTemplate);
	}

	@Test
	void saveProductOfferingPriceTest() {
		ProductOfferingPrice productOfferingPrice = new ProductOfferingPrice();
		productOfferingPrice.setAggregateId(productOfferingPrice.getId());
		productOfferingPriceService.saveProductOfferingPrice(productOfferingPrice);
		Mockito.verify(mongoTemplate, Mockito.times(1)).save(productOfferingPrice);
	}

	@Test
	void getProductOfferingPricesTest() throws UnsupportedEncodingException {
		Long offset = 0L;
		Long limit = 1L;
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("param1", "value1");
		List<ProductOfferingPrice> productOfferingPriceList = new ArrayList<>();
		ProductOfferingPrice offer1 = new ProductOfferingPrice();
		offer1.setId("offerId1");
		productOfferingPriceList.add(offer1);

		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(ProductOfferingPrice.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(productOfferingPriceList);

		List<ProductOfferingPrice> returnedProductOfferingPriceList = productOfferingPriceService
				.getProductOfferingPrices(requestParams, offset, limit, null);
		assertNotNull(returnedProductOfferingPriceList);
		assertEquals(1, returnedProductOfferingPriceList.size());
		assertEquals("offerId1", returnedProductOfferingPriceList.get(0).getId());
	}

	// @Test
	// void getProductOfferingPriceByIdTest() {
	// 	productOfferingPrice.setId("product_id123");
	// 	when(mongoTemplate.findById("product_id123", ProductOfferingPrice.class)).thenReturn(productOfferingPrice);
	// 	ProductOfferingPrice returnedProductOfferingPrice = productOfferingPriceService
	// 			.getProductOfferingPriceById("product_id123");
	// 	assertEquals(productOfferingPrice.getId(), returnedProductOfferingPrice.getId());
	// }

	@Test
	void fetchProductSpecificationByIdAndFieldListTest() {

		List<String> fieldList = List.of("validFor");
		OffsetDateTime startDateTime = OffsetDateTime.now();
		ProductOfferingPrice productOfferingPriceLocal = new ProductOfferingPrice();
		productOfferingPriceLocal.id("product_id123")
				.validFor(new TimePeriod().startDateTime(startDateTime).endDateTime(startDateTime.plusDays(7)));
//		when(mongoTemplate.findOne(any(Query.class), eq(ProductOfferingPrice.class))).thenReturn(productOfferingPriceLocal);
		// ---- MOCK DOCUMENT RETURN ----
		Document doc = new Document();
		doc.put("_id", "product_id123");
		doc.put("id", "product_id123");
		doc.put("validFor", productOfferingPriceLocal.getValidFor());
		doc.put("@type", "InstallmentCharge");

		when(mongoTemplate.findById(
				eq("product_id123"),
				eq(Document.class),
				anyString()
		)).thenReturn(doc);
		// ----  FIX: MOCK CONVERTER ----
		when(mongoTemplate.getConverter()).thenReturn(mongoConverter);

		when(mongoConverter.read(
				any(Class.class),
				any(Document.class)
		)).thenReturn(productOfferingPriceLocal);
		ProductOfferingPrice returnedProductOfferingPrice = productOfferingPriceService
				.getProductOfferingPriceById("product_id123", fieldList);
		assertNotNull(returnedProductOfferingPrice);
		assertEquals("product_id123",returnedProductOfferingPrice.getId());
		assertNotNull(returnedProductOfferingPrice.getValidFor());

		;
	}

	@Test
	void removeProductOfferingPriceTest() {
		productOfferingPrice.setId("product_id123");
		productOfferingPriceService.removeProductOfferingPrice("product_id123");
		Query query = Query.query(Criteria.where("id").is("product_id123"));
		Mockito.verify(mongoTemplate).remove(query, ProductOfferingPrice.class);
	}

}

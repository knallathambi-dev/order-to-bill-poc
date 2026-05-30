// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productoffering.service.impl.QueryServiceImpl;

class QueryServiceImplTest extends ProductOfferingApplicationTests {

	private QueryServiceImpl queryService;

	private RestTemplate restTemplate;

	private ProductSpecification actualProductSpecification;

	private Category actualCategory;
	@Mock
	private ConfigurableProperties configurableProperties;

	@MockBean
	private AccessTokenInterceptor accessTokenInterceptor;

	@BeforeEach
	void setUp() {
		queryService = new QueryServiceImpl();
		restTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(queryService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(queryService, "configurableProperties", configurableProperties);
		ReflectionTestUtils.setField(queryService, "accessTokenInterceptor", accessTokenInterceptor);
		actualProductSpecification = new ProductSpecification().id("productSpecId")
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).supportEntity(SupportEntity.CFSSPEC);
		actualCategory = new Category().id("categoryId").lifecycleStatus(CategoryLifeCycle.ACTIVE.toString());
	}

	@Test
	void fetchProductSpecByIdTest() {
		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductSpecification.class)))
				.thenReturn(new ResponseEntity<>(actualProductSpecification, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		ProductSpecification targetProductSpecification = queryService.fetchProductSpecById("productSpecId", null);

		Assertions.assertNotNull(targetProductSpecification);
		Assertions.assertEquals(ProductSpecificationLifecycle.ACTIVE, targetProductSpecification.getLifecycleStatus());
	}

	@Test
	void fetchProductSpecificationsTest() {
		List<ProductSpecification> productSpecifications = new ArrayList<>();
		ProductSpecification specification1 = new ProductSpecification().id("prod_spec1")
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
		ProductSpecification specification2 = new ProductSpecification().id("prod_spec2")
				.lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED);
		productSpecifications.add(specification1);
		productSpecifications.add(specification2);

		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification?id=prod_spec1,prod_spec2");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductSpecification>>>any()))
						.thenReturn(new ResponseEntity<>(productSpecifications, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductSpecification> productSpecificationList = queryService
				.fetchProductSpecifications("prod_spec1,prod_spec2", null);

		Assertions.assertNotNull(productSpecificationList);
		Assertions.assertEquals(ProductSpecificationLifecycle.ACTIVE,
				productSpecificationList.get(0).getLifecycleStatus());
		Assertions.assertEquals(ProductSpecificationLifecycle.LAUNCHED,
				productSpecificationList.get(1).getLifecycleStatus());
	}

	@Test
	void fetchProductSpecificationsByProductSpecificationLifeCycleStatusTest() {
		List<ProductSpecification> productSpecifications = new ArrayList<>();

		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification?state=INSTUDY");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductSpecification>>>any()))
						.thenReturn(new ResponseEntity<>(productSpecifications, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductSpecification> productSpecificationList = queryService
				.fetchProductSpecifications("prod_spec1,prod_spec2", null);

		Assertions.assertNotNull(productSpecificationList);
	}

	/*
	 * test when checking productSpec. gets failed
	 */
	@Test
	void fetchProductSpecByIdFailureTest() {
		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductSpecification.class)))
				.thenThrow(new DiscoClientException("Exception while checking product specification by id"));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		assertThrows(DiscoClientException.class, () -> queryService.fetchProductSpecById("productSpecId", null));

	}

	@Test
	void fetchCategoryTest() {
		List<Category> categories = new ArrayList<>();
		categories.add(new Category().id("BOS_B2B_Classification"));
		when(configurableProperties.getCategoryUrl()).thenReturn("http://localhost:3000/category");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<Category>>>any()))
				.thenReturn(new ResponseEntity<>(categories, HttpStatus.OK));
		categories = queryService.fetchCategory(null);
		Assertions.assertNotNull(categories);
		Assertions.assertEquals("BOS_B2B_Classification", categories.get(0).getId());
	}

	@Test
	void fetchMarketSegmentsTest() {
		List<MarketSegmentAdmin> marketSegmentAdmins = new ArrayList<>();
		MarketSegmentAdmin admin = new MarketSegmentAdmin();
		admin.setId("1266");
		marketSegmentAdmins.add(admin);
		when(configurableProperties.getMarketUrl()).thenReturn("http://localhost:3000/market");
		Mockito.when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				ArgumentMatchers.<ParameterizedTypeReference<List<MarketSegmentAdmin>>>any())
		).thenReturn(new ResponseEntity<>(marketSegmentAdmins, HttpStatus.OK));
		List<MarketSegmentAdmin> result = queryService.fetchMarketSegments(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals("1266", result.get(0).getId());
	}

	@Test
	void fetchChannelsTest() {
		List<ChannelAdmin> channelAdmins = new ArrayList<>();
		ChannelAdmin admin = new ChannelAdmin();
		admin.setAtType("web");
		channelAdmins.add(admin);
		when(configurableProperties.getChannelUrl()).thenReturn("http://localhost:3000/channel");
		Mockito.when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				ArgumentMatchers.<ParameterizedTypeReference<List<ChannelAdmin>>>any())
		).thenReturn(new ResponseEntity<>(channelAdmins, HttpStatus.OK));
		List<ChannelAdmin> result = queryService.fetchChannels(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals("web", result.get(0).getAtType());
	}
	@Test
	void fetchProductOfferingTest() {
		List<ProductOffering> productOfferings = new ArrayList<>();
		productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.INSTUDY));

		when(configurableProperties.getProductOfferingUrl())
				.thenReturn("http://localhost:8087/productCatalogManagement/v1/productOffering");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductOffering>>>any()))
						.thenReturn(new ResponseEntity<>(productOfferings, HttpStatus.OK));
		List<ProductOffering> productOfferings1 = queryService.fetchProductOffering(null);

		Assertions.assertNotNull(productOfferings1);
		Assertions.assertEquals(ProductOfferingLifecycle.INSTUDY, productOfferings1.get(0).getLifecycleStatus());
	}

	@Test
	void fetchProductOfferingsByProductSpecIdTest() {
		List<ProductOffering> pOs = new ArrayList<>();
		when(configurableProperties.getProductOfferingUrl())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v1/serviceSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductOffering>>>any()))
				.thenReturn(new ResponseEntity<>(pOs, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductSpecId("productSpecId");
		Assertions.assertNotNull(productOfferings);

	}

	@Test
	void fetchProductOfferingsByProductOfferingPriceTest() {
		List<ProductOffering> pOs = new ArrayList<>();
		when(configurableProperties.getProductOfferingUrl())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v1/serviceSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductOffering>>>any()))
				.thenReturn(new ResponseEntity<>(pOs, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductOffering> productOfferings = queryService
				.fetchProductOfferingsByProductOfferingPriceId("productOfferingPriceId");
		Assertions.assertNotNull(productOfferings);

	}

	@Test
	void fetchProductOfferingsByProductOfferingLifeCycleStatusTest() {
		List<ProductOffering> pOs = new ArrayList<>();
		when(configurableProperties.getProductOfferingUrl())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v1/serviceSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductOffering>>>any()))
				.thenReturn(new ResponseEntity<>(pOs, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByLifeCycleStatus("INSTUDY");
		Assertions.assertNotNull(productOfferings);

	}
	@Test
	void fetchCategoryByIdTest() {
		when(configurableProperties.getCategoryUrl())
				.thenReturn("http://localhost:8087/productCatalogManagement/v1/category");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Category.class)))
				.thenReturn(new ResponseEntity<>(actualCategory, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		Category targetCategory = queryService.fetchCategoryById("categoryId");

		Assertions.assertNotNull(targetCategory);
		Assertions.assertEquals(CategoryLifeCycle.ACTIVE.toString(), targetCategory.getLifecycleStatus());
	}

}
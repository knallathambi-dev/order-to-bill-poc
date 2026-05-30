// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ChannelRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.MarketSegmentRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;


public class QueryServiceImplTest extends ProductSpecificationApplicationTests {

	private QueryServiceImpl queryService;

	private RestTemplate restTemplate;

	private ProductSpecification actualProductSpecification;

	@Mock
	private ConfigurableProperties configurableProperties;

	@MockBean
	private AccessTokenInterceptor accessTokenInterceptor;
	private static String accessToken = "Bearer 123";

	@BeforeEach
	void setUp() {
		queryService = new QueryServiceImpl();
		restTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(queryService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(queryService, "configurableProperties", configurableProperties);
		ReflectionTestUtils.setField(queryService, "accessTokenInterceptor", accessTokenInterceptor);
		actualProductSpecification = new ProductSpecification().id("productSpecId")
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).supportEntity(SupportEntity.CFSSPEC);
	}

	@Test
	void getServiceSpecByIdTest() {
		ServiceSpecification serviceSpecification = new ServiceSpecification().id("cfs1").lifecycleStatus("active");
		when(configurableProperties.getCfsQuery())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v3/serviceSpecification");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ServiceSpecification.class)))
				.thenReturn(new ResponseEntity<>(serviceSpecification, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		ServiceSpecification serviceSpecification1 = queryService.getServiceSpecById("cfs1", accessToken);

		Assertions.assertNotNull(serviceSpecification1);
		Assertions.assertEquals(ServiceSpecLifeCycleEnum.ACTIVE.toString(), serviceSpecification1.getLifecycleStatus());
	}

	@Test
	void fetchProductSpecByIdTest() {
		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/serviceCatalogManagement/v3/productSpecification");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductSpecification.class)))
				.thenReturn(new ResponseEntity<>(actualProductSpecification, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		ProductSpecification targetProductSpecification = queryService.fetchProductSpecById("productSpecId", accessToken);

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
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/serviceCatalogManagement/v3/productSpecification?id=prod_spec1,prod_spec2");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductSpecification>>>any()))
						.thenReturn(new ResponseEntity<>(productSpecifications, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductSpecification> productSpecificationList = queryService
				.fetchProductSpecifications("prod_spec1,prod_spec2", accessToken);

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
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/serviceCatalogManagement/v3/productSpecification?state=INSTUDY");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductSpecification>>>any()))
						.thenReturn(new ResponseEntity<>(productSpecifications, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		List<ProductSpecification> productSpecificationList = queryService
				.fetchProductSpecifications("prod_spec1,prod_spec2", accessToken);

		Assertions.assertNotNull(productSpecificationList);
	}

	/*
	 * test when checking productSpec. gets failed
	 */
	@Test
	void fetchProductSpecByIdFailureTest() {
		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/serviceCatalogManagement/v3/productSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductSpecification.class)))
				.thenThrow(new DiscoClientException("Exception while checking product specification by id"));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		assertThrows(DiscoClientException.class, () -> queryService.fetchProductSpecById("productSpecId", accessToken));

	}

	@Test
	void fetchMarketSegmentsTest() {
		List<MarketSegmentRef> marketSegmentRefs = new ArrayList<>();
		marketSegmentRefs.add(new MarketSegmentRef().id("1266"));
		when(configurableProperties.getMarketUrl()).thenReturn("http://localhost:3000/market");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<MarketSegmentRef>>>any()))
				.thenReturn(new ResponseEntity<>(marketSegmentRefs, HttpStatus.OK));
		marketSegmentRefs = queryService.fetchMarketSegments(accessToken);
		Assertions.assertNotNull(marketSegmentRefs);
		Assertions.assertEquals("1266", marketSegmentRefs.get(0).getId());
	}

	@Test
	void fetchChannelsTest() {
		List<ChannelRef> channelRefs = new ArrayList<>();
		channelRefs.add(new ChannelRef().type("web"));
		when(configurableProperties.getChannelUrl()).thenReturn("http://localhost:3000/channel");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ChannelRef>>>any()))
				.thenReturn(new ResponseEntity<>(channelRefs, HttpStatus.OK));
		channelRefs = queryService.fetchChannels(accessToken);
		Assertions.assertNotNull(channelRefs);
		Assertions.assertEquals("web", channelRefs.get(0).getType());
	}

}
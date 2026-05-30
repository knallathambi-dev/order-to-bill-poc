// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;
import com.orange.discobole.productcatalog.productofferingprice.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.interceptor.AccessTokenInterceptor;

class QueryServiceImplTest extends ProductOfferingPriceApplicationTests {

	private QueryServiceImpl queryService;

	private RestTemplate restTemplate;
	
	@MockBean
	private AccessTokenInterceptor accessTokenInterceptor;
	
	@Mock
	private ConfigurableProperties configurableProperties;
	ProductOfferingPrice pop;

	@BeforeEach
	void setUp() {
		queryService = new QueryServiceImpl();
		restTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(queryService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(queryService, "configurableProperties", configurableProperties);
		ReflectionTestUtils.setField(queryService, "accessTokenInterceptor", accessTokenInterceptor);
		pop=new ProductOfferingPrice().id("1");
	}
	
	@Test
	void fetchProductOfferingsPricesByProductOfferingPriceLifeCycleStatusTest() {
		when(configurableProperties.getProductOfferingPriceUrl())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v1/serviceSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductOfferingPrice.class)))
				.thenReturn(new ResponseEntity<>(pop, HttpStatus.OK));
		ProductOfferingPrice productOfferingPrices = queryService.getProductOfferingPrice("1");
		Assertions.assertNotNull(productOfferingPrices);
	}


}
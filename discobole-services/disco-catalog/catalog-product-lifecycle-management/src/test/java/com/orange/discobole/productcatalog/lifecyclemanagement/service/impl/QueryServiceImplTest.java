// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

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

import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.lifecyclemanagement.exception.DiscoClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
 class QueryServiceImplTest extends ManageLifeCycleApplicationTests {

	private QueryServiceImpl queryService;

	private RestTemplate restTemplate;

	private ProductSpecification actualProductSpecification;


	@Mock
	private ConfigurableProperties configurableProperties;

	@MockBean
	private AccessTokenInterceptor accessTokenInterceptor;

	String accessToken = "Bearer 123";
	@BeforeEach
	void setUp() {
		queryService = new QueryServiceImpl();
		restTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(queryService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(queryService, "configurableProperties", configurableProperties);
		ReflectionTestUtils.setField(queryService, "accessTokenInterceptor", accessTokenInterceptor);
		actualProductSpecification = new ProductSpecification().id("productSpecId")
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).supportEntity(SupportEntity.CFSSPEC);
//
	}

	

	@Test
	void fetchProductSpecByIdTest() {
		when(configurableProperties.getProductSpecQuery()).thenReturn(
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductSpecification.class)))
				.thenReturn(new ResponseEntity<>(actualProductSpecification, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
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
				"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification?id=prod_spec1,prod_spec2");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductSpecification>>>any()))
						.thenReturn(new ResponseEntity<>(productSpecifications, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		List<ProductSpecification> productSpecificationList = queryService
				.fetchProductSpecifications("prod_spec1,prod_spec2",accessToken);

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
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		List<ProductSpecification> productSpecificationList = queryService
				.fetchProductSpecifications("prod_spec1,prod_spec2",accessToken);

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
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		assertThrows(DiscoClientException.class, () -> queryService.fetchProductSpecById("productSpecId", accessToken));

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
		List<ProductOffering> productOfferings1 = queryService.fetchProductOffering(accessToken);

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
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductSpecId("productSpecId", accessToken);
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
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductOfferingPriceId("productOfferingPriceId",accessToken);
		Assertions.assertNotNull(productOfferings);

	}
	
	@Test
	void fetchProductOfferingsPricesByProductOfferingPriceTest() {
		List<ProductOfferingPrice> pOPs = new ArrayList<>();
		when(configurableProperties.getProductOfferingPriceUrl())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v1/serviceSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductOfferingPrice>>>any()))
				.thenReturn(new ResponseEntity<>(pOPs, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		List<ProductOfferingPrice> productOfferingPrices = queryService.getProductOfferingPricesByProductOfferingPriceId("productOfferingPriceId",accessToken);
		Assertions.assertNotNull(productOfferingPrices);

	}
	
	@Test
	void fetchBundleAndContractProductOfferingByBundlingProductOfferingIdTest() {
		List<ProductOffering> pOs = new ArrayList<>();
		when(configurableProperties.getProductOfferingUrl())
				.thenReturn("http://localhost:8087/serviceCatalogManagement/v1/serviceSpecification");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<ProductOffering>>>any()))
				.thenReturn(new ResponseEntity<>(pOs, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		List<ProductOffering> productOfferings = queryService.fetchBundleAndContractProductOfferingByBundlingProductOfferingId("BundledProductOffering.id",accessToken);
		Assertions.assertNotNull(productOfferings);
	}
		@Test
		void fetchProductOfferingPriceByIdTest() {
			when(configurableProperties.getProductOfferingPriceUrl()).thenReturn(
					"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification");
			when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductOfferingPrice.class)))
					.thenReturn(new ResponseEntity<>(new ProductOfferingPrice().id("1"), HttpStatus.OK));
			Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
			ProductOfferingPrice targetPOP = queryService.getProductOfferingPrice("1", accessToken);
			Assertions.assertNotNull(targetPOP);
		}	
		@Test
		void fetchProductOfferingByIdTest() {
			when(configurableProperties.getProductOfferingUrl()).thenReturn(
					"http://catalog-bos-dev.kermit-noprod-b.itn.intraorange/productCatalogManagement/v1/productSpecification");
			when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductOffering.class)))
					.thenReturn(new ResponseEntity<>(new ProductOffering().id("1"), HttpStatus.OK));
			Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
			ProductOffering targetPO = queryService.fetchProductOfferingById("1", accessToken);
			Assertions.assertNotNull(targetPO);
		}
	
	
	
	
	
	
	
	


}
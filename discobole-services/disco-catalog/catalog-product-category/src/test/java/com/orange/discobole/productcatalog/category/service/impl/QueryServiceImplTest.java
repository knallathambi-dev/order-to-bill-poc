// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

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

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryLifeCycle;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;

class QueryServiceImplTest extends CategoryApplicationTests {

	private QueryServiceImpl queryService;

	private RestTemplate restTemplate;

    private ProductOffering productoffering;
	private Category actualCategory;
	private CategoryEntityRelationship categoryRelationship;
	@Mock
	private ConfigurableProperties configurableProperties;

	@MockBean
	private AccessTokenInterceptor accessTokenInterceptor;
	private String accessToken = "mockToken";

	@BeforeEach
	void setUp() {
		queryService = new QueryServiceImpl();
		restTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(queryService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(queryService, "configurableProperties", configurableProperties);
		ReflectionTestUtils.setField(queryService, "accessTokenInterceptor", accessTokenInterceptor);
		actualCategory = new Category().id("categoryId").lifecycleStatus(CategoryLifeCycle.ACTIVE.toString());
		categoryRelationship=new CategoryEntityRelationship();
		productoffering = new ProductOffering();
	}

    @Test
	void fetchCategoryByIdTest() {
		when(configurableProperties.getCategoryUrl())
				.thenReturn("http://localhost:8087/productCatalogManagement/v3/category");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Category.class)))
				.thenReturn(new ResponseEntity<>(actualCategory, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		Category targetCategory = queryService.fetchCategoryById("categoryId", accessToken);

		Assertions.assertNotNull(targetCategory);
		Assertions.assertEquals(CategoryLifeCycle.ACTIVE.toString(), targetCategory.getLifecycleStatus());
	}
    
    @Test
	void fetchCategoryEntityByIdTest() {
		when(configurableProperties.getCategoryEntityUrl())
				.thenReturn("http://localhost:8087/productCatalogManagement/v3/categoryEntity/"+"categoryEntityId");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(CategoryEntityRelationship.class)))
				.thenReturn(new ResponseEntity<>(categoryRelationship, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		CategoryEntityRelationship targetCategory = queryService.fetchCategoryEntityById("categoryEntityId", accessToken);

		Assertions.assertNotNull(targetCategory);
	}
    
    @Test
   	void fetchProductOfferingByIdTest() {
   		when(configurableProperties.getProductOfferingUrl())
   				.thenReturn("http://localhost:8087/productCatalogManagement/v1/productOffering");
   		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ProductOffering.class)))
   				.thenReturn(new ResponseEntity<>(productoffering, HttpStatus.OK));
   		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
   		ProductOffering target = queryService.fetchProductOfferingById("productOfferingId", accessToken);
		Assertions.assertNotNull(target);
   	}    

	@Test 
	void fetchCategoryEntityBySubCategoryId() {
		List<Category> ctgs = new ArrayList<>();
		when(configurableProperties.getCategoryUrl())
				.thenReturn("http://localhost:8087/productCatalogManagement/v1/category");
		Mockito.when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				ArgumentMatchers.<ParameterizedTypeReference<List<Category>>>any()))
				.thenReturn(new ResponseEntity<>(ctgs, HttpStatus.OK));
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
		
		  List<Category> ctgs1 =
		  queryService.fetchCategoryEntityBySubCategoryId("categoryEntityId", accessToken);
		  Assertions.assertNotNull(ctgs1);
		 
	}	
	
}
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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
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

@WebMvcTest(controllers = ProductOfferingController.class)
@WithMockUser
class ProductOfferingControllerTest extends CatalogApplicationTests {

	private static final String BASE_URL = "/productCatalogManagement/v1";
	private static final String SEPARATOR = "/";

	@MockBean
	private ProductOfferingService productOfferingService;

	@MockBean
	private CategoryEntityRelationshipService categoryEntityService;

	@MockBean
	private CategoryService categoryService;

	@Resource
	private MockMvc mvc;

	@Captor
	private ArgumentCaptor<Map<String, Object>> captor;

	@Test
	void findProductOfferingsTest() throws Exception {
		Map<String, Object> productOfferings = new HashMap<>();
		productOfferings.put("data", List.of(new ProductOffering()));
		productOfferings.put("count", 1L);
		given(productOfferingService.fetchProductOfferingWithCount(captor.capture(), any(), any(),any())).willReturn(productOfferings);
		mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productOffering").param("state", "active"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		Assertions.assertEquals(34, captor.getValue().size());
	}

	@Test
	void findProductOfferingsNullTest() throws Exception {
		Map<String, Object> mockResponse = new HashMap<>();
		given(productOfferingService.fetchProductOfferingWithCount(captor.capture(), any(), any(),any())).willReturn(mockResponse);
		mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productOffering").param("state", "active"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
		Assertions.assertEquals(34, captor.getValue().size());
	}

	@Test
	void productOfferingByIdTest() throws Exception {
		ProductOffering productOffering = new ProductOffering();
		productOffering.id("productOfferingId1");
		Set<CategoryRef> categories = new HashSet<CategoryRef>();
		categories.add(new CategoryRef().id("c01"));
		CategoryEntityRelationship entity = new CategoryEntityRelationship();
		entity.id("productOfferingId1").categories(categories);

		given(productOfferingService.fetchProductOfferingById("productOfferingId1")).willReturn(productOffering);
		given(categoryEntityService.fetchEntityById("productOfferingId1")).willReturn(entity);
		given(categoryService.fetchCategoryById("c01")).willReturn(new Category().id("c01").name("cname"));
		mvc.perform(
				MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productOffering" + SEPARATOR + "productOfferingId1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void productOfferingByIdNullTest() throws Exception {
		given(productOfferingService.fetchProductOfferingById("productOfferingId1")).willReturn(null);
		CategoryEntityRelationship entity = new CategoryEntityRelationship();
		entity.id("productOfferingId1");
		Set<CategoryRef> categories = new HashSet<CategoryRef>();
		categories.add(new CategoryRef().id("c01"));
		given(categoryEntityService.fetchEntityById("productOfferingId1")).willReturn(entity);

		mvc.perform(
				MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productOffering" + SEPARATOR + "productOfferingId1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}

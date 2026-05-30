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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingPriceService;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductOfferingPriceController.class)
@WithMockUser
class ProductOfferingPriceControllerTest extends CatalogApplicationTests {

	private static final String BASE_URL = "/productCatalogManagement/v1";
	private static final String SEPARATOR = "/";

	@MockBean
	private ProductOfferingPriceService productOfferingPriceService;

	@Resource
	private MockMvc mvc;

	@Captor
	private ArgumentCaptor<Map<String, Object>> captor;

//	@Test
//	void findProductOfferingPricesTest() throws Exception {
//		List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
//		when(productOfferingPriceService.getProductOfferingPrices(captor.capture(), any(), any(), any())).thenReturn(productOfferingPrices);
//		mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productOfferingPrice").param("@type",
//				ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.toString()))
//				.andExpect(MockMvcResultMatchers.status().isOk());
//		Assertions.assertEquals(14, captor.getValue().size());
//	}

//	@Test
//	void findProductOfferingsNullTest() throws Exception {
//		when(productOfferingPriceService.getProductOfferingPrices(captor.capture(), any(), any(), any())).thenReturn(null);
//		mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "productOfferingPrice").param("@type",
//				ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.toString()))
//				.andExpect(MockMvcResultMatchers.status().isNoContent());
//		Assertions.assertEquals(14, captor.getValue().size());
//	}

	@Test
	void productOfferingByIdTest() throws Exception {
		ProductOfferingPrice productOfferingPrice = new ProductOfferingPrice();
		productOfferingPrice.id("productOfferingPriceId1");
		when(productOfferingPriceService.getProductOfferingPriceById("productOfferingPriceId1"))
				.thenReturn(productOfferingPrice);
		mvc.perform(MockMvcRequestBuilders
				.get(BASE_URL + SEPARATOR + "productOfferingPrice" + SEPARATOR + "productOfferingPriceId1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void productOfferingByIdNullTest() throws Exception {
		when(productOfferingPriceService.getProductOfferingPriceById("productOfferingPriceId1")).thenReturn(null);
		mvc.perform(MockMvcRequestBuilders
				.get(BASE_URL + SEPARATOR + "productOfferingPrice" + SEPARATOR + "productOfferingId1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}
}
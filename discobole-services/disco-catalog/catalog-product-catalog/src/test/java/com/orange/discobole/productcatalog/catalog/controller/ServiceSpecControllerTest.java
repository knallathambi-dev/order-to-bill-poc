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
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.service.ServiceSpecService;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ServiceSpecController.class)
@WithMockUser(username = "BOS", roles = { "CatalogAdministrator" })
class ServiceSpecControllerTest extends CatalogApplicationTests {

	private static final String BASE_URL = "/serviceCatalogManagement/v1";
	private static final String SEPARATOR = "/";

	@MockBean
	private ServiceSpecService serviceSpecService;

	@Resource
	private MockMvc mvc;

	@Captor
	private ArgumentCaptor<Map<String, Object>> captor;

	@Test
	void findServiceSpecificationsTest() throws Exception {
		//List<ServiceSpecification> serviceSpecifications = new ArrayList<>();
		Map<String, Object> serviceSpecifications = new HashMap<>();
		serviceSpecifications.put("data", List.of(new ServiceSpecification()));
		serviceSpecifications.put("count", 1L);
		when(serviceSpecService.fetchServiceSpecificationWithCount(captor.capture(),any(), any(),any())).thenReturn(serviceSpecifications);
		mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "serviceSpecification"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		Assertions.assertEquals(8, captor.getValue().size());
	}

//	@Test
//	void findServiceSpecificationsNullTest() throws Exception {
//		when(serviceSpecService.fetchServiceSpecificationWithCount(captor.capture(),any(),any(),any())).thenReturn(null);
//		mvc.perform(MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "serviceSpecification"))
//				.andExpect(MockMvcResultMatchers.status().isNoContent());
//		Assertions.assertEquals(8, captor.getValue().size());
//	}

	@Test
	void findServiceSpecificationByIdTest() throws Exception {
		ServiceSpecification serviceSpecification = new ServiceSpecification();
		serviceSpecification.id("serviceSpecId1");
		when(serviceSpecService.fetchServiceSpecificationById("serviceSpecId1")).thenReturn(serviceSpecification);
		mvc.perform(
				MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "serviceSpecification" + SEPARATOR + "serviceSpecId1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void findServiceSpecificationByIdNullTest() throws Exception {
		ServiceSpecification serviceSpecification = null;
		when(serviceSpecService.fetchServiceSpecificationById("serviceSpecId1")).thenReturn(serviceSpecification);
		mvc.perform(
				MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "serviceSpecification" + SEPARATOR + "serviceSpecId1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@WithAnonymousUser
	void findServiceSpecificationByIdUnauthorizedTest() throws Exception {
		ServiceSpecification serviceSpecification = new ServiceSpecification();
		serviceSpecification.id("serviceSpecId1");
		when(serviceSpecService.fetchServiceSpecificationById("serviceSpecId1")).thenReturn(serviceSpecification);
		mvc.perform(
				MockMvcRequestBuilders.get(BASE_URL + SEPARATOR + "serviceSpecification" + SEPARATOR + "serviceSpecId1"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
}
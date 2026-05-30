// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.exception.DiscoClientException;


class SchemaControllerTest {

	private SchemaController schemaController;

	@Mock
	private ObjectMapper objectMapper;

	SchemaControllerTest() {
		schemaController = new SchemaController();
		objectMapper = Mockito.mock(ObjectMapper.class);
		ReflectionTestUtils.setField(schemaController, "objectMapper", objectMapper);
	}

	@Test
	void testHomeControllerSwagger() throws JsonMappingException, JsonProcessingException {
		String schemaName = "test";
		assertThrows(DiscoClientException.class, () -> schemaController.getSchema(schemaName));
	}

}

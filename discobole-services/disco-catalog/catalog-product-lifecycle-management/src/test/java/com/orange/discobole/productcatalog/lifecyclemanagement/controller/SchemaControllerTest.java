// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orange.discobole.processflow.exception.DiscoManagedException;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import com.orange.discobole.processflow.exception.DiscoClientException;

class SchemaControllerTest extends ManageLifeCycleApplicationTests {
	SchemaController schemaController;

	@Mock
	ObjectMapper objectMapper;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
		schemaController = new SchemaController();
		ReflectionTestUtils.setField(schemaController, "objectMapper", objectMapper);
	}

	@Test
	void testGetSchemaSuccess() throws Exception {
		String schemaName = "CancelEntityOperation";
		JsonNode mockJsonNode = mock(JsonNode.class);
		when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);

		ResponseEntity<JsonNode> response = schemaController.getSchema(schemaName);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockJsonNode, response.getBody());
		verify(objectMapper).readTree(contains(schemaName));
	}

	@Test
	void testGetSchemaInvalidName() {
		String invalidSchemaName = "Invalid@Schema!";
		assertThrows(DiscoManagedException.class, () -> {
			schemaController.getSchema(invalidSchemaName);
		});
	}

	@Test
	void testGetSchemaFileNotFound() throws Exception {
		String schemaName = "MissingSchema";
		when(objectMapper.readTree(anyString())).thenThrow(new NullPointerException());

		assertThrows(DiscoClientException.class, () -> {
			schemaController.getSchema(schemaName);
		});
	}

	@Test
	void testGetSchemaReadError() throws Exception {
		String schemaName = "ErrorSchema";
		when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("Read error"));

		assertThrows(DiscoClientException.class, () -> {
			schemaController.getSchema(schemaName);
		});
	}
}


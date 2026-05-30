// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.DiscoManagedException;
import com.orange.discobole.productcatalog.lifecyclemanagement.util.FileUtil;

import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The schema controller is used to handle the schema.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

@RestController
@RequestMapping(value = "/schemas")
public class SchemaController {

	private static final String DISCO_LS_INVALID_SCHEMA_NAME="DISCO_LS_INVALID_SCHEMA_NAME";
	@Resource
	ObjectMapper objectMapper;

	/**
	 * getSchema method is used to get/read the schema in JSON format.
	 *
	 * @param schemaName the schema name
	 * @return schema JSON
	 */
	@GetMapping(path = "/{schemaName}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JsonNode> getSchema(@PathVariable final String schemaName) {
		if(!isValidSchemaName(schemaName)){
			throw new DiscoManagedException(DISCO_LS_INVALID_SCHEMA_NAME);
		}
		JsonNode file;
		try {
			file = objectMapper.readTree(FileUtil.read("/schemas/" + schemaName + ".json"));
		}
		catch(NullPointerException e){
			throw new DiscoClientException("File Not Found");
		}
		catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
		return ResponseEntity.ok().body(file);
	}
	private boolean isValidSchemaName(String schemaName) {
		// Basic validation: only allow alphanumeric, underscores, and hyphens
		return schemaName != null && schemaName.matches("^[a-zA-Z0-9_-]+$");
	}
}

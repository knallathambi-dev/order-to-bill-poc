// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The schema controller is used to handle the schema.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

@RestController
@RequestMapping(value = "/schemas")
public class SchemaController {

	@Resource
	ObjectMapper objectMapper;

	/**
	 * getSchema method is used to get/read the schema in JSON format.
	 *
	 * @param schemaName the schema name
	 * @return schema JSON
	 */
	@GetMapping(value = "/{schemaName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getSchema(@PathVariable final String schemaName) {

		try {
			// Validate schema name – allow only safe characters
			if (!schemaName.matches("^[a-zA-Z0-9_-]+$")) {
				throw new DiscoClientException("Invalid schema name.");
			}

			// Resolve path safely to prevent path traversal
			Path basePath = Paths.get("/schemas").toAbsolutePath().normalize();
			Path targetPath = basePath.resolve(schemaName + ".json").normalize();

			if (!targetPath.startsWith(basePath)) {
				throw new DiscoClientException("Access denied.");
			}

			// Read file content securely
			String fileContent = FileUtil.read(targetPath.toString());

			// Convert file content to pretty JSON
			String formattedJson = objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(objectMapper.readTree(fileContent));

			//Return JSON safely with correct content type
			return ResponseEntity.ok(formattedJson);

		} catch (Exception e) {
			throw new DiscoClientException("Error reading schema: " + e.getMessage());
		}
	}
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.productcatalog.category.util.FileUtil;

import jakarta.annotation.Resource;
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

	@Resource
	ObjectMapper objectMapper;

	/**
	 * getSchema method is used to get/read the schema in JSON format.
	 *
	 * @param schemaName the schema name
	 * @return schema JSON
	 */
	@GetMapping("/{schemaName}")
	public String getSchema(@PathVariable final String schemaName) {

		String file;
		try {
			file = objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(objectMapper.readTree(FileUtil.read("/schemas/" + schemaName + ".json")));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}

		return file;
	}
}

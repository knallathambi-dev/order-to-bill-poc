// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.processflow.dto.generated.Error;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/productCatalogManagement/v1/error")

public interface ErrorController {

	@PostMapping
	ResponseEntity<List<Error>> createErrorRepresentation(
			@Valid @RequestBody List<Error> errorRepresentationDb);

	@GetMapping
	ResponseEntity<List<Error>> fetchErrorRepresentations();

	@GetMapping("/{id}")
	ResponseEntity<Error> fetchErrorRepresentationById(@PathVariable String id);
}

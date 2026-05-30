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

package com.orange.discobole.processflow.controller.impl;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.controller.ErrorController;
import com.orange.discobole.processflow.dto.generated.Error;
import com.orange.discobole.processflow.handler.ErrorService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@Tag(name = "error")
@ConditionalOnProperty(value = "cqrs.error-enabled", havingValue = "true", matchIfMissing = true)
@Component("processFlowErrorController")
public class ErrorControllerImpl implements ErrorController {
	@Resource
	private ErrorService service;

	@Override
	public ResponseEntity<List<Error>> createErrorRepresentation(
			@Valid List<Error> errorRepresentationDb) {

		final List<Error> erorrcreated = service.saveError(errorRepresentationDb);
		return ResponseEntity.status(HttpStatus.CREATED).body(erorrcreated);
	}

	@Override
	public ResponseEntity<List<Error>> fetchErrorRepresentations() {

		return ResponseEntity.ok(service.fetchError());
	}

	@Override
	public ResponseEntity<Error> fetchErrorRepresentationById(String id) {
		return ResponseEntity.ok(service.fetchErrorById(id));
	}

}

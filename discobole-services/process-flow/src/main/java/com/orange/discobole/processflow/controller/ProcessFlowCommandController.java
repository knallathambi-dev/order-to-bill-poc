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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;

import jakarta.validation.Valid;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Controller to handle PROCESS FLOW command operations.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@RestController
@RequestMapping("/processManagement/v1/processFlow")
public interface ProcessFlowCommandController {

    /**
     * endpoint to create new {@code ProcessFlow}.
     *
     * @param processFlowCreate process  flow create request with Process Type, Channel, System,
     *                          Related Party, Characteristic and Correlation id
     * @return {@code ProcessFlow}
     */
    @PostMapping
    ResponseEntity<ProcessFlow> createProcessFlow(@Valid @RequestBody ProcessFlowCreate processFlowCreate,  @ApiIgnore @RequestHeader(value = "Authorization", required = false) String token);

    /**
     * endpoint to delete process flow by id.
     *
     * @param id the process flow id to be deleted
     * @return {@code Void}
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProcessFlow(@PathVariable String id);

}

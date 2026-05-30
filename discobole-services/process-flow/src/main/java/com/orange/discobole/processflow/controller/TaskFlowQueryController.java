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

import com.orange.discobole.processflow.dto.generated.TaskFlow;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Controller to handle TASK FLOW query operations of a process flow
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@RestController
@RequestMapping("/processManagement/v1/processFlow/{processFlowId}/taskFlow")
public interface TaskFlowQueryController {

    /**
     * endpoint to get active tasks of a process flow
     *
     * @param processFlowId aka process flow id
     * @return List of {@code TaskFlow}
     */
    @GetMapping
    ResponseEntity<List<TaskFlow>> fetchTaskFlows(@PathVariable String processFlowId, @ApiIgnore @RequestHeader(value = "Authorization", required = false) String token);

    /**
     * endpoint to get task flows of process instance by task id
     *
     * @param processFlowId aka process flow id
     * @param id            unique task flow id
     * @return {@code TaskFlow}
     */
    @GetMapping("/{id}")
    ResponseEntity<TaskFlow> fetchTaskFlowById(@PathVariable String processFlowId, @PathVariable String id, @ApiIgnore @RequestHeader(value = "Authorization", required = false) String token);

}

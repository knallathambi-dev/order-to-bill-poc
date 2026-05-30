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
import springfox.documentation.annotations.ApiIgnore;


import java.util.List;

/**
 * Controller to handle PROCESS FLOW query operations.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@RestController
@RequestMapping("/processManagement/v1/processFlow")
public interface ProcessFlowQueryController {

    /**
     * endpoint to get all active process flows by related party id or related entity id.
     *
     * @param relatedPartyID  related party id
     * @param relatedEntityId related entity id
     * @return List of {@code ProcessFlow}
     */
    @GetMapping
    ResponseEntity<List<ProcessFlow>> fetchProcessFlows(@RequestParam(name = "relatedParty.id", required = false) String relatedPartyId,
                                                        @RequestParam(name = "relatedParty.name", required = false) String relatedPartyName,
                                                        @RequestParam(name = "relatedEntity.id", required = false) String relatedEntityId,
                                                        @ApiIgnore @RequestHeader(value = "Authorization", required = false) String token);

    /**
     * endpoint to get process flow by id.
     *
     * @param id aka process instance id
     * @return {@code ProcessFlow}
     */
    @GetMapping("/{id}")
    ResponseEntity<ProcessFlow> fetchProcessFlowById(@PathVariable String id, @ApiIgnore @RequestHeader(value = "Authorization", required = false) String token);

}

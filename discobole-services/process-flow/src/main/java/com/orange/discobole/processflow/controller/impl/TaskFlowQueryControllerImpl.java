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

import com.orange.discobole.processflow.util.TokenConverter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.controller.TaskFlowQueryController;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.handler.FlowLinkHandler;
import com.orange.discobole.processflow.service.TaskFlowQueryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "processFlow")
@ConditionalOnProperty(value = "cqrs.query-enabled", havingValue = "true", matchIfMissing = true)
@Component("taskFlowQueryController")
public class TaskFlowQueryControllerImpl implements TaskFlowQueryController {

    @Resource
    private TaskFlowQueryService taskFlowService;

    @Resource
    private FlowLinkHandler flowLinkHandler;

    @Value("${process-flow.role.admin}")
    private String admin;

    @Override
    public ResponseEntity<List<TaskFlow>> fetchTaskFlows(final String processFlowId, String token) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id",processFlowId);

        List<String> roles = TokenConverter.convert(token);
        String relatedPartyIdFromToken = TokenConverter.getRelatedPartyIdFromToken(token);
        List<TaskFlow> taskList = new ArrayList<>();

        if (roles.contains(admin)){
            taskList = taskFlowService.findTaskFlowsByProcessFlowId(requestParams);
        } else if (relatedPartyIdFromToken!=null) {
            requestParams.put("taskFlow.relatedParty.id", relatedPartyIdFromToken);
            taskList = taskFlowService.findTaskFlowsByProcessFlowId(requestParams);
        }
        if (taskList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        taskList.forEach(t -> flowLinkHandler.addLinks(processFlowId, t));
        return ResponseEntity.ok(taskList);
    }

    @Override
    public ResponseEntity<TaskFlow> fetchTaskFlowById(final String processFlowId, final String id,final String token) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id",processFlowId);
        requestParams.put("taskFlow._id",id);

        List<String> roles = TokenConverter.convert(token);
        String relatedPartyIdFromToken = TokenConverter.getRelatedPartyIdFromToken(token);
        TaskFlow taskFlow = null;
        if (roles.contains(admin)){
            taskFlow = taskFlowService.findTaskFlowById(requestParams);
        } else if (relatedPartyIdFromToken!=null) {
            requestParams.put("taskFlow.relatedParty.id", relatedPartyIdFromToken);
            taskFlow = taskFlowService.findTaskFlowById(requestParams);
        }
        if (taskFlow == null) {
            return ResponseEntity.noContent().build();
        }
        flowLinkHandler.addLinks(processFlowId, taskFlow);
        return ResponseEntity.ok(taskFlow);
    }

}

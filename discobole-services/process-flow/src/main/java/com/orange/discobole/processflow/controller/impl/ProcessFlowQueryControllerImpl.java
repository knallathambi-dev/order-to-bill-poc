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

import com.orange.discobole.processflow.controller.ProcessFlowQueryController;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.handler.FlowLinkHandler;
import com.orange.discobole.processflow.service.ProcessFlowQueryService;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "processFlow")
@ConditionalOnProperty(value = "cqrs.query-enabled", havingValue = "true", matchIfMissing = true)
@Component("processFlowQueryController")
public class ProcessFlowQueryControllerImpl implements ProcessFlowQueryController {

    @Resource
    private ProcessFlowQueryService processFlowService;

    @Resource
    private FlowLinkHandler flowLinkHandler;

    @Value("${process-flow.role.admin}")
    private String admin;

    @Override
    public ResponseEntity<List<ProcessFlow>> fetchProcessFlows(String relatedPartyId, String relatedPartyName, String relatedEntityId, String token) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("relatedParty.id", relatedPartyId);
        requestParams.put("relatedParty.name", relatedPartyName);
        requestParams.put("relatedEntity.id", relatedEntityId);
        List<String> roles = TokenConverter.convert(token);

        String relatedPartyIdFromToken = TokenConverter.getRelatedPartyIdFromToken(token);
        List<ProcessFlow> processFlows = new ArrayList<>();

        if(roles.contains(admin) || (!ObjectUtils.isEmpty(relatedPartyId) && relatedPartyIdFromToken.equalsIgnoreCase(relatedPartyId))) {
             processFlows = processFlowService.findProcessFlows(requestParams);
        } else if(ObjectUtils.isEmpty(relatedPartyId) && relatedPartyIdFromToken!=null ){
            requestParams.put("relatedParty.id",relatedPartyIdFromToken);
            processFlows = processFlowService.findProcessFlows(requestParams);
        }
        if (processFlows.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        for (ProcessFlow processFlow : processFlows) {
            flowLinkHandler.addLinks(processFlow);
        }
        return ResponseEntity.ok(processFlows);
    }

    @Override
    public ResponseEntity<ProcessFlow> fetchProcessFlowById(final String id, String token) {
        List<String> roles = TokenConverter.convert(token);
        String relatedPartyIdFromToken = TokenConverter.getRelatedPartyIdFromToken(token);

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id", id);

        ProcessFlow processFlow = null;
        if(roles.contains(admin)){
            processFlow = processFlowService.findProcessFlowById(requestParams);
        } else if(relatedPartyIdFromToken!=null) {
            requestParams.put("relatedParty.id", relatedPartyIdFromToken);
            processFlow = processFlowService.findProcessFlowById(requestParams);
        }

        if (processFlow == null) {
            return ResponseEntity.noContent().build();
        }

        flowLinkHandler.addLinks(processFlow);
        return ResponseEntity.ok(processFlow);
    }

}

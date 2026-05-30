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

import com.orange.discobole.processflow.controller.ProcessFlowCommandController;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import com.orange.discobole.processflow.exception.AuthenticationException;
import com.orange.discobole.processflow.handler.FlowLinkHandler;
import com.orange.discobole.processflow.service.ProcessFlowCommandService;
import com.orange.discobole.processflow.util.TokenConverter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Tag(name = "processFlow")
@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
@Component("processFlowCommandController")
public class ProcessFlowCommandControllerImpl implements ProcessFlowCommandController {

    @Resource
    private ProcessFlowCommandService processFlowService;
    @Value("${process-flow.role.admin}")
    private String admin;
    @Resource
    private FlowLinkHandler flowLinkHandler;
    /*
     * @Resource private MongoTemplate mongoTemplate;
     */

    @Override
    public ResponseEntity<ProcessFlow> createProcessFlow(@Valid final ProcessFlowCreate processFlowCreate, String token) {
        List<String> userRoles = TokenConverter.convert(token);

        if (!userRoles.contains(admin)) {
            boolean hasRelatedParties = !CollectionUtils.isEmpty(processFlowCreate.getRelatedParty());

            if (hasRelatedParties) {
                String authenticatedPartyId = TokenConverter.getRelatedPartyIdFromToken(token);

                if (StringUtils.isBlank(authenticatedPartyId)) {
                    throw new AuthenticationException("Authentication failed. Missing party information.");
                }

                boolean isAuthorizedForParty = processFlowCreate.getRelatedParty().stream()
                        .anyMatch(relatedParty -> authenticatedPartyId.equals(relatedParty.getId()));

                if (!isAuthorizedForParty) {
                    throw new AuthenticationException("You are not authorized to create a process flow for this party.");
                }
            }
        }

        final ProcessFlow createdProcessFlow = processFlowService.createProcessFlow(processFlowCreate);
        flowLinkHandler.addLinks(createdProcessFlow);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProcessFlow);
    }

    @Override
    public ResponseEntity<Void> deleteProcessFlow(final String id) {
        processFlowService.deleteProcessFlow(id);
        return ResponseEntity.noContent().build();
    }
}
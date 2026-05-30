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

import com.orange.discobole.processflow.controller.TaskFlowCommandController;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.AuthenticationException;
import com.orange.discobole.processflow.handler.FlowLinkHandler;
import com.orange.discobole.processflow.repository.ProcessFlowRepo;
import com.orange.discobole.processflow.service.TaskFLowCommandService;
import com.orange.discobole.processflow.util.TokenConverter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Tag(name = "processFlow")
@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
@Component("taskFlowCommandController")
public class TaskFlowCommandControllerImpl implements TaskFlowCommandController {

    @Resource
    private TaskFLowCommandService taskFlowService;
    @Resource
    ProcessFlowRepo processFlowRepo;
    @Resource
    private FlowLinkHandler flowLinkHandler;
    @Value("${process-flow.role.admin}")
    private String admin;

    @Override
    public ResponseEntity<TaskFlow> updateTaskFlow(final String processFlowId, final String id, final TaskFlowUpdate taskFlowUpdate, String token) {
        List<String> userRoles = TokenConverter.convert(token);

        if (!userRoles.contains(admin)) {
            String authenticatedPartyId = TokenConverter.getRelatedPartyIdFromToken(token);

            if (StringUtils.isBlank(authenticatedPartyId)) {
                throw new AuthenticationException("Authentication failed. Missing party information.");
            }

            boolean hasRelatedParties = !CollectionUtils.isEmpty(taskFlowUpdate.getRelatedParty());

            if (hasRelatedParties) {
                boolean isAuthorizedForParty = taskFlowUpdate.getRelatedParty().stream()
                        .anyMatch(relatedParty -> authenticatedPartyId.equals(relatedParty.getId()));

                if (!isAuthorizedForParty) {
                    throw new AuthenticationException("You are not authorized to update this task flow.");
                }
            } else {
                Optional<ProcessFlow> processFlow = processFlowRepo.findById(processFlowId);

                if (processFlow.isEmpty()) {
                    throw new ResourceNotFoundException("Process flow not found with id: " + processFlowId);
                }

                List<RelatedParty> existingRelatedParties = processFlow.get().getRelatedParty();

                if (!CollectionUtils.isEmpty(existingRelatedParties)) {
                    boolean isAuthorizedForExistingParty = existingRelatedParties.stream()
                            .anyMatch(relatedParty -> authenticatedPartyId.equals(relatedParty.getId()));

                    if (!isAuthorizedForExistingParty) {
                        throw new AuthenticationException("You are not authorized to update this task flow.");
                    }
                }
            }
        }

        final TaskFlow updatedTask = taskFlowService.updateTaskFlow(processFlowId, id, taskFlowUpdate);
        flowLinkHandler.addLinks(processFlowId, updatedTask);
        return ResponseEntity.ok(updatedTask);
    }
}
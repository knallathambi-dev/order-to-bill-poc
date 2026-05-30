// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.ResolutionState;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProcessFlowManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.FalloutHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessFlowManagementImpl implements ProcessFlowManagement {

    private final WebClientUtil webClientUtil;

    private final DiscoServiceUrl discoServiceUrl;

    private final ObjectMapper objectMapper;

    @Override
    public ProcessFlow getProcessFlowById(String falloutId) {
        String falloutUrl = discoServiceUrl.getFalloutById(falloutId);
        return getProcessFlow(falloutUrl, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public List<ProcessFlow> getProcessFlowByRelatedEntityId(String relatedEntityId) {
        String falloutUrl = discoServiceUrl.getFalloutByRelatedEntityId(relatedEntityId);
        List<ProcessFlow> processFlows = getProcessFlow(falloutUrl, new ParameterizedTypeReference<>() {
        });
        return processFlows.stream().filter(processFlow -> !processFlow.getLinks().getNextTaskstoBePerformed().isEmpty()).toList();
    }

    @Override
    public void createProcessFlow(ProcessFlowCreate processFlowCreate) {
        String falloutUrl = discoServiceUrl.createFallout();
        try {
            webClientUtil.send(HttpMethod.POST, falloutUrl, ProcessFlow.class, objectMapper.writeValueAsString(processFlowCreate), (retryBackoffSpec, retrySignal) -> {
                        WebClientResponseException exception = (WebClientResponseException) retrySignal.failure();
                        return new CoodNonRecoverableAndNonRetryableException(new FalloutHttpFailedException(exception.getResponseBodyAsString(),
                                falloutUrl,
                                exception.getStatusCode(),
                                WebClientRetryStrategy.getExceptionCode(exception),
                                exception));
                    }, processFlowResponse -> log.info("OrderOrchestrationFalloutUrl | OrderOrchestrationFallout | Post method successfully done of fallout : " + processFlowResponse.getId()),
                    new FalloutHttpFailedException());
        } catch (Exception e) {
            log.error("OrchestrationDeliveryFalloutManagementImpl | createFalloutProcess | cannot create fallout process due to error", e);
        }
    }

    @Override
    public void submitResolutionSateWithReason(String url, ResolutionState resolutionState, String reason) {
        ObjectCharacteristic selectResolutionStateWithReasonCharacteristic = new ObjectCharacteristic();
        selectResolutionStateWithReasonCharacteristic.name("SelectResolutionStateWithReason");
        selectResolutionStateWithReasonCharacteristic.setType(ObjectCharacteristic.class.getSimpleName());
        selectResolutionStateWithReasonCharacteristic.setValueType(Object.class.getSimpleName());
        selectResolutionStateWithReasonCharacteristic.value(Map.of(
                "reason", reason,
                "resolutionState", resolutionState.getValue()
        ));
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setCharacteristic(List.of(selectResolutionStateWithReasonCharacteristic));
        try {
            webClientUtil.send(HttpMethod.PATCH, url, TaskFlow.class, objectMapper.writeValueAsString(taskFlow), (retryBackoffSpec, retrySignal) -> {
                        WebClientResponseException exception = (WebClientResponseException) retrySignal.failure();
                        return new CoodNonRecoverableAndNonRetryableException(new FalloutHttpFailedException(exception.getResponseBodyAsString(),
                                url,
                                exception.getStatusCode(),
                                WebClientRetryStrategy.getExceptionCode(exception),
                                exception));
                    }, response -> log.info("OrderOrchestrationFalloutUrl | OrderOrchestrationFallout | Patch method successfully done for fallout task flow id: " + response.getId()),
                    new FalloutHttpFailedException());
        } catch (Exception e) {
            log.error("OrchestrationDeliveryFalloutManagementImpl | submit resolution state with reason | cannot submit resolution state due to error", e);
        }
    }

    private <T> T getProcessFlow(String url, ParameterizedTypeReference<T> typeReference) {
        return webClientUtil.send(HttpMethod.GET, url, typeReference, (retryBackoffSpec, retrySignal) -> {
                    WebClientResponseException exception = (WebClientResponseException) retrySignal.failure();
                    return new CoodNonRecoverableAndNonRetryableException(new FalloutHttpFailedException(exception.getResponseBodyAsString(),
                            url,
                            exception.getStatusCode(),
                            WebClientRetryStrategy.getExceptionCode(exception),
                            exception));
                }, processFlowResponse -> log.info("OrderOrchestrationFalloutUrl | OrderOrchestrationFallout | Get method successfully done of fallout : {}", processFlowResponse),
                new FalloutHttpFailedException());
    }
}

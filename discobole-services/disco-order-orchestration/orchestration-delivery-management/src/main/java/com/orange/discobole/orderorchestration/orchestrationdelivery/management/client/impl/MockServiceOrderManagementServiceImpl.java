// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.httpfailed.ServiceOrderHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.MockMobileServiceOrderBatchingService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceOrderManagementService;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class MockServiceOrderManagementServiceImpl implements ServiceOrderManagementService {

    private final WebClientUtil webClientUtil;

    private final ObjectMapper objectMapper;

    private final MockMobileServiceOrderBatchingService serviceOrderBatchingService;

    @Value("${config.serviceOrderBatching.enabled:true}")
    private boolean serviceOrderBatchingEnabled;

    @Override
    @SneakyThrows
    public Mono<ServiceOrder> createServiceOrderInSOM(ServiceOrder serviceOrderRequest, String serviceOrderingUrl) {
        // Check if batching is enabled and if this is a mobile factory from the url
        if (serviceOrderBatchingEnabled && serviceOrderingUrl.contains("mobile")) {
            return serviceOrderBatchingService.createServiceOrderInSOM(serviceOrderRequest, serviceOrderingUrl);
        } else {
            return webClientUtil.sendMono(HttpMethod.POST, serviceOrderingUrl, ServiceOrder.class, objectMapper.writeValueAsString(serviceOrderRequest), (retryBackoffSpec, retrySignal) -> {
                WebClientResponseException exception = (WebClientResponseException) retrySignal.failure();
                return new CoodNonRecoverableAndNonRetryableException(new ServiceOrderHttpFailedException(exception.getResponseBodyAsString(),
                        serviceOrderingUrl,
                        exception.getStatusCode(),
                        WebClientRetryStrategy.getExceptionCode(exception),
                        exception));
            }, serviceOrder -> log.info("Post method successfully done of ServiceOrderId: {}", serviceOrder.getId()), new ServiceOrderHttpFailedException());
        }
    }
}

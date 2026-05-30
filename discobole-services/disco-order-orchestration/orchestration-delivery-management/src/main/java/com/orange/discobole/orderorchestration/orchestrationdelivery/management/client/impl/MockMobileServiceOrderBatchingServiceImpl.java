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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.httpfailed.ServiceOrderHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.MockMobileServiceOrderBatchingService;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockMobileServiceOrderBatchingServiceImpl implements MockMobileServiceOrderBatchingService {

    private final Sinks.Many<BatchedRequest> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<BatchedRequest> flux = sink.asFlux();

    private final WebClientUtil webClientUtil;
    private final ObjectMapper objectMapper;

    @Value("${config.serviceOrderBatching.maxBatchSize:20}")
    private int maxBatchSize;

    @Value("${config.serviceOrderBatching.batchCollectionSeconds:30}")
    private int batchCollectionSeconds;

    @PostConstruct
    public void postConstruct() {
        flux.bufferTimeout(maxBatchSize, Duration.ofSeconds(batchCollectionSeconds))
                .filter(batch -> !batch.isEmpty())
                .concatMap(this::processBatch) // sequential
                .subscribe();
    }

    @Override
    public Mono<ServiceOrder> createServiceOrderInSOM(ServiceOrder serviceOrderRequest, String somRef) {
        BatchedRequest br = new BatchedRequest(serviceOrderRequest, somRef);

        // Sinks don't accept concurrent calls
        // The fix for this is to retry the emit if the cause of the failure was a concurrent call
        // Emit with a custom handler that retries indefinitely for concurrency issues
        sink.emitNext(br, (signalType, emitResult) ->
            // Only retry if the failure is due to concurrent access (FAIL_NON_SERIALIZED)
            emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED
        );

        // Return the Mono that will be completed when the batch result is available
        return br.getMono();
    }

    /**
     * Process a batch of requests: send one HTTP call, then fan out.
     */
    private Mono<Void> processBatch(List<BatchedRequest> batch) {
        log.debug("Started sending batch service orders {}",
                batch.stream().map(BatchedRequest::getRequest).toList());
        // somRef is the same for all the batch, we can take it from the first element
        String batchSomRef = String.format("%s/batch", batch.get(0).getSomRef());

        // send a list of ServiceOrder requests in one go
        List<ServiceOrder> serviceOrderRequests = batch.stream()
                .map(BatchedRequest::getRequest)
                .toList();

        Mono<List<ServiceOrder>> batchedResponseList;

        try {
            String requestBody = objectMapper.writeValueAsString(serviceOrderRequests);

            batchedResponseList = webClientUtil.sendMono(
                    HttpMethod.POST,
                    batchSomRef,
                    new ParameterizedTypeReference<>() {
                    },
                    requestBody,
                    (retryBackoffSpec, retrySignal) -> {
                        WebClientResponseException exception =
                                (WebClientResponseException) retrySignal.failure();
                        return new CoodNonRecoverableAndNonRetryableException(
                                new ServiceOrderHttpFailedException(
                                        exception.getResponseBodyAsString(),
                                        batchSomRef,
                                        exception.getStatusCode(),
                                        WebClientRetryStrategy.getExceptionCode(exception),
                                        exception
                                )
                        );
                    },
                    responseList -> // do on success function
                            log.debug("Post method successfully done for {} ServiceOrders",
                                    responseList),
                    new ServiceOrderHttpFailedException()
            );

        } catch (JsonProcessingException e) {
            batch.forEach(br -> br.getSink().tryEmitError(e));
            return Mono.empty();
        }

        return batchedResponseList
                .flatMapMany(Flux::fromIterable)  // Flux<ServiceOrder>
                .zipWithIterable(batch)           // pair each response with BatchedRequest
                .doOnNext(tuple -> {
                    ServiceOrder response = tuple.getT1();
                    BatchedRequest br = tuple.getT2();
                    br.getSink().tryEmitValue(response);
                })
                .then()
                .onErrorResume(ex -> {
                    batch.forEach(br -> br.getSink().tryEmitError(ex));
                    return Mono.empty();
                });
    }

    @Getter
    private static class BatchedRequest {
        private final ServiceOrder request;
        private final String somRef;
        private final Mono<ServiceOrder> mono;   // what you return to the caller
        private final Sinks.One<ServiceOrder> sink;

        BatchedRequest(ServiceOrder request, String somRef) {
            this.request = request;
            this.somRef = somRef;
            this.sink = Sinks.one();
            this.mono = sink.asMono();
        }
    }
}

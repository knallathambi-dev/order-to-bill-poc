// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomWebClientInterceptor implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        log.info("Outgoing request - method: [{}], url: [{}]", request.method(), request.url());
        logRequestDetails(request);

        return next.exchange(request)
                .doOnSuccess(response -> logResponseDetails(response, request))
                .doOnError(throwable -> log.error("Request failed - method: [{}], url: [{}], error: [{}]",
                        request.method(), request.url(), throwable.getMessage()));
    }

    private void logRequestDetails(ClientRequest request) {
        log.debug("Request headers - method: [{}], url: [{}], headers: [{}]",
                request.method(), request.url(), request.headers());
    }

    private void logResponseDetails(ClientResponse response, ClientRequest request) {
        int statusCode = response.statusCode().value();

        if (response.statusCode().isError()) {
            log.warn("Response error - method: [{}], url: [{}], status: [{}]]",
                    request.method(), request.url(), statusCode);
        } else {
            log.info("Response received - method: [{}], url: [{}], status: [{}]]",
                    request.method(), request.url(), statusCode);
        }
        log.debug("Response headers - method: [{}], url: [{}], headers: [{}]",
                request.method(), request.url(), response.headers().asHttpHeaders());
    }
}
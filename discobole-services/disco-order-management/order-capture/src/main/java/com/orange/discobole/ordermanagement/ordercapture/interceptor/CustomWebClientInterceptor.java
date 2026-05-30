// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class CustomWebClientInterceptor implements ExchangeFilterFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomWebClientInterceptor.class);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        logRequestDetails(request);
        return next.exchange(request)
                .doOnSuccess(this::logResponseDetails);
    }

    private void logRequestDetails(ClientRequest request) {
        LOGGER.info("Request Method: {}", request.method());
        LOGGER.info("Request URI: {}", request.url());
        LOGGER.info("Request Headers: {}", request.headers());
        // You can access the request body if needed: request.body()
    }

    private void logResponseDetails(ClientResponse response) {
        LOGGER.info("Response Status Code: {}", response.statusCode());
        LOGGER.info("Response Headers: {}", response.headers().asHttpHeaders());
        // You can access the response body if needed: response.bodyToMono(String.class)
    }
}
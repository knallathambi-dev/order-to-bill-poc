// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.handler;

import com.orange.discobole.processflow.exception.AuthenticationException;
import com.orange.discobole.processflow.exception.DiscoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WebClientErrorHandler implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        log.debug("Intercepting outgoing request - method: [{}], url: [{}]", request.method(), request.url());

        return next.exchange(request)
                .doOnError(throwable -> handleError(request, throwable))
                .flatMap(response -> {
                    HttpStatusCode statusCode = response.statusCode();
                    if (!statusCode.isError()) {
                        log.debug("Request completed successfully - method: [{}], url: [{}], status: [{}]",
                                request.method(), request.url(), statusCode.value());
                        return Mono.just(response);
                    } else {
                        log.warn("Request returned error - method: [{}], url: [{}], status: [{}]",
                                request.method(), request.url(), statusCode.value());
                        return handleError(response, request);
                    }
                });
    }

    private void handleError(ClientRequest request, Throwable throwable) {
        log.error("Exception during request execution - method: [{}], url: [{}], error: [{}]",
                request.method(), request.url(), throwable.getMessage(), throwable);
        Mono.error(new DiscoException(
                String.format("Error while call %s with method %s", request.url(), request.method()), throwable));
    }

    private Mono<ClientResponse> handleError(ClientResponse response, ClientRequest request) {
        HttpStatusCode statusCode = response.statusCode();

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            log.error("Authentication failure - received HTTP [{}] UNAUTHORIZED for method: [{}], url: [{}]",
                    statusCode.value(), request.method(), request.url());
            return Mono.error(new AuthenticationException("Unauthorized Request"));
        }

        if (statusCode.is5xxServerError()) {
            log.error("Server error received - HTTP status: [{}], method: [{}], url: [{}]",
                    statusCode.value(), request.method(), request.url());
        } else if (statusCode.is4xxClientError()) {
            log.warn("Client error received - HTTP status: [{}], method: [{}], url: [{}]",
                    statusCode.value(), request.method(), request.url());
        }

        return Mono.just(response);
    }
}
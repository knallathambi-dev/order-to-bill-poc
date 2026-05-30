// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.handler;

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
        return next.exchange(request).doOnError(throwable -> handleError(request, throwable)).flatMap(response -> {
            if (!response.statusCode().isError()) {
                return Mono.just(response);
            } else {
                return handleError(response);
            }
        });
    }

    private Mono<ClientResponse> handleError(ClientRequest request, Throwable throwable) {
        log.error("Error executing request: {} {}", request.method(), request.url(), throwable);
        return Mono.error(new DiscoException(String.format("Error while call %s with method %s", request.url(), request.method()), throwable));
    }

    private Mono<ClientResponse> handleError(ClientResponse response) {
        HttpStatusCode statusCode = response.statusCode();
        log.error("Error response: {}", statusCode);
        if (statusCode == HttpStatus.UNAUTHORIZED) {
            return Mono.error(new AuthenticationException("Unauthorized Request"));
        }
        return Mono.just(response);
    }
}
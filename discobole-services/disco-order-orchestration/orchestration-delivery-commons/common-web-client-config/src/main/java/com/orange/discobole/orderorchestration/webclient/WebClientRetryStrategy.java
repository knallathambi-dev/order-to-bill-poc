// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.webclient;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.*;
import static org.springframework.http.HttpStatus.*;

public class WebClientRetryStrategy {
    private static final Map<HttpStatusCode, ExceptionCode> retriableStatues = Map.of(
            REQUEST_TIMEOUT, HTTP_REQUEST_TIMEOUT,
            TOO_EARLY, HTTP_TOO_EARLY,
            TOO_MANY_REQUESTS, HTTP_TOO_MANY_REQUESTS,
            BAD_GATEWAY, HTTP_BAD_GATEWAY,
            SERVICE_UNAVAILABLE, HTTP_SERVICE_UNAVAILABLE,
            GATEWAY_TIMEOUT, HTTP_GATEWAY_TIMEOUT,
            UNAUTHORIZED, HTTP_UNAUTHORIZED,
            FORBIDDEN, HTTP_FORBIDDEN
    );

    private WebClientRetryStrategy() {
    }

    public static boolean retryStrategy(Throwable throwable) {
        return throwable instanceof WebClientResponseException && retriableStatues.containsKey(((WebClientResponseException) throwable).getStatusCode());
    }

    public static ExceptionCode getExceptionCode(WebClientResponseException exception) {
        if (retryStrategy(exception)) {
            return retriableStatues.get(exception.getStatusCode());
        }

        throw new IllegalStateException("Could not get exception code from retry strategy", exception);
    }
}

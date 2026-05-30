// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestLogger {

    /**
     * asynchronously logs HTTP request
     *
     * @param request incoming request
     * @param body    request body
     */


    @Async
    public void logRequestDetails(final HttpRequest request, final byte[] body) {
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(request.getMethod()).append(": ").append(request.getURI())
                .append(System.lineSeparator()).append("Headers: ").append(request.getHeaders())
                .append(System.lineSeparator()).append("Payload: ============================>")
                .append(System.lineSeparator()).append(new String(body, StandardCharsets.UTF_8));
        log.debug("Request info - {}", logBuilder);
    }

    /**
     * asynchronously logs HTTP response
     *
     * @param response incoming response
     * @throws IOException signal for IOException
     */
    @Async
    public void logResponseDetails(final ClientHttpResponse response) throws IOException {
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(response.getStatusCode()).append(": ").append(response.getStatusText())
                .append(System.lineSeparator()).append("Headers: ").append(response.getHeaders());
        log.debug("Response info - {}", logBuilder);
    }
}
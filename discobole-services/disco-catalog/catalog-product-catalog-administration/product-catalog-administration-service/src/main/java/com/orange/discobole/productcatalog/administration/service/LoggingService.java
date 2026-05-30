// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger LOGGER = LogManager.getLogger(LoggingService.class);

    @Async
    public void logRequestDetails(final HttpRequest request, final byte[] body) {
        String logMessage = request.getMethod() + ": " + request.getURI().toString()
                + System.lineSeparator() + "Headers: " + request.getHeaders()
                + System.lineSeparator() + "Payload: ============================>"
                + System.lineSeparator() + new String(body, StandardCharsets.UTF_8);
        LOGGER.debug("Request info - {}", logMessage);
    }

    @Async
    public void logResponseDetails(final ClientHttpResponse response) throws IOException {
        String logMessage = response.getStatusCode() + ": " + response.getStatusText()
                + System.lineSeparator() + "Headers: " + response.getHeaders();
        LOGGER.debug("Response info - {}", logMessage);
    }
}

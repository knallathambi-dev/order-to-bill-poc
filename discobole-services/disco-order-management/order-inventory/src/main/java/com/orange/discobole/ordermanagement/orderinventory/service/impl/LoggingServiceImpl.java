// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.orderinventory.service.LoggingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Objects;

@Service
public class LoggingServiceImpl implements LoggingService {

    private final Logger log = LoggerFactory.getLogger(LoggingServiceImpl.class);

    private final ObjectMapper objectMapper;
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public LoggingServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        // Extract request details
        String method = requestWrapper.getMethod();
        String uri = requestWrapper.getRequestURI();
        String queryString = requestWrapper.getQueryString();
        // Log request details
        log.debug("Received {} request to {} with queryString {}", method, uri, queryString);
        String requestBody = getJsonFromObject(body);
        log.debug("Request Body: {}", requestBody);
    }

    private String getJsonFromObject(Object body) {
        if (Objects.nonNull(body)) {
            try {
                return objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                log.error("Error while print request body {} ", e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        // Log responseBody
        String responseBody = getJsonFromObject(body);
        log.debug("Response Body: {}", responseBody);

        // Log response status
        int status = httpServletResponse.getStatus();
        log.debug("Response Status: {}", status);
    }
}
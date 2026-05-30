// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.service.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Objects;

@Service
@Slf4j
public class LoggingServiceImpl implements LoggingService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        // Extract request details
        String method = sanitizeInput(requestWrapper.getMethod());
        String url = sanitizeInput(requestWrapper.getRequestURI());
        String queryString = sanitizeInput(requestWrapper.getQueryString());
        // Log request details
        log.debug("Received {} request to {} ?", method, url, queryString);
        String requestBody = sanitizeInput(getJsonFromObject(body));
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
        log.debug("Response body: {}", responseBody);
        // Log response status
       int status = httpServletResponse.getStatus();
        log.debug("Response Status: {}", status);

    }


    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        // Remove CRLF sequences
        return input.replace("\r", "").replace("\n", "");
    }


}

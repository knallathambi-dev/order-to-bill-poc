// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.productofferingprice.service.LoggingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Objects;

@Service

public class LoggingServiceImpl implements LoggingService {



    private ObjectMapper objectMapper;

    @Autowired
    public LoggingServiceImpl(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }
    private static final Logger LOGGER = LogManager.getLogger(LoggingServiceImpl.class);

    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        // Extract request details
        String method = requestWrapper.getMethod();
        String url = requestWrapper.getRequestURI();

        // Log request details
        LOGGER.debug("Received {} request to {} ?",method, url);
        String requestBody = getJsonFromObject(body);
        LOGGER.debug("Request Body: {}", requestBody);
    }

    private String getJsonFromObject(Object body) {
        if (Objects.nonNull(body)) {
            try {
                return objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                LOGGER.error("Error while print request body {} ", e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        // Log responseBody
        String responseBody = getJsonFromObject(body);
        LOGGER.debug("Response Body: {}", responseBody);

        // Log response status
        int status = httpServletResponse.getStatus();
        LOGGER.debug("Response Status: {}", status);
    }

}

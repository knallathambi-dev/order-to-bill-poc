package com.orange.discobole.eventservice.exception;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * RestTemplateResponseErrorHandler class is used to handle errors which might
 * come while calling restTemplate methods.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger LOGGER = LogManager.getLogger(RestTemplateResponseErrorHandler.class);
    /*
     * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.
     * springframework.http.client.ClientHttpResponse)
     */
    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        HttpStatusCode res = httpResponse.getStatusCode();
        LOGGER.info("http status code for the response -{}",res);
        if (HttpStatus.UNAUTHORIZED == res)
            throw new AuthenticationException("Missing credentials");
        throw new DiscoClientException(httpResponse.getStatusText());
    }

    /*
     * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.
     * springframework.http.client.ClientHttpResponse)
     */
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        Series error = ((HttpStatus) httpResponse.getStatusCode()).series();
        LOGGER.info("http status code for the response -{}",httpResponse.getStatusCode());
        return error == Series.CLIENT_ERROR || error == Series.SERVER_ERROR;
    }

}


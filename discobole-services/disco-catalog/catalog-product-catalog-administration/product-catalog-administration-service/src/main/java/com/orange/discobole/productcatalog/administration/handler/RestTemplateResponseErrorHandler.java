// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.handler;

import java.io.IOException;

import com.orange.discobole.productcatalog.administration.exception.AuthenticationException;
import com.orange.discobole.productcatalog.administration.exception.DiscoClientException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

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
        return error == HttpStatus.Series.CLIENT_ERROR || error == HttpStatus.Series.SERVER_ERROR;
    }

}






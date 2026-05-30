// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Interceptor to log incoming requests and outgoing response.
 *
 * @author Prateek Gupta
 * @since 1.1
 *
 */

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger LOGGER = LogManager.getLogger(RestTemplateInterceptor.class);
    

	/**
	 * Intercept.
	 *
	 * @param request   the request
	 * @param body      the body
	 * @param execution the execution
	 * @return the client http response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/*
	 * @see
	 * org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.
	 * springframework.http.HttpRequest, byte[],
	 * org.springframework.http.client.ClientHttpRequestExecution)
	 */
	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		logRequestDetails(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponseDetails(response);
		return response;
	}

	/**
	 * asynchronously logs HTTP request
	 *
	 * @param request incoming request
	 * @param body    request body
	 */
	public void logRequestDetails(final HttpRequest request, final byte[] body) {
			final StringBuilder logBuilder = new StringBuilder();
			logBuilder.append(request.getMethod()).append(": ").append(request.getURI().toString())
					.append(System.lineSeparator()).append("Payload: ============================>")
					.append(System.lineSeparator()).append(new String(body, StandardCharsets.UTF_8));
			LOGGER.debug("Request info - {}", logBuilder);
	}

	/**
	 * asynchronously logs HTTP response
	 *
	 * @param response incoming response
	 * @throws IOException signal for IOException
	 */
	public void logResponseDetails(final ClientHttpResponse response) throws IOException {
			final StringBuilder logBuilder = new StringBuilder();
            try {
                logBuilder.append(response.getStatusCode()).append(": ").append(response.getStatusText())
                        .append(System.lineSeparator()).append("Headers: ").append(response.getHeaders());
            } catch (IOException e) {
				LOGGER.debug("Response error : {}", e.getMessage());
            }
            LOGGER.debug("Response info - {}", logBuilder);
		}
	}
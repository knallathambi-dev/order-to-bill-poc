// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.config;

import com.orange.discobole.processflow.handler.RestTemplateResponseErrorHandler;
import com.orange.discobole.productcatalog.productoffering.interceptor.RestTemplateInterceptor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * The RestTemplateConfig class is used to configure RestTemplate.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Configuration
public class RestTemplateConfig {

	/**
	 * Creates RestTemplate bean with HttpClient 5 timeouts, error handler and interceptor.
	 *
	 * @return configured {@link org.springframework.web.client.RestTemplate}
	 */
	@Bean
	public RestTemplate restTemplate() {
		// Define the timeout configuration using the HttpClient 5 API
		RequestConfig requestConfig = RequestConfig.custom()
				// Timeout for obtaining a connection from the pool
				.setConnectionRequestTimeout(Timeout.ofSeconds(5))
				// Timeout for waiting for the response after the request is sent
				.setResponseTimeout(Timeout.ofSeconds(15))
				.build();

		// Create the Apache HttpClient with the timeout configuration
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();

		// Plug HttpClient into Spring's RestTemplate
		HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);

		RestTemplate restClient = new RestTemplate(requestFactory);
		restClient.setErrorHandler(new RestTemplateResponseErrorHandler());
		restClient.getInterceptors().add(new RestTemplateInterceptor());
		return restClient;
	}
}

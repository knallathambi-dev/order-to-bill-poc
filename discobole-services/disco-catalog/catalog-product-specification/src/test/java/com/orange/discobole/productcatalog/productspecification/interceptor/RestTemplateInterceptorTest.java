// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.interceptor;

import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.config.RestTemplateConfig;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for interceptor to log incoming requests and outgoing response.
 *
 * @author Piyush Goel
 * @since 1.0
 *
 */
@ContextConfiguration(classes = { RestTemplateConfig.class })
class RestTemplateInterceptorTest extends ProductSpecificationApplicationTests {

	@Resource
	private RestTemplate restTemplate;

	private ClientHttpRequestInterceptor clientHttpRequestInterceptor;

	@BeforeEach
	void setUp() {
		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		if (interceptors.isEmpty())
			fail();
		clientHttpRequestInterceptor = Mockito.spy(interceptors.get(0));
		interceptors.removeAll(interceptors);
		interceptors.add(clientHttpRequestInterceptor);
		restTemplate.setInterceptors(interceptors);
	}

	@Test
	void givenRestTemplate_whenRequested_then_VerifyInvocationOfInterceptMethod() throws IOException {
		String mockUrl = "http://localhost/mockEndPoint";

		try {
			restTemplate.exchange(mockUrl, HttpMethod.POST, null, String.class);
			verify(clientHttpRequestInterceptor, times(1)).intercept(any(HttpRequest.class), any(byte[].class),
					any(ClientHttpRequestExecution.class));
		} catch (DiscoClientException e) {
			verify(clientHttpRequestInterceptor, times(1)).intercept(any(HttpRequest.class), any(byte[].class),
					any(ClientHttpRequestExecution.class));
		} catch (ResourceAccessException e) {
			verify(clientHttpRequestInterceptor, times(1)).intercept(any(HttpRequest.class), any(byte[].class),
					any(ClientHttpRequestExecution.class));
		}
	}

}

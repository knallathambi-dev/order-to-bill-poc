// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import com.orange.discobole.processflow.handler.RestTemplateResponseErrorHandler;
import com.orange.discobole.productcatalog.catalog.interceptor.RestTemplateInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * The RestTemplateConfig class is used to do configuration for rest template
 * 
 * @author Piyush Goel
 * @since 1.0
 */
@Configuration
public class RestTemplateConfig {

	/**
	 * Creates rest template bean
	 *
	 * @return {@code org.springframework.web.client.RestTemplate}
	 */
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restClient = new RestTemplate();
		restClient.setErrorHandler(new RestTemplateResponseErrorHandler());
		restClient.getInterceptors().add(new RestTemplateInterceptor());
		return restClient;
	}
}

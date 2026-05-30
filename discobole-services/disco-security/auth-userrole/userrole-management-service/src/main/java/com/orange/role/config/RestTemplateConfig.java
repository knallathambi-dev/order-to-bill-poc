// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.orange.role.interceptor.RestTemplateInterceptor;

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
		restClient.getInterceptors().add(new RestTemplateInterceptor());
		return restClient;
	}

}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.orange.discobole.productcatalog.catalog.interceptor.InvalidRequestParamInterceptor;

/**
 * Invalid Request Param Interceptor Config is to register Invalid Request Param
 * Interceptor into Interceptor Registry.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

@Component
public class InvalidRequestParamInterceptorConfig implements WebMvcConfigurer {
	
	private final InvalidRequestParamInterceptor invalidRequestParamInterceptor;

	@Autowired
	public InvalidRequestParamInterceptorConfig(InvalidRequestParamInterceptor invalidRequestParamInterceptor) {
		this.invalidRequestParamInterceptor = invalidRequestParamInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(invalidRequestParamInterceptor);
	}
}

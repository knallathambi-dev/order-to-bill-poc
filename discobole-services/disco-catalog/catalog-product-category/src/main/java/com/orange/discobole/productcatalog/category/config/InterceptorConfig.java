// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;

/**
 * Defines callback methods to customize the Java-based configuration.
 * <p>
 * Configuration classes may implement this interface to be called back and
 * given a chance to customize the default configuration.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

	/**
	 * Adds the interceptors.
	 *
	 * @param registry the interceptor registry
	 */
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(accessTokenInterceptor());
	}

	/**
	 * Access token interceptor.
	 *
	 * @return the access token interceptor
	 */
	@Bean
	public AccessTokenInterceptor accessTokenInterceptor() {
		return new AccessTokenInterceptor();
	}

}

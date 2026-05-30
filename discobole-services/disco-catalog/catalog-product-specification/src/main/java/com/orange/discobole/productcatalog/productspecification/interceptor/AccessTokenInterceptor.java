// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Implementation of pre interceptors to get the authorization token.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class AccessTokenInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = LogManager.getLogger(AccessTokenInterceptor.class);
	private ThreadLocal<String> token = new ThreadLocal<>();

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
		LOGGER.info("calling preHandle {}",Thread.currentThread().getName());
		token.set(request.getHeader(HttpHeaders.AUTHORIZATION));
		return true;
	}

	/**
	 * @return authorization token
	 */
	public String getToken() {
		return token.get();
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		LOGGER.info("calling afterCompletion {}",Thread.currentThread().getName());
		token.remove();
	}
}

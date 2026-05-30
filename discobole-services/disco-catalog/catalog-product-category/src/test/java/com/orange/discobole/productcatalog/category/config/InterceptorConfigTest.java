// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.InterceptorConfig;
import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;



class InterceptorConfigTest extends CategoryApplicationTests {

	@InjectMocks
	private InterceptorConfig interceptorConfig;

	@Test
	void testAccessTokenInterceptor() {
		AccessTokenInterceptor resp = interceptorConfig.accessTokenInterceptor();
		Assertions.assertNotNull(resp);
	}

	@Test
	void testAddInterceptors() {
		InterceptorRegistry registry = Mockito.mock(InterceptorRegistry.class);
		interceptorConfig.addInterceptors(registry);
		Assertions.assertNotNull(registry);
	}
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.interceptor;

import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

 class AccessTokenInterceptorTest extends ManageLifeCycleApplicationTests {
@InjectMocks
private AccessTokenInterceptor accessToken;
@Mock
private  ThreadLocal<String> token;
@Mock
HttpServletRequest request;

@Mock
HttpServletResponse response;

@BeforeEach
 void init() {
	ReflectionTestUtils.setField(accessToken,"token", token);
}
@Test
void testPreHandle() throws Exception {
	when(request.getHeader(anyString())).thenReturn("fvfrvvrvvrve");
	
		boolean flag=accessToken.preHandle(request, response, accessToken);
		
		assertEquals(true,flag);
	
	}
@Test
void testGetToken() {
	String fetchedToken =accessToken.getToken();
	assertNull(fetchedToken);
}
}


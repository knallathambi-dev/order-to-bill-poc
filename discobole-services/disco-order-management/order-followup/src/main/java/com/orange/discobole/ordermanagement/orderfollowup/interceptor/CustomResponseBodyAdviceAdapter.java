// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.interceptor;

import com.orange.discobole.ordermanagement.orderfollowup.service.LoggingService;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class CustomResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {
    private final LoggingService loggingService;

    public CustomResponseBodyAdviceAdapter(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        String requestUri = serverHttpRequest.getURI().getPath();
        if (!requestUri.startsWith("/actuator") &&
                serverHttpRequest instanceof ServletServerHttpRequest servletRequest &&
                serverHttpResponse instanceof ServletServerHttpResponse servletResponse) {

            loggingService.logResponse(
                    servletRequest.getServletRequest(),
                    servletResponse.getServletResponse(),
                    body);
        }
        return body;
    }
}

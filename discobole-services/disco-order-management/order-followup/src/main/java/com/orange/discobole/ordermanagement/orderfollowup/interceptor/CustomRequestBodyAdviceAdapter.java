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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@ControllerAdvice
public class CustomRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {
    private final LoggingService loggingService;

    private final HttpServletRequest httpServletRequest;

    public CustomRequestBodyAdviceAdapter(LoggingService loggingService, HttpServletRequest httpServletRequest) {
        this.loggingService = loggingService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        if (!httpServletRequest.getRequestURI().contains("actuator")) {
            loggingService.logRequest(httpServletRequest, body);
        }
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
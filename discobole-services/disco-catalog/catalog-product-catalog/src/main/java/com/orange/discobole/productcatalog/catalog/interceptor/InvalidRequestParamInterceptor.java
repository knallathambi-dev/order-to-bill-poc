// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.orange.discobole.productcatalog.catalog.exception.DiscoException;

import java.util.*;
import java.util.stream.Stream;

/**
 * Invalid Request Param Interceptor is to check if any invalid request
 * parameter is passed in URL.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

@Component
public class InvalidRequestParamInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(handler instanceof HandlerMethod handlerMethod){
            HandlerMethod m = handlerMethod;

            List<String> expectedParameters = Stream.of(m.getMethodParameters())
                    .flatMap(p -> Stream.of(p.getParameterAnnotation(RequestParam.class)))
                    .filter(Objects::nonNull)
                    .map(RequestParam::name).toList();

            List<String> requestParameters = Collections.list(request.getParameterNames());

            requestParameters.removeAll(expectedParameters);
            if (!requestParameters.isEmpty()) {
                throw new DiscoException("Invalid request parameter(s) entered: " + requestParameters);
            }
        }
        return true;
    }
}

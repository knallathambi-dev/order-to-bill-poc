// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.interceptor;

import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.exception.model.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;
import java.util.stream.Stream;

@Component
public class EmptyQueryParamValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod methodHandler) {
            List<String> expectedParameters = Stream.of(methodHandler.getMethodParameters())
                    .flatMap(p -> Stream.of(p.getParameterAnnotation(RequestParam.class)))
                    .filter(Objects::nonNull)
                    .map(RequestParam::name).toList();

            List<String> requestParameters = Collections.list(request.getParameterNames());
            Map<String, String[]> parameterMap = new LinkedHashMap<>(request.getParameterMap());
            List<String> unexpectedParameters = new ArrayList<>(requestParameters);
            unexpectedParameters.removeAll(expectedParameters);
            if (!unexpectedParameters.isEmpty()) {
                throw new ProductOrderInventoryException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER.getCode(), ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER.getStatus(), BusinessException.UNSUPPORTED_FILTER + String.join(", ", unexpectedParameters));
            }

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String param = entry.getKey();
                String[] paramValues = entry.getValue();
                if (paramValues != null && paramValues.length == 1 && paramValues[0].isEmpty()) {
                    throw new ProductOrderInventoryException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER.getCode(), ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER.getStatus(),
                            param + BusinessException.NOT_BE_EMPTY);
                }
            }
        }
        return true;
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.interceptor;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.NOT_BE_EMPTY;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.UNSUPPORTED_FILTER;

@Component
public class EmptyQueryParamValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod methodHandler = (HandlerMethod) handler;
            List<String> expectedParameters = Stream.of(methodHandler.getMethodParameters())
                    .flatMap(p -> Stream.of(
                            p.getParameterAnnotation(RequestParam.class),
                            p.getParameterAnnotation(RequestPart.class)
                    ))
                    .filter(Objects::nonNull)
                    .map(annotation -> {
                        if (annotation instanceof RequestParam requestParamAnnotation) {
                            return requestParamAnnotation.name();
                        } else if (annotation instanceof RequestPart requestPartAnnotation) {
                            return requestPartAnnotation.name();
                        }
                        return null;
                    })
                    .toList();
            List<String> requestParameters = new ArrayList<>();
            Map<String, String[]> parameterMap = new HashMap<>();
            buildParameterAndParts(request, requestParameters, parameterMap);
            List<String> unexpectedParameters = new ArrayList<>(requestParameters);
            unexpectedParameters.removeAll(expectedParameters);
            if (!unexpectedParameters.isEmpty()) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), UNSUPPORTED_FILTER + String.join(", ", unexpectedParameters));
            }

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String param = entry.getKey();
                String[] paramValues = Arrays.stream(entry.getValue())
                        .filter(Objects::nonNull)
                        .toArray(String[]::new);
                if (paramValues != null && paramValues.length == 1 && paramValues[0].isEmpty()) {
                    throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(),
                            param + NOT_BE_EMPTY);
                }
            }
        }
        return true;
    }

    public void buildParameterAndParts(HttpServletRequest request, List<String> requestParameters, Map<String, String[]> parameterMap) throws Exception {
        requestParameters.addAll(Collections.list(request.getParameterNames()));
        parameterMap.clear();
        parameterMap.putAll(request.getParameterMap());
        try {
            String contentType = request.getContentType();
            if (contentType != null && (contentType.startsWith("multipart/form-data") || contentType.startsWith("multipart/mixed"))) {
                for (Part part : request.getParts()) {
                    String partName = part.getName();
                    String submittedFileName = part.getSubmittedFileName();
                    if (submittedFileName != null) {
                        parameterMap.put(partName, new String[]{submittedFileName});
                        requestParameters.add(partName);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error retrieving parts from the request.", e);
        }

    }
}
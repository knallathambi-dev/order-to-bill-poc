// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model.httpfailed;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.CoodHttpFailedException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class ServiceCatalogHttpFailedException extends CoodHttpFailedException {
    public ServiceCatalogHttpFailedException(){}

    public ServiceCatalogHttpFailedException(String message, String uriString, HttpStatusCode statusCode, WebClientResponseException webClientResponseException) {
        super(message, uriString, statusCode, ExceptionCode.HTTP_INVALID_RESPONSE, webClientResponseException);
        super.setStackTrace(webClientResponseException.getStackTrace());
        this.coodError = CoodError.builder()
                .message(exceptionCode.getMessagePattern().formatted(uriString, message, statusCode))
                .reason(exceptionCode.getReason())
                .code(exceptionCode.getCode())
                .build();
    }

    public ServiceCatalogHttpFailedException(String message, String uriString, HttpStatusCode statusCode, ExceptionCode exceptionCode) {
        super(message, uriString, statusCode, exceptionCode);
        this.coodError = CoodError.builder()
                .message(exceptionCode.getMessagePattern().formatted(uriString, message))
                .reason(exceptionCode.getReason())
                .code(exceptionCode.getCode())
                .build();
    }

    public String getLogMessage() {
        return "%s | COOD_SERVICE_CATALOG_HTTP_FAILED_EXCEPTION | URL: {%s} | HTTP_STATUS: {%s} | message: %s".formatted(getExceptionCode(),
                getUrl(), getHttpStatus(), getMessage());
    }

}

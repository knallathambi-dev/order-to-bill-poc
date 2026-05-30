// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public abstract class CoodHttpFailedException extends CoodException {

    protected ExceptionCode exceptionCode;

    @Getter
    private String url;

    @Getter
    private HttpStatusCode httpStatus;

    public CoodHttpFailedException() {
        super();
    }

    public CoodHttpFailedException(ExceptionCode exceptionCode, Exception exception, Object... parameters) {
        super(CoodError.builder()
                .reason(exceptionCode.getReason())
                .message(exceptionCode.getMessagePattern().formatted(parameters))
                .code(exceptionCode.getCode()).build());
        super.addSuppressed(exception);
        super.setStackTrace(exception.getStackTrace());
    }

    protected CoodHttpFailedException(String message, String url, HttpStatusCode statusCode) {
        super(message);
        this.url = url;
        this.httpStatus = statusCode;
    }

    protected CoodHttpFailedException(String message, String url, HttpStatusCode statusCode, ExceptionCode exceptionCode) {
        super(message);
        this.url = url;
        this.httpStatus = statusCode;
        this.exceptionCode = exceptionCode;
    }

    protected CoodHttpFailedException(String message, String url, WebClientResponseException webClientResponseException) {
        super(message, webClientResponseException);
        this.url = url;
        this.httpStatus = null;
    }

    protected CoodHttpFailedException(String message, String uriString, HttpStatusCode statusCode, WebClientResponseException webClientResponseException) {
        this(message, uriString, webClientResponseException);
        this.httpStatus = statusCode;
        super.setStackTrace(webClientResponseException.getStackTrace());
    }

    protected CoodHttpFailedException(String message, String uriString, HttpStatusCode statusCode, ExceptionCode exceptionCode, WebClientResponseException webClientResponseException) {
        super(message, webClientResponseException);
        this.httpStatus = statusCode;
        this.url = uriString;
        this.exceptionCode = exceptionCode;
        super.setStackTrace(webClientResponseException.getStackTrace());
    }

    public String getExceptionCode() {
        return exceptionCode.toString();
    }

    public String getMessage() {
        return "COOD_HTTP_FAILED_EXCEPTION | URL: {%s} | HTTP_STATUS: {%s} | message: %s".formatted(url, this.httpStatus, this.coodError.message());
    }
}

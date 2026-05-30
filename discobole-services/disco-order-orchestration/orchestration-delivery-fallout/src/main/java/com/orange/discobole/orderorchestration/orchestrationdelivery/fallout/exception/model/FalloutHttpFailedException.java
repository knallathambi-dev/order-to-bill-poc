// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public abstract class FalloutHttpFailedException extends FalloutException implements FalloutKafkaException {

    private static final ExceptionCode exceptionCode = ExceptionCode.FALLOUT_HTTP_FAILED_EXCEPTION;

    @Getter
    private final String url;

    @Getter
    private HttpStatusCode httpStatus;

    protected FalloutHttpFailedException(String message, String url, HttpStatusCode statusCode) {
        super(message);
        this.url = url;
        this.httpStatus = statusCode;
    }

    protected FalloutHttpFailedException(String message, String url, WebClientResponseException webClientResponseException) {
        super(message, webClientResponseException);
        this.url = url;
        this.httpStatus = null;
    }

    protected FalloutHttpFailedException(String message, String uriString, HttpStatusCode statusCode, WebClientResponseException webClientResponseException) {
        this(message, uriString, webClientResponseException);
        this.httpStatus = statusCode;
        super.setStackTrace(webClientResponseException.getStackTrace());
    }

    @Override
    public boolean getIsHandled() {
        return this.isHandled;
    }

    @Override
    public void setIsHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }

    public String getExceptionCode() {
        return exceptionCode.toString();
    }

    public String getLogMessage() {
        return "FALLOUT_HTTP_FAILED_EXCEPTION | URL: {%s} | HTTP_STATUS: {%s} | message: %s\n%s".formatted(url, this.httpStatus, getMessage(), super.getStackTrace());
    }
}

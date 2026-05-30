// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed;

import com.orange.discobole.orderorchestration.exception.model.CoodHttpFailedException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class CPIBHttpFailedException extends CoodHttpFailedException {

    public CPIBHttpFailedException() {
        super();
    }


    public CPIBHttpFailedException(String message, String url, HttpStatusCode statusCode) {
        super(message, url, statusCode);
    }

    public CPIBHttpFailedException(String message, String url, WebClientResponseException webClientResponseException) {
        super(message, url, webClientResponseException);
    }

    public CPIBHttpFailedException(String message, String uriString, HttpStatusCode statusCode, WebClientResponseException webClientResponseException) {
        super(message, uriString, statusCode, webClientResponseException);
        super.setStackTrace(webClientResponseException.getStackTrace());
    }

    public CPIBHttpFailedException(String message, String uriString, HttpStatusCode statusCode, ExceptionCode exceptionCode, WebClientResponseException webClientResponseException) {
        super(message, uriString, statusCode, exceptionCode, webClientResponseException);
        super.setStackTrace(webClientResponseException.getStackTrace());
        this.coodError = CoodError.builder()
                        .message(exceptionCode.getMessagePattern().formatted(uriString, message))
                        .reason(exceptionCode.getReason())
                        .code(exceptionCode.getCode())
                .build();
    }

    public String getLogMessage() {
        return "%s | COOD_CPIB_HTTP_FAILED_EXCEPTION | URL: {%s} | HTTP_STATUS: {%s} | message: %s".formatted(getExceptionCode(),
                getUrl(), getHttpStatus(), getMessage());
    }

}

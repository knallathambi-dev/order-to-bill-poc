// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.units.exception;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionResponse;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Error;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.RestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void handleOrchestrationPlanNotFoundException() {
        OrchestrationPlanNotFoundException ex = new OrchestrationPlanNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND, UUID.randomUUID().toString());
        ResponseEntity<Error> response = handler.handle(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleNoHandlerFoundException() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/path", null);
        ResponseEntity<ExceptionResponse> response = handler.handleNoHandlerFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleHttpRequestMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        ResponseEntity<ExceptionResponse> response = handler.handleHttpRequestMethodNotSupported(ex);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void handleMismatchedInputException() {
        TypeMismatchException ex = new TypeMismatchException("value", String.class);
        ResponseEntity<ExceptionResponse> response = handler.handleMismatchedInputException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");
        ResponseEntity<ExceptionResponse> response = handler.handleIllegalArgumentException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

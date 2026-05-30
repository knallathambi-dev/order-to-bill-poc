// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.handler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.Error;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutIncidentApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestErrorCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestHttpStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

    @ExceptionHandler(FalloutIncidentApiQueryParamException.class)
    @ResponseBody
    public ResponseEntity<Error> handle(FalloutIncidentApiQueryParamException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Error.builder()
                        .code(e.getCode())
                        .reason(e.getReason())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(FalloutException.class)
    @ResponseBody
    public ResponseEntity<Error> handle(FalloutException e) {
        RestErrorCode restErrorCode = RestHttpStatusMapper.getRestErrorCode(ExceptionCode.valueOf(e.getCode()));
        return ResponseEntity.status(restErrorCode.getHttpStatus())
                .body(Error.builder()
                        .code(String.valueOf(restErrorCode.getErrorCode()))
                        .reason(e.getMessage())
                        .message(restErrorCode.getMessage())
                        .build());
    }

    // this is added for security protection
    // so if any error fails with an exception that we didn't handle the internal info is not exposed
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Error> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(FALLOUT_INTERNAL_SERVER_EXCEPTION.getHttpStatus())
                .body(Error.builder()
                        .code("21")
                        .reason("Internal error")
                        .status("INTERNAL_SERVER_ERROR")
                        .message("An unexpected error occurred. Please contact support.")
                        .build());
    }
}

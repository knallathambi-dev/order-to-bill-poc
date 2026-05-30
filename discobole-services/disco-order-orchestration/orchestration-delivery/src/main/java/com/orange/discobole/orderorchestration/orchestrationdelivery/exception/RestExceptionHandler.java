// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.exception;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Error;
import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionResponse;
import com.orange.discobole.orderorchestration.exception.model.constants.RestErrorCode;
import com.orange.discobole.orderorchestration.exception.model.constants.RestHttpStatusMapper;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.OrchestrationPlanValidationException;
import com.orange.discobole.orderorchestration.exception.model.validations.PlanApiQueryParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Locale;

import static com.orange.discobole.orderorchestration.exception.model.enums.ErrorCodeEnum.*;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(CoodException.class)
    @ResponseBody
    public ResponseEntity<Error> handle(CoodException e) {
        RestErrorCode restErrorCode = RestHttpStatusMapper.getRestErrorCode(ExceptionCode.valueOf(e.getCode()));
        return ResponseEntity.status(restErrorCode.getHttpStatus())
                .body(Error.builder()
                        .code(String.valueOf(restErrorCode.getErrorCode()))
                        .reason(e.getMessage())
                        .message(restErrorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(PlanApiQueryParamException.class)
    @ResponseBody
    public ResponseEntity<Error> handle(PlanApiQueryParamException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Error.builder()
                        .code(e.getCode())
                        .reason(e.getCoodError().reason())
                        .message(e.getCoodError().message())
                        .build());
    }

    @ExceptionHandler(OrchestrationPlanNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Error> handle(OrchestrationPlanNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Error.builder()
                        .code(String.valueOf(60))
                        .reason(e.getCoodError().reason())
                        .message(e.getCoodError().message())
                        .build());
    }

    @ExceptionHandler(OrchestrationPlanValidationException.class)
    @ResponseBody
    public ResponseEntity<Error> handle(OrchestrationPlanValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Error.builder()
                        .code(String.valueOf(e.getHttpCode()))
                        .reason(e.getCoodError().reason())
                        .message(e.getCoodError().message())
                        .build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error(HttpStatus.NOT_FOUND.getReasonPhrase(), ex);
        ExceptionResponse notFoundExceptionException = new ExceptionResponse(
                HttpStatus.NOT_FOUND,
                RESOURCE_NOT_FOUND.getCode(),
                RESOURCE_NOT_FOUND.getStatus(),
                "Recheck this URL: " + ex.getRequestURL()
        );
        return new ResponseEntity<>(notFoundExceptionException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> "%s %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        String message = String.join(", ", errors);
        ExceptionResponse invalidParameterException = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                MISSING_INPUT.getCode(),
                MISSING_INPUT.getStatus(),
                message
        );
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), ex);
        ExceptionResponse methodNotAllowedException = new ExceptionResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                METHOD_NOT_ALLOWED.getCode(),
                METHOD_NOT_ALLOWED.getStatus(),
                ex.getMessage()
        );
        return new ResponseEntity<>(methodNotAllowedException, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handleBindException(BindException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        String errorMessage = null;
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            errorMessage = fieldError.getDefaultMessage();
        } else {
            errorMessage = "Error while Bind Field.";
        }
        ExceptionResponse invalidParameterException = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                MISSING_INPUT.getCode(),
                MISSING_INPUT.getStatus(),
                errorMessage
        );
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.beans.NotReadablePropertyException.class)
    public ResponseEntity<ExceptionResponse> handleNotReadablePropertyException(org.springframework.beans.NotReadablePropertyException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                MISSING_INPUT.getCode(),
                "Invalid property access",
                ex.getMessage()
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({TypeMismatchException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionResponse> handleMismatchedInputException(TypeMismatchException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        String propertyName = ex.getPropertyName();
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                String.format("%s value is not a valid type", propertyName)
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                ex.getMessage()
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR.getCode(),
                INTERNAL_ERROR.getStatus(),
                "An unexpected error occurred. Please contact support."
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

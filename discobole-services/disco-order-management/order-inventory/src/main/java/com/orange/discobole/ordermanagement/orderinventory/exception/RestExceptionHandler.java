// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.exception;

import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum;
import com.orange.discobole.ordermanagement.orderinventory.exception.model.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error(HttpStatus.NOT_FOUND.getReasonPhrase(), ex);
        ExceptionResponse notFoundExceptionException = new ExceptionResponse(HttpStatus.NOT_FOUND, ErrorCodeEnum.RESOURCE_NOT_FOUND.getCode(), ErrorCodeEnum.RESOURCE_NOT_FOUND.getStatus(), "Recheck this URL: " + ex.getRequestURL());
        return new ResponseEntity<>(notFoundExceptionException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();
        String message = String.join(", ", errors);
        ExceptionResponse invalidParameterException = new ExceptionResponse(HttpStatus.BAD_REQUEST, ErrorCodeEnum.MISSING_INPUT.getCode(), ErrorCodeEnum.MISSING_INPUT.getStatus(), message);
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), ex);
        ExceptionResponse methodNotAllowedException = new ExceptionResponse(HttpStatus.METHOD_NOT_ALLOWED, ErrorCodeEnum.METHOD_NOT_ALLOWED.getCode(), ErrorCodeEnum.METHOD_NOT_ALLOWED.getStatus(), ex.getMessage());
        return new ResponseEntity<>(methodNotAllowedException, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ProductOrderInventoryException.class)
    public ResponseEntity<Object> handleProductInventoryException(ProductOrderInventoryException ex) {
        log.error(String.format("%s error thrown ", ex.getExceptionResponse().getStatus()), ex);
        return ResponseEntity.status(ex.getExceptionResponse().getStatus()).body(ex.getExceptionResponse());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handleBindException(BindException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        String errorMessage;
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            errorMessage = fieldError.getDefaultMessage();
        } else {
            errorMessage = "Error while Bind Field.";
        }
        ExceptionResponse invalidParameterException = new ExceptionResponse(HttpStatus.BAD_REQUEST, ErrorCodeEnum.MISSING_INPUT.getCode(), ErrorCodeEnum.MISSING_INPUT.getStatus(), errorMessage);
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.beans.NotReadablePropertyException.class)
    public ResponseEntity<ExceptionResponse> handleNotReadablePropertyException(org.springframework.beans.NotReadablePropertyException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, ErrorCodeEnum.MISSING_INPUT.getCode(), "Invalid property access", ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
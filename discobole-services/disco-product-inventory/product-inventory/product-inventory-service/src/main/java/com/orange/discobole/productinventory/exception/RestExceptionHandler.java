// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.exception;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.orange.discobole.productinventory.exception.model.ExceptionResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import static com.orange.discobole.productinventory.constant.Constant.BAD_URL_OR_RESOURCE_NOT_FOUND;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.VALUE_IS_NOT_A_VALID_TYPE;


@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler {


    // TODO You should add a default exception handler that handles an Exception that is not caught by the classes below and return a
    //     proper response in that case too which could be done using

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error(HttpStatus.NOT_FOUND.getReasonPhrase(), ex);
        ExceptionResponse notFoundExceptionException = new ExceptionResponse(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), BAD_URL_OR_RESOURCE_NOT_FOUND);
        return new ResponseEntity<>(notFoundExceptionException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(ConstraintViolationException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        List<String> errors = ex.getConstraintViolations()
                .stream().map(fieldError -> "%s %s".formatted(fieldError.getPropertyPath(), fieldError.getMessage()))
                .toList();
        String message = String.join(", ", errors);
        ExceptionResponse invalidParameterException =
                handleException(ex.getConstraintViolations(),
                        constraintViolation -> Objects.isNull(((ConstraintViolation<?>) constraintViolation).getInvalidValue()),
                        message);
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> "%s %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        String message = String.join(", ", errors);
        ExceptionResponse invalidParameterException =
                handleException(ex.getBindingResult().getFieldErrors(),
                        fieldError -> Objects.isNull(((FieldError) fieldError).getRejectedValue()),
                        message);
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), ex);
        ExceptionResponse methodNotAllowedException = new ExceptionResponse(HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED.getCode(), METHOD_NOT_ALLOWED.getStatus(), ex.getMessage());
        return new ResponseEntity<>(methodNotAllowedException, HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(ProductInventoryException.class)
    public ResponseEntity<Object> handleProductInventoryException(ProductInventoryException ex) {
        log.error(String.format("%s error thrown ", ex.getExceptionResponse().getStatus()), ex);
        return ResponseEntity.status(ex.getExceptionResponse().getStatus()).body(ex.getExceptionResponse());
    }

    @ExceptionHandler(UnsupportedTypeException.class)
    public ResponseEntity<Object> handleJobSpecificationTypeNotSupportedException(UnsupportedTypeException ex) {
        log.error(String.format("%s error thrown ", ex.getExceptionResponse().getStatus()), ex);
        return ResponseEntity.status(ex.getExceptionResponse().getStatus()).body(ex.getExceptionResponse());
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
        ExceptionResponse invalidParameterException = new ExceptionResponse(HttpStatus.BAD_REQUEST, MISSING_INPUT.getCode(), MISSING_INPUT.getStatus(), errorMessage);
        return new ResponseEntity<>(invalidParameterException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotReadablePropertyException.class)
    public ResponseEntity<ExceptionResponse> handleNotReadablePropertyException(NotReadablePropertyException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, MISSING_INPUT.getCode(), "Invalid property access", ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Invalid Input";
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        if (null != ex.getCause()) {
            if (ex.getCause() instanceof InvalidTypeIdException) {
                message = "Invalid '@type' Field";
            } else if (ex.getCause() instanceof JsonMappingException jsonMappingEx) {
                String fieldName = getFullFieldName(jsonMappingEx);
                message = String.format("Invalid '%s' Field", fieldName);
            }
        }
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), message);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    private static String getFullFieldName(JsonMappingException ex) {
        List<String> fieldNames = ex
                .getPath()
                .stream()
                .map(item -> null == item.getFieldName() ? String.format("[%d]", item.getIndex()) : item.getFieldName())
                .toList();
        StringBuilder fieldName = new StringBuilder();
        for (String name : fieldNames) {
            if (fieldName.isEmpty()) {
                fieldName = new StringBuilder(name);
            } else {
                fieldName.append(name.charAt(0) == '[' ? name : "." + name); // not to add dot in case of array index
            }
        }
        return fieldName.toString();
    }

    @ExceptionHandler({TypeMismatchException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionResponse> handleMismatchedInputException(TypeMismatchException ex, Locale locale) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        String propertyName = ex.getPropertyName();
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), String.format(VALUE_IS_NOT_A_VALID_TYPE, propertyName));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse handleException(Collection<?> collection, Predicate<Object> conditionError, String message) {
        if (!CollectionUtils.isEmpty(collection) && collection.stream().filter(conditionError).count() == collection.size()) {
            return new ExceptionResponse(HttpStatus.BAD_REQUEST, MISSING_INPUT.getCode(), MISSING_INPUT.getStatus(), message);
        } else {
            return new ExceptionResponse(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), message);
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ExceptionResponse errorResponse = new ExceptionResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED.getCode(), ACCESS_DENIED.getStatus(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        ExceptionResponse missingServletRequestParameterException = new ExceptionResponse(HttpStatus.BAD_REQUEST, MISSING_REQUEST_PARAMETER.getCode(), MISSING_REQUEST_PARAMETER.getStatus(), ex.getBody().getDetail());
        return new ResponseEntity<>(missingServletRequestParameterException, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        ExceptionResponse missingServletRequestPartException = new ExceptionResponse(HttpStatus.BAD_REQUEST, MISSING_REQUEST_PARAMETER.getCode(), MISSING_REQUEST_PARAMETER.getStatus(), ex.getMessage());
        return new ResponseEntity<>(missingServletRequestPartException, HttpStatus.BAD_REQUEST);
    }
}

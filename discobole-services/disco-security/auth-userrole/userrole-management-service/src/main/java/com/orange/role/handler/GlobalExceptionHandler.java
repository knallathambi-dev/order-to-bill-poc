// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.handler;


import com.orange.role.exception.DiscoException;
import com.orange.role.exception.ErrorResponse;
import com.orange.role.exception.MissingBodyFieldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DiscoException.class)
    public ResponseEntity<ErrorResponse> handleDiscoException(DiscoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getReason());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MissingBodyFieldException.class)
    public ResponseEntity<ErrorResponse> handleMissingBodyFieldException(MissingBodyFieldException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getReason(), ex.getMessage());
        errorResponse.setStatus("Bad Request");
        errorResponse.setReferenceError(null);
        errorResponse.setType(null);
        errorResponse.setBaseType(null);
        errorResponse.setSchemaLocation(null);
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(HttpMessageNotReadableException ex) {

        ErrorResponse errorResponse = new ErrorResponse(21, ex.getMessage(), ex.getMessage());
        errorResponse.setStatus("Bad Request---->");
        errorResponse.setReferenceError(null);
        errorResponse.setType(null);
        errorResponse.setBaseType(null);
        errorResponse.setSchemaLocation(null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(
            MissingRequestHeaderException ex) {
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(), ex.getMessage());
        if ("Authorization".equalsIgnoreCase(ex.getHeaderName())) {
            errorResponse.setCode(401);
            errorResponse.setReferenceError(null);
            errorResponse.setType(null);
            errorResponse.setBaseType(null);
            errorResponse.setSchemaLocation(null);
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

}

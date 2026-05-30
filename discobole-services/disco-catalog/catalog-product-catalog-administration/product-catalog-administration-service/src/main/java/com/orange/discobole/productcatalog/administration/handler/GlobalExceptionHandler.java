// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.handler;
import com.orange.discobole.productcatalog.administration.exception.DiscoException;
import com.orange.discobole.productcatalog.administration.exception.ErrorResponse;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import org.springframework.http.ResponseEntity;
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
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getReason(),ex.getMessage());
        errorResponse.setStatus("Bad Request");
        errorResponse.setReferenceError(null);
        errorResponse.setType(null);
        errorResponse.setBaseType(null);
        errorResponse.setSchemaLocation(null);
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}

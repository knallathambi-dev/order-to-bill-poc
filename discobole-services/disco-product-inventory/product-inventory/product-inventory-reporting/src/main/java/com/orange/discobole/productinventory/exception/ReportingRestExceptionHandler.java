// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.exception;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ReportingRestExceptionHandler {

    @ExceptionHandler(UnsupportedTypeException.class)
    public ResponseEntity<Object> handleUnsupportedTypeException(UnsupportedTypeException ex) {
        log.error(String.format("%s error thrown ", ex.getExceptionResponse().getStatus()), ex);
        return ResponseEntity.status(ex.getExceptionResponse().getStatus()).body(ex.getExceptionResponse());
    }

    @ExceptionHandler(ReportingException.class)
    public ResponseEntity<Object> handleReportingException(ReportingException ex) {
        log.error(String.format("%s error thrown ", ex.getExceptionResponse().getStatus()), ex);
        return ResponseEntity.status(ex.getExceptionResponse().getStatus()).body(ex.getExceptionResponse());
    }
}

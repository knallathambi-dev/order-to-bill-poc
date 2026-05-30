// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.exception;

import com.orange.discobole.productinventory.dto.Error;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
public final class ReportingException extends RuntimeException implements Serializable {

    private final transient Error exceptionResponse;

    public ReportingException(HttpStatus status, int code, String reason) {
        this.exceptionResponse = new Error(code, reason, status);
    }

    public ReportingException(HttpStatus status, int code, String reason, String message) {
        this.exceptionResponse = new Error(code, reason, message, status);
    }

    public ReportingException(String message, Throwable reason) {
        super(message, reason);
        this.exceptionResponse = null;
    }

    public ReportingException(String message) {
        super(message);
        this.exceptionResponse = null;
    }

    public ReportingException(Error exceptionResponse) {
        this.exceptionResponse = exceptionResponse;
    }

}

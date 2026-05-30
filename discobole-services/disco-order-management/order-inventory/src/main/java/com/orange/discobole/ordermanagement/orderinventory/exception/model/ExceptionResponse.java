// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@Getter
public class ExceptionResponse implements Serializable {
    private final Integer code;
    private final String reason;
    private final HttpStatus status;
    private String message;

    public ExceptionResponse(HttpStatus status, String reason) {
        this.status = status;
        this.reason = reason;
        this.code = null;
    }

    public ExceptionResponse(HttpStatus status, Integer code, String reason, String message) {
        this.status = status;
        this.code = code;
        this.reason = reason;
        this.message = message;
    }

    public ExceptionResponse(HttpStatus status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.code = null;
    }

    public ExceptionResponse(HttpStatus status, Integer code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.reason = null;
    }


    public ExceptionResponse(String reason) {
        this.status = null;
        this.code = null;
        this.reason = reason;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String details) {
        this.message = details;
    }
}
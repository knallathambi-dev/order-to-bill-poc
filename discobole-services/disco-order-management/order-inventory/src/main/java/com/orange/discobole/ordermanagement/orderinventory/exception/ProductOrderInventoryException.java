// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.exception;

import com.orange.discobole.ordermanagement.orderinventory.dto.Error;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
public final class ProductOrderInventoryException extends RuntimeException implements Serializable {

    private final transient Error exceptionResponse;

    public ProductOrderInventoryException(HttpStatus status, int code, String reason) {
        this.exceptionResponse = new Error(code, reason, status);
    }

    public ProductOrderInventoryException(HttpStatus status, int code, String reason, String message) {
        this.exceptionResponse = new Error(code, reason, message, status);
    }

    public ProductOrderInventoryException(String message, Throwable reason) {
        super(message, reason);
        this.exceptionResponse = null;
    }

    public ProductOrderInventoryException(String message) {
        super(message);
        this.exceptionResponse = null;
    }

    public ProductOrderInventoryException(Error exceptionResponse) {
        this.exceptionResponse = exceptionResponse;
    }

    public Error getExceptionResponse() {
        return exceptionResponse;
    }
}
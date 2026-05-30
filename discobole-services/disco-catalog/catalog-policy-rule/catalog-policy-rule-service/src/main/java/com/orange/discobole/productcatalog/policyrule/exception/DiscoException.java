// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.exception;

import org.springframework.http.HttpStatus;

public class DiscoException extends RuntimeException {

    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    private static final Integer CODE = 1;

    private String message;

    /**
     * Instantiates a new disco exception.
     */
    public DiscoException() {
        super();
    }

    /**
     * Instantiates a new disco exception.
     *
     * @param reason the reason for exception
     */
    public DiscoException(String reason) {
        super(reason);
    }

    /**
     * Instantiates a new disco exception.
     *
     * @param reason the reason for exception
     * @param cause  throws exception
     */
    public DiscoException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public HttpStatus getStatus() {
        return STATUS;
    }

    public Integer getCode() {
        return CODE;
    }

    public String getReason() {
        return super.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

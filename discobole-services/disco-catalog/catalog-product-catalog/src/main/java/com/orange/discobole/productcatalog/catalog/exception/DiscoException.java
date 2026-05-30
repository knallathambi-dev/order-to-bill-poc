// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.exception;

import org.springframework.http.HttpStatus;

/**
 * BOS Parent Exception.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public class DiscoException extends RuntimeException {

    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    private static final Integer CODE = 1;

    private final String reason;

    private String message;

    /**
     * Instantiates a new bos exception.
     */
    public DiscoException() {
        super();
        this.reason = "unknown";
    }

    /**
     * Instantiates a new bos exception.
     *
     * @param reason the reason for exception
     */
    public DiscoException(String reason) {
        super(reason);
        this.reason = reason;
    }

    /**
     * Instantiates a new bos exception.
     *
     * @param reason the reason for exception
     * @param cause  throws exception
     */
    public DiscoException(String reason, Throwable cause) {
        super(reason, cause);
        this.reason = reason;
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

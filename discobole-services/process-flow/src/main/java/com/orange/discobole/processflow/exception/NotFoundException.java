// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.exception;

import org.springframework.http.HttpStatus;

/**
 * Resource Not Found Exception.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public class NotFoundException extends DiscoException {

    private static final HttpStatus status = HttpStatus.NOT_FOUND;

    private static final Integer CODE = 60;

    /**
     * Instantiates a new not found exception.
     *
     * @param reason the reason for exception
     */
    public NotFoundException(String reason) {
        super(reason);
    }

    /**
     * Instantiates a new not found exception.
     *
     * @param reason the reason for exception
     * @param cause  throws exception
     */
    public NotFoundException(String reason, Throwable cause) {
        super(reason, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public Integer getCode() {
        return CODE;
    }

}

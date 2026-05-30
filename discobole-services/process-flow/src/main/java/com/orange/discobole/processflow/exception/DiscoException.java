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
 * Disco Parent Exception.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public class DiscoException extends RuntimeException {

    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    private static final Integer CODE = 1;

    private final String reason;

    private String message;

    private String baseType;
	private String type;
	private String schemaLocation;
	private String referenceError;
    
    /**
     * Instantiates a new disco exception.
     */
    public DiscoException() {
        super();
        this.reason = "unknown";
    }

    /**
     * Instantiates a new disco exception.
     *
     * @param reason the reason for exception
     */
    public DiscoException(String reason) {
        super(reason);
        this.reason = reason;
    }
    
    /**
     * Instantiates a new disco exception.
     *
     * @param reason the reason for exception
     */
    public DiscoException(String reason,String baseType) {
        super(reason);
        this.reason = reason;
        this.baseType=baseType;
    }
    

    /**
     * Instantiates a new disco exception.
     *
     * @param reason the reason for exception
     * @param cause  throws exception
     */
    public DiscoException(String reason, Throwable cause) {
        super(reason, cause);
        this.reason = reason;
    }

    
    
    public DiscoException(String reason, String message, String baseType, String type, String schemaLocation,
			String referenceError) {
		super();
		this.reason = reason;
		this.message = message;
		this.baseType = baseType;
		this.type = type;
		this.schemaLocation = schemaLocation;
		this.referenceError = referenceError;
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
	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public String getReferenceError() {
		return referenceError;
	}

	public void setReferenceError(String referenceError) {
		this.referenceError = referenceError;
	}
	
    
}

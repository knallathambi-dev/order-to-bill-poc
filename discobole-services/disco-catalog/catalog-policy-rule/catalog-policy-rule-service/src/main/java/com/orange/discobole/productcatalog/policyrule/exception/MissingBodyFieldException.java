// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public class MissingBodyFieldException extends DiscoException {
    private  HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private Integer code = 23;
    private String reason;
    private String message;

    private String referenceError;

    @JsonProperty("@type")
    private String type;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;

    public String getReferenceError() {
        return referenceError;
    }

    public void setReferenceError(String referenceError) {
        this.referenceError = referenceError;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }




    /**
     * Instantiates a new bos exception.
     */
    public MissingBodyFieldException() {
       super();
    }

    /**
     * Instantiates a new bos exception.
     *
     * @param reason the reason for exception
     */
    public MissingBodyFieldException(String reason) {
        super(reason);
        this.reason = reason;

    }

    /**
     * Instantiates a new bos exception.
     *
     * @param reason the reason for exception
     * @param cause  throws exception
     */
    public MissingBodyFieldException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public MissingBodyFieldException(Integer code, String reason, String message) {
        this.code = code;
        this.reason = reason;
        this.message = message;

    }

    public MissingBodyFieldException(HttpStatus STATUS, Integer code, String reason, String message) {
        this.code = code;
        this.reason = reason;
        this.message = message;
        this.STATUS = STATUS;
    }

    @Override
    public HttpStatus getStatus() {
        return STATUS;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

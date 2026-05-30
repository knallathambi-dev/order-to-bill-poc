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

public class ServiceResponseException extends RuntimeException{
    private Integer code;
    private String message;
    private String reason;
    private String status;
    private String referenceError;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;

    public ServiceResponseException(Integer code, String message, String reason, String status) {
        this.code = code;
        this.message = message;
        this.reason = reason;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ServiceResponseException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", referenceError='" + referenceError + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                '}';
    }

}

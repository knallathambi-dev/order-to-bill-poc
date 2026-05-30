// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
@lombok.experimental.Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.FieldNameConstants

public class Error {
    @NotNull
    private int code;
    @NotNull
    private String reason;
    private String message;
    private HttpStatus status;
    private String referenceError;
    private String baseType;
    private URI schemaLocation;
    private String type;

    public Error(int code, String reason, HttpStatus status) {
        this.code = code;
        this.reason = reason;
        this.status = status;
    }

    public Error(int code, String reason, String message, HttpStatus status) {
        this.code = code;
        this.reason = reason;
        this.message = message;
        this.status = status;
    }

    public static Error.ErrorBuilder createErrorBuilder(int code, String reason) {
        return Error.builder().code(code).reason(reason);
    }

    /**
     * Application relevant detail, defined in the API or a common list.
     **/
    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Explanation of the reason for the error which can be shown to a client user.
     **/
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * More details and corrective actions related to the error which can be shown to a client user.
     **/
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * HTTP Error code extension
     **/
    @JsonProperty("status")
    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    /**
     * URI of documentation describing the error.
     **/
    @JsonProperty("referenceError")
    public String getReferenceError() {
        return referenceError;
    }

    public void setReferenceError(String referenceError) {
        this.referenceError = referenceError;
    }

    /**
     * When sub-classing, this defines the super-class.
     **/
    @JsonProperty("baseType")
    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     **/
    @JsonProperty("schemaLocation")
    public URI getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(URI schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * When sub-classing, this defines the subclass entity name.
     **/
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {

        return "{\n" +
                "  status: " + status + "\n" +
                "  code: " + code + "\n" +
                "  reason: " + reason + "\n" +
                "  message: " + message + "\n" +
                "}\n";
    }
}
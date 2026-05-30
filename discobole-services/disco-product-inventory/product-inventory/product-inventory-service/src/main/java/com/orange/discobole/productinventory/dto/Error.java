// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;


/**
 * Used when an API throws an Error, typically with an HTTP error response-code (3xx, 4xx, 5xx)
 **/


@JsonInclude(NON_EMPTY)
@Schema(description = "Used when an API throws an Error, typically with a HTTP error response-code (3xx, 4xx, 5xx)")
@lombok.experimental.Accessors(chain = true)
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
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


    private String schemaLocation;


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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Application relevant detail, defined in the API or a common list.")
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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Explanation of the reason for the error which can be shown to a client user.")
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
    @Schema(description = "More details and corrective actions related to the error which can be shown to a client user.")
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
    @Schema(description = "HTTP Error code extension")
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
    @Schema(description = "URI of documentation describing the error.")
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
    @Schema(description = "When sub-classing, this defines the super-class.")
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
    @Schema(description = "A URI to a JSON-Schema file that defines additional attributes and relationships")
    @JsonProperty("schemaLocation")
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * When sub-classing, this defines the sub-class entity name.
     **/
    @Schema(description = "When sub-classing, this defines the sub-class entity name.")
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



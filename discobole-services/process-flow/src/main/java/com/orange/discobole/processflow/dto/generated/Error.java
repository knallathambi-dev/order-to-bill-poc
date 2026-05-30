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

package com.orange.discobole.processflow.dto.generated;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
@Schema(description = "Used when and API throws an Error, typically with a HTTP error response code (3xx, 4xx, 5xx)")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-03-06T07:55:20.984+05:30")
public class Error {
	@JsonProperty("id")
	private String id = null;
	
    @JsonProperty("code")
    private Integer code = null;

    @JsonProperty("reason")
    private String reason = null;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("referenceError")
    private String referenceError = null;
    
    @JsonProperty("@baseType")
    private String baseType = null;

    @JsonProperty("@type")
    private String type = null;

    @JsonProperty("@schemaLocation")
    private String schemaLocation = null;

    public Error code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * @return code
     **/
    @Schema(required = true, defaultValue = "")
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Error reason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * @return reason
     **/
    @Schema(required = true, defaultValue = "")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Error id(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * @return reason
     **/
    @Schema(required = true, defaultValue = "")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Error message(String message) {
        this.message = message;
        return this;
    }

    /**
     * @return message
     **/
    @Schema(defaultValue = "")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Error status(String status) {
        this.status = status;
        return this;
    }

    /**
     * @return status
     **/
    @Schema(defaultValue = "")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Error referenceError(String referenceError) {
        this.referenceError = referenceError;
        return this;
    }

    /**
     * @return referenceError
     **/
    @Schema(defaultValue = "")
    public String getReferenceError() {
        return referenceError;
    }

    public void setReferenceError(String referenceError) {
        this.referenceError = referenceError;
    }
    
    
    public Error baseType(String type) {
        this.baseType = type;
        return this;
    }
    
    @Schema(defaultValue = "")
    public String getBaseType() {
        return baseType;
    }
    
    public void setBaseType(String type) {
        this.baseType = type;
    }
    
    public Error type(String type) {
        this.type = type;
        return this;
    }

    /**
     * @return type
     **/
    @Schema(defaultValue = "")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Error schemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
        return this;
    }

    /**
     * @return schemaLocation
     **/
    @Schema(defaultValue = "")
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Error errorRepresentation = (Error) o;
        return Objects.equals(this.code, errorRepresentation.code) &&
                Objects.equals(this.reason, errorRepresentation.reason) &&
                Objects.equals(this.message, errorRepresentation.message) &&
                Objects.equals(this.status, errorRepresentation.status) &&
                Objects.equals(this.referenceError, errorRepresentation.referenceError) &&
                Objects.equals(this.type, errorRepresentation.type) &&
                Objects.equals(this.schemaLocation, errorRepresentation.schemaLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, reason, message, status, referenceError, type, schemaLocation);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorRepresentation {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    referenceError: ").append(toIndentedString(referenceError)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    schemaLocation: ").append(toIndentedString(schemaLocation)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

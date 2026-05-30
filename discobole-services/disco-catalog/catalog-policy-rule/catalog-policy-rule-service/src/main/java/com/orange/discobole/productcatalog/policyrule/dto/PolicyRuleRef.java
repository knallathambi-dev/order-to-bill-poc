// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
package com.orange.discobole.productcatalog.policyrule.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.annotation.Generated;

/**
 * The Policy Rule Ref for associations.
 */
@ApiModel(description = "The Policy Rule Ref for associations.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-30T16:59:05.795+05:30")
public class PolicyRuleRef {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("href")
    private String href = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("@baseType")
    private String baseType = null;

    @JsonProperty("@schemaLocation")
    private String schemaLocation = null;

    @JsonProperty("@type")
    private String type = null;

    @JsonProperty("@referredType")
    private String referredType = null;

    public PolicyRuleRef id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of a related entity.
     *
     * @return id
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of a related entity.")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PolicyRuleRef href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Reference of the related entity.
     *
     * @return href
     **/
    @ApiModelProperty(value = "Reference of the related entity.")
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public PolicyRuleRef name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the related entity.
     *
     * @return name
     **/
    @ApiModelProperty(value = "Name of the related entity.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolicyRuleRef baseType(String baseType) {
        this.baseType = baseType;
        return this;
    }

    /**
     * When sub-classing, this defines the super-class
     *
     * @return baseType
     **/
    @ApiModelProperty(value = "When sub-classing, this defines the super-class")
    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public PolicyRuleRef schemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
        return this;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and
     * relationships
     *
     * @return schemaLocation
     **/
    @ApiModelProperty(value = "A URI to a JSON-Schema file that defines additional attributes and relationships")
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public PolicyRuleRef type(String type) {
        this.type = type;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class entity name
     *
     * @return type
     **/
    @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PolicyRuleRef referredType(String referredType) {
        this.referredType = referredType;
        return this;
    }

    /**
     * The actual type of the target instance when needed for disambiguation.
     *
     * @return referredType
     **/
    @ApiModelProperty(value = "The actual type of the target instance when needed for disambiguation.")
    public String getReferredType() {
        return referredType;
    }

    public void setReferredType(String referredType) {
        this.referredType = referredType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PolicyRuleRef policyRuleRef = (PolicyRuleRef) o;
        return Objects.equals(this.id, policyRuleRef.id) && Objects.equals(this.href, policyRuleRef.href)
                && Objects.equals(this.name, policyRuleRef.name)
                && Objects.equals(this.baseType, policyRuleRef.baseType)
                && Objects.equals(this.schemaLocation, policyRuleRef.schemaLocation)
                && Objects.equals(this.type, policyRuleRef.type)
                && Objects.equals(this.referredType, policyRuleRef.referredType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, name, baseType, schemaLocation, type, referredType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PolicyRuleRef {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    baseType: ").append(toIndentedString(baseType)).append("\n");
        sb.append("    schemaLocation: ").append(toIndentedString(schemaLocation)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    referredType: ").append(toIndentedString(referredType)).append("\n");
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


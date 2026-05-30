// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service;

import com.fasterxml.jackson.annotation.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ValidFor;
import jakarta.annotation.Generated;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A dependency, substitution or exclusivity relationship between/among service specifications.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "href",
    "name",
    "relationshipType",
    "role",
    "validFor",
    "@baseType",
    "@schemaLocation",
    "@type",
    "@referredType"
})
@Generated("jsonschema2pojo")
public class ServiceSpecRelationship {

    /**
     * unique identifier
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("unique identifier")
    private String id;
    /**
     * Hyperlink reference
     * 
     */
    @JsonProperty("href")
    @JsonPropertyDescription("Hyperlink reference")
    private URI href;
    /**
     * Name of the related entity.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Name of the related entity.")
    private String name;
    /**
     * Type of relationship such as dependency, substitution or exclusivity
     * (Required)
     * 
     */
    @JsonProperty("relationshipType")
    @JsonPropertyDescription("Type of relationship such as dependency, substitution or exclusivity")
    private String relationshipType;
    /**
     * The association role for this service specification
     * 
     */
    @JsonProperty("role")
    @JsonPropertyDescription("The association role for this service specification")
    private String role;
    /**
     * A dependency, substitution or exclusivity relationship between/among service specifications.
     * 
     */
    @JsonProperty("validFor")
    @JsonPropertyDescription("A dependency, substitution or exclusivity relationship between/among service specifications.")
    private ValidFor validFor;
    /**
     * When sub-classing, this defines the super-class
     * 
     */
    @JsonProperty("@baseType")
    @JsonPropertyDescription("When sub-classing, this defines the super-class")
    private String baseType;
    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     * 
     */
    @JsonProperty("@schemaLocation")
    @JsonPropertyDescription("A URI to a JSON-Schema file that defines additional attributes and relationships")
    private URI schemaLocation;
    /**
     * When sub-classing, this defines the sub-class Extensible name
     * 
     */
    @JsonProperty("@type")
    @JsonPropertyDescription("When sub-classing, this defines the sub-class Extensible name")
    private String type;
    /**
     * The actual type of the target instance when needed for disambiguation.
     * 
     */
    @JsonProperty("@referredType")
    @JsonPropertyDescription("The actual type of the target instance when needed for disambiguation.")
    private String referredType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * unique identifier
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * unique identifier
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Hyperlink reference
     * 
     */
    @JsonProperty("href")
    public URI getHref() {
        return href;
    }

    /**
     * Hyperlink reference
     * 
     */
    @JsonProperty("href")
    public void setHref(URI href) {
        this.href = href;
    }

    /**
     * Name of the related entity.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Name of the related entity.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Type of relationship such as dependency, substitution or exclusivity
     * (Required)
     * 
     */
    @JsonProperty("relationshipType")
    public String getRelationshipType() {
        return relationshipType;
    }

    /**
     * Type of relationship such as dependency, substitution or exclusivity
     * (Required)
     * 
     */
    @JsonProperty("relationshipType")
    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * The association role for this service specification
     * 
     */
    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    /**
     * The association role for this service specification
     * 
     */
    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * A dependency, substitution or exclusivity relationship between/among service specifications.
     * 
     */
    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    /**
     * A dependency, substitution or exclusivity relationship between/among service specifications.
     * 
     */
    @JsonProperty("validFor")
    public void setValidFor(ValidFor validFor) {
        this.validFor = validFor;
    }

    /**
     * When sub-classing, this defines the super-class
     * 
     */
    @JsonProperty("@baseType")
    public String getBaseType() {
        return baseType;
    }

    /**
     * When sub-classing, this defines the super-class
     * 
     */
    @JsonProperty("@baseType")
    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     * 
     */
    @JsonProperty("@schemaLocation")
    public URI getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     * 
     */
    @JsonProperty("@schemaLocation")
    public void setSchemaLocation(URI schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * When sub-classing, this defines the sub-class Extensible name
     * 
     */
    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    /**
     * When sub-classing, this defines the sub-class Extensible name
     * 
     */
    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * The actual type of the target instance when needed for disambiguation.
     * 
     */
    @JsonProperty("@referredType")
    public String getReferredType() {
        return referredType;
    }

    /**
     * The actual type of the target instance when needed for disambiguation.
     * 
     */
    @JsonProperty("@referredType")
    public void setReferredType(String referredType) {
        this.referredType = referredType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

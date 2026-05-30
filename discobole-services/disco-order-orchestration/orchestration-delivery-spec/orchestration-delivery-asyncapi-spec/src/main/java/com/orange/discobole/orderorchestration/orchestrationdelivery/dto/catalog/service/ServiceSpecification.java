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
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.RelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ValidFor;
import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.*;


/**
 * ServiceSpecification is a class that offers characteristics to describe a type of service.
 * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "href",
    "description",
    "isBundle",
    "lastUpdate",
    "lifecycleStatus",
    "name",
    "version",
    "attachment",
    "constraint",
    "entitySpecRelationship",
    "featureSpecification",
    "relatedParty",
    "resourceSpecification",
    "serviceLevelSpecification",
    "serviceSpecRelationship",
    "specCharacteristic",
    "targetEntitySchema",
    "validFor",
    "@baseType",
    "@schemaLocation",
    "@type"
})
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSpecification {

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
     * Description of the specification
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("Description of the specification")
    private String description;
    /**
     * isBundle determines whether specification represents a single specification (false), or a bundle of specifications (true).
     * 
     */
    @JsonProperty("isBundle")
    @JsonPropertyDescription("isBundle determines whether specification represents a single specification (false), or a bundle of specifications (true).")
    private Boolean isBundle;
    /**
     * Date and time of the last update of the specification
     * 
     */
    @JsonProperty("lastUpdate")
    @JsonPropertyDescription("Date and time of the last update of the specification")
    private Date lastUpdate;
    /**
     * Used to indicate the current lifecycle status of this catalog item
     * 
     */
    @JsonProperty("lifecycleStatus")
    @JsonPropertyDescription("Used to indicate the current lifecycle status of this catalog item")
    private String lifecycleStatus;
    /**
     * Name given to the specification
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Name given to the specification")
    private String name;
    /**
     * specification version
     * 
     */
    @JsonProperty("version")
    @JsonPropertyDescription("specification version")
    private String version;
    /**
     * Attachments that may be of relevance to this specification, such as picture, document, media
     * 
     */
    @JsonProperty("attachment")
    @JsonPropertyDescription("Attachments that may be of relevance to this specification, such as picture, document, media")
    private List<Attachment> attachment;
    /**
     * This is a list of constraint references applied to this specification
     * 
     */
    @JsonProperty("constraint")
    @JsonPropertyDescription("This is a list of constraint references applied to this specification")
    private List<Attachment> constraint;
    /**
     * Relationship to another specification
     * 
     */
    @JsonProperty("entitySpecRelationship")
    @JsonPropertyDescription("Relationship to another specification")
    private List<Attachment> entitySpecRelationship;
    /**
     * A list of Features for this specification.
     * 
     */
    @JsonProperty("featureSpecification")
    @JsonPropertyDescription("A list of Features for this specification.")
    private List<Attachment> featureSpecification;
    /**
     * Parties who manage or otherwise have an interest in this specification
     * 
     */
    @JsonProperty("relatedParty")
    @JsonPropertyDescription("Parties who manage or otherwise have an interest in this specification")
    private List<RelatedParty> relatedParty;
    /**
     * A list of resource specification references (ResourceSpecificationRef [*]). The ResourceSpecification is required for a service specification with type ResourceFacingServiceSpecification (RFSS).
     * 
     */
    @JsonProperty("resourceSpecification")
    @JsonPropertyDescription("A list of resource specification references (ResourceSpecificationRef [*]). The ResourceSpecification is required for a service specification with type ResourceFacingServiceSpecification (RFSS).")
    private List<Attachment> resourceSpecification;
    /**
     * A list of service level specifications related to this service specification, and which will need to be satisifiable for corresponding service instances; e.g. Gold, Platinum
     * 
     */
    @JsonProperty("serviceLevelSpecification")
    @JsonPropertyDescription("A list of service level specifications related to this service specification, and which will need to be satisifiable for corresponding service instances; e.g. Gold, Platinum")
    private List<Attachment> serviceLevelSpecification;
    /**
     * A list of service specifications related to this specification, e.g. migration, substitution, dependency or exclusivity relationship
     * 
     */
    @JsonProperty("serviceSpecRelationship")
    @JsonPropertyDescription("A list of service specifications related to this specification, e.g. migration, substitution, dependency or exclusivity relationship")
    private List<ServiceSpecRelationship> serviceSpecRelationship;
    /**
     * List of characteristics that the entity can take
     * 
     */
    @JsonProperty("specCharacteristic")
    @JsonPropertyDescription("List of characteristics that the entity can take")
    private Set<CharacteristicSpecification> specCharacteristic;
    /**
     * ServiceSpecification is a class that offers characteristics to describe a type of service.
     * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
     * 
     */
    @JsonProperty("targetEntitySchema")
    @JsonPropertyDescription("ServiceSpecification is a class that offers characteristics to describe a type of service.\nFunctionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.")
    private Attachment targetEntitySchema;
    /**
     * ServiceSpecification is a class that offers characteristics to describe a type of service.
     * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
     * 
     */
    @JsonProperty("validFor")
    @JsonPropertyDescription("ServiceSpecification is a class that offers characteristics to describe a type of service.\nFunctionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.")
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
     * Description of the specification
     * 
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Description of the specification
     * 
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * isBundle determines whether specification represents a single specification (false), or a bundle of specifications (true).
     * 
     */
    @JsonProperty("isBundle")
    public Boolean getIsBundle() {
        return isBundle;
    }

    /**
     * isBundle determines whether specification represents a single specification (false), or a bundle of specifications (true).
     * 
     */
    @JsonProperty("isBundle")
    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    /**
     * Date and time of the last update of the specification
     * 
     */
    @JsonProperty("lastUpdate")
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Date and time of the last update of the specification
     * 
     */
    @JsonProperty("lastUpdate")
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Used to indicate the current lifecycle status of this catalog item
     * 
     */
    @JsonProperty("lifecycleStatus")
    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    /**
     * Used to indicate the current lifecycle status of this catalog item
     * 
     */
    @JsonProperty("lifecycleStatus")
    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    /**
     * Name given to the specification
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Name given to the specification
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * specification version
     * 
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * specification version
     * 
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Attachments that may be of relevance to this specification, such as picture, document, media
     * 
     */
    @JsonProperty("attachment")
    public List<Attachment> getAttachment() {
        return attachment;
    }

    /**
     * Attachments that may be of relevance to this specification, such as picture, document, media
     * 
     */
    @JsonProperty("attachment")
    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    /**
     * This is a list of constraint references applied to this specification
     * 
     */
    @JsonProperty("constraint")
    public List<Attachment> getConstraint() {
        return constraint;
    }

    /**
     * This is a list of constraint references applied to this specification
     * 
     */
    @JsonProperty("constraint")
    public void setConstraint(List<Attachment> constraint) {
        this.constraint = constraint;
    }

    /**
     * Relationship to another specification
     * 
     */
    @JsonProperty("entitySpecRelationship")
    public List<Attachment> getEntitySpecRelationship() {
        return entitySpecRelationship;
    }

    /**
     * Relationship to another specification
     * 
     */
    @JsonProperty("entitySpecRelationship")
    public void setEntitySpecRelationship(List<Attachment> entitySpecRelationship) {
        this.entitySpecRelationship = entitySpecRelationship;
    }

    /**
     * A list of Features for this specification.
     * 
     */
    @JsonProperty("featureSpecification")
    public List<Attachment> getFeatureSpecification() {
        return featureSpecification;
    }

    /**
     * A list of Features for this specification.
     * 
     */
    @JsonProperty("featureSpecification")
    public void setFeatureSpecification(List<Attachment> featureSpecification) {
        this.featureSpecification = featureSpecification;
    }

    /**
     * Parties who manage or otherwise have an interest in this specification
     * 
     */
    @JsonProperty("relatedParty")
    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    /**
     * Parties who manage or otherwise have an interest in this specification
     * 
     */
    @JsonProperty("relatedParty")
    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    /**
     * A list of resource specification references (ResourceSpecificationRef [*]). The ResourceSpecification is required for a service specification with type ResourceFacingServiceSpecification (RFSS).
     * 
     */
    @JsonProperty("resourceSpecification")
    public List<Attachment> getResourceSpecification() {
        return resourceSpecification;
    }

    /**
     * A list of resource specification references (ResourceSpecificationRef [*]). The ResourceSpecification is required for a service specification with type ResourceFacingServiceSpecification (RFSS).
     * 
     */
    @JsonProperty("resourceSpecification")
    public void setResourceSpecification(List<Attachment> resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
    }

    /**
     * A list of service level specifications related to this service specification, and which will need to be satisifiable for corresponding service instances; e.g. Gold, Platinum
     * 
     */
    @JsonProperty("serviceLevelSpecification")
    public List<Attachment> getServiceLevelSpecification() {
        return serviceLevelSpecification;
    }

    /**
     * A list of service level specifications related to this service specification, and which will need to be satisifiable for corresponding service instances; e.g. Gold, Platinum
     * 
     */
    @JsonProperty("serviceLevelSpecification")
    public void setServiceLevelSpecification(List<Attachment> serviceLevelSpecification) {
        this.serviceLevelSpecification = serviceLevelSpecification;
    }

    /**
     * A list of service specifications related to this specification, e.g. migration, substitution, dependency or exclusivity relationship
     * 
     */
    @JsonProperty("serviceSpecRelationship")
    public List<ServiceSpecRelationship> getServiceSpecRelationship() {
        return serviceSpecRelationship;
    }

    /**
     * A list of service specifications related to this specification, e.g. migration, substitution, dependency or exclusivity relationship
     * 
     */
    @JsonProperty("serviceSpecRelationship")
    public void setServiceSpecRelationship(List<ServiceSpecRelationship> serviceSpecRelationship) {
        this.serviceSpecRelationship = serviceSpecRelationship;
    }

    /**
     * List of characteristics that the entity can take
     * 
     */
    @JsonProperty("specCharacteristic")
    public Set<CharacteristicSpecification> getSpecCharacteristic() {
        return specCharacteristic;
    }

    /**
     * List of characteristics that the entity can take
     * 
     */
    @JsonProperty("specCharacteristic")
    public void setSpecCharacteristic(Set<CharacteristicSpecification> specCharacteristic) {
        this.specCharacteristic = specCharacteristic;
    }

    /**
     * ServiceSpecification is a class that offers characteristics to describe a type of service.
     * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
     * 
     */
    @JsonProperty("targetEntitySchema")
    public Attachment getTargetEntitySchema() {
        return targetEntitySchema;
    }

    /**
     * ServiceSpecification is a class that offers characteristics to describe a type of service.
     * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
     * 
     */
    @JsonProperty("targetEntitySchema")
    public void setTargetEntitySchema(Attachment targetEntitySchema) {
        this.targetEntitySchema = targetEntitySchema;
    }

    /**
     * ServiceSpecification is a class that offers characteristics to describe a type of service.
     * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
     * 
     */
    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    /**
     * ServiceSpecification is a class that offers characteristics to describe a type of service.
     * Functionally, it acts as a template by which Services may be instantiated. By sharing the same specification, these services would therefore share the same set of characteristics.
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

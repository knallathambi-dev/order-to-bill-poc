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
import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This class defines a characteristic specification.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "configurable",
    "description",
    "extensible",
    "isUnique",
    "maxCardinality",
    "minCardinality",
    "name",
    "regex",
    "valueType",
    "charSpecRelationship",
    "characteristicValueSpecification",
    "validFor",
    "@baseType",
    "@schemaLocation",
    "@type",
    "@valueSchemaLocation"
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Generated("jsonschema2pojo")
public class CharacteristicSpecification {

    /**
     * Unique ID for the characteristic
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("Unique ID for the characteristic")
    private String id;
    /**
     * If true, the Boolean indicates that the target Characteristic is configurable
     * 
     */
    @JsonProperty("configurable")
    @JsonPropertyDescription("If true, the Boolean indicates that the target Characteristic is configurable")
    private Boolean configurable;
    /**
     * A narrative that explains the CharacteristicSpecification.
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("A narrative that explains the CharacteristicSpecification.")
    private String description;
    /**
     * An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.
     * 
     */
    @JsonProperty("extensible")
    @JsonPropertyDescription("An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.")
    private Boolean extensible;
    /**
     * An indicator that specifies if a value is unique for the specification. Possible values are; "unique while value is in effect" and "unique whether value is in effect or not"
     * 
     */
    @JsonProperty("isUnique")
    @JsonPropertyDescription("An indicator that specifies if a value is unique for the specification. Possible values are; \"unique while value is in effect\" and \"unique whether value is in effect or not\"")
    private Boolean isUnique;
    /**
     * The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.
     * 
     */
    @JsonProperty("maxCardinality")
    @JsonPropertyDescription("The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.")
    private Integer maxCardinality;
    /**
     * The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.
     * 
     */
    @JsonProperty("minCardinality")
    @JsonPropertyDescription("The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.")
    private Integer minCardinality;
    /**
     * A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.")
    private String name;
    /**
     * A rule or principle represented in regular expression used to derive the value of a characteristic value.
     * 
     */
    @JsonProperty("regex")
    @JsonPropertyDescription("A rule or principle represented in regular expression used to derive the value of a characteristic value.")
    private String regex;
    /**
     * A kind of value that the characteristic can take on, such as numeric, text and so forth
     * 
     */
    @JsonProperty("valueType")
    @JsonPropertyDescription("A kind of value that the characteristic can take on, such as numeric, text and so forth")
    private String valueType;
    /**
     * An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.
     * 
     */
    @JsonProperty("charSpecRelationship")
    @JsonPropertyDescription("An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.")
    private List<CharSpecRelationship> charSpecRelationship;
    /**
     * A CharacteristicValueSpecification object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a CharacteristicSpecification object. The values of the attributes in the CharacteristicValueSpecification object describe the values of the attributes that a corresponding Characteristic object can take on.
     * 
     */
    @JsonProperty("characteristicValueSpecification")
    @JsonPropertyDescription("A CharacteristicValueSpecification object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a CharacteristicSpecification object. The values of the attributes in the CharacteristicValueSpecification object describe the values of the attributes that a corresponding Characteristic object can take on.")
    private List<CharSpecRelationship> characteristicValueSpecification;
    /**
     * This class defines a characteristic specification.
     * 
     */
    @JsonProperty("validFor")
    @JsonPropertyDescription("This class defines a characteristic specification.")
    private CharSpecRelationship validFor;
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
     * This (optional) field provides a link to the schema describing the value type.
     * 
     */
    @JsonProperty("@valueSchemaLocation")
    @JsonPropertyDescription("This (optional) field provides a link to the schema describing the value type.")
    private String valueSchemaLocation;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Unique ID for the characteristic
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * Unique ID for the characteristic
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * If true, the Boolean indicates that the target Characteristic is configurable
     * 
     */
    @JsonProperty("configurable")
    public Boolean getConfigurable() {
        return configurable;
    }

    /**
     * If true, the Boolean indicates that the target Characteristic is configurable
     * 
     */
    @JsonProperty("configurable")
    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    /**
     * A narrative that explains the CharacteristicSpecification.
     * 
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * A narrative that explains the CharacteristicSpecification.
     * 
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.
     * 
     */
    @JsonProperty("extensible")
    public Boolean getExtensible() {
        return extensible;
    }

    /**
     * An indicator that specifies that the values for the characteristic can be extended by adding new values when instantiating a characteristic for a resource.
     * 
     */
    @JsonProperty("extensible")
    public void setExtensible(Boolean extensible) {
        this.extensible = extensible;
    }

    /**
     * An indicator that specifies if a value is unique for the specification. Possible values are; "unique while value is in effect" and "unique whether value is in effect or not"
     * 
     */
    @JsonProperty("isUnique")
    public Boolean getIsUnique() {
        return isUnique;
    }

    /**
     * An indicator that specifies if a value is unique for the specification. Possible values are; "unique while value is in effect" and "unique whether value is in effect or not"
     * 
     */
    @JsonProperty("isUnique")
    public void setIsUnique(Boolean isUnique) {
        this.isUnique = isUnique;
    }

    /**
     * The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.
     * 
     */
    @JsonProperty("maxCardinality")
    public Integer getMaxCardinality() {
        return maxCardinality;
    }

    /**
     * The maximum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where five is the value for the maxCardinality.
     * 
     */
    @JsonProperty("maxCardinality")
    public void setMaxCardinality(Integer maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    /**
     * The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.
     * 
     */
    @JsonProperty("minCardinality")
    public Integer getMinCardinality() {
        return minCardinality;
    }

    /**
     * The minimum number of instances a CharacteristicValue can take on. For example, zero to five phone numbers in a group calling plan, where zero is the value for the minCardinality.
     * 
     */
    @JsonProperty("minCardinality")
    public void setMinCardinality(Integer minCardinality) {
        this.minCardinality = minCardinality;
    }

    /**
     * A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * A word, term, or phrase by which this characteristic specification is known and distinguished from other characteristic specifications.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A rule or principle represented in regular expression used to derive the value of a characteristic value.
     * 
     */
    @JsonProperty("regex")
    public String getRegex() {
        return regex;
    }

    /**
     * A rule or principle represented in regular expression used to derive the value of a characteristic value.
     * 
     */
    @JsonProperty("regex")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * A kind of value that the characteristic can take on, such as numeric, text and so forth
     * 
     */
    @JsonProperty("valueType")
    public String getValueType() {
        return valueType;
    }

    /**
     * A kind of value that the characteristic can take on, such as numeric, text and so forth
     * 
     */
    @JsonProperty("valueType")
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.
     * 
     */
    @JsonProperty("charSpecRelationship")
    public List<CharSpecRelationship> getCharSpecRelationship() {
        return charSpecRelationship;
    }

    /**
     * An aggregation, migration, substitution, dependency or exclusivity relationship between/among Specification Characteristics.
     * 
     */
    @JsonProperty("charSpecRelationship")
    public void setCharSpecRelationship(List<CharSpecRelationship> charSpecRelationship) {
        this.charSpecRelationship = charSpecRelationship;
    }

    /**
     * A CharacteristicValueSpecification object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a CharacteristicSpecification object. The values of the attributes in the CharacteristicValueSpecification object describe the values of the attributes that a corresponding Characteristic object can take on.
     * 
     */
    @JsonProperty("characteristicValueSpecification")
    public List<CharSpecRelationship> getCharacteristicValueSpecification() {
        return characteristicValueSpecification;
    }

    /**
     * A CharacteristicValueSpecification object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a CharacteristicSpecification object. The values of the attributes in the CharacteristicValueSpecification object describe the values of the attributes that a corresponding Characteristic object can take on.
     * 
     */
    @JsonProperty("characteristicValueSpecification")
    public void setCharacteristicValueSpecification(List<CharSpecRelationship> characteristicValueSpecification) {
        this.characteristicValueSpecification = characteristicValueSpecification;
    }

    /**
     * This class defines a characteristic specification.
     * 
     */
    @JsonProperty("validFor")
    public CharSpecRelationship getValidFor() {
        return validFor;
    }

    /**
     * This class defines a characteristic specification.
     * 
     */
    @JsonProperty("validFor")
    public void setValidFor(CharSpecRelationship validFor) {
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
     * This (optional) field provides a link to the schema describing the value type.
     * 
     */
    @JsonProperty("@valueSchemaLocation")
    public String getValueSchemaLocation() {
        return valueSchemaLocation;
    }

    /**
     * This (optional) field provides a link to the schema describing the value type.
     * 
     */
    @JsonProperty("@valueSchemaLocation")
    public void setValueSchemaLocation(String valueSchemaLocation) {
        this.valueSchemaLocation = valueSchemaLocation;
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

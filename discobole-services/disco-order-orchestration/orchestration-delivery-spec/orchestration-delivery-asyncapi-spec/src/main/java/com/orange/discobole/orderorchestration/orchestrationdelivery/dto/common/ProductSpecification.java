// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common;

import com.fasterxml.jackson.annotation.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.*;
import jakarta.annotation.Generated;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "brand",
        "description",
        "isBundle",
        "lastUpdate",
        "lifecycleStatus",
        "name",
        "productNumber",
        "version",
        "supportEntity",
        "productSpecCharacteristic",
        "productSpecificationRelationship",
        "relatedParty",
        "serviceSpecification",
        "validFor",
        "relatedResource",
        "operationSpecification",
        "@type",
        "usageSpecification"
})
@Generated("jsonschema2pojo")
@Builder
@Jacksonized
public class ProductSpecification {

    @JsonProperty("id")
    private String id;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("description")
    private String description;
    @JsonProperty("isBundle")
    private Boolean isBundle;
    @JsonProperty("lastUpdate")
    private String lastUpdate;
    @JsonProperty("lifecycleStatus")
    private String lifecycleStatus;
    @JsonProperty("name")
    private String name;
    @JsonProperty("productNumber")
    private String productNumber;
    @JsonProperty("version")
    private String version;
    @JsonProperty("supportEntity")
    private String supportEntity;
    @JsonProperty("productSpecCharacteristic")
    private List<ProductSpecificationCharacteristic> productSpecificationCharacteristic;
    @JsonProperty("productSpecificationRelationship")
    private List<ProductSpecificationRelationship> productSpecificationRelationship;
    @JsonProperty("relatedParty")
    private List<RelatedParty> relatedParty;
    @JsonProperty("serviceSpecification")
    private List<ServiceSpecification> serviceSpecification;
    @JsonProperty("validFor")
    private ValidFor validFor;
    @JsonProperty("relatedResource")
    private List<RelatedResource> relatedResource;
    @JsonProperty("operationSpecification")
    private List<OperationSpecification> operationSpecification;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("usageSpecification")
    private List<UsageSpecification> usageSpecification;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("brand")
    public String getBrand() {
        return brand;
    }

    @JsonProperty("brand")
    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("isBundle")
    public Boolean getIsBundle() {
        return isBundle;
    }

    @JsonProperty("isBundle")
    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    @JsonProperty("lastUpdate")
    public String getLastUpdate() {
        return lastUpdate;
    }

    @JsonProperty("lastUpdate")
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @JsonProperty("lifecycleStatus")
    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    @JsonProperty("lifecycleStatus")
    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("productNumber")
    public String getProductNumber() {
        return productNumber;
    }

    @JsonProperty("productNumber")
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("supportEntity")
    public String getSupportEntity() {
        return supportEntity;
    }

    @JsonProperty("supportEntity")
    public void setSupportEntity(String supportEntity) {
        this.supportEntity = supportEntity;
    }

    @JsonProperty("productSpecCharacteristic")
    public List<ProductSpecificationCharacteristic> getProductSpecificationCharacteristic() {
        return productSpecificationCharacteristic;
    }

    @JsonProperty("productSpecCharacteristic")
    public void setProductSpecificationCharacteristic(List<ProductSpecificationCharacteristic> productSpecificationCharacteristic) {
        this.productSpecificationCharacteristic = productSpecificationCharacteristic;
    }

    @JsonProperty("productSpecificationRelationship")
    public List<ProductSpecificationRelationship> getProductSpecificationRelationship() {
        return productSpecificationRelationship;
    }

    @JsonProperty("productSpecificationRelationship")
    public void setProductSpecificationRelationship(List<ProductSpecificationRelationship> productSpecificationRelationship) {
        this.productSpecificationRelationship = productSpecificationRelationship;
    }

    @JsonProperty("relatedParty")
    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    @JsonProperty("relatedParty")
    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    @JsonProperty("serviceSpecification")
    public List<ServiceSpecification> getServiceSpecification() {
        return serviceSpecification;
    }

    @JsonProperty("serviceSpecification")
    public void setServiceSpecification(List<ServiceSpecification> serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
    }

    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    @JsonProperty("validFor")
    public void setValidFor(ValidFor validFor) {
        this.validFor = validFor;
    }

    @JsonProperty("relatedResource")
    public List<RelatedResource> getRelatedResource() {
        return relatedResource;
    }

    @JsonProperty("relatedResource")
    public void setRelatedResource(List<RelatedResource> relatedResource) {
        this.relatedResource = relatedResource;
    }

    @JsonProperty("operationSpecification")
    public List<OperationSpecification> getOperationSpecification() {
        return operationSpecification;
    }

    @JsonProperty("operationSpecification")
    public void setOperationSpecification(List<OperationSpecification> operationSpecification) {
        this.operationSpecification = operationSpecification;
    }

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("usageSpecification")
    public List<UsageSpecification> getUsageSpecification() {
        return usageSpecification;
    }

    @JsonProperty("usageSpecification")
    public void setUsageSpecification(List<UsageSpecification> usageSpecification) {
        this.usageSpecification = usageSpecification;
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

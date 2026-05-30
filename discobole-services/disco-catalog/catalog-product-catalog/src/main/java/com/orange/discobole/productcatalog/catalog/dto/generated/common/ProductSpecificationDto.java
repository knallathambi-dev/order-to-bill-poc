// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.common;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.productcatalog.catalog.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.OperationSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductConfigurationSpec;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.UsageSpecification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.annotation.Generated;

/**
 * Is a detailed description of a tangible or intangible object made available
 * externally in the form of a ProductOffering to customers or other parties
 * playing a party role.
 */
@ApiModel(description = "Is a detailed description of a tangible or intangible object made available externally in the form of a ProductOffering to customers or other parties playing a party role.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-02-20T16:59:05.795+05:30")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSpecificationDto {
    @JsonProperty(ProductSpecConstants.ID)
    private String id = null;

    @JsonProperty(ProductSpecConstants.HREF)
    private String href = null;

    @JsonProperty(ProductSpecConstants.BRAND)
    private String brand = null;

    @JsonProperty(ProductSpecConstants.DESCRIPTION)
    private String description = null;

    @JsonProperty(ProductSpecConstants.IS_BUNDLE)
    private Boolean isBundle = null;

    @JsonProperty(ProductSpecConstants.LAST_UPDATE)
    private OffsetDateTime lastUpdate = null;

    @JsonProperty(ProductSpecConstants.LIFE_CYCLE_STATUS)
    private ProductSpecificationLifecycle lifecycleStatus = null;

    @JsonProperty(ProductSpecConstants.NAME)
    private String name = null;

    @JsonProperty(ProductSpecConstants.PRODUCT_NUMBER)
    private String productNumber = null;

    @JsonProperty(ProductSpecConstants.VERSION)
    private String version = null;

    @JsonProperty(ProductSpecConstants.SUPPORT_ENTITY)
    private SupportEntity supportEntity = null;

    @JsonProperty(ProductSpecConstants.POLICY_RULES)
    private List<PolicyRuleRef> policyRuleRef = null;

    @JsonProperty(ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTIC)
    private List<ProductSpecificationCharacteristic> productSpecCharacteristic = null;

    @JsonProperty(ProductSpecConstants.PRODUCT_SPECIFICATION_RELATIONSHIP)
    private List<ProductSpecificationRelationship> productSpecificationRelationship = null;

    @JsonProperty(ProductSpecConstants.RELATED_PARTY)
    private List<RelatedParty> relatedParty = null;

    @JsonProperty(ProductSpecConstants.RESOURCE_SPECIFICATION)
    private List<ResourceSpecificationRef> resourceSpecification = null;

    @JsonProperty(ProductSpecConstants.SERVICE_SPECIFICATION)
    private List<ServiceSpecificationRef> serviceSpecification = null;

    @JsonProperty(ProductSpecConstants.VALID_FOR)
    private TimePeriod validFor = null;

    @JsonProperty(ProductSpecConstants.PRODUCT_USAGE_SPECIFICATION)
    private List<UsageSpecification> productUsageSpecification = null;

    @JsonProperty(ProductSpecConstants.RELATED_RESOURCE)
    private List<RelatedResource> relatedResource = null;

    @JsonProperty(ProductSpecConstants.OPERATION_SPECIFICATION)
    private List<OperationSpecification> operationSpecification = null;

    @JsonProperty(ProductSpecConstants.STOCK_ITEM_TYPE)
    private StockItemType stockItemType = null;

    @JsonProperty("@baseType")
    private String baseType = null;

    @JsonProperty("@schemaLocation")
    private String schemaLocation = null;

    @JsonProperty("@type")
    private String type = null;

    @JsonProperty(ProductSpecConstants.USAGE_SPECIFICATION)
    private List<UsageSpecification> usageSpecification = null;

    private List<ProductConfigurationSpec> productConfiguration = null;

    public ProductSpecificationDto() {
    }

    public ProductSpecificationDto id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the product specification
     *
     * @return id
     **/
    @ApiModelProperty(value = "Unique identifier of the product specification")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductSpecificationDto href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Reference of the product specification
     *
     * @return href
     **/
    @ApiModelProperty(value = "Reference of the product specification")
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public ProductSpecificationDto brand(String brand) {
        this.brand = brand;
        return this;
    }

    /**
     * The manufacturer or trademark of the specification
     *
     * @return brand
     **/
    @ApiModelProperty(value = "The manufacturer or trademark of the specification")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ProductSpecificationDto description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A narrative that explains in detail what the product specification is
     *
     * @return description
     **/
    @ApiModelProperty(value = "A narrative that explains in detail what the product specification is")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductSpecificationDto isBundle(Boolean isBundle) {
        this.isBundle = isBundle;
        return this;
    }

    /**
     * isBundle determines whether a productSpecification represents a single
     * productSpecification (false), or a bundle of productSpecification (true).
     *
     * @return isBundle
     **/
    @ApiModelProperty(value = "isBundle determines whether a productSpecification represents a single productSpecification (false), or a bundle of productSpecification (true).")
    public Boolean isIsBundle() {
        return isBundle;
    }

    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    public ProductSpecificationDto lastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    /**
     * Date and time of the last update
     *
     * @return lastUpdate
     **/
    @ApiModelProperty(value = "Date and time of the last update")
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ProductSpecificationDto lifecycleStatus(ProductSpecificationLifecycle lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
        return this;
    }

    /**
     * Get lifecycleStatus
     *
     * @return lifecycleStatus
     **/
    @ApiModelProperty(value = "")
    public ProductSpecificationLifecycle getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(ProductSpecificationLifecycle lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public ProductSpecificationDto name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the product specification
     *
     * @return name
     **/
    @ApiModelProperty(value = "Name of the product specification")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductSpecificationDto productNumber(String productNumber) {
        this.productNumber = productNumber;
        return this;
    }

    /**
     * An identification number assigned to uniquely identity the specification
     *
     * @return productNumber
     **/
    @ApiModelProperty(value = "An identification number assigned to uniquely identity the specification")
    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public ProductSpecificationDto version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Product specification version
     *
     * @return version
     **/
    @ApiModelProperty(value = "Product specification version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ProductSpecificationDto supportEntity(SupportEntity supportEntity) {
        this.supportEntity = supportEntity;
        return this;
    }

    /**
     * Get supportEntity
     *
     * @return supportEntity
     **/
    @ApiModelProperty(required = true, value = "")
    public SupportEntity getSupportEntity() {
        return supportEntity;
    }

    public void setSupportEntity(SupportEntity supportEntity) {
        this.supportEntity = supportEntity;
    }

    public ProductSpecificationDto productSpecCharacteristic(
            List<ProductSpecificationCharacteristic> productSpecCharacteristic) {
        this.productSpecCharacteristic = productSpecCharacteristic;
        return this;
    }

    public ProductSpecificationDto addProductSpecCharacteristicItem(
            ProductSpecificationCharacteristic productSpecCharacteristicItem) {
        if (productSpecCharacteristic == null) {
            productSpecCharacteristic = new ArrayList<>();
        }
        productSpecCharacteristic.add(productSpecCharacteristicItem);
        return this;
    }

    /**
     * Get productSpecCharacteristic
     *
     * @return productSpecCharacteristic
     **/
    @ApiModelProperty(value = "")
    public List<ProductSpecificationCharacteristic> getProductSpecCharacteristic() {
        return productSpecCharacteristic;
    }

    public void setProductSpecCharacteristic(List<ProductSpecificationCharacteristic> productSpecCharacteristic) {
        this.productSpecCharacteristic = productSpecCharacteristic;
    }

    public ProductSpecificationDto policyRuleRef(List<PolicyRuleRef> policyRuleRef) {
        this.policyRuleRef = policyRuleRef;
        return this;
    }

    public ProductSpecificationDto addPolicyRuleItem(PolicyRuleRef policyRuleRefItem) {
        if (policyRuleRef == null) {
            policyRuleRef = new ArrayList<>();
        }
        policyRuleRef.add(policyRuleRefItem);
        return this;
    }

    /**
     * Get policyRuleRef
     *
     * @return policyRuleRef
     **/
    @ApiModelProperty(value = "")
    public List<PolicyRuleRef> getPolicyRuleRef() {
        return policyRuleRef;
    }

    public void setPolicyRuleRef(List<PolicyRuleRef> policyRuleRef) {
        this.policyRuleRef = policyRuleRef;
    }


    public ProductSpecificationDto productSpecificationRelationship(
            List<ProductSpecificationRelationship> productSpecificationRelationship) {
        this.productSpecificationRelationship = productSpecificationRelationship;
        return this;
    }

    public ProductSpecificationDto addProductSpecificationRelationshipItem(
            ProductSpecificationRelationship productSpecificationRelationshipItem) {
        if (productSpecificationRelationship == null) {
            productSpecificationRelationship = new ArrayList<>();
        }
        productSpecificationRelationship.add(productSpecificationRelationshipItem);
        return this;
    }

    /**
     * Get productSpecificationRelationship
     *
     * @return productSpecificationRelationship
     **/
    @ApiModelProperty(value = "")
    public List<ProductSpecificationRelationship> getProductSpecificationRelationship() {
        return productSpecificationRelationship;
    }

    public void setProductSpecificationRelationship(
            List<ProductSpecificationRelationship> productSpecificationRelationship) {
        this.productSpecificationRelationship = productSpecificationRelationship;
    }

    public ProductSpecificationDto relatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    public ProductSpecificationDto addRelatedPartyItem(RelatedParty relatedPartyItem) {
        if (relatedParty == null) {
            relatedParty = new ArrayList<>();
        }
        relatedParty.add(relatedPartyItem);
        return this;
    }

    /**
     * Get relatedParty
     *
     * @return relatedParty
     **/
    @ApiModelProperty(value = "")
    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public ProductSpecificationDto resourceSpecification(List<ResourceSpecificationRef> resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
        return this;
    }

    public ProductSpecificationDto addResourceSpecificationItem(ResourceSpecificationRef resourceSpecificationItem) {
        if (resourceSpecification == null) {
            resourceSpecification = new ArrayList<>();
        }
        resourceSpecification.add(resourceSpecificationItem);
        return this;
    }

    /**
     * Get resourceSpecification
     *
     * @return resourceSpecification
     **/
    @ApiModelProperty(value = "")
    public List<ResourceSpecificationRef> getResourceSpecification() {
        return resourceSpecification;
    }

    public void setResourceSpecification(List<ResourceSpecificationRef> resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
    }

    public ProductSpecificationDto serviceSpecification(List<ServiceSpecificationRef> serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
        return this;
    }

    public ProductSpecificationDto addServiceSpecificationItem(ServiceSpecificationRef serviceSpecificationItem) {
        if (serviceSpecification == null) {
            serviceSpecification = new ArrayList<>();
            if(serviceSpecificationItem == null){
                return this;
            }
        }
        serviceSpecification.add(serviceSpecificationItem);
        return this;
    }

    /**
     * Get serviceSpecification
     *
     * @return serviceSpecification
     **/
    @ApiModelProperty(value = "")
    public List<ServiceSpecificationRef> getServiceSpecification() {
        return serviceSpecification;
    }

    public void setServiceSpecification(List<ServiceSpecificationRef> serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
    }

    public ProductSpecificationDto validFor(TimePeriod validFor) {
        this.validFor = validFor;
        return this;
    }

    /**
     * Get validFor
     *
     * @return validFor
     **/
    @ApiModelProperty(value = "")
    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public ProductSpecificationDto productUsageSpecification(List<UsageSpecification> productUsageSpecification) {
        this.productUsageSpecification = productUsageSpecification;
        return this;
    }

    public ProductSpecificationDto addProductUsageSpecificationItem(UsageSpecification productUsageSpecificationItem) {
        if (productUsageSpecification == null) {
            productUsageSpecification = new ArrayList<>();
        }
        productUsageSpecification.add(productUsageSpecificationItem);
        return this;
    }

    /**
     * Get productUsageSpecification
     *
     * @return productUsageSpecification
     **/
    @ApiModelProperty(value = "")
    public List<UsageSpecification> getProductUsageSpecification() {
        return productUsageSpecification;
    }

    public void setProductUsageSpecification(List<UsageSpecification> productUsageSpecification) {
        this.productUsageSpecification = productUsageSpecification;
    }

    public ProductSpecificationDto relatedResource(List<RelatedResource> relatedResource) {
        this.relatedResource = relatedResource;
        return this;
    }

    public ProductSpecificationDto addRelatedResourceItem(RelatedResource relatedResourceItem) {
        if (relatedResource == null) {
            relatedResource = new ArrayList<>();
        }
        relatedResource.add(relatedResourceItem);
        return this;
    }

    public ProductSpecificationDto operationSpecification(List<OperationSpecification> operationSpecification) {
        this.operationSpecification = operationSpecification;
        return this;
    }

    public ProductSpecificationDto addOperationSpecificationItem(OperationSpecification operationSpecificationItem) {
        if (operationSpecification == null) {
            operationSpecification = new ArrayList<>();
        }
        operationSpecification.add(operationSpecificationItem);
        return this;
    }

    public ProductSpecificationDto stockItemType(StockItemType stockItemType) {
        this.stockItemType = stockItemType;
        return this;
    }

    /**
     * Get stockItemType
     *
     * @return stockItemType
     **/
    @ApiModelProperty(value = "")
    public StockItemType getStockItemType() {
        return stockItemType;
    }

    public void setStockItemType(StockItemType stockItemType) {
        this.stockItemType = stockItemType;
    }

    public ProductSpecificationDto baseType(String baseType) {
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

    public ProductSpecificationDto schemaLocation(String schemaLocation) {
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

    public ProductSpecificationDto type(String type) {
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

    public ProductSpecificationDto productConfiguration(List<ProductConfigurationSpec> productConfiguration) {
        this.productConfiguration = productConfiguration;
        return this;
    }

    /**
     *
     * @return productConfiguration
     **/
    @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")
    public List<ProductConfigurationSpec> getProductConfiguration() {
        return productConfiguration;
    }

    public void setProductConfiguration(List<ProductConfigurationSpec> productConfiguration) {
        this.productConfiguration = productConfiguration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductSpecificationDto productSpecification = (ProductSpecificationDto) o;
        return Objects.equals(id, productSpecification.id)
                && Objects.equals(href, productSpecification.href) && Objects.equals(brand, productSpecification.brand)
                && Objects.equals(description, productSpecification.description)
                && Objects.equals(isBundle, productSpecification.isBundle)
                && Objects.equals(lastUpdate, productSpecification.lastUpdate)
                && Objects.equals(lifecycleStatus, productSpecification.lifecycleStatus)
                && Objects.equals(name, productSpecification.name)
                && Objects.equals(productNumber, productSpecification.productNumber)
                && Objects.equals(version, productSpecification.version)
                && Objects.equals(supportEntity, productSpecification.supportEntity)
                && Objects.equals(policyRuleRef, productSpecification.policyRuleRef)
                && Objects.equals(productSpecCharacteristic, productSpecification.productSpecCharacteristic)
                && Objects.equals(productSpecificationRelationship,
                productSpecification.productSpecificationRelationship)
                && Objects.equals(relatedParty, productSpecification.relatedParty)
                && Objects.equals(resourceSpecification, productSpecification.resourceSpecification)
                && Objects.equals(serviceSpecification, productSpecification.serviceSpecification)
                && Objects.equals(validFor, productSpecification.validFor)
                && Objects.equals(productUsageSpecification, productSpecification.productUsageSpecification)
                && Objects.equals(relatedResource, productSpecification.relatedResource)
                && Objects.equals(operationSpecification, productSpecification.operationSpecification)
                && Objects.equals(stockItemType, productSpecification.stockItemType)
                && Objects.equals(baseType, productSpecification.baseType)
                && Objects.equals(schemaLocation, productSpecification.schemaLocation)
                && Objects.equals(this.productConfiguration, productSpecification.productConfiguration)
                && Objects.equals(type, productSpecification.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, brand, description, isBundle, lastUpdate, lifecycleStatus, name, productNumber,
                version, supportEntity, productSpecCharacteristic, policyRuleRef, productSpecificationRelationship, relatedParty,
                resourceSpecification, serviceSpecification, validFor, productUsageSpecification, relatedResource,
                operationSpecification, stockItemType, baseType, schemaLocation, type, productConfiguration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProductSpecification {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    isBundle: ").append(toIndentedString(isBundle)).append("\n");
        sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
        sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    productNumber: ").append(toIndentedString(productNumber)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    supportEntity: ").append(toIndentedString(supportEntity)).append("\n");
        sb.append("    productSpecCharacteristic: ").append(toIndentedString(productSpecCharacteristic)).append("\n");
        sb.append("    productSpecificationRelationship: ").append(toIndentedString(productSpecificationRelationship))
                .append("\n");
        sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
        sb.append("    resourceSpecification: ").append(toIndentedString(resourceSpecification)).append("\n");
        sb.append("    serviceSpecification: ").append(toIndentedString(serviceSpecification)).append("\n");
        sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
        sb.append("    policyRuleRef: ").append(toIndentedString(policyRuleRef)).append("\n");
        sb.append("    productUsageSpecification: ").append(toIndentedString(productUsageSpecification)).append("\n");
        sb.append("    relatedResource: ").append(toIndentedString(relatedResource)).append("\n");
        sb.append("    operationSpecification: ").append(toIndentedString(operationSpecification)).append("\n");
        sb.append("    stockItemType: ").append(toIndentedString(stockItemType)).append("\n");
        sb.append("    baseType: ").append(toIndentedString(baseType)).append("\n");
        sb.append("    productConfiguration: ").append(toIndentedString(productConfiguration)).append("\n");
        sb.append("    schemaLocation: ").append(toIndentedString(schemaLocation)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

    public List<OperationSpecification> getOperationSpecification() {
        return operationSpecification;
    }

    public void setOperationSpecification(List<OperationSpecification> operationSpecification) {
        this.operationSpecification = operationSpecification;
    }

    public List<UsageSpecification> getUsageSpecification() {
        return usageSpecification;
    }

    public void setUsageSpecification(List<UsageSpecification> usageSpecification) {
        this.usageSpecification = usageSpecification;
    }

    public List<RelatedResource> getRelatedResource() {
        return relatedResource;
    }

    public void setRelatedResource(List<RelatedResource> relatedResource) {
        this.relatedResource = relatedResource;
    }

}


// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
        "@baseType",
        "@referredType",
        "@schemaLocation",
        "@type",
        "href",
        "id",
        "name",
        "version"
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceSpecification {

    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@referredType")
    private String referredType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("href")
    private String href;
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("version")
    private String version;

    @JsonProperty("@baseType")
    public String getBaseType() {
        return baseType;
    }

    @JsonProperty("@baseType")
    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    @JsonProperty("@referredType")
    public String getReferredType() {
        return referredType;
    }

    @JsonProperty("@referredType")
    public void setReferredType(String referredType) {
        this.referredType = referredType;
    }

    @JsonProperty("@schemaLocation")
    public String getSchemaLocation() {
        return schemaLocation;
    }

    @JsonProperty("@schemaLocation")
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceSpecification)) {
            return false;
        }

        ServiceSpecification that = (ServiceSpecification) o;

        if (getBaseType() != null ? !getBaseType().equals(that.getBaseType()) : that.getBaseType() != null) {
            return false;
        }
        if (getReferredType() != null ? !getReferredType().equals(that.getReferredType()) : that.getReferredType() != null) {
            return false;
        }
        if (getSchemaLocation() != null ? !getSchemaLocation().equals(that.getSchemaLocation()) : that.getSchemaLocation() != null) {
            return false;
        }
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) {
            return false;
        }
        if (getHref() != null ? !getHref().equals(that.getHref()) : that.getHref() != null) {
            return false;
        }
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        return getVersion() != null ? getVersion().equals(that.getVersion()) : that.getVersion() == null;
    }

    @Override
    public int hashCode() {
        int result = getBaseType() != null ? getBaseType().hashCode() : 0;
        result = 31 * result + (getReferredType() != null ? getReferredType().hashCode() : 0);
        result = 31 * result + (getSchemaLocation() != null ? getSchemaLocation().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getHref() != null ? getHref().hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        return result;
    }
}
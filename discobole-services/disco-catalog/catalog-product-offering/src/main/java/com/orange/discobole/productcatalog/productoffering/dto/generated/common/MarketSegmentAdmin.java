// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class MarketSegmentAdmin {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("@type")
    private String atType;
    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private String schemaLocation;

    public MarketSegmentAdmin() {
    }

    @Override
    public String toString() {
        return "MarketSegmentAdmin{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", atType='" + atType + '\'' +
                ", atBaseType='" + atBaseType + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", href='" + href + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
    }

    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonProperty("href")
    private String href;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("lastModifiedDate")
    private LocalDateTime lastModifiedDate;
}

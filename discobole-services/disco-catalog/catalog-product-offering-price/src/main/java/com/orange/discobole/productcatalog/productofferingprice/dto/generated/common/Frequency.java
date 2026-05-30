// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Frequency {

    @JsonProperty("id")
    private String id;
    @JsonProperty("href")
    private String href;
    @JsonProperty("schemaLocation")
    private String schemaLocation;
    @JsonProperty("lastUpdate")
    private String lastUpdate;
    @JsonProperty("frequencyCode")
    private String frequencyCode;
    @JsonProperty("frequencyLabel")
    private String frequencyLabel;
    @JsonProperty("numberOfDays")
    private Integer numberOfDays;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(String frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public String getFrequencyLabel() {
        return frequencyLabel;
    }

    public void setFrequencyLabel(String frequencyLabel) {
        this.frequencyLabel = frequencyLabel;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    @Override
    public String toString() {
        return "Frequency{" +
                "id='" + id + '\'' +
                ", href='" + href + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", frequencyCode='" + frequencyCode + '\'' +
                ", frequencyLabel='" + frequencyLabel + '\'' +
                ", numberOfDays=" + numberOfDays +
                '}';
    }
}

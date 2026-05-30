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

public class Currency {

    @JsonProperty("id")
    private String id;
    @JsonProperty("href")
    private String href;
    @JsonProperty("schemaLocation")
    private String schemaLocation;
    @JsonProperty("lastUpdate")
    private String lastUpdate;
    @JsonProperty("code")
    private String code;
    @JsonProperty("label")
    private String label;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Currency id(String id) {
        this.id = id;
        return this;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public Currency code(String code) {
        this.code = code;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id='" + id + '\'' +
                ", href='" + href + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", code='" + code + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}

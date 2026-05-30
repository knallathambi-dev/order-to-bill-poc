// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CFSRelationshipRestriction {

    @JsonProperty("id")
    private String id;
    @JsonProperty("href")
    private String href;
    @JsonProperty("schemaLocation")
    private String schemaLocation;
    @JsonProperty("lastUpdate")
    private String lastUpdate;
    @JsonProperty("isRelationshipRestricted")
    private boolean isRelationshipRestricted;

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

    public boolean isRelationshipRestricted() {
        return isRelationshipRestricted;
    }

    public void setRelationshipRestricted(boolean relationshipRestricted) {
        isRelationshipRestricted = relationshipRestricted;
    }

    @Override
    public String toString() {
        return "CFSRelationshipRestriction{" +
                "id='" + id + '\'' +
                ", href='" + href + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", isRelationshipRestricted=" + isRelationshipRestricted +
                '}';
    }
}

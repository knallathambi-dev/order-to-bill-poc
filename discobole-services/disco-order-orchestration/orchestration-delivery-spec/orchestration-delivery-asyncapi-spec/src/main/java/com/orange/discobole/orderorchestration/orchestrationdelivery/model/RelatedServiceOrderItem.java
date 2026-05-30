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
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
        "id",
        "factoryRef"
})
@AllArgsConstructor
@NoArgsConstructor
public class RelatedServiceOrderItem {

    @JsonProperty("id")
    private String id;

    @JsonProperty("factoryRef")
    private String factoryRef;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("factoryRef")
    public String getFactoryRef() {
        return factoryRef;
    }

    @JsonProperty("factoryRef")
    public void setFactoryRef(String factoryRef) {
        this.factoryRef = factoryRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelatedServiceOrderItem)) {
            return false;
        }

        RelatedServiceOrderItem that = (RelatedServiceOrderItem) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        return getFactoryRef() != null ? getFactoryRef().equals(that.getFactoryRef()) : that.getFactoryRef() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFactoryRef() != null ? getFactoryRef().hashCode() : 0);
        return result;
    }
}

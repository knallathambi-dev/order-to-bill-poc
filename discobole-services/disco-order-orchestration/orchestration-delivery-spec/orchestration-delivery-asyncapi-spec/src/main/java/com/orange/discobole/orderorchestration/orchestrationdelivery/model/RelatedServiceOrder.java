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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@JsonPropertyOrder({
        "id",
        "SOMRef", "orderItemId"

})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatedServiceOrder {

    @JsonProperty("id")
    private String id;

    @JsonProperty("orderItemId")
    private String orderItemId;

    @JsonProperty("SOMRef")
    private String somRef;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("SOMRef")
    public String getSomRef() {
        return somRef;
    }

    @JsonProperty("SOMRef")
    public void setSomRef(String somRef) {
        this.somRef = somRef;
    }

    @JsonProperty("orderItemId")
    public String getOrderItemId() {
        return orderItemId;
    }

    @JsonProperty("orderItemId")
    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RelatedServiceOrder that = (RelatedServiceOrder) o;
        return Objects.equals(id, that.id) && Objects.equals(orderItemId, that.orderItemId) && Objects.equals(somRef, that.somRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderItemId, somRef);
    }
}

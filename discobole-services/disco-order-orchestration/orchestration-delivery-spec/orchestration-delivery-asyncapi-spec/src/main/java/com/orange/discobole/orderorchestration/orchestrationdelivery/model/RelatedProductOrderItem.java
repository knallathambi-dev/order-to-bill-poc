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
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@JsonPropertyOrder({
        "id",
        "action",
        "quantity"
})
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class RelatedProductOrderItem {

    @NotNull
    @JsonProperty("id")
    private String id;

    @JsonProperty("action")
    private String action;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public RelatedProductOrderItem(String id, String action, Integer quantity) {
        this.id = id;
        this.action = action;
        this.quantity = Objects.nonNull(quantity) ? quantity : 1;
    }
}

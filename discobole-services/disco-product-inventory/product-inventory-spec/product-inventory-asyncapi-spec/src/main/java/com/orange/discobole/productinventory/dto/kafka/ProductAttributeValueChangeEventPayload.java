// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.orange.discobole.productinventory.dto.v1.Product;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
    "product"
})
@Generated("jsonschema2pojo")
public class ProductAttributeValueChangeEventPayload implements Serializable
{

    /**
     * Product
     * <p>
     * 
     * 
     */
    @JsonProperty("product")
    @Valid
    private Product product;
    private final static long serialVersionUID = 8770875536330343464L;

}

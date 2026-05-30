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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.annotation.processing.Generated;
import java.io.Serializable;


/**
 * ProductStateChangeEvent
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonPropertyOrder({
    "event"
})
@Generated("jsonschema2pojo")
public class ProductStateChangeEvent
    extends BaseEvent
    implements Serializable
{

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("event")
    @Valid
    @NotNull
    private ProductStateChangeEventPayload event;
    private final static long serialVersionUID = -68593549393498517L;

}

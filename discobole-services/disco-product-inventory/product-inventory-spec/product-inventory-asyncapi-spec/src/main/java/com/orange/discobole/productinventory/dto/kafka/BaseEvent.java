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
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.time.Instant;


/**
 * BaseEvent
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonPropertyOrder({
        "eventId",
        "eventTime",
        "eventType",
        "correlationId",
        "@type"
})
@Generated("jsonschema2pojo")
public class BaseEvent implements Serializable {

    private final static long serialVersionUID = -4680751144000788402L;
    /**
     * The identifier of the notification.
     * (Required)
     */
    @JsonProperty("eventId")
    @JsonPropertyDescription("The identifier of the notification.")
    @NotNull
    private String eventId;
    /**
     * Time of the event occurrence.
     * (Required)
     */
    @JsonProperty("eventTime")
    @JsonPropertyDescription("Time of the event occurrence.")
    @NotNull
    private Instant eventTime;
    /**
     * The type of the notification.
     * (Required)
     */
    @JsonProperty("eventType")
    @JsonPropertyDescription("The type of the notification.")
    @NotNull
    private String eventType;
    /**
     * The correlation id for this event.
     */
    @JsonProperty("correlationId")
    @JsonPropertyDescription("The correlation id for this event.")
    private String correlationId;
    @JsonProperty("title")
    @JsonPropertyDescription("The title of this event.")
    private String title;
    @JsonProperty("domain")
    @JsonPropertyDescription("The domain of this event.")
    private String domain;
    /**
     * When sub-classing, this defines the sub-class entity name
     * (Required)
     */
    @JsonProperty("@type")
    @JsonPropertyDescription("When sub-classing, this defines the sub-class entity name")
    @NotNull
    private String type = "event";

}

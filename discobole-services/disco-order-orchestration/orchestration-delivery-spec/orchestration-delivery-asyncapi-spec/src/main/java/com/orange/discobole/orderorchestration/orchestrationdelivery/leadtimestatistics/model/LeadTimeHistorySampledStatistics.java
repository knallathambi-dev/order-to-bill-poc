// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonPropertyOrder({"id", "sampleWindow", "minActualLeadTime", "maxActualLeadTime", "averageActualLeadTime", "sampleSize"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
@SuperBuilder
@FieldNameConstants
public abstract class LeadTimeHistorySampledStatistics {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("sampleWindow")
    @Field("sampleWindow")
    private Instant sampleWindow; // hour bucket (UTC)

    @JsonProperty("minActualLeadTime")
    @Field(value = "minActualLeadTime")
    private Long minActualLeadTime;

    @JsonProperty("maxActualLeadTime")
    @Field(value = "maxActualLeadTime")
    private Long maxActualLeadTime;

    @JsonProperty("averageActualLeadTime")
    @Field(value = "averageActualLeadTime")
    private Float averageActualLeadTime;

    @JsonProperty("sampleSize")
    @Field("sampleSize")
    private Integer sampleSize;
}

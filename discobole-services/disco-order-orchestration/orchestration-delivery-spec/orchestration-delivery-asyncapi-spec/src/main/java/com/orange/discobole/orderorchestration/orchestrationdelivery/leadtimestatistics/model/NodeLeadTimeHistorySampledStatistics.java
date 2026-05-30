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
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"productSpecId", "deliveryFactoryName",})
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
@SuperBuilder
@Document(collection = "nodeLeadTimeHistorySampledStatistics")
@FieldNameConstants
public class NodeLeadTimeHistorySampledStatistics extends LeadTimeHistorySampledStatistics {

    @JsonProperty("productSpecId")
    @Field("productSpecId")
    private String productSpecId;

    @JsonProperty("deliveryFactoryName")
    @Field("deliveryFactoryName")
    private String deliveryFactoryName;

}

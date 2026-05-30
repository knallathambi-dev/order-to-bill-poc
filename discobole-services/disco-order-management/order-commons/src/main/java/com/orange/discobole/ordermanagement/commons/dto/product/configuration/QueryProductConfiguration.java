// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueryProductConfiguration {
    private String id;
    private String href;
    private Boolean instantSync;
    private TaskStateType state;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("contextCharacteristic")
    private List<Characteristic> contextCharacteristics;
    @JsonProperty("computedProductConfigurationItem")
    private List<QueryProductConfigurationItem> computedProductConfigurationItems;
    @JsonProperty("requestProductConfigurationItem")
    private List<QueryProductConfigurationItem> requestedProductConfigurationItems;
    @JsonProperty("relatedParty")
    private List<RelatedPartyRefOrPartyRoleRef> relatedParties;
    private ChannelRef channel;
    @JsonProperty("contextEntity")
    private EntityRef contextEntities;
}
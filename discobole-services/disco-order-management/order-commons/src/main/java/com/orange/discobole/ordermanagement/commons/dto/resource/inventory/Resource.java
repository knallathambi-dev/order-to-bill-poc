// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.resource.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private String href;
    private String id;
    private String category;
    private String description;
    private String endOperatingDate;
    private String name;
    private String value;
    private String resourceVersion;
    private String startOperatingDate;
    private List<ActivationFeature> activationFeature;
    private String administrativeState;
    private List<Attachment> attachment;
    private List<Note> note;
    private String operationalState;
    private Place place;
    private List<RelatedParty> relatedParty;
    private List<ResourceCharacteristic> resourceCharacteristic;
    private List<ResourceRelationship> resourceRelationship;
    private ResourceSpecification resourceSpecification;
    private String resourceStatus;
    private String usageState;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
}
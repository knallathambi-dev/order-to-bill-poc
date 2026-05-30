// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.key.OffsetDateTimeKeyDeserializer;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfferingQualificationItem {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.NATURAL, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @JsonDeserialize(keyUsing = OffsetDateTimeKeyDeserializer.class)
    private OffsetDateTime expectedActivationDate;
    private String qualificationItemResult;
    private ProductActionType action;
    private TaskStateType state;
    private TerminationError terminationError;
    @JsonProperty("note")
    private List<Note> notes;
    private ProductOfferingRef productOffering;
    @JsonProperty("alternateProductOfferingProposal")
    private List<AlternateProductOfferingProposal> alternateProductOfferingProposals;
    private ProductRefOrValue product;
    @JsonProperty("eligibilityUnavailabilityReason")
    private List<EligibilityUnavailabilityReason> eligibilityUnavailabilityReasons;
    @JsonProperty("qualificationItemRelationship")
    private List<QualificationItemRelationship> qualificationItemRelationships;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfferingQualification {
    private String id;
    private String href;
    private String description;
    private OffsetDateTime effectiveQualificationDate;
    private OffsetDateTime expectedPOQCompletionDate;
    private OffsetDateTime expirationDate;
    private Boolean instantSyncQualification;
    private OffsetDateTime productOfferingQualificationDate;
    private Boolean provideAlternative;
    private Boolean provideUnavailabilityReason;
    private Boolean provideOnlyAvailable;
    private String qualificationResult;
    private OffsetDateTime requestedPOQCompletionDate;
    private TaskStateType state;
    @JsonProperty("relatedParty")
    private List<RelatedParty> relatedParties;
    @JsonProperty("place")
    private List<RelatedPlaceRefOrValue> places;
    @JsonProperty("note")
    private List<Note> notes;
    private CategoryRef category;
    @JsonProperty("channel")
    private ChannelRef channelRef;
    @JsonProperty("productOfferingQualificationItem")
    private List<ProductOfferingQualificationItem> productOfferingQualificationItems;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
}
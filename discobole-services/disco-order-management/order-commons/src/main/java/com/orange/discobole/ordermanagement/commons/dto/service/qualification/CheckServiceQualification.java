// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.service.qualification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckServiceQualification {
    private String description;
    private String qualificationResult;
    private OffsetDateTime expectedQualificationDate;
    private Boolean provideAlternative;
    private Boolean isAppointmentRequired;
    private Boolean instantSyncQualification;
    private List<RelatedPartyRefOrPartyRoleRef> relatedParty;
    private List<ServiceQualificationItem> serviceQualificationItem;
    @JsonProperty("@type")
    private String type;
}
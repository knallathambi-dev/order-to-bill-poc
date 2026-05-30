// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.party.management;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartyCreditProfile {
    private String id;
    private String href;
    private String creditAgencyType;
    private String creditAgencyName;
    private String ratingReference;
    private Integer ratingScore;
    private TimePeriod validFor;
    private String baseType;
    private String schemaLocation;
    private String type;

}

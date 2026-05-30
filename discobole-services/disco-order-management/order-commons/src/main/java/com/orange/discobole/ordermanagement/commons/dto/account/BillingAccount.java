// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccount {
    private String id;
    private URI href;
    private String accountType;
    private String description;
    @DateTimeFormat(
            iso = ISO.DATE_TIME
    )
    private Instant lastModified;
    private String name;
    private String paymentStatus;
    private BillStructure billStructure;
    private String ratingType;
    private String state;
    private Money creditLimit;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private URI schemaLocation;
    @JsonProperty("@type")
    private String type;
    private List<RelatedPartyRefOrPartyRoleRef> relatedParty;
}
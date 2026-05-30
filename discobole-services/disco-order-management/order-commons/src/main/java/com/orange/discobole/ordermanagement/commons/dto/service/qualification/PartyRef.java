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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartyRef extends PartyRefOrPartyRoleRef {
    @JsonProperty("@baseType")
    private String atBaseType;
    @JsonProperty("@schemaLocation")
    private URI schemaLocation;
    private String href;
    private String id;
    private String name;
    @JsonProperty("@referredType")
    private String referredType;
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserRole {
    @JsonProperty("id")
    private String id;

    @JsonProperty("involvementRole")
    private String involvementRole;

    @JsonProperty("@type")
    @JsonAlias("atType")
    private String type;

    @JsonProperty("entitlement")
    private List<Entitlement> entitlement;

}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.common.web.client.config.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("involvementRole")
    private String involvementRole = null;

    @JsonProperty("@type")
    private String type = null;

    @JsonProperty("entitlement")
    private List<Entitlement> entitlement = null;

    @Override
    public String toString() {
        return "UserRole [id=" + id + ", involvementRole=" + involvementRole + ", type=" + type + ", entitlement="
                + entitlement + "]";
    }

}

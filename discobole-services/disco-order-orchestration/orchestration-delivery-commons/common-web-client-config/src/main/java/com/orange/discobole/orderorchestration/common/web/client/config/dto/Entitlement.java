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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entitlement {

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("action")
    private String action = null;

    @JsonProperty("function")
    private String function = null;

    @JsonProperty("@type")
    private String type = null;

    @Override
    public String toString() {
        return "Entitlement [id=" + id + ", action=" + action + ", function=" + function + ", type=" + type + "]";
    }

}

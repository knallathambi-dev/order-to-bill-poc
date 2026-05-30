// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.characteristic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.enums.ResolutionState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectResolutionStateWithReason {
    @JsonProperty("reason")
    @NotBlank
    @NotNull
    @Size(max = 250)
    private String reason;

    @JsonProperty("resolutionState")
    @NotNull
    @ReadOnly(true)
    private ResolutionState resolutionState;
}

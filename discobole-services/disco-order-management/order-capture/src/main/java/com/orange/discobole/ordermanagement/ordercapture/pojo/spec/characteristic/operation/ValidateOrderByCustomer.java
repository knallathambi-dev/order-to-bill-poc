// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.ordermanagement.ordercapture.enums.ValidationStatus;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOrderByCustomer {
    @JsonProperty("productOrderId")
    @NotNull
    @NotBlank
    @ReadOnly(true)
    private String id;
    @JsonProperty("orderValidationStatus")
    @NotNull
    private ValidationStatus status;
}
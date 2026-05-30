// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    @NotNull
    private Boolean reservePhysicalResourceEnabled;
    @NotNull
    private Boolean reserveLogicalResourceEnabled;
    @NotNull
    private Boolean checkCommercialEligibilityEnabled;
    @NotNull
    private Boolean checkPaymentRefEnabled;
    @NotNull
    private Boolean checkBillingAccountRefEnabled;
    @NotNull
    private Boolean checkAndSetBillCycleDateEnabled;
    @NotNull
    private Boolean checkPartyManagementEnabled;
    @NotNull
    private Boolean checkTechnicalEligibilityEnabled;
    @NotNull
    private Boolean checkAppointmentRefEnabled;
    @NotNull
    private Boolean checkFinancialEligibilityEnabled;
}
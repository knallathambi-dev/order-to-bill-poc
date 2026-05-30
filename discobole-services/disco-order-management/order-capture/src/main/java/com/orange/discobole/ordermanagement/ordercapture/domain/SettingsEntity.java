// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "settings")
public class SettingsEntity {
    @Id
    private String id;
    private boolean reservePhysicalResourceEnabled;
    private boolean reserveLogicalResourceEnabled;
    private boolean checkCommercialEligibilityEnabled;
    private boolean checkPaymentRefEnabled;
    private boolean checkBillingAccountRefEnabled;
    private boolean checkAndSetBillCycleDateEnabled;
    private boolean checkPartyManagementEnabled;
    private boolean checkTechnicalEligibilityEnabled;
    private boolean checkAppointmentRefEnabled;
    private boolean checkFinancialEligibilityEnabled;
}
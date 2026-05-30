// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductOperationalStatus {
    CREATED("Created"),
    CONFIRMED("Confirmed"),
    PENDINGACTIVE("PendingActive"),
    PENDINGCANCEL("PendingCancel"),
    LOCKED("Locked"),
    CANCELLED("Cancelled"),
    ABORTED("Aborted"),
    ACTIVE("Active"),
    INDISTURBANCE("InDisturbance"),
    PENDINGMODIFICATION("PendingModification"),
    PENDINGTERMINATE("PendingTerminate"),
    TERMIONATED("Terminated");

    private final String operationalStatus;

    ProductOperationalStatus(String operationalStatus) {this.operationalStatus = operationalStatus;}

    @JsonCreator
    public static ProductOperationalStatus fromValue(String operationalStatus) {
        for (ProductOperationalStatus operationalStatusTypeEnum : ProductOperationalStatus.values()) {
            if (operationalStatusTypeEnum.operationalStatus.equalsIgnoreCase(operationalStatus)) {
                return operationalStatusTypeEnum;
            }
        }
        return null;
    }

    @JsonValue
    public String getOperationalStatus() {
        return operationalStatus;
    }


}

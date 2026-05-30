// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductOperationalStatusTypeEnum {
    CREATED("Created"),
    PENDINGACTIVE("PendingActive"),
    CANCELLED("Cancelled"),
    ACTIVE("Active"),
    PENDINGTERMINATED("PendingTerminated"),
    TERMINATED("Terminated"),
    ABORTED("Aborted"),
    CONFIRMED("Confirmed"),
    PENDINGMODIFICATION("PendingModification"),
    PENDINGCANCEL("PendingCancel"),
    INDISTURBANCE("InDisturbance"),
    LOCKED("Locked");

    private final String operationalStatus;

    ProductOperationalStatusTypeEnum(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    @JsonCreator
    public static ProductOperationalStatusTypeEnum fromValue(String operationalStatus) {
        for (ProductOperationalStatusTypeEnum operationalStatusTypeEnum : ProductOperationalStatusTypeEnum.values()) {
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

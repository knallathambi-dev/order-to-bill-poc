// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class StatusReportingFields extends BaseDateDerivedFields {
    private Long activeCount;
    private Long terminatedCount;
    private Long cancelledCount;
    private Long abortedCount;
    private Long createdCount;
    private Long soldCount;

    public StatusReportingFields(StatusReportingFields p) {
        super(p.getDayOfMonth(), p.getIsoDayOfWeek(), p.getDayOfYear(), p.getDate());
        this.activeCount = p.getActiveCount();
        this.terminatedCount = p.getTerminatedCount();
        this.cancelledCount = p.getCancelledCount();
        this.abortedCount = p.getAbortedCount();
        this.createdCount = p.getCreatedCount();
        this.soldCount = p.getSoldCount();
    }
}

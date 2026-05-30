// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model.job;

import com.orange.discobole.productinventory.dto.v1.DatePeriod;
import com.orange.discobole.productinventory.dto.v1.JobFrequency;
import com.orange.discobole.productinventory.dto.v1.JobSchedulerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class JobSpecificationScheduler {
    private JobSchedulerType atType;
    private JobFrequency frequency;
    private OffsetDateTime plannedDate;

    private DatePeriod scheduledPeriod;

    private String executionTime;

}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation;

import com.orange.discobole.productinventory.dto.v1.ImmediateJobScheduler;
import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationType;
import com.orange.discobole.productinventory.dto.v1.OneTimeJobScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportJobValidator {
    private ImportJobValidator() {
    }

    public static boolean isCreationObjectValid(JobSpecification jobSpecification) {
        var isImmediateOrOneTimeJobScheduler = jobSpecification.getSchedule() instanceof ImmediateJobScheduler || jobSpecification.getSchedule() instanceof OneTimeJobScheduler;
        return JobSpecificationType.IMPORTJOBSPECIFICATION.getValue().equals(jobSpecification.getAtType()) && isImmediateOrOneTimeJobScheduler;
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util.creator;

import com.orange.discobole.productinventory.dto.v1.*;
import lombok.SneakyThrows;

import java.time.OffsetDateTime;

public class JobSpecificationCreator {

    @SneakyThrows
    public static ImportJobSpecification.ImportJobSpecificationBuilder createImportJobSpecificationBuilderWithOneTimeJobScheduler(OffsetDateTime plannedDate) {
        return ImportJobSpecification
                .builder()
                .atType(JobSpecificationType.IMPORTJOBSPECIFICATION.getValue())
                .importType(ImportTypeEnum.INSERT)
                .contentType(ContentTypeEnum.JSON)
                .schedule(OneTimeJobScheduler
                        .builder()
                        .atType(JobSchedulerType.ONETIMEJOBSCHEDULER.getValue())
                        .plannedDate(plannedDate)
                        .build());

    }

    @SneakyThrows
    public static ImportJobSpecification.ImportJobSpecificationBuilder createImportJobSpecificationBuilderWithImmediateJobScheduler(ContentTypeEnum contentTypeEnum) {
        return ImportJobSpecification
                .builder()
                .atType(JobSpecificationType.IMPORTJOBSPECIFICATION.getValue())
                .importType(ImportTypeEnum.INSERT)
                .contentType(contentTypeEnum)
                .schedule(ImmediateJobScheduler
                        .builder()
                        .atType(JobSchedulerType.IMMEDIATEJOBSCHEDULER.getValue())
                        .build());

    }
    @SneakyThrows
    public static PurgeJobSpecification.PurgeJobSpecificationBuilder createPurgeJobSpecificationBuilderWithJobScheduler(String query, PurgeTypeEnum purgeType,
                                                                                                                               JobScheduler jobSchedule) {
        return PurgeJobSpecification
                .builder()
                .atType("PurgeJobSpecification")
                .purgeType(purgeType)
                .query(query)
                .schedule(jobSchedule);

    }


}
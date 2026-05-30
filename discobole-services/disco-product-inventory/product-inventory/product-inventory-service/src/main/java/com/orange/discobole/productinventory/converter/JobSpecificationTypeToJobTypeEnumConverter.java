// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter;

import com.orange.discobole.productinventory.dto.v1.JobSpecificationType;
import com.orange.discobole.productinventory.dto.v1.JobTypeEnum;

public class JobSpecificationTypeToJobTypeEnumConverter {

    public static JobTypeEnum convert(JobSpecificationType source) {
        return switch (source) {
            case TERMINATIONJOBSPECIFICATION -> JobTypeEnum.TERMINATIONJOB;
            case EXPORTJOBSPECIFICATION -> JobTypeEnum.EXPORTJOB;
            case PURGEJOBSPECIFICATION -> JobTypeEnum.PURGEJOB;
            case IMPORTJOBSPECIFICATION -> JobTypeEnum.IMPORTJOB;
        };
    }
}

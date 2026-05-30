// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model.job;

import com.orange.discobole.productinventory.model.JobReportProductRefEntity;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Document(collection = "jobReport")
public class JobReportEntity {
    private String id;
    private String jobId;
    private String jobSpecificationId;
    private List<JobReportProductRefEntity> failedProducts;
    private List<JobReportProductRefEntity> succeededProducts;
}

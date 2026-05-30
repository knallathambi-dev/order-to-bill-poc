// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model.job;

import com.orange.discobole.productinventory.dto.v1.JobStatusType;
import com.orange.discobole.productinventory.dto.v1.JobTypeEnum;
import com.orange.discobole.productinventory.dto.v1.TimePeriod;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@CompoundIndex(name = "initial_status_index", def = "{'status': 1}", partialFilter = "{ 'status': 'INITIAL'}")
@Document(collection = "job")
public class JobEntity {
    private String id;
    private JobSpecificationEntityRef jobSpecification;
    private TimePeriod executionPeriod;
    private OffsetDateTime plannedDate;
    private JobStatusType status;
    private String fileName;
    private String errorLog;
    private JobTypeEnum atType;
    private String atBaseType;

}

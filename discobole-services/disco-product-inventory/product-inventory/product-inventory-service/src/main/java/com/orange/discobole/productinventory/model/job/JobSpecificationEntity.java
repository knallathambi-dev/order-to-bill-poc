// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model.job;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Document(collection = "jobSpecification")
public class JobSpecificationEntity {
    private String id;
    private String name;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private JobSpecificationType atType;
    private String atBaseType;
    private JobSpecificationStatusType lifecycleStatus;
    private List<JobSpecificationStatusChange> lifeCycleStatusChange;
    private TimePeriod activePeriod;
    private String errorLog;
    private String query;
    private FileType fileType;
    private OffsetDateTime creationDate;
    private JobSpecificationScheduler schedule;
    private PurgeTypeEnum purgeType;
    private Set<String> fields;
    private Boolean checkCatalog = false;
    private ImportTypeEnum importType;
}

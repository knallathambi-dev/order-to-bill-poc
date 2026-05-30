// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * Entity representing the state of the cron job run for reporting.
 * This entity is used to track whether the cron job has run for a specific date.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Document(collection = "report-cron-run-state")
public class ReportCronRunState {

    @Id
    private ObjectId id;
    private LocalDate date;
    private boolean hasRun;
}

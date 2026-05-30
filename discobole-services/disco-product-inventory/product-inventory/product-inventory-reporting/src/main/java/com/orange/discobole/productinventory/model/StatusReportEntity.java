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
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@Document(collection = "status-report")
@CompoundIndex(def = "{'date': 1, 'dayOfMonth': 1}")  // Compound index for date and dayOfMonth
@CompoundIndex(def = "{'date': 1, 'isoDayOfWeek': 1}")  // Compound index for date and isoDayOfWeek
@CompoundIndex(def = "{'date': 1, 'dayOfYear': 1}")  // Compound index for date and dayOfYear
public class StatusReportEntity extends StatusReportingFields {
    private ObjectId id;

    public StatusReportEntity(StatusReportingFields p) {
        super(p);
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.query.purge;

import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationStatusType;
import com.orange.discobole.productinventory.validation.pageable.impl.FieldFetcher;
import com.orange.discobole.productinventory.validation.query.QueryValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.LIFE_CYCLE_STATUS;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.ALL_LIFE_CYCLE_STATUS_VALUES_SHOULD_BE_IN;

@Slf4j
public class JobPurgeTypeQueryValidator extends PurgeTypeQueryValidator implements QueryValidator {
    public static final List<JobSpecificationStatusType> JOB_SPECIFICATION_STATUS_CAN_BE_DELETED = List.of(JobSpecificationStatusType.TERMINATED, JobSpecificationStatusType.CREATED);
    private static final FieldFetcher<JobSpecification> JOB_SPECIFICATION_FIELD_FETCHER = new FieldFetcher<>(JobSpecification.class);

    @Override
    public void validate(String query) {
        validate(query, LIFE_CYCLE_STATUS, ALL_LIFE_CYCLE_STATUS_VALUES_SHOULD_BE_IN, JOB_SPECIFICATION_STATUS_CAN_BE_DELETED, JOB_SPECIFICATION_FIELD_FETCHER);
    }

}

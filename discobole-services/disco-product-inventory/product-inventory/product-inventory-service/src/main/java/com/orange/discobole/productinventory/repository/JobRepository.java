// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;


import com.orange.discobole.productinventory.dto.v1.JobStatusType;
import com.orange.discobole.productinventory.model.job.JobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;


@Repository
public interface JobRepository extends MongoRepository<JobEntity, String> {

    List<JobEntity> findJobsByStatusAndPlannedDateLessThanEqual(JobStatusType status, OffsetDateTime offsetDateTime);
    JobEntity findByJobSpecification_Id(String id);
    void deleteByJobSpecificationId(String id);

}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;


import com.orange.discobole.productinventory.model.job.JobReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface JobReportRepository extends MongoRepository<JobReportEntity, String> {
    void deleteByJobSpecificationIdIn(Set<String> jobSpecificationIds);
    void deleteByJobSpecificationId(String jobSpecificationId);

    Optional<JobReportEntity> findByJobId(String jobId);
}

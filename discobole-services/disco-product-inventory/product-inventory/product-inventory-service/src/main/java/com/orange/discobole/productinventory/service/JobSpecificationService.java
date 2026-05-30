// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface JobSpecificationService {
    Integer getTotalCount(MultiValueMap<String, Object> multiValueMap);

    Optional<JobSpecificationEntity> getJobSpecificationEntityById(String id) throws ProductInventoryException;

    List<JobSpecification> getJobSpecifications(PageableTMF pageable);

    JobSpecification getJobSpecificationById(String id, String fields);

    void deleteJobSpecification(String id);

    JobSpecificationEntity save(JobSpecificationEntity any);

    JobSpecificationEntity updateJobSpecificationEntity(JobSpecificationEntity jobSpecificationEntity);

}

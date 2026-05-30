// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;


import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public interface JobSpecificationRepository extends MongoRepository<JobSpecificationEntity, String> {
    void deleteByIdIn(Collection<String> ids);
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;


import com.orange.discobole.productinventory.dto.v1.Job;
import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.dto.v1.ProductRefOrValue;

import java.util.Collection;


public interface HrefGeneratorService {
    String getBaseUrlProductApi();

    String getProductHref(String productId, String baseUrl);

    void generateHrefProductRelationships(Collection<? extends ProductRefOrValue> products);

    void generateHrefJobSpecification(Collection<JobSpecification> jobSpecifications);

    void generateHrefJob(Collection<Job> jobs);
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.repository;

import com.orange.disco.admin.FrequencyTypes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FrequencyTypesRepository extends MongoRepository<FrequencyTypes,String> {
    Optional<FrequencyTypes> findByfrequencyLabel(String frequencyLabel);

    Optional<FrequencyTypes> findByfrequencyCode(String frequencyCode);
}

// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.repository;

import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SettingsRepository extends MongoRepository<SettingsEntity, String> {
    SettingsEntity getSettingsById(String id);
}
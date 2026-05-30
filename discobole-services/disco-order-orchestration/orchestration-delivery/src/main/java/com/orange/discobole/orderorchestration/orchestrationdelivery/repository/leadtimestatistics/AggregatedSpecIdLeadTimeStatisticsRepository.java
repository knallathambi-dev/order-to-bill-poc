
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.AggregatedSpecIdLeadTimeStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AggregatedSpecIdLeadTimeStatisticsRepository extends MongoRepository<AggregatedSpecIdLeadTimeStatistics, String> {
    AggregatedSpecIdLeadTimeStatistics findByProductSpecId(String productSpecId);
}

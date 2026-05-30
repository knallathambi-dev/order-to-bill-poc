// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics;

import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NodeLeadTimeHistoryStatisticsRepository
        extends MongoRepository<NodeLeadTimeHistorySampledStatistics, String> {
}

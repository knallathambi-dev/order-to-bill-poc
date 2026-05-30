// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.leadtimestatistics;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.ContractLeadTimeStatisticsResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.NodeLeadTimeStatisticsResponse;

import java.time.Instant;
import java.util.List;

public interface LeadTimeStatisticsService {
    NodeLeadTimeStatisticsResponse getNodeLeadTimeHistoryStatistics(
            String productSpecId,
            String deliveryFactoryName,
            boolean includeMin,
            boolean includeMax,
            boolean includeAvg,
            Instant start,
            Instant end,
            Integer approximateCount,
            List<String> responseProjectionFields,
            int safeOffset,
            int safeLimit,
            String normalizedSort
    );

    ContractLeadTimeStatisticsResponse getContractLeadTimeHistoryStatistics(
            String contractName,
            boolean includeMin,
            boolean includeMax,
            boolean includeAvg,
            Instant start,
            Instant end,
            Integer approximateCount,
            List<String> responseProjectionFields,
            int safeOffset,
            int safeLimit,
            String normalizedSort
    );
}

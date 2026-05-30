// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records;

public record LeadTimeHistorySampledStatisticsRecord(String contractName, String specificationId, String deliveryFactoryName, String minActualLeadTime, String maxActualLeadTime, String averageActualLeadTime, String sampleSize, String sampleWindow) {
}

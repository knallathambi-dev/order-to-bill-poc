// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.Map;

public interface ResourceInventoryService {
    Map<String, List<Resource>> getReservedResourceList(Map<String, List<Resource>> productOrderItemLogicalResources);

    @Retryable(retryFor = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    Map<String, List<Resource>> getAvailableResources(Map<String, List<String>> productOrderItemLogicalResources);

    Map<String, List<Resource>> checkAndReserveLogicalResources(Map<String, List<String>> productOrderItemLogicalResources);

    void rollBackReservedResource(List<String> resourceIds);


}
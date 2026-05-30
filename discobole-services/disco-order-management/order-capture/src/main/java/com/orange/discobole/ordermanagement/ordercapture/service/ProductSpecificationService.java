// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

public interface ProductSpecificationService {
    @Retryable(retryFor = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    List<ProductSpecification> fetchProductSpecifications(List<String> productSpecificationIdList);
}
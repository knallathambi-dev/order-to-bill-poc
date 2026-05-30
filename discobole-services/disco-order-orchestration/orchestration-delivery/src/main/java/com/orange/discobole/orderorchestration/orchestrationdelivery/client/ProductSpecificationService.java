// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.PCHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductSpecificationValidationException;

import java.util.List;
import java.util.Map;

public interface ProductSpecificationService {
    ProductSpecification retrieveProductSpecById(String id) throws ProductSpecificationValidationException;

    Map<String, ProductSpecification> retrieveProductSpecificationsFromProductOrderItem(List<ProductOrderItem> productOrderItemDTOS, String orchestrationPlanId) throws PCHttpFailedException;

}

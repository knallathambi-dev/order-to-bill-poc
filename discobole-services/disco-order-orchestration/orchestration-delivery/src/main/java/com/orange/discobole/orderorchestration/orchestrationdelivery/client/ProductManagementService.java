// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client;

import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.CPIBHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;

import java.util.List;


public interface ProductManagementService {

    List<Product> getProductsByOrderIdAndItemIds(List<String> orderItemIds, String productOrderId) throws ProductOrderValidationException;

    List<Product> getProductsByFields(List<String> productsId, List<String> fields) throws CPIBHttpFailedException;

    void updateCFSOperationalStatus(Product product, ProductOperationalStatusType operationalStatusType);

    void updateTangibleOperationalStatus(Product product, ProductOperationalStatusType operationalStatusType);

    void updateActualCPIBProductCharacteristicBasedOnServiceOrderItemCompletionStatue(Product product, OrchestrationPlanNode orchestrationPlanNode);
}

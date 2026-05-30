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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Product;

import java.util.List;
import java.util.Map;

public interface ProductOrderService {
    ProductOrder createProductOrder(ProductOrder productOrder);

    void updateProductOrderInventoryState(ProductOrder productOrder, ProductOrderStateType productOrderState);

    void updateOrderItemsAndOrderTotalPrice(ProductOrder productOrderDTO, List<ProductOrderItem> productOrderItems, List<OrderPrice> orderTotalPrices);

    Map<String, List<String>> getProductOrderItemLogicalResourcesIds(Map<String, String> productOrderItemProdSpecMap, List<ProductSpecification> productSpecifications);

    List<String> getPhysicalProductOrderItems(Map<String, String> productOrderItemProductSpecIdMap, List<ProductSpecification> productSpecifications);

    void addResourceRef(ProductOrder productOrder, Map<String, List<ResourceRef>> productOrderItemResourcesMap);

    void addPaymentRef(ProductOrder productOrder, Map<String, List<String>> orderItemPaymentRefList);

    void addBillingAccountRef(ProductOrder productOrder, Map<String, String> orderItemBillingAccountRef);

    void addAppointmentRef(ProductOrder productOrder, Map<String, String> orderItemAppointmentRef);

    void updateProduct(ProductOrder productOrder);

    List<Product> createProducts(ProductOrder productOrder, Map<String, String> physicalProductOrderItemSpecSerialNumberMap, String requestedConfigurationAction, String contractProductId);

    void updateProductOrderRelatedParties(ProductOrder productOrder);

    void updateValidityCharacteristic(ProductOrder productOrder, Map<ProductOrderItem, ValidityCharacteristic> validityMap);

    void updateRequestedCompletionDate(ProductOrder productOrder);
}
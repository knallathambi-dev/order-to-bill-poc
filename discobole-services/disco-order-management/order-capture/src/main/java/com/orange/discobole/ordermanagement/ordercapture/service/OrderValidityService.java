// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.ADD;


@Service
@Slf4j
public class OrderValidityService {

    private final BillingCycleService billingCycleService;
    private final ProductOrderService productOrderService;
    private final ProductInventoryValidityCharacteristicService productInventoryValidityCharacteristicService;


    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderValidityService(ProductOrderService productOrderService, BillingCycleService billingCycleService, ProductInventoryValidityCharacteristicService productInventoryValidityCharacteristicService) {
        this.productOrderService = productOrderService;
        this.billingCycleService = billingCycleService;
        this.productInventoryValidityCharacteristicService = productInventoryValidityCharacteristicService;
    }

    private Map<ProductOrderItem, ValidityCharacteristic> extractValidityCharacteristics(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(item -> ADD.equals(item.getAction().getValue())
                        && item.getProduct() instanceof Product product
                        && !CollectionUtils.isEmpty(product.getProductCharacteristic())
                        && product.getProductCharacteristic().stream().anyMatch(ValidityCharacteristic.class::isInstance))
                .collect(Collectors.toMap(
                        item -> item,
                        item -> {
                            Product product = (Product) item.getProduct();
                            return product.getProductCharacteristic().stream()
                                    .filter(ValidityCharacteristic.class::isInstance)
                                    .map(ValidityCharacteristic.class::cast)
                                    .filter(validity -> validity.getValue() != null && Objects.nonNull(validity.getValue().getValidTo())
                                            && validity.getValue().getValidTo().isBefore(Instant.now()))
                                    .toList();
                        }
                ))
                .entrySet().stream()
                .filter(entry -> !CollectionUtils.isEmpty(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));
    }

    private void updateValidityDatesBasedOnBillingCycle(Map<ProductOrderItem, ValidityCharacteristic> validityMap, Instant nextBillCycle) {
        validityMap.forEach((productOrderItem, validityCharacteristic) ->
                validityCharacteristic.getValue().setValidTo(nextBillCycle));
    }

    public void adjustOrderRequestedCompletionDate(ProductOrder productOrder, String relatedPartyId) {
        if (Objects.nonNull(productOrder.getRequestedCompletionDate()) && productOrder.getRequestedCompletionDate().isBefore(Instant.now())) {
            Instant nextBillCycle = billingCycleService.getNextBillingDate(relatedPartyId);
            productOrder.setRequestedCompletionDate(nextBillCycle);
            productOrderService.updateRequestedCompletionDate(productOrder);
        }
    }

    public void handleInvalidValidityCharacteristics(ProductOrder productOrder, String relatedPartyId, boolean isAccepted) {
        Map<ProductOrderItem, ValidityCharacteristic> validityMap =
                extractValidityCharacteristics(productOrder.getProductOrderItem());

        if (validityMap.isEmpty()) {
            return;
        }

        Instant nextBillingCycle =
                billingCycleService.getNextBillingDate(relatedPartyId);

        updateValidityDatesBasedOnBillingCycle(validityMap, nextBillingCycle);
        productOrderService.updateValidityCharacteristic(productOrder, validityMap);

        if (isAccepted) {
            productInventoryValidityCharacteristicService.updateProductValidityCharacteristic(validityMap);
        }
    }
}

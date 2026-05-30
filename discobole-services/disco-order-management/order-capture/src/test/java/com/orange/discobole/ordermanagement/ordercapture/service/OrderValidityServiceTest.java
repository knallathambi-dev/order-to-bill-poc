// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;


import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType.ADD;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderValidityServiceTest {

    @InjectMocks
    @Spy
    private OrderValidityService orderValidityService;
    @Mock
    private ProductOrderService productOrderService;

    @Mock
    private BillingCycleService billingCycleService;
    @Mock
    private ProductInventoryValidityCharacteristicService productInventoryValidityCharacteristicService;

    @Test
    @DisplayName("Given product order " +
            "when requested completion date is before now" +
            "then it should updated ")
    void shouldUpdateRequestedCompletionDateWhenItIsBeforeNow() {
        //Given
        ProductOrder productOrder = ProductOrder
                .builder()
                .id("123")
                .build();
        productOrder.setRequestedCompletionDate(Instant.now().minusSeconds(3600));
        com.orange.discobole.processflow.dto.generated.RelatedParty mockParty = new com.orange.discobole.processflow.dto.generated.RelatedParty();
        mockParty.setId("taskRelatedParty");
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.parse("2100-01-01T23:59:59Z"));
        doNothing().when(productOrderService).updateRequestedCompletionDate(productOrder);

        //When
        orderValidityService.adjustOrderRequestedCompletionDate(productOrder, mockParty.getId());

        //Then
        assertNotNull(productOrder.getRequestedCompletionDate());
        assertTrue(productOrder.getRequestedCompletionDate().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Given a ProductOrderItem with 'Validity' characteristic, " +
            "when updateValidityCharacteristic is called, " +
            "then updateProducts is called with the correct JSON patch.")
    void shouldUpdateValidityCharacteristicAndCallUpdateProductsWhenOrderIsAck() {
        //Given
        ProductOrder productOrder = ProductOrder
                .builder()
                .id("123")
                .state(ProductOrderStateType.ACKNOWLEDGED)
                .build();
        String productId = "testProductId";

        ValidityCharacteristic existing = ValidityCharacteristic.builder()
                .name("Validity")
                .value(ValidityValue.builder()
                        .validTo(Instant.parse("2025-12-31T23:59:59Z"))
                        .build())
                .build();
        Product product = com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                .id(productId)
                .productCharacteristic(List.of(existing))
                .build();
        ProductOrderItem orderItem = ProductOrderItem.builder()
                .id("myItemId")
                .action(ADD)
                .product(product)
                .build();
        productOrder.setProductOrderItem(List.of(orderItem));
        com.orange.discobole.processflow.dto.generated.RelatedParty mockParty = new com.orange.discobole.processflow.dto.generated.RelatedParty();
        mockParty.setId("taskRelatedParty");
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.parse("2100-01-01T23:59:59Z"));
        doNothing().when(productOrderService).updateValidityCharacteristic(any(), anyMap());

        //When
        orderValidityService.handleInvalidValidityCharacteristics(productOrder, mockParty.getId(), false);

        //Then
        verify(orderValidityService, times(1)).handleInvalidValidityCharacteristics(productOrder, mockParty.getId(), false);
    }

    @Test
    @DisplayName("Given a ProductOrderItem with 'Validity' characteristic, " +
            "when updateValidityCharacteristic is called, " +
            "then updateProducts is called with the correct JSON patch.")
    void shouldUpdateValidityCharacteristicAndCallUpdateProductsWhenOrderIsAccepted() {
        //Given
        ProductOrder productOrder = ProductOrder
                .builder()
                .id("123")
                .state(ProductOrderStateType.ACCEPTED)
                .build();

        String productId = "testProductId";
        ValidityCharacteristic existing = ValidityCharacteristic.builder()
                .name("Validity")
                .value(ValidityValue.builder()
                        .validTo(Instant.parse("2025-12-31T23:59:59Z"))
                        .build())
                .build();
        Product product = com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                .id(productId)
                .productCharacteristic(List.of(existing))
                .build();
        ProductOrderItem orderItem = ProductOrderItem.builder()
                .id("myItemId")
                .action(ADD)
                .product(product)
                .build();
        productOrder.setProductOrderItem(List.of(orderItem));
        doNothing().when(productOrderService).updateValidityCharacteristic(any(), anyMap());
        com.orange.discobole.processflow.dto.generated.RelatedParty mockParty = new com.orange.discobole.processflow.dto.generated.RelatedParty();
        mockParty.setId("taskRelatedParty");

        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.parse("2100-01-01T23:59:59Z"));
        doNothing().when(productInventoryValidityCharacteristicService).updateProductValidityCharacteristic(any());


        //When
        orderValidityService.handleInvalidValidityCharacteristics(productOrder, mockParty.getId(), true);

        //Then
        verify(orderValidityService, times(1)).handleInvalidValidityCharacteristics(productOrder, mockParty.getId(), true);
    }

}
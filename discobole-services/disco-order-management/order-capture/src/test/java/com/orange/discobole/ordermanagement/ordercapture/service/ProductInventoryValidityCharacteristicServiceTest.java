// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType.ADD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductInventoryValidityCharacteristicServiceTest {
    @Spy
    @InjectMocks
    private ProductInventoryValidityCharacteristicService productInventoryValidityCharacteristicService;
    @Mock
    private ProductInventoryService productInventoryService;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Given a ProductOrderItem with 'Validity' characteristic, " +
            "when updateProductValidityCharacteristic is called, " +
            "then updateProducts is called with the correct JSON patch.")
    void shouldUpdateValidityCharacteristic() throws JsonProcessingException {
        //Given
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
        List<com.orange.discobole.productinventory.dto.v1.Product> products = List.of(createProduct());
        when(productInventoryService.getProductByIds(anyList())).thenReturn(products);
        when(productInventoryService.updateProducts(anyString())).thenReturn(products);
        when(objectMapper.writeValueAsString(any())).thenReturn("patchToStringValue");
        Map<ProductOrderItem, ValidityCharacteristic> validityMap = Map.of(orderItem, existing);
        //When
        productInventoryValidityCharacteristicService.updateProductValidityCharacteristic(validityMap);

        //Then
        verify(productInventoryValidityCharacteristicService, times(1)).updateProductValidityCharacteristic(validityMap);
    }

    private com.orange.discobole.productinventory.dto.v1.Product createProduct() {
        return com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id("testProductId")
                .productCharacteristic(List.of(com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic.builder()
                        .id("123")
                        .name("Validity")
                        .value(Value.builder()
                                .validTo(OffsetDateTime.parse("2026-01-10T14:17:33Z")).build())
                        .build()))
                .atType("Product")
                .build();
    }


}
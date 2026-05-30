// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.util.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OptionalProductOfferingValidatorTest {

    private final OptionalProductOfferingValidator optionalProductOfferingValidator = new OptionalProductOfferingValidator();

    @Test
    @DisplayName("Given a valid atomic accessory product offering, " +
            "when isValid is called, " +
            "then it shouldReturnValidResponse")
    void shouldReturnValidResponseWhenValidAtomicProductOffering() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE, true);

        // When
        ResponseResult result = optionalProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.ACCESSORY_SELECTED_OFFER, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid bundle accessory product offering, " +
            "when isValid is called, " +
            "then it shouldReturnValidResponse")
    void shouldReturnValidResponseWhenValidBundledProductOffering() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE, true);

        // When
        ResponseResult result = optionalProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.ACCESSORY_SELECTED_OFFER, result.getDescription());
    }

    @ParameterizedTest
    @MethodSource("invalidProductOfferingProvider")
    @DisplayName("Given an invalid product offering, " +
            "when isValid is called, " +
            "then it shouldReturnInvalidResponse")
    void shouldReturnInvalidResponseWhenInvalidProductOffering(ProductOffering productOffering) {
        // When
        ResponseResult result = optionalProductOfferingValidator.isValid(productOffering);

        // Then
        assertFalse(result.getResult());
        assertEquals(DescriptionConstants.SELECTED_OFFER_NOT_VALID, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid logical atomic product offering, " +
            "when isValid is called, " +
            "then it shouldReturnValidResponse")
    void shouldReturnValidResponseWhenValidLogicalAtomicProductOffering() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE, false);

        // When
        ResponseResult result = optionalProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.LOGICAL_SELECTED_OFFER, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid logical bundle product offering, " +
            "when isValid is called, " +
            "then it shouldReturnValidResponse")
    void shouldReturnValidResponseWhenValidLogicalBundledProductOffering() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE, false);

        // When
        ResponseResult result = optionalProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.LOGICAL_SELECTED_OFFER, result.getDescription());
    }

    private ProductOffering createProductOffering(String type, boolean isSellable) {
        return ProductOffering.builder()
                .type(type)
                .lifecycleStatus(ServiceConstants.LAUNCHED)
                .isSellable(isSellable)
                .build();
    }

    private Stream<Arguments> invalidProductOfferingProvider() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(ProductOffering.builder().build()),
                Arguments.of(ProductOffering.builder()
                        .type(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE)
                        .lifecycleStatus(ServiceConstants.LAUNCHED)
                        .build())
        );
    }
}
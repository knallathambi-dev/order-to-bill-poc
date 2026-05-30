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
class AcquisitionProductOfferingValidatorTest {

    private static final String INVALID_TYPE = "InvalidProductOffering";
    private static final String INVALID_LIFECYCLE = "invalid lifecycle";
    private final AcquisitionProductOfferingValidator acquisitionProductOfferingValidator = new AcquisitionProductOfferingValidator();

    @Test
    @DisplayName("Given a valid contract product offering in launched state, " +
            "when isValid is called, " +
            "then it should return a valid response")
    void shouldReturnValidResponseWhenValidContractProductOfferingLaunched() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE, ServiceConstants.LAUNCHED, true);

        // When
        ResponseResult result = acquisitionProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.CONTRACT_SELECTED_OFFER, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid contract product offering in active state, " +
            "when isValid is called, " +
            "then it should return a valid response")
    void shouldReturnValidResponseWhenValidContractProductOfferingActive() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE, ServiceConstants.ACTIVE, true);

        // When
        ResponseResult result = acquisitionProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.CONTRACT_SELECTED_OFFER, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid atomic accessory product offering, " +
            "when isValid is called, " +
            "then it shouldReturnValidResponse")
    void shouldReturnValidResponseWhenValidAtomicAccessoryProductOffering() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE, ServiceConstants.LAUNCHED, true);

        // When
        ResponseResult result = acquisitionProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.ACCESSORY_SELECTED_OFFER, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid bundle accessory product offering, " +
            "when isValid is called, " +
            "then it shouldReturnValidResponse")
    void shouldReturnValidResponseWhenValidBundleAccessoryProductOffering() {
        // Given
        ProductOffering productOffering = createProductOffering(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE, ServiceConstants.LAUNCHED, true);

        // When
        ResponseResult result = acquisitionProductOfferingValidator.isValid(productOffering);

        // Then
        assertTrue(result.getResult());
        assertEquals(DescriptionConstants.ACCESSORY_SELECTED_OFFER, result.getDescription());
    }

    @ParameterizedTest
    @MethodSource("invalidAccessoryProductOfferingProvider")
    @DisplayName("Given an invalid accessory product offering, " +
            "when isValid is called, " +
            "then it shouldReturnInvalidResponse")
    void shouldReturnInvalidResponseWhenInvalidAccessoryProductOffering(ProductOffering productOffering) {
        // Given & When
        ResponseResult result = acquisitionProductOfferingValidator.isValid(productOffering);

        // Then
        assertFalse(result.getResult());
        assertEquals(DescriptionConstants.SELECTED_OFFER_NOT_VALID, result.getDescription());
    }

    private ProductOffering createProductOffering(String type, String lifecycleStatus, boolean isSellable) {
        return ProductOffering.builder()
                .type(type)
                .lifecycleStatus(lifecycleStatus)
                .isSellable(isSellable)
                .build();
    }

    private Stream<Arguments> invalidAccessoryProductOfferingProvider() {
        return Stream.of(
                Arguments.of(createProductOffering(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE, ServiceConstants.LAUNCHED, false)),
                Arguments.of(createProductOffering(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE, ServiceConstants.LAUNCHED, false)),
                Arguments.of(createProductOffering(null, ServiceConstants.LAUNCHED, true)),
                Arguments.of(createProductOffering(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE, null, true)),
                Arguments.of(createProductOffering(null, null, true)),
                Arguments.of(createProductOffering(INVALID_TYPE, ServiceConstants.LAUNCHED, true)),
                Arguments.of(createProductOffering(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE, INVALID_LIFECYCLE, true)),
                Arguments.of(createProductOffering(INVALID_TYPE, INVALID_LIFECYCLE, true))
        );
    }
}
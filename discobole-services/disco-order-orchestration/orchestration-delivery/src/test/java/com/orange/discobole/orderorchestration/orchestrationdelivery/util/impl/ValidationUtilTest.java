// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.Mockito.when;

public class ValidationUtilTest {

    public static final String PRODUCT_ORDER_DTO_JSON_FILE = "/ProductOrderDTO.json";

    public static final String INVALID_PRODUCT_ORDER_DTO_JSON_FILE = "/invalidProductOrder.json";

    @Test
    void testGetViolationsWithInvalidObject() {

        ProductOrder validProductOrder = JsonUtil.readObjectFromResource(INVALID_PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });

        Set<ConstraintViolation<ProductOrder>> violations = ValidationUtil.getViolations(validProductOrder);

        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void testGetViolationsWithValidObject() {

        ProductOrder validProductOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });

        Set<ConstraintViolation<ProductOrder>> violations = ValidationUtil.getViolations(validProductOrder);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void testIsValidWithValidObject() {

        ProductOrder validProductOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });

        boolean isValid = ValidationUtil.isValid(validProductOrder);

        Assertions.assertTrue(isValid);
    }

    @Test
    void testIsValidWithInvalidObject() {
        ProductOrder invalidProductOrder = JsonUtil.readObjectFromResource(INVALID_PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });
        try (MockedStatic<ValidationUtil> validationUtilMock = Mockito.mockStatic(ValidationUtil.class)) {
            Set<ConstraintViolation<?>> mockViolations = createMockViolations();
            validationUtilMock.when(() -> ValidationUtil.getViolations(invalidProductOrder)).thenReturn(mockViolations);
            boolean isValid = ValidationUtil.isValid(invalidProductOrder);
            // Rest of your test code
            Assertions.assertFalse(isValid);
            mockViolations.forEach(Mockito::reset);
        }
    }

    private <T> Set<ConstraintViolation<?>> createMockViolations() {
        return Set.of(getSetConstraintViolation("ralated party violation"), getSetConstraintViolation("product order item violations"),
                getSetConstraintViolation("product specification violation"));
    }

    @NotNull
    private static ConstraintViolation<Set<RelatedParty>> getSetConstraintViolation(String message) {
        ConstraintViolation<Set<RelatedParty>> violationRelatedParty = Mockito.mock(ConstraintViolation.class);
        when(violationRelatedParty.getMessage()).thenReturn(message);
        return violationRelatedParty;
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.product_management.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl.ProductManagementServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ProductManagementServiceImplTest {

    @SuppressWarnings("PMD.UnusedPrivateField")
    private static final String PRODUCT_MANAGEMENT_URL = "http://localhost:8082/api/productManagement/v4/product";

    private static final String NOT_FOUND_PRODUCT_ORDER_URL = "notFoundURL";

    @Mock
    DiscoServiceUrl discoServiceUrl;

    @Mock
    Logger log;

    @InjectMocks
    ProductManagementServiceImpl productManagementServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenNotFoundCpibUrlWhenRetrieveProductThenThrowDiscoException() {
        //given
        when(discoServiceUrl.getProductManagementUrl()).thenReturn(NOT_FOUND_PRODUCT_ORDER_URL);
        //when

        OrchestrationPlanNode nodeInput = JsonUtil.readObjectFromResource("/orchestrationNode.json", new TypeReference<>() {
        });
        List<String> itemIds = List.of(nodeInput.getRelatedProductOrderItem().get(0).getId());
        //then
        assertThrows(IllegalArgumentException.class, () ->
            productManagementServiceImpl.getProductsByOrderIdAndItemIds(itemIds, "443")

        );
    }

    @Test
    void givenEmptyProductIdWhenGettingProductInCpibThenThrowInvalidParameterException() {
        List<String> itemIds = List.of("");
        String orderId = "";

        assertThrows(ProductOrderValidationException.class, () ->
                productManagementServiceImpl.getProductsByOrderIdAndItemIds(itemIds, orderId)
        );
    }
}


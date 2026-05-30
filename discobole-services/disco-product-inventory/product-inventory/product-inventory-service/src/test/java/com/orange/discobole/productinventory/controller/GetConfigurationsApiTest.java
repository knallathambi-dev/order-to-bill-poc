// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.dto.v1.Config;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;

import static com.orange.discobole.productinventory.constant.TestConstant.CONFIGURATIONS_V_1_SERVICE_VERSION_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetConfigurationsApiTest extends AbstractTest {
    @Value("${config.pagination.limit}")
    private int paginationLimit;

    @Value("${config.path.productCatalogUrl}")
    private String productCatalogUrl;

    @Value("${config.path.resourceInventoryManagementUrl}")
    private String resourceInventoryManagementUrl;

    @Value("${config.enable.productCatalog}")
    private boolean enableProductCatalogCheck;

    @Value("${config.enable.resourceInventoryManagement}")
    private boolean enableResourceInventoryManagementCheck;

    @Test
    void givenServiceIsRunning_whenGetServiceVersion_thenSuccess() throws Exception {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, CONFIGURATIONS_V_1_SERVICE_VERSION_URI);
        resultActions.andExpect(status().isOk());
        Config config = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(paginationLimit, config.getPaginationLimit());
        assertEquals(productCatalogUrl, config.getProductCatalogUrl());
        assertEquals(resourceInventoryManagementUrl, config.getResourceInventoryManagementUrl());
        assertEquals(enableProductCatalogCheck, config.getProductCatalogEnabled());
        assertEquals(enableResourceInventoryManagementCheck, config.getResourceInventoryManagementEnabled());
    }


}
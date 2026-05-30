// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.config.ApplicationConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = {
        DiscoServiceUrlImpl.class,
        ApplicationConfigProperties.class
})
class DiscoServiceUrlImplTest {

    @Autowired
    private DiscoServiceUrlImpl discoServiceUrl;

    @MockBean
    private ApplicationConfigProperties applicationConfigProperties;

    private static final String CATALOG_MANAGEMENT_PRODUCT_SPECIFICATION_URL = "/productCatalogManagement/v1/productSpecification";

    private static final String SERVICE_ORDERING_SERVICE_ORDER_URL = "/serviceOrdering/v1/serviceOrder";

    @Test
    void testGetProductSpecByIdUrl() {
        Mockito.when(applicationConfigProperties.getProductSpecByIdCatalogUrl()).thenReturn(CATALOG_MANAGEMENT_PRODUCT_SPECIFICATION_URL);

        String productSpecId = "12345";
        String actualUrl = discoServiceUrl.getProductSpecByIdUrl(productSpecId);

        String expectedUrl = CATALOG_MANAGEMENT_PRODUCT_SPECIFICATION_URL + "/" + productSpecId;
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testGetServiceOrderingUrl() {
        Mockito.when(applicationConfigProperties.getServiceOrderingUrl()).thenReturn(SERVICE_ORDERING_SERVICE_ORDER_URL);

        String actualUrl = discoServiceUrl.getServiceOrderingUrl();

        assertEquals(SERVICE_ORDERING_SERVICE_ORDER_URL, actualUrl);
    }
}

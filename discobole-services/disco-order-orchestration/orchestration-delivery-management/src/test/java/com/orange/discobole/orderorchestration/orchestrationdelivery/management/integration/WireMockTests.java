// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.integration;


import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.debezium.EnableDebeziumIntegration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
@Slf4j
class WireMockTests extends BaseAbstractionIntegrationTest {
    private static final String SIM_CARD_PRODUCT_SPECIFICATION = "SimCardProductSpecification.json";

    private void setupWiremock() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        wireMockServer.stubFor(get("/productCatalogManagement/v1/productSpecification/df32402e-ceb9-4467-aafd-fec0bbff3124")
                .willReturn(ok().withBodyFile(SIM_CARD_PRODUCT_SPECIFICATION).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

    }

    @Test
    void testMockedRequest() {
        setupWiremock();
        String url = wireMockServer.baseUrl() + "/productCatalogManagement/v1/productSpecification/df32402e-ceb9-4467-aafd-fec0bbff3124";
        ResponseEntity<Object> result = WebClient.create()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(Object.class)
                .block();
        assert result != null;
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}

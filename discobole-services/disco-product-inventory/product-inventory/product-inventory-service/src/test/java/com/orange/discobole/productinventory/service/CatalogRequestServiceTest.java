// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.CatalogEntityRef;
import com.orange.discobole.productinventory.service.impl.CatalogRequestServiceImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

class CatalogRequestServiceTest extends AbstractTest {

    @Autowired
    private CatalogRequestServiceImpl catalogRequestService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void listProductSpecifications_shouldCacheResults() throws IOException {
        Set<String> ids = Set.of(VALID_PRODUCT_SPECIFICATION_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCTS_SPECIFICATION_IDS, PRODUCTS_SPECIFICATION_JSON, HttpStatus.OK.value());
        Mono<ResponseEntity<CatalogEntityRef[]>> responseMono = catalogRequestService.listProductSpecifications(ids);
        ResponseEntity<CatalogEntityRef[]> response = responseMono.block();
        assertThat(response).isNotNull().extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(Objects.requireNonNull(response.getBody())[0].getId()).isEqualTo(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);
        assertThat(response.getBody()[1].getId()).isEqualTo(VALID_PRODUCT_SPECIFICATION_ID);
        String cacheKey = "productSpecifications:" + String.join(",", ids);
        Object cachedValue =  Objects.requireNonNull(Objects.requireNonNull(cacheManager.getCache("catalogCache")).get(cacheKey)).get();
        assertThat(cachedValue).isNotNull();
    }

    @Test
    void listProductOffering_shouldCacheResults() throws IOException {
        Set<String> ids = Set.of(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_OFFERING_CONTRACT_ID);
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_IDS, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Mono<ResponseEntity<CatalogEntityRef[]>> responseMono = catalogRequestService.listProductOfferings(ids);
        ResponseEntity<CatalogEntityRef[]> response = responseMono.block();
        assertThat(response).isNotNull().extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
        String cacheKey = "productOfferings:" + String.join(",", ids);
        Object cachedValue =  Objects.requireNonNull(Objects.requireNonNull(cacheManager.getCache("catalogCache")).get(cacheKey)).get();
        assertThat(cachedValue).isNotNull();
    }

    @Test
    void listProductOfferingPrice_shouldCacheResults() throws IOException {
        Set<String> ids = Set.of(VALID_PRODUCT_OFFERING_PRICE_ID);
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        Mono<ResponseEntity<CatalogEntityRef[]>> responseMono = catalogRequestService.listProductOfferingPrices(ids);
        ResponseEntity<CatalogEntityRef[]> response = responseMono.block();
        assertThat(response).isNotNull().extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(Objects.requireNonNull(response.getBody())[0].getId()).isEqualTo(VALID_PRODUCT_OFFERING_PRICE_ID);
        String cacheKey = "productOfferingPrices:" + String.join(",", ids);
        Object cachedValue =  Objects.requireNonNull(Objects.requireNonNull(cacheManager.getCache("catalogCache")).get(cacheKey)).get();
        assertThat(cachedValue).isNotNull();
    }


}
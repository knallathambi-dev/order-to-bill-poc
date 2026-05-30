// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.ResourceInventoryResponseDTO;
import com.orange.discobole.productinventory.service.impl.ResourceInventoryRequestServiceImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceInventoryRequestServiceTest extends AbstractTest {

    @Autowired
    private ResourceInventoryRequestServiceImpl resourceInventoryRequestService;


    @Test
    void resourceInventory_shouldCacheResults() throws IOException {
        // Clear the cache before the test to ensure clean state
        Objects.requireNonNull(cacheManager.getCache("resourceInventoryCache")).clear();

        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        Mono<ResponseEntity<ResourceInventoryResponseDTO>> responseMono = resourceInventoryRequestService.getResourceInventoryById(VALID_REALIZING_RESOURCE_ID);
        ResponseEntity<ResourceInventoryResponseDTO> response = responseMono.block();

        assertThat(response).isNotNull().extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isEqualTo(VALID_REALIZING_RESOURCE_ID);

        String cacheKey = "resourceInventoryById:" + String.join(",", VALID_REALIZING_RESOURCE_ID);
        Object cachedValue = Objects.requireNonNull(Objects.requireNonNull(cacheManager.getCache("resourceInventoryCache")).get(cacheKey)).get();
        assertThat(cachedValue).isNotNull();
    }



}
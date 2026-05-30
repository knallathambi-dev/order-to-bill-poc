// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.config.ApplicationConfigProperties;
import com.orange.discobole.productinventory.dto.ResourceInventoryResponseDTO;
import com.orange.discobole.productinventory.service.ResourceInventoryRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ResourceInventoryRequestServiceImpl implements ResourceInventoryRequestService {

    private final WebClient webClient;

    private final ApplicationConfigProperties applicationConfigProperties;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ResourceInventoryRequestServiceImpl(WebClient webClient, ApplicationConfigProperties applicationConfigProperties) {
        this.webClient = webClient;
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    @Cacheable(value = "resourceInventoryCache", key = "'resourceInventoryById:' + #id")
    public Mono<ResponseEntity<ResourceInventoryResponseDTO>> getResourceInventoryById(String id) {
        log.debug("get resource inventory by id: {}", applicationConfigProperties.getResourceInventoryManagementUrl().replaceAll("/$", "") + "/" + id);
        return webClient.get()
                .uri(applicationConfigProperties.getResourceInventoryManagementUrl().replaceAll("/$", "") + "/" + id)
                .retrieve().toEntity(ResourceInventoryResponseDTO.class);
    }
}
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
import com.orange.discobole.productinventory.dto.CatalogEntityRef;
import com.orange.discobole.productinventory.service.CatalogRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class CatalogRequestServiceImpl implements CatalogRequestService {
    private static final String PRODUCT_CATALOG_MANAGEMENT_PATH_PREFIX = "productCatalogManagement";
    private static final String V1 = "v1";
    private static final String PRODUCT_OFFERING_PATH_SUFFIX = "productOffering";
    private static final String PRODUCT_SPECIFICATION_PATH_SUFFIX = "productSpecification";
    private static final String PRODUCT_OFFERING_PRICE_PATH_SUFFIX = "productOfferingPrice";
    private final WebClient webClient;
    private final ApplicationConfigProperties applicationConfigProperties;


    @Override
    @Cacheable(value = "catalogCache", key = "'productOfferings:' + #ids")
    public Mono<ResponseEntity<CatalogEntityRef[]>> listProductOfferings(Set<String> ids) {
        log.debug("listProductOfferings for ids: {}", ids);
        return requestResourceIfIdsNotEmpty(PRODUCT_OFFERING_PATH_SUFFIX, ids);
    }


    private Mono<ResponseEntity<CatalogEntityRef[]>> requestResourceIfIdsNotEmpty(String productOfferingPathSuffix, Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            // Return an empty response if ids are empty
            return Mono.just(ResponseEntity.noContent().build());
        }

        // Proceed with fetching the resource if ids are not empty
        return fetchWithRetry(webClient, getUrlWithParams(productOfferingPathSuffix, ids));
    }

    @Override
    @Cacheable(value = "catalogCache", key = "'productSpecifications:' + #ids")
    public Mono<ResponseEntity<CatalogEntityRef[]>> listProductSpecifications(Set<String> ids) {
        log.debug("listProductSpecifications for ids: {}", ids);
        return requestResourceIfIdsNotEmpty(PRODUCT_SPECIFICATION_PATH_SUFFIX, ids);
    }

    @Override
    @Cacheable(value = "catalogCache", key = "'productOfferingPrices:' + #ids")
    public Mono<ResponseEntity<CatalogEntityRef[]>> listProductOfferingPrices(Set<String> ids) {
        log.debug("listProductOfferingPrices for ids: {}", ids);
        return requestResourceIfIdsNotEmpty(PRODUCT_OFFERING_PRICE_PATH_SUFFIX, ids);
    }

    private String getUrlWithParams(String pathSegment, Set<String> ids) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(applicationConfigProperties.getProductCatalogUrl());
        builder.pathSegment(PRODUCT_CATALOG_MANAGEMENT_PATH_PREFIX);
        builder.pathSegment(V1);
        builder.pathSegment(pathSegment);
        if (ids != null && !ids.isEmpty()) {
            builder.queryParam("id", ids.toArray());
        }
        return builder.toUriString();
    }

    private Mono<ResponseEntity<CatalogEntityRef[]>> fetchWithRetry(WebClient webClient, String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .toEntity(CatalogEntityRef[].class)
                .retryWhen(Retry.backoff(
                                        applicationConfigProperties.getWebClientMaxRetryAttempts(),
                                        Duration.ofMillis(applicationConfigProperties.getWebClientMaxRetryDelay())
                                )
                                .jitter(0.75)
                                .filter(Exception.class::isInstance)
                                .doAfterRetry(retrySignal ->
                                        log.warn("Retrying due to exception", retrySignal.failure())
                                )
                );
    }


}
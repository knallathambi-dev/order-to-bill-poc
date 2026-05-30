// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingService;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.ordercapture.service.util.ProductOfferingResultValidator;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class ProductOfferingServiceImpl implements ProductOfferingService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOfferingServiceImpl(DiscoServiceUrl serviceUrl, WebClient webClient) {
        this.discoServiceUrl = serviceUrl;
        this.webClient = webClient;
    }

    @Override
    public ResponseResult isProductOfferingExist(String productOfferingId, ProductOfferingResultValidator validator) {
        log.info("Checking product offering existence with id: {}", productOfferingId);
        if (isBlank(productOfferingId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_OFFERING_ID);
        }
        return checkProductOfferingInCatalog(productOfferingId, validator);
    }

    @Override
    public ProductOffering getProductOfferingById(String productOfferingId) {
        log.info("Checking product offering existence with id: {}", productOfferingId);
        if (isBlank(productOfferingId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_OFFERING_ID);
        }
        return checkProductOfferingInCatalog(productOfferingId);
    }

    private ResponseResult checkProductOfferingInCatalog(String productOfferingId, ProductOfferingResultValidator validator) {
        String productOfferingUri = discoServiceUrl.getProductOfferingByIdUrl(productOfferingId);
        return webClient.get()
                .uri(productOfferingUri)
                .retrieve()
                .toEntity(ProductOffering.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(validator.isValid(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(DescriptionConstants.SELECTED_OFFER_NOT_VALID));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.CATALOG_SERVICE_UNREACHABLE));
                })
                .block();
    }

    private ProductOffering checkProductOfferingInCatalog(String productOfferingId) {
        String productOfferingUri = discoServiceUrl.getProductOfferingByIdUrl(productOfferingId);
        return webClient.get()
                .uri(productOfferingUri)
                .retrieve()
                .toEntity(ProductOffering.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(DescriptionConstants.SELECTED_OFFER_NOT_VALID));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.CATALOG_SERVICE_UNREACHABLE));
                })
                .block();
    }
}
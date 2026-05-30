// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.OrderInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;

@Component
@Slf4j
public class OrderInventoryServiceImpl implements OrderInventoryService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderInventoryServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public ProductOrder createProductOrder(ProductOrder productOrderDTO) {
        log.debug("Request to save product order : {}", productOrderDTO);
        Assert.notNull(productOrderDTO, ExceptionMessage.PRODUCT_ORDER_MAY_NOT_BE_NULL);
        String orderInventoryServiceUrl = discoServiceUrl.getOrderInventoryUrl();
        return webClient.post()
                .uri(orderInventoryServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productOrderDTO)
                .retrieve()
                .toEntity(ProductOrder.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return validateAndReturnResult(responseEntity.getBody());
                }).onErrorResume(
                        error -> Mono.error(new DiscoException(DescriptionConstants.INTERNAL_SERVER_ERROR)))
                .block();
    }

    @Override
    public Optional<ProductOrder> getProductOrderById(String id) {
        log.debug("Request to get product order : {}", id);
        if (StringUtils.isBlank(id)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_ORDER_ID);
        }
        String orderInventoryServiceUrl = discoServiceUrl.getProductOrderInventoryByIdUrl(id);
        return webClient.get()
                .uri(orderInventoryServiceUrl)
                .retrieve()
                .toEntity(ProductOrder.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return validateAndReturnResult(responseEntity.getBody());
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(DescriptionConstants.SELECTED_PRODUCT_ORDER_DOES_NOT_EXIST));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.INTERNAL_SERVER_ERROR));
                })
                .blockOptional();
    }

    private Mono<ProductOrder> validateAndReturnResult(ProductOrder result) {
        if (isValidProductResult(result)) {
            return Mono.just(result);
        } else {
            return Mono.error(new DiscoException(DescriptionConstants.INTERNAL_SERVER_ERROR));
        }
    }

    private boolean isValidProductResult(ProductOrder productOrderDTO) {
        return Objects.nonNull(productOrderDTO) && StringUtils.isNotBlank(productOrderDTO.getId());
    }
}
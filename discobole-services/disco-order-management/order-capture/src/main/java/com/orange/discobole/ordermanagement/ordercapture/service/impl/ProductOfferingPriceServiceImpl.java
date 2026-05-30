// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.ProductOfferingPrice;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingPriceService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;

@Component
@Slf4j
public class ProductOfferingPriceServiceImpl implements ProductOfferingPriceService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOfferingPriceServiceImpl(DiscoServiceUrl serviceUrl, WebClient webClient) {
        this.discoServiceUrl = serviceUrl;
        this.webClient = webClient;
    }

    @Override
    public List<ProductOfferingPrice> fetchProductOfferingPrices(List<String> productOfferingPricesIdList) {
        if (CollectionUtils.isEmpty(productOfferingPricesIdList)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_OFFERING_PRICE_PARAMETERS);
        }

        String productOfferingPriceUrl = discoServiceUrl.getProductOfferingPriceUrl();

        return webClient.get()
                .uri(productOfferingPriceUrl, uri -> uri
                        .queryParam("id", String.join(",", productOfferingPricesIdList))
                        .build())
                .retrieve()
                .toEntityList(ProductOfferingPrice.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    List<ProductOfferingPrice> productOfferingPrices = response.getBody();
                    if (!CollectionUtils.isEmpty(productOfferingPrices)) {
                        return Mono.just(productOfferingPrices);
                    }
                    return Mono.error(new DiscoException(ExceptionMessage.PRODUCT_OFFERING_PRICES_NOT_FOUND));
                })
                .onErrorResume(error -> {
                    if (error instanceof DiscoException discoException) {
                        return Mono.error(discoException);
                    } else {
                        return Mono.error(new DiscoException(DescriptionConstants.PRODUCT_OFFERING_PRICE_SERVICE_UNREACHABLE));
                    }
                })
                .block();
    }
}
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductSpecificationService;
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
public class ProductSpecificationServiceImpl implements ProductSpecificationService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductSpecificationServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public List<ProductSpecification> fetchProductSpecifications(List<String> productSpecificationIdList) {
        if (CollectionUtils.isEmpty(productSpecificationIdList)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_SPECIFICATION_PARAMETERS);
        }
        String productSpecificationUrl = discoServiceUrl.getProductSpecificationUrl();
        return webClient.get()
                .uri(productSpecificationUrl, uri -> uri
                        .queryParam("id", String.join(",", productSpecificationIdList))
                        .build())
                .retrieve()
                .toEntityList(ProductSpecification.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    List<ProductSpecification> productSpecifications = response.getBody();
                    if (!CollectionUtils.isEmpty(productSpecifications)) {
                        return Mono.just(productSpecifications);
                    }
                    return Mono.error(new DiscoException(ExceptionMessage.PRODUCT_SPECIFICATION_CANNOT_BE_FOUND));

                })
                .onErrorResume(error -> {
                    if (error instanceof DiscoException discoException) {
                        return Mono.error(discoException);
                    } else {
                        return Mono.error(new DiscoException(DescriptionConstants.PRODUCT_SPECIFICATION_SERVICE_UNREACHABLE));
                    }
                })
                .block();
    }
}
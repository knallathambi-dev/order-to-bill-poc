// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.ProductOfferingQualification;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.ProductOfferingQualificationItem;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.TaskStateType;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingQualificationService;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.ordercapture.util.ConstraintValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;

@Component
@Slf4j
public class ProductOfferingQualificationServiceImpl implements ProductOfferingQualificationService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOfferingQualificationServiceImpl(DiscoServiceUrl serviceUrl, WebClient webClient) {
        this.discoServiceUrl = serviceUrl;
        this.webClient = webClient;
    }

    @Override
    public ResponseResult isProductOfferingQualified(ProductOfferingQualification productOfferingQualification) {
        log.info("Start checking product offering qualification");
        if (!ConstraintValidator.isValid(productOfferingQualification)) {
            return ResponseResult.builder()
                    .result(false)
                    .description(DescriptionConstants.CONFIGURED_OFFER_UNQUALIFIED)
                    .build();
        }
        return checkProductOfferingQualification(productOfferingQualification);
    }

    private ResponseResult checkProductOfferingQualification(ProductOfferingQualification productOfferingQualification) {
        setExpectedActivationDate(productOfferingQualification);
        String productOfferingUri = discoServiceUrl.getProductOfferingQualificationUrl();
        return webClient.post()
                .uri(productOfferingUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productOfferingQualification)
                .retrieve()
                .toEntity(ProductOfferingQualification.class)
                .flatMap(productOfferingQualificationEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, productOfferingQualificationEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, productOfferingQualificationEntity.getBody());
                    ProductOfferingQualification productOfferingQualificationResponse = productOfferingQualificationEntity.getBody();
                    return Mono.just(isQualified(productOfferingQualificationResponse));
                })
                .onErrorResume(error -> Mono.just(ResponseResult.builder()
                        .result(false)
                        .description(DescriptionConstants.QUALIFICATION_SERVICE_UNREACHABLE)
                        .build())
                )
                .block();
    }

    private void setExpectedActivationDate(ProductOfferingQualification productOfferingQualification) {
        if (Objects.nonNull(productOfferingQualification) && !CollectionUtils.isEmpty(productOfferingQualification.getProductOfferingQualificationItems())) {

            OffsetDateTime currentDateTime = OffsetDateTime.now();
            List<ProductOfferingQualificationItem> missingDateItems = productOfferingQualification.getProductOfferingQualificationItems().stream()
                    .filter(item -> Objects.isNull(item.getExpectedActivationDate())).toList();

            List<ProductOfferingQualificationItem> providedDateItems = productOfferingQualification.getProductOfferingQualificationItems().stream()
                    .filter(item -> Objects.nonNull(item.getExpectedActivationDate())).toList();

            missingDateItems.forEach(item -> item.setExpectedActivationDate(currentDateTime));

            List<ProductOfferingQualificationItem> allItems = new ArrayList<>();
            allItems.addAll(missingDateItems);
            allItems.addAll(providedDateItems);
            productOfferingQualification.setProductOfferingQualificationItems(allItems);
        }
    }

    private ResponseResult isQualified(ProductOfferingQualification productOfferingQualification) {
        if (productOfferingQualification == null || productOfferingQualification.getState() == null) {
            return ResponseResult.builder()
                    .result(false)
                    .description(DescriptionConstants.CONFIGURED_OFFER_UNQUALIFIED)
                    .build();
        }

        String qualificationResult = productOfferingQualification.getQualificationResult();
        TaskStateType state = productOfferingQualification.getState();

        if (state == TaskStateType.DONE && qualificationResult != null) {
            if (qualificationResult.equalsIgnoreCase(ServiceConstants.QUALIFIED_STATUS)) {
                return ResponseResult.builder()
                        .result(true)
                        .build();
            } else {
                return ResponseResult.builder()
                        .result(false)
                        .description(DescriptionConstants.SELECTED_OFFER_UNQUALIFIED)
                        .build();
            }
        } else if (state == TaskStateType.TERMINATEDWITHERROR) {
            return ResponseResult.builder()
                    .result(false)
                    .description(DescriptionConstants.QUALIFICATION_NOT_FULFILLED)
                    .build();
        } else {
            return ResponseResult.builder()
                    .result(false)
                    .description(DescriptionConstants.QUALIFICATION_NOT_FULFILLED)
                    .build();
        }
    }
}
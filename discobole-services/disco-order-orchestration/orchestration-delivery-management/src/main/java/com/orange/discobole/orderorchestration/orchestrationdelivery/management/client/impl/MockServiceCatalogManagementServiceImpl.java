// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.impl;


import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.httpfailed.ServiceCatalogHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceCatalogManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class MockServiceCatalogManagementServiceImpl implements ServiceCatalogManagementService {
    private final WebClientUtil webClientUtil;

    @Override
    public List<ServiceSpecification> getServiceSpecificationByIds(List<String> productsIds, String serviceCatalogManagementUrl) {
        if (CollectionUtils.isEmpty(productsIds)) {
            return Collections.emptyList();
        }

        String serviceSpecUrl = UriComponentsBuilder.fromHttpUrl(serviceCatalogManagementUrl).queryParam("id", productsIds).buildAndExpand(productsIds).toUriString();
        return webClientUtil.send(HttpMethod.GET, serviceSpecUrl, new ParameterizedTypeReference<>() {
        }, (retryBackoffSpec, retrySignal) -> {
            WebClientResponseException webClientResponseException = (WebClientResponseException) retrySignal.failure();
            log.error("ServiceCatalogManagementServiceImpl | getServiceSpecificationByIds error while fetch service specifications", webClientResponseException);
            throw CoodRecoverableAndNonRetryableException
                    .of(new ServiceCatalogHttpFailedException(webClientResponseException.getMessage(), serviceSpecUrl, webClientResponseException.getStatusCode(), webClientResponseException));
        }, response -> {
            //handle 204 response
            log.info("ServiceCatalogManagementServiceImpl | getServiceSpecificationByIds | retrieved service specifications {} successfully", productsIds);
            log.debug("ServiceCatalogManagementServiceImpl | getServiceSpecificationByIds | retrieved service specifications {} with response {}", productsIds, response);
            if (CollectionUtils.isEmpty(response)) {
                throw CoodRecoverableAndNonRetryableException
                        .of(new ServiceCatalogHttpFailedException(response.toString(), serviceSpecUrl, HttpStatus.NO_CONTENT, ExceptionCode.HTTP_NO_CONTENT));
            }
        }, new ServiceCatalogHttpFailedException()); //to do use service catalog http call and verify this param usage
    }
}

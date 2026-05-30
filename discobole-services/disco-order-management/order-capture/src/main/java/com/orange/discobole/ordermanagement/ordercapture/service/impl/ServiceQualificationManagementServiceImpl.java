// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.service.qualification.CheckServiceQualification;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.ServiceQualificationManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;

@Component
@Slf4j
public class ServiceQualificationManagementServiceImpl implements ServiceQualificationManagementService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ServiceQualificationManagementServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public CheckServiceQualification checkServiceQualification(CheckServiceQualification checkServiceQualification) {
        log.debug("Request to create new service qualification  : {}", checkServiceQualification);
        if (Objects.isNull(checkServiceQualification)) {
            throw new InvalidParameterException(ExceptionMessage.SERVICE_QUALIFICATION_IS_NULL);
        }

        String serviceQualificationManagementServiceUrl = discoServiceUrl.getServiceQualificationManagementUrl();
        return webClient.post()
                .uri(serviceQualificationManagementServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(checkServiceQualification)
                .retrieve()
                .toEntity(CheckServiceQualification.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());

                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                }).onErrorResume(
                        error -> Mono.error(new DiscoException(DescriptionConstants.SERVICE_QUALIFICATION_MANAGEMENT_SERVICE_UNREACHABLE))
                ).block();
    }
}
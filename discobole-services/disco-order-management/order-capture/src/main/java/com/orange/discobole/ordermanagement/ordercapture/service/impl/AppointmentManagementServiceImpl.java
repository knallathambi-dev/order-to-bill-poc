// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.appointment.Appointment;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.AppointmentManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
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

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class AppointmentManagementServiceImpl implements AppointmentManagementService {

    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AppointmentManagementServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public ResponseResult fetchAppointmentById(String id) {
        if (isBlank(id)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_APPOINTMENT_ID);
        }
        String appointmentUri = discoServiceUrl.getAppointmentManagementByIdUrl(id);
        return webClient.get()
                .uri(appointmentUri)
                .retrieve()
                .toEntity(Appointment.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());

                    return Mono.just(ResponseResult.builder()
                            .result(true)
                            .build());
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(DescriptionConstants.INVALID_ID_FOR_APPOINTMENT));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.APPOINTMENT_SERVICE_UNREACHABLE));
                })
                .block();
    }
}
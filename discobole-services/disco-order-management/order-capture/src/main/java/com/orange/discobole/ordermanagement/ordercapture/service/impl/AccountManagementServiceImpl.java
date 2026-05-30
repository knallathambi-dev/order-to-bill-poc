// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.account.BillingAccount;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.AccountManagementService;
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

import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class AccountManagementServiceImpl implements AccountManagementService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccountManagementServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public ResponseResult checkBillingAccount(String billingAccountId) {
        if (isBlank(billingAccountId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_BILLING_ACCOUNT_ID);
        }

        String accountServiceUrl = discoServiceUrl.getAccountManagementByIdUrl(billingAccountId);
        return webClient.get()
                .uri(accountServiceUrl)
                .retrieve()
                .toEntity(BillingAccount.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());

                    return Mono.just(ResponseResult.builder().result(true).build());
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(format(DescriptionConstants.INVALID_BILLING_ACCOUNT_REFERENCE, billingAccountId)));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.ACCOUNT_SERVICE_UNREACHABLE));
                })
                .block();
    }

    @Override
    public BillingAccount fetchBillingAccountByRelatedPartyId(String relatedPartyId) {
        if (isBlank(relatedPartyId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_RELATED_PARTY_ID);
        }

        String accountServiceUrl = discoServiceUrl.getAccountManagement();
        return webClient.get()
                .uri(accountServiceUrl, uri -> uri
                        .queryParam("relatedParty.id", relatedPartyId)
                        .build())
                .retrieve()
                .toEntity(BillingAccount.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(format(DescriptionConstants.INVALID_RELATED_PARTY_ID_FOR_BILLING_ACCOUNT, relatedPartyId)));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.ACCOUNT_SERVICE_UNREACHABLE));
                })
                .block();
    }
}
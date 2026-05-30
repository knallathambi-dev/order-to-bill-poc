// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.role.PartyRole;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.PartyRoleManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;

@Component
@Slf4j
public class PartyRoleManagementServiceImpl implements PartyRoleManagementService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public PartyRoleManagementServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public List<PartyRole> getPartyRoles(String partyRoleId, String partyName) {
        if (StringUtils.isBlank(partyRoleId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PARTY_ROLE_PARAMETERS);
        }
        String partyRoleUrl = discoServiceUrl.getPartyRoleManagementUrl();
        return webClient.get()
                .uri(partyRoleUrl, uri -> uri
                        .queryParam("fields", "id,partyRoleSpecification.name")
                        .queryParam("engagedParty.id", partyRoleId)
                        .queryParam("name", partyName)
                        .build())
                .retrieve()
                .toEntityList(PartyRole.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    List<PartyRole> partyRoles = response.getBody();
                    if (!CollectionUtils.isEmpty(partyRoles)) {
                        return Mono.just(partyRoles);
                    } else {
                        return Mono.error(new DiscoException(ExceptionMessage.PARTY_ROLES_CANNOT_BE_FOUND));
                    }
                })
                .onErrorResume(error -> {
                    if (error instanceof DiscoException discoException) {
                        return Mono.error(discoException);
                    } else {
                        return Mono.error(new DiscoException(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE));
                    }
                })
                .block();
    }

    @Override
    public PartyRole createPartyRole(PartyRole partyRole) {
        log.debug("Request to create new party role : {}", partyRole);
        if (Objects.isNull(partyRole)) {
            throw new InvalidParameterException(ExceptionMessage.PARTY_ROLE_MAY_NOT_BE_NULL);
        }
        String partyRoleManagementServiceUrl = discoServiceUrl.getPartyRoleManagementUrl();
        return webClient.post()
                .uri(partyRoleManagementServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(partyRole)
                .retrieve()
                .toEntity(PartyRole.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                }).onErrorResume(
                        error -> Mono.error(new DiscoException(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE))
                ).block();
    }
}
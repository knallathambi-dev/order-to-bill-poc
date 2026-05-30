// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.productinventory.dto.UserRole;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserRolesRequestService {
    @Qualifier("userWebClient")
    private final WebClient webClient;
    private final SecurityConfigProperties securityConfigProperties;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "WebClient and SecurityConfigProperties are effectively immutable Spring beans managed by the container"
    )
    public UserRolesRequestService(@Qualifier("userWebClient") WebClient webClient, SecurityConfigProperties securityConfigProperties) {
        this.webClient = webClient;
        this.securityConfigProperties = securityConfigProperties;
    }

    @Cacheable(value = "userAuthorizationCache", key = "#token")
    public List<UserRole> fetchUserRoles(String token) {
        String rolesUrl = securityConfigProperties.getUserRoleRetrievalUrl();
        log.debug("Fetching user roles from: {}", rolesUrl);
        List<UserRole> emptyRes = new ArrayList<>(); // for sonar
        return webClient.get()
                .uri(rolesUrl)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntityList(UserRole.class)
                .map(response -> {
                    HttpStatusCode statusCode = response.getStatusCode();
                    log.debug("User roles API response status: {}", statusCode);

                    if (statusCode.isSameCodeAs(HttpStatus.OK)) {
                        List<UserRole> body = response.getBody();
                        if (body != null && !body.isEmpty()) {
                            log.info("Successfully fetched {} user roles", body.size());
                            log.debug("User roles: {}", body);
                            return body;
                        } else {
                            log.warn("User roles API returned empty response");
                            return emptyRes;
                        }
                    }
                    log.warn("User roles API returned non-OK status: {}", statusCode);
                    return emptyRes;
                })
                .doOnError(error -> log.error("Error fetching user roles: {}", error.getMessage(), error))
                .onErrorReturn(emptyRes)
                .block();
    }


}
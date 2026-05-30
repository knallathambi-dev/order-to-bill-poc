// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.user.role.UserRole;
import com.orange.discobole.ordermanagement.ordercapture.config.SecurityConfigProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.USER_ROLES_CACHE;

@Component
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

    @Cacheable(value = USER_ROLES_CACHE, key = "#token")
    public List<UserRole> fetchUserRoles(String token) {
        String rolesUrl = securityConfigProperties.getUserRoleRetrievalUrl();
        log.debug("Fetching user roles from: {}", rolesUrl);
        try {
            return webClient.get()
                    .uri(rolesUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToFlux(UserRole.class)
                    .collectList()
                    .doOnSuccess(roles -> {
                        if (roles.isEmpty()) {
                            log.warn("User roles API returned empty response");
                        } else {
                            log.info("Successfully fetched {} user roles", roles.size());
                            log.debug("User roles: {}", roles);
                        }
                    })
                    .doOnError(error -> log.error("Error fetching user roles: {}", error.getMessage(), error))
                    .onErrorReturn(Collections.emptyList())
                    .block();

        } catch (Exception e) {
            log.error("Unexpected error while fetching user roles", e);
            return Collections.emptyList();
        }

    }

}

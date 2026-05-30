// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.common.web.client.config;

import com.orange.discobole.orderorchestration.common.web.client.config.dto.Entitlement;
import com.orange.discobole.orderorchestration.common.web.client.config.dto.UserRole;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final String userRoleRetrievalUrl;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public KeycloakGrantedAuthoritiesConverter(String userRoleRetrievalUrl) {
        this.userRoleRetrievalUrl = userRoleRetrievalUrl;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> resourceAccess = source.getClaimAsMap("resource_access");
        List<String> clientRole = new ArrayList<>();
        // Extract roles from resource_access
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Object clientRoles = entry.getValue();

                if (clientRoles instanceof Map) {
                    // Assuming roles is a List<String> within the clientRoles Map
                    List<String> roles = ((Map<?, ?>) clientRoles).containsKey("roles")
                            ? (List<String>) ((Map<?, ?>) clientRoles).get("roles") : List.of();
                    clientRole.addAll(roles);
                }

            }
        }
        List<String> entitlements = getEntitlements(clientRole, source.getTokenValue());

        return entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    }

    private List<String> getEntitlements(List<String> roles, String jwtToken) {
        List<UserRole> userRoles = fetchUserRoles(roles, jwtToken);
        List<String> entitlements = new ArrayList<>();
        if (userRoles != null) {
            for (UserRole role : userRoles) {
                for (Entitlement entitlement : role.getEntitlement()) {
                    entitlements.add(entitlement.getId());
                }
            }
        }
        return entitlements;
    }

    public List<UserRole> fetchUserRoles(List<String> clientRoles, String jwtToken) {
        return WebClient.create()
                .get()
                .uri(userRoleRetrievalUrl)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserRole>>() {
                })
                .doOnError(t -> t.getCause() instanceof ConnectException, t -> {
                    log.error("Can't fetch user roles {} with error {}", clientRoles, t.toString());
                    Mono.empty();
                }).block();
    }

}

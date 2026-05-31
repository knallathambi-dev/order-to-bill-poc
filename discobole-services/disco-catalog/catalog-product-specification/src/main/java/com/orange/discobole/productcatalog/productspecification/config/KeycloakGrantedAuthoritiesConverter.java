// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.config;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.processflow.handler.RestTemplateResponseErrorHandler;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.Entitlement;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.UserRole;
import com.orange.discobole.productcatalog.productspecification.interceptor.RestTemplateInterceptor;

@Component
public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private RestTemplate restTemplate;

    @Value("${spring.userRoleRetrievalUrl}")
    private String userRoleRetrievalUrl;

    private static final Logger LOGGER = LogManager.getLogger(KeycloakGrantedAuthoritiesConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        String token = source.getTokenValue();
        Map<String, Object> resourceAccess = source.getClaimAsMap("resource_access");
        List<String> entitlements = new ArrayList<>();
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Object clientRoles = entry.getValue();
                if (clientRoles instanceof Map<?, ?> clientRolesMap && clientRolesMap.containsKey("roles")) {
                    Object roles = clientRolesMap.get("roles");
                    if (roles instanceof List<?> roleList) {
                        for (Object role : roleList) {
                            if (role != null) {
                                entitlements.add(role.toString());
                            }
                        }
                    }
                }
            }
        }

        entitlements.addAll(getEntitlements(token));
        if (hasLocalAdminRole(source)) {
            entitlements.add("disco-admin");
        }
        return entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private boolean hasLocalAdminRole(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        if (realmAccess != null) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List<?> roleList) {
                return roleList.stream().anyMatch("OTB_ADMIN"::equals);
            }
        }
        return false;
    }

    private List<String> getEntitlements(String token) {
        List<UserRole> userRoles = fetchUserRoles(token);
        List<String> entitlements = new ArrayList<>();
        if (userRoles != null) {
            for (UserRole role : userRoles) {
                for (Entitlement ent : role.getEntitlement()) {
                    entitlements.add(ent.getId());
                }
            }
        }
        return entitlements;
    }

    public List<UserRole> fetchUserRoles(String token) {
        String marketUrl = UriComponentsBuilder.fromUriString(userRoleRetrievalUrl)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            return getRestTemplate()
                    .exchange(
                            marketUrl,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<List<UserRole>>() {
                            }).getBody();
        } catch (Exception exception) {
            LOGGER.warn("Falling back to local JWT roles because user-role lookup failed: {}", exception.getMessage());
            return Collections.emptyList();
        }
       
    }

    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        }
        return restTemplate;
    }

}

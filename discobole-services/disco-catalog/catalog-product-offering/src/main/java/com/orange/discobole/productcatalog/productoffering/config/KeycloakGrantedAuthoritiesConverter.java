// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.config;

import com.orange.discobole.processflow.handler.RestTemplateResponseErrorHandler;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.Entitlement;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.UserRole;
import com.orange.discobole.productcatalog.productoffering.interceptor.RestTemplateInterceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import java.util.*;

@Component
public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private RestTemplate restTemplate;

    @Value("${spring.userRoleRetrievalUrl}")
    private String userRoleRetrievalUrl;

    private final Map<List<String>, List<UserRole>> rolesCache = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(KeycloakGrantedAuthoritiesConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        String token = source.getTokenValue();
        Map<String, Object> resourceAccess = source.getClaimAsMap("resource_access");
        List<String> clientRole = new ArrayList<>();
        // Extract roles from resource_access
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Object clientRoles = entry.getValue();

                if (clientRoles instanceof Map) {
                    // Assuming roles is a List<String> within the clientRoles Map
                    List<String> roles = ((Map<?, ?>) clientRoles).containsKey("roles")
                            ? (List<String>) ((Map<?, ?>) clientRoles).get("roles")
                            : null;
                    clientRole.addAll(roles);
                }
            }
        }

        List<String> entitlements = getEntitlements(clientRole,token);
        return entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private List<String> getEntitlements(List<String> roles,String token) {

        /*Use an immutable list as a safe map key*/
        List<String> key = List.copyOf(roles);
        List<UserRole> userRoles = rolesCache.computeIfAbsent(key,k ->fetchUserRoles(token));
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

        ResponseEntity<List<UserRole>> response = getRestTemplate().exchange(
                marketUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UserRole>>() {}
        );

        if(response.getStatusCode().is2xxSuccessful()){


            return response.getBody();
        }
        LOGGER.info("User Role Fetched Failed : {}",response.getStatusCode());
        return new ArrayList<>();
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

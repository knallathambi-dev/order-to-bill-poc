// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.config.security.KeycloakGrantedAuthoritiesConverter;
import com.orange.discobole.productinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.productinventory.dto.Entitlement;
import com.orange.discobole.productinventory.dto.UserRole;
import com.orange.discobole.productinventory.service.impl.UserRolesRequestService;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeycloakGrantedAuthoritiesConverterTest extends AbstractTest {

    @Autowired
    private WebClient webClient;

    private UserRolesRequestService userRolesRequestService;

    @Autowired
    private SecurityConfigProperties securityConfigProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private KeycloakGrantedAuthoritiesConverter converter;

    @BeforeEach
    public void beforeEach() {
        securityConfigProperties.setEnableTmf627Validation(true);
        userRolesRequestService = new UserRolesRequestService(webClient, securityConfigProperties);
        converter = new KeycloakGrantedAuthoritiesConverter(userRolesRequestService, securityConfigProperties);
    }

    @Test
    void givenNoRolesInToken_whenExtractRole_thenEmptyList() throws IOException {
        String token = "test-token-no-roles";
        mockUserAuthWithToken(token, List.of());
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "none").claim("sub", "user").build();
        Collection<GrantedAuthority> grantedAuthorityList = converter.convert(jwt);
        assertEquals(0, grantedAuthorityList.size());
    }

    @Test
    void givenDefaultRolesInToken_whenExtractRole_thenSucceed() throws IOException {
        String token = "test-token-default-roles";
        String readRole = "ReadProduct";
        String createRole = "CreateProduct";

        List<UserRole> userRoles = List.of(
                UserRole.builder()
                        .involvementRole(readRole)
                        .entitlement(List.of(Entitlement.builder().id("x500").build()))
                        .build(),
                UserRole.builder()
                        .involvementRole(createRole)
                        .entitlement(List.of(Entitlement.builder().id("x501").build()))
                        .build()
        );

        mockUserAuthWithToken(token, userRoles);
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "none").claim("sub", "user").build();
        Collection<GrantedAuthority> grantedAuthorityList = converter.convert(jwt);
        assertEquals(2, grantedAuthorityList.size());

        List<String> entitlementIdsExtracted = grantedAuthorityList.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        assertTrue(entitlementIdsExtracted.containsAll(List.of("x500", "x501")));
    }

    @Test
    void givenDefaultAndNonDefaultRolesInToken_whenExtractRole_thenSucceed() throws IOException {
        String token = "test-token-mixed-roles";
        String readRole = "ReadProduct";
        String createRole = "CreateProduct";
        String testRole = "Test";

        List<UserRole> userRoles = List.of(
                UserRole.builder()
                        .involvementRole(readRole)
                        .entitlement(List.of(Entitlement.builder().id("x500").build()))
                        .build(),
                UserRole.builder()
                        .involvementRole(createRole)
                        .entitlement(List.of(Entitlement.builder().id("x501").build()))
                        .build(),
                UserRole.builder()
                        .involvementRole(testRole)
                        .entitlement(List.of(Entitlement.builder().id("x504").build()))
                        .build()
        );

        mockUserAuthWithToken(token, userRoles);
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "none").claim("sub", "user").build();
        Collection<GrantedAuthority> grantedAuthorityList = converter.convert(jwt);
        assertEquals(3, grantedAuthorityList.size());

        List<String> entitlementIdsExtracted = grantedAuthorityList.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        assertTrue(entitlementIdsExtracted.containsAll(List.of("x500", "x501", "x504")));
    }

    @Test
    void givenTmf627ValidationDisabled_whenExtractRole_thenEmptyList() {
        String token = "test-token-disabled";
        securityConfigProperties.setEnableTmf627Validation(false);
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "none").claim("sub", "user").build();
        Collection<GrantedAuthority> grantedAuthorityList = converter.convert(jwt);
        assertEquals(0, grantedAuthorityList.size());
    }

    private void mockUserAuthWithToken(String token, List<UserRole> userRoles) throws IOException {
        wireMock.stubFor(
                get(urlPathEqualTo("/userRolePermission/v1/userRoles"))
                        .withHeader("Authorization", equalTo("Bearer " + token))
                        .willReturn(aResponse().withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                                .withBody(objectMapper.writeValueAsString(userRoles))
                        )
        );
    }
}
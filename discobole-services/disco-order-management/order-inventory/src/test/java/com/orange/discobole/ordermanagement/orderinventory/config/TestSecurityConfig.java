// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import com.orange.discobole.ordermanagement.orderinventory.service.SecurityService;
import com.orange.discobole.ordermanagement.orderinventory.service.impl.UserRolesRequestService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
@EnableWebSecurity
@EnableMethodSecurity()
@Order(0)
public class TestSecurityConfig {

    @Bean
    @Primary
    @Profile("test")
    @Order(0)
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests in tests
                )
                .build();
    }

    @Bean
    @Primary
    @Profile("test")
    public SecurityService securityService() {
        SecurityService mockSecurityService = mock(SecurityService.class);

        // Allow all role checks by default
        when(mockSecurityService.hasRoleEntitlement(anyString())).thenReturn(true);

        return mockSecurityService;
    }

    @Bean
    @Primary
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    @Bean
    @Primary
    public UserRolesRequestService userRolesRequestService() {
        return mock(UserRolesRequestService.class);
    }
}
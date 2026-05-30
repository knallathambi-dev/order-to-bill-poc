// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config.security;

import com.orange.discobole.ordermanagement.orderinventory.config.KeycloakGrantedAuthoritiesConverter;
import com.orange.discobole.ordermanagement.orderinventory.service.impl.UserRolesRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Order(1)
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private static final String[] IGNORED_PATH_URI = {
            "/",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/api-docs/**",
            "/async-api/**",
            "/swagger-ui/**",
            "/monitoring/**",
            "/status",
            "/doc",
            "/actuator/**",
            "/v3/api-docs/**",
            "/version"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {

        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(IGNORED_PATH_URI).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(UserRolesRequestService userRolesRequestService, SecurityConfigProperties securityConfigProperties) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter(userRolesRequestService, securityConfigProperties));
        return jwtAuthenticationConverter;
    }
}
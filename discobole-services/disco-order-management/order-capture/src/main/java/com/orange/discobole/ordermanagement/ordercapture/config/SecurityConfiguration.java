// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;

import com.orange.discobole.ordermanagement.ordercapture.service.impl.UserRolesRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Order(1)
@Profile({"!test"})
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class SecurityConfiguration {
    private static final String[] IGNORED_PATH_URI = {
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/monitoring/**",
            "/actuator/**"
    };
    @Value("#{'${app.security.roleEntitlements}'.split(',')}")
    private String[] roleEntitlements;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(IGNORED_PATH_URI).permitAll()
                        .requestMatchers("/processManagement/v1/processFlow/**").hasAnyAuthority(roleEntitlements)
                        .anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(UserRolesRequestService userRolesRequestService) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter(userRolesRequestService));
        return jwtAuthenticationConverter;
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.config;

import com.orange.discobole.orderorchestration.common.web.client.config.KeycloakGrantedAuthoritiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
@Profile("!no-security")
public class SecurityConfiguration {

    @Value("${app.security.userRoleRetrievalUrl}")
    private String userRoleRetrievalUrl;

    @Value("${app.security.keycloakReadEntitlements}")
    private String[] keycloakReadEntitlements;

    @Value("${app.security.keycloakReadAndWriteEntitlements}")
    private String[] keycloakReadAndWriteEntitlements;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/error", "/swagger-ui/**", "/async-api/**", "/actuator/**", "/v3/api-docs/**", "/api-docs/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .requestMatchers(HttpMethod.GET, "/falloutIncident/**")
                                .hasAnyAuthority(Stream.concat(
                                        Arrays.stream(keycloakReadEntitlements),
                                        Arrays.stream(keycloakReadAndWriteEntitlements)
                                ).toArray(String[]::new))
                                .requestMatchers("/processManagement/**")
                                .hasAnyAuthority(keycloakReadAndWriteEntitlements)
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2Configurer -> oauth2Configurer.jwt(jwtConfigurer -> jwtConfigurer
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter(userRoleRetrievalUrl));
        return converter;
    }
}

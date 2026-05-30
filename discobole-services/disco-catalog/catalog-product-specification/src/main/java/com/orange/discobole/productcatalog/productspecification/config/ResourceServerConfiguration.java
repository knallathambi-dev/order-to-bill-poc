// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;

/**
 * configuration for resource server.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class ResourceServerConfiguration {
	public static final String AUTHORITIES_CLAIM_NAME = "roles";

	@Value("${spring.keycloak}")
	private String keycloakUrl;

	@Value("${spring.keycloakRolesEntitlements}")
	private String[] keycloakRolesEntitlements;

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			// -- Swagger UI(OpenAPI)
			"/monitoring/**", "/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/swagger-ui.html",
			"/webjars/**", "/v3/api-docs/**", "/swagger-ui/**", "/actuator/**", "/actuator/prometheus/**" };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(requests -> requests.requestMatchers(CLASSPATH_RESOURCE_LOCATIONS).permitAll()
				.requestMatchers("/processManagement/v1/processFlow/**")
				.hasAnyAuthority(keycloakRolesEntitlements).anyRequest().authenticated());

		// JWT Validation Configuration
		http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
				.jwtAuthenticationConverter(jwtAuthenticationConverter()).jwkSetUri(keycloakUrl)));
		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
		return converter;
	}

	@Bean
	public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
		return new KeycloakGrantedAuthoritiesConverter();
	}

}

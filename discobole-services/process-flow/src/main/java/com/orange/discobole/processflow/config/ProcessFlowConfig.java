// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Component to include base package
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@ComponentScan("com.orange.discobole.processflow")
public class ProcessFlowConfig {

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http
				.csrf((csrf) -> csrf.disable());
		return http.build();
//		http
//				.csrf().disable()
//				.authorizeHttpRequests(requests -> requests
//						.requestMatchers(HttpMethod.GET, "/", "/static/**", "/index.html", "/api/users/me").permitAll()
//						.requestMatchers(HttpMethod.POST, "/api/users").permitAll()
//						.requestMatchers(HttpMethod.GET, "/api/users/login", "/api/users/{username}", "/api/users/logout", "/api/customers", "/api/storages").authenticated()
//						.requestMatchers(HttpMethod.POST, "/api/customers", "/api/storages").authenticated()
//						.requestMatchers(HttpMethod.PUT, "/api/customers/{id}", "/api/storages/{id}").authenticated()
//						.requestMatchers(HttpMethod.DELETE, "/api/users/{id}", "/api/storages/{id}", "/api/customers/{id}").authenticated()
//						.anyRequest().denyAll()).httpBasic();
//		return http.build();
	}

//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails user = User.withDefaultPasswordEncoder()
//				.username("user")
//				.password("password")
//				.roles("USER")
//				.build();
//		return new InMemoryUserDetailsManager(user);
//	}
}

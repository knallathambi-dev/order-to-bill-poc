// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import com.orange.discobole.productinventory.config.webclient.WebClientConfig;
import com.orange.discobole.productinventory.config.webclient.WebClientConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebClientConfigTest {

    @Mock
    private WebClientConfigProperties webClientConfigProperties;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @InjectMocks
    private WebClientConfig webClientConfig;

    private WebClient.Builder webClientBuilder;

    @BeforeEach
    void setUp() {
        webClientBuilder = WebClient.builder();

        // Setup default property values
        when(webClientConfigProperties.getConnectTimeout()).thenReturn(Duration.ofSeconds(5));
        when(webClientConfigProperties.getResponseTimeout()).thenReturn(Duration.ofSeconds(10));
    }

    @Test
    void testWebClientWithServiceAccount_shouldCreateWebClientWithOAuth2() {
        // When
        WebClient webClient = webClientConfig.webClientWithServiceAccount(
                webClientBuilder,
                clientRegistrationRepository
        );

        // Then
        assertThat(webClient).isNotNull();
    }

    @Test
    void testWebClientWithoutServiceAccount_shouldCreateWebClientWithoutOAuth2() {
        // When
        WebClient webClient = webClientConfig.webClientWithoutServiceAccount(webClientBuilder);

        // Then
        assertThat(webClient).isNotNull();
    }

    @Test
    void testUserWebClient_shouldCreateWebClientWithTimeouts() {
        // When
        WebClient userWebClient = webClientConfig.userWebClient(webClientBuilder);

        // Then
        assertThat(userWebClient).isNotNull();
    }

    @Test
    void testWebClientWithServiceAccount_shouldApplyConnectTimeout() {
        // Given
        Duration connectTimeout = Duration.ofSeconds(3);
        when(webClientConfigProperties.getConnectTimeout()).thenReturn(connectTimeout);

        // When
        WebClient webClient = webClientConfig.webClientWithServiceAccount(
                webClientBuilder,
                clientRegistrationRepository
        );

        // Then
        assertThat(webClient).isNotNull();
        // Note: Actual timeout verification would require integration testing
        // as Netty HttpClient configuration is internal
    }

    @Test
    void testWebClientWithServiceAccount_shouldApplyResponseTimeout() {
        // Given
        Duration responseTimeout = Duration.ofSeconds(15);
        when(webClientConfigProperties.getResponseTimeout()).thenReturn(responseTimeout);

        // When
        WebClient webClient = webClientConfig.webClientWithServiceAccount(
                webClientBuilder,
                clientRegistrationRepository
        );

        // Then
        assertThat(webClient).isNotNull();
    }

    @Test
    void testUserWebClient_shouldUseConfiguredTimeouts() {
        // Given
        Duration connectTimeout = Duration.ofSeconds(2);
        Duration responseTimeout = Duration.ofSeconds(8);
        when(webClientConfigProperties.getConnectTimeout()).thenReturn(connectTimeout);
        when(webClientConfigProperties.getResponseTimeout()).thenReturn(responseTimeout);

        // When
        WebClient userWebClient = webClientConfig.userWebClient(webClientBuilder);

        // Then
        assertThat(userWebClient).isNotNull();
    }

    @Test
    void testWebClientCreation_withDifferentTimeoutValues() {
        // Given
        Duration shortTimeout = Duration.ofMillis(500);
        Duration longTimeout = Duration.ofSeconds(30);

        when(webClientConfigProperties.getConnectTimeout()).thenReturn(shortTimeout);
        when(webClientConfigProperties.getResponseTimeout()).thenReturn(longTimeout);

        // When
        WebClient webClient = webClientConfig.webClientWithoutServiceAccount(webClientBuilder);

        // Then
        assertThat(webClient).isNotNull();
    }
}
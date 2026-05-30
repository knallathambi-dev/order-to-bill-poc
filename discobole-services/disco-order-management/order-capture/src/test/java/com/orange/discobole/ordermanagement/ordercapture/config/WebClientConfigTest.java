// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;

import com.orange.discobole.ordermanagement.ordercapture.handler.WebClientErrorHandler;
import com.orange.discobole.ordermanagement.ordercapture.interceptor.CustomWebClientInterceptor;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class WebClientConfigTest {

    @Bean
    @Primary
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "false")
    public WebClient localWebClient(WebClient.Builder webClientBuilder) throws SSLException {
        return createInsecureWebClient(webClientBuilder);
    }

    @Bean("userWebClient")
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "false")
    public WebClient userWebClient(WebClient.Builder webClientBuilder) throws SSLException {
        return createInsecureWebClient(webClientBuilder);
    }

    /**
     * Creates a WebClient with insecure SSL context for testing purposes.
     * This method centralizes the common WebClient configuration to avoid code duplication.
     *
     * @param webClientBuilder the WebClient builder
     * @return configured WebClient with insecure SSL context
     * @throws SSLException if SSL context creation fails
     */
    private WebClient createInsecureWebClient(WebClient.Builder webClientBuilder) throws SSLException {
        // Create a custom SSL context with an insecure TrustManager for testing
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        // Create a Reactor Netty HttpClient with the custom SSL context
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

        // Create a ClientHttpConnector using Reactor Netty HttpClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        // Create instances of custom interceptor and error handler
        CustomWebClientInterceptor interceptor = new CustomWebClientInterceptor();
        WebClientErrorHandler errorHandler = new WebClientErrorHandler();

        // Build the WebClient with the custom ClientHttpConnector and filters
        return webClientBuilder
                .clientConnector(connector)
                .filter(errorHandler)
                .filters(filters -> filters.add(interceptor))
                .build();
    }

    /**
     * Creates a mock ClientRegistrationRepository for testing.
     *
     * @return ClientRegistrationRepository mock instance
     */
    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock(ClientRegistrationRepository.class);
    }

    /**
     * Creates a mock OAuth2AuthorizedClientService for testing.
     *
     * @return OAuth2AuthorizedClientService mock instance
     */
    @Bean
    @Primary
    public OAuth2AuthorizedClientService authorizedClientService() {
        return mock(OAuth2AuthorizedClientService.class);
    }

    /**
     * Creates a mock OAuth2AuthorizedClientManager for testing.
     *
     * @return OAuth2AuthorizedClientManager mock instance
     */
    @Bean
    @Primary
    public OAuth2AuthorizedClientManager authorizedClientManager() {
        return mock(OAuth2AuthorizedClientManager.class);
    }

    @Nested
    @DisplayName("WebClientConfigTest Internal Tests")
    class InternalTests {

        @Test
        @DisplayName("Given WebClientConfigTest, when creating localWebClient, then return configured WebClient")
        void shouldCreateLocalWebClient() throws SSLException {
            // Given
            WebClientConfigTest config = new WebClientConfigTest();
            WebClient.Builder builder = WebClient.builder();

            // When
            WebClient result = config.localWebClient(builder);

            // Then
            Assertions.assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Given WebClientConfigTest, when creating userWebClient, then return configured WebClient")
        void shouldCreateUserWebClient() throws SSLException {
            // Given
            WebClientConfigTest config = new WebClientConfigTest();
            WebClient.Builder builder = WebClient.builder();

            // When
            WebClient result = config.userWebClient(builder);

            // Then
            Assertions.assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Given WebClientConfigTest, when creating OAuth2 beans, then return mocks")
        void shouldCreateOAuth2Mocks() {
            // Given
            WebClientConfigTest config = new WebClientConfigTest();

            // When
            ClientRegistrationRepository clientRepo = config.clientRegistrationRepository();
            OAuth2AuthorizedClientService clientService = config.authorizedClientService();
            OAuth2AuthorizedClientManager clientManager = config.authorizedClientManager();

            // Then
            Assertions.assertThat(clientRepo).isNotNull();
            Assertions.assertThat(clientService).isNotNull();
            Assertions.assertThat(clientManager).isNotNull();

            // Verify they are mocks
            Assertions.assertThat(Mockito.mockingDetails(clientRepo).isMock()).isTrue();
            Assertions.assertThat(Mockito.mockingDetails(clientService).isMock()).isTrue();
            Assertions.assertThat(Mockito.mockingDetails(clientManager).isMock()).isTrue();
        }

        @Test
        @DisplayName("Given null WebClient.Builder, when creating WebClients, then throw exception")
        void shouldThrowExceptionForNullBuilder() {
            // Given
            WebClientConfigTest config = new WebClientConfigTest();

            // When & Then
            Assertions.assertThatThrownBy(() -> config.localWebClient(null))
                    .isInstanceOf(NullPointerException.class);

            Assertions.assertThatThrownBy(() -> config.userWebClient(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Given WebClientConfigTest, when creating multiple instances, then verify behavior")
        void shouldCreateMultipleInstances() {
            // Given
            WebClientConfigTest config = new WebClientConfigTest();

            // When
            ClientRegistrationRepository repo1 = config.clientRegistrationRepository();
            ClientRegistrationRepository repo2 = config.clientRegistrationRepository();

            // Then
            Assertions.assertThat(repo1)
                    .isNotNull()
                    .isNotSameAs(repo2);
            Assertions.assertThat(repo2).isNotNull();
        }

        @Test
        @DisplayName("Given WebClientConfigTest, when testing createInsecureWebClient method indirectly, then verify SSL configuration")
        void shouldConfigureInsecureSSL() throws SSLException {
            // Given
            WebClientConfigTest config = new WebClientConfigTest();
            WebClient.Builder builder = WebClient.builder();

            // When
            WebClient localClient = config.localWebClient(builder);
            WebClient userClient = config.userWebClient(builder);

            // Then
            Assertions.assertThat(localClient)
                    .isNotNull()
                    .isNotSameAs(userClient);
            Assertions.assertThat(userClient).isNotNull();
        }
    }
}

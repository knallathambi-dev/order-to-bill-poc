// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.common.web.client.config;


import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "true", matchIfMissing = true)
    @Profile({"!test", "!dev"})
    public WebClient webClient(WebClient.Builder webClientBuilder, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        return createWebClient(webClientBuilder, httpClient, authorizedClientManager);
    }

    @Bean
    @Profile("!test")
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                                        OAuth2AuthorizedClientService authorizedClientService) {

        InMemoryOAuth2AuthorizedClientService authClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);

        AuthorizedClientServiceOAuth2AuthorizedClientManager authClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authClientService);

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().refreshToken()
                .clientCredentials().build();
        authClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authClientManager;
    }

    @Bean
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "false")
    @Profile({"dev"})
    public WebClient localWebClient(WebClient.Builder webClientBuilder, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) throws SSLException {
        // Create a custom SSL context with an insecure TrustManager
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        // Create a Reactor Netty HttpClient with the custom SSL context
        HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        return createWebClient(webClientBuilder, httpClient, authorizedClientManager);
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        // Create an instance of your custom interceptor
        CustomWebClientInterceptor interceptor = new CustomWebClientInterceptor();
        // Create an instance of your custom interceptor error
        WebClientErrorHandler handlerError = new WebClientErrorHandler();
        // Build the WebClient with the custom ClientHttpConnector

        WebClient client = webClientBuilder.clientConnector(connector).filter(handlerError).build();
        // Add the interceptor to the WebClient's filters
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("keycloak");
        return client.mutate().filters(filters -> {
            filters.add(interceptor);
            filters.add(oauth);
        }).build();
    }
}

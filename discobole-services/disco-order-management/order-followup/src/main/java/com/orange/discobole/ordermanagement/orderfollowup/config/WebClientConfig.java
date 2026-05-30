// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.config;

import com.orange.discobole.ordermanagement.orderfollowup.handler.WebClientErrorHandler;
import com.orange.discobole.ordermanagement.orderfollowup.interceptor.CustomWebClientInterceptor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.List;

@Configuration
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Slf4j
public class WebClientConfig {

    private final WebClientConfigProperties webClientConfigProperties;

    @Bean
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "true", matchIfMissing = true)
    @Profile({"!test", "!dev"})
    public WebClient webClient(WebClient.Builder webClientBuilder, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        log.info("Creating WebClient with SSL enabled");
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        return createWebClient(webClientBuilder, createHttpClient(), authorizedClientManager);
    }

    @Bean
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "false")
    @Profile({"dev"})
    public WebClient localWebClient(WebClient.Builder webClientBuilder, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) throws SSLException {
        log.info("Creating WebClient with SSL disabled (dev profile - insecure TrustManager)");
        // Create a custom SSL context with an insecure TrustManager
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        // Create a Reactor Netty HttpClient with the custom SSL context
        HttpClient httpClient = createHttpClient();
        httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        return createWebClient(webClientBuilder, httpClient, authorizedClientManager);
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository) {
        log.debug("Configuring OAuth2 authorized client manager");

        InMemoryOAuth2AuthorizedClientService authClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        AuthorizedClientServiceOAuth2AuthorizedClientManager authClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authClientService);
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .clientCredentials()
                .build();
        authClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authClientManager;
    }

    private HttpClient createHttpClient() {
        log.debug("Creating HttpClient with connectTimeout: {}, responseTimeout: {}",
                webClientConfigProperties.getConnectTimeout(), webClientConfigProperties.getResponseTimeout());
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) webClientConfigProperties.getConnectTimeout().toMillis())
                .responseTimeout(webClientConfigProperties.getResponseTimeout());
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.clientConnector(connector).filters(filters -> filters.addAll(getFiltersList(authorizedClientManager))).build();
    }

    private List<ExchangeFilterFunction> getFiltersList(AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        // Create an instance of your custom interceptor
        CustomWebClientInterceptor interceptor = new CustomWebClientInterceptor();
        // Create an instance of your custom interceptor error
        WebClientErrorHandler handlerError = new WebClientErrorHandler();
        // Create an instance of your custom ServletOAuth2AuthorizedClientExchangeFilterFunction
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("keycloak");

        return List.of(interceptor, handlerError, oauth);
    }
}
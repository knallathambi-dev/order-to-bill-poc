// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config.webclient;

import com.orange.discobole.productinventory.handler.WebClientErrorHandler;
import com.orange.discobole.productinventory.interceptor.WebClientLoggingInterceptor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class WebClientConfig {

    private final WebClientConfigProperties webClientConfigProperties;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "config.webclient.useServiceAccount", havingValue = "true", matchIfMissing = true)
    public WebClient webClientWithServiceAccount(WebClient.Builder webClientBuilder, ClientRegistrationRepository clientRegistrationRepository) {
        return createWebClient(webClientBuilder, createHttpClient(), createAuthorizedClientManager(clientRegistrationRepository));
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "config.webclient.useServiceAccount", havingValue = "false")
    public WebClient webClientWithoutServiceAccount(WebClient.Builder webClientBuilder) {
        return createWebClient(webClientBuilder, createHttpClient(), null);
    }
    @Bean
    public WebClient userWebClient(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) webClientConfigProperties.getConnectTimeout().toMillis())
                .responseTimeout(webClientConfigProperties.getResponseTimeout());

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) webClientConfigProperties.getConnectTimeout().toMillis())
                .responseTimeout(webClientConfigProperties.getResponseTimeout());
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.clientConnector(connector).filters(filters -> filters.addAll(getFiltersList(authorizedClientManager))).build();
    }

    private List<ExchangeFilterFunction> getFiltersList(AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        WebClientLoggingInterceptor interceptor = new WebClientLoggingInterceptor();
        WebClientErrorHandler handlerError = new WebClientErrorHandler();
        ArrayList<ExchangeFilterFunction> filters = new ArrayList<>(List.of(interceptor, handlerError));
        if (Objects.nonNull(authorizedClientManager)) {
            ServletOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
            oauth.setDefaultClientRegistrationId("keycloak");
            filters.add(oauth);
        }
        return filters;
    }

    private AuthorizedClientServiceOAuth2AuthorizedClientManager createAuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository) {

        InMemoryOAuth2AuthorizedClientService authClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        AuthorizedClientServiceOAuth2AuthorizedClientManager authClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                authClientService);

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().refreshToken().clientCredentials().build();
        authClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authClientManager;
    }
}

// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import com.orange.discobole.ordermanagement.orderinventory.handler.WebClientErrorHandler;
import com.orange.discobole.ordermanagement.orderinventory.interceptor.CustomWebClientInterceptor;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
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
    @Primary
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "true", matchIfMissing = true)
    @Profile({"!test", "!dev"})
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        return createWebClient(webClientBuilder, createHttpClient());
    }

    @Bean
    @Primary
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "false")
    @Profile({"dev"})
    public WebClient localWebClient(WebClient.Builder webClientBuilder) throws SSLException {
        // Create a custom SSL context with an insecure TrustManager
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        // Create a Reactor Netty HttpClient with the custom SSL context
        HttpClient httpClient = createHttpClient();
        httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        return createWebClient(webClientBuilder, httpClient);
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
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) webClientConfigProperties.getConnectTimeout().toMillis())
                .responseTimeout(webClientConfigProperties.getResponseTimeout());
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.clientConnector(connector).filters(filters -> filters.addAll(getFiltersList())).build();
    }

    private List<ExchangeFilterFunction> getFiltersList() {
        // Create an instance of your custom interceptor
        CustomWebClientInterceptor interceptor = new CustomWebClientInterceptor();
        // Create an instance of your custom interceptor error
        WebClientErrorHandler handlerError = new WebClientErrorHandler();

        return List.of(interceptor, handlerError);
    }
}

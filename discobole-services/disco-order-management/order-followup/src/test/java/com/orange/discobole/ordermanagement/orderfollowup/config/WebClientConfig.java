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
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnProperty(value = "app.security.ssl.enabled", havingValue = "false")
    @Profile({"test"})
    public WebClient localWebClient(WebClient.Builder webClientBuilder) throws SSLException {
        // Create a custom SSL context with an insecure TrustManager
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        // Create a Reactor Netty HttpClient with the custom SSL context
        HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        // Create a ClientHttpConnector using Reactor Netty HttpClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        // Create an instance of your custom interceptor
        CustomWebClientInterceptor interceptor = new CustomWebClientInterceptor();
        // Create an instance of your custom interceptor error
        WebClientErrorHandler handlerError = new WebClientErrorHandler();
        // Build the WebClient with the custom ClientHttpConnector

        WebClient client = webClientBuilder.clientConnector(connector).filter(handlerError).build();
        // Add the interceptor to the WebClient's filters
        return client.mutate().filters(filters -> {
            filters.add(interceptor);
        }).build();
    }
}
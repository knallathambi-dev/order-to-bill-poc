// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.base;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.common.web.client.config.WebClientErrorHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Configuration
public class IntegrationTestConfig {

    @Bean(destroyMethod = "stop", initMethod = "start")
    public WireMockServer wireMockServer() {
        return new WireMockServer(options().port(9997));
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        WebClientErrorHandler handlerError = new WebClientErrorHandler();

        return webClientBuilder.clientConnector(connector).filter(handlerError).build();
    }

}

// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.util;


import com.orange.discobole.orderorchestration.orchestrationdelivery.management.exception.OrchestrationPlanNodeErrorMessagesException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = WebClientUtil.class)
@ExtendWith(SpringExtension.class)
@Disabled
class WebClientUtilTest {

    @MockBean
    private WebClient webClient;

    @Autowired
    private WebClientUtil webClientUtil;


    @Test
    void testSend_withClassResponse_successfulResponse() {
        // Arrange
        String uri = "http://test.com";
        HttpMethod httpMethod = HttpMethod.GET;
        String responseBody = "ResponseBody";
        String productsIds = "1,2";
        BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> invalidResponse = (retryBackoffSpec, retrySignal) -> {
            throw new OrchestrationPlanNodeErrorMessagesException(List.of(OrchestrationNodeErrorMessage.builder()
                    .message("Error encountered when fetching service specification ids: %s".formatted(productsIds))
                    .code("INVALID_RESPONSE")
                    .reason("Failed to fetch the service specification details")
                    .build())); // Simulate an exception for testing
        };
        Consumer<String> notFound = response -> {
            throw OrchestrationPlanNodeErrorMessagesException.of(List.of(OrchestrationNodeErrorMessage.builder()
                    .message("Error encountered when fetching service specification ids: %s".formatted(productsIds))
                    .code("NOT_FOUND")
                    .reason("Failed to fetch the service specification details")
                    .build()));
        };

        // Mock the WebClient behavior
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.method(httpMethod)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        Mono<String> justMonon = Mono.just(responseBody);
        when(responseSpec.bodyToMono(String.class)).thenReturn(justMonon);
        when(responseSpec.onStatus(any(),any())).thenReturn(responseSpec);
        when(justMonon.retryWhen(any())).thenReturn(justMonon);


        // Act


        String result = webClientUtil.send(httpMethod, uri, new ParameterizedTypeReference<>() {}, invalidResponse, notFound, null);

        // Assert
        assertNotNull(result);
        assertEquals(responseBody, result);
        verify(webClient).method(httpMethod);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void testSend_withParameterizedTypeReference_successfulResponse() {
        // Arrange
        String uri = "http://test.com";
        HttpMethod httpMethod = HttpMethod.GET;
        ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<>() {
        };
        String responseBody = "ResponseBody";

        // Mock the WebClient behavior
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.method(httpMethod)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(typeReference)).thenReturn(Mono.just(responseBody));

        // Act
        String result = webClientUtil.send(httpMethod, uri, typeReference, any(), any(), any());

        // Assert
        assertNotNull(result);
        assertEquals(responseBody, result);
        verify(webClient).method(httpMethod);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).bodyToMono(typeReference);
    }

    @Test
    void testSend_withBodyAndTypeReference_successfulResponse() {
        // Arrange
        String uri = "http://test.com";
        HttpMethod httpMethod = HttpMethod.POST;
        ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<>() {
        };
        String body = "RequestBody";
        String responseBody = "ResponseBody";

        // Mock the WebClient behavior
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.method(httpMethod)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(body)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(typeReference)).thenReturn(Mono.just(responseBody));

        // Act
        String result = webClientUtil.send(httpMethod, uri, typeReference, any(), any(), any());

        // Assert
        assertNotNull(result);
        assertEquals(responseBody, result);
        verify(webClient).method(httpMethod);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodyUriSpec).bodyValue(body);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(typeReference);
    }

    @Test
    void testSend_withClassResponseAndBody_successfulResponse() {
        // Arrange
        String uri = "http://test.com";
        HttpMethod httpMethod = HttpMethod.POST;
        String body = "RequestBody";
        String responseBody = "ResponseBody";

        // Mock the WebClient behavior
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.method(httpMethod)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(body)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseBody));

        // Act
        String result = webClientUtil.send(httpMethod, uri, String.class, body, any(), any(), any());

        // Assert
        assertNotNull(result);
        assertEquals(responseBody, result);
        verify(webClient).method(httpMethod);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodyUriSpec).bodyValue(body);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }
}

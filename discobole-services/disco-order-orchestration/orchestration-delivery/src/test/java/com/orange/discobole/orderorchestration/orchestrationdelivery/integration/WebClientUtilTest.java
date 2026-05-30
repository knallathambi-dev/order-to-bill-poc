// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration;

import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.exception.CoodHttpServiceUnavailableException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.CPIBHttpFailedException;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class, SpringExtension.class})
@Slf4j
class WebClientUtilTest extends BaseAbstractionIntegrationTest {

    @Autowired
    WebClientUtil webClientUtil;

    @Test
    void givenDummyWireMockStubWithRequestTimeoutHttpStatusCode_whenGetWebClient_thenEnteredOnRetryExhaustedThrow() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        String dummyUrl = "/dummy/url";
        wireMockServer.stubFor(get(dummyUrl).willReturn(aResponse().withStatus(408)));

        String url = wireMockServer.baseUrl() + dummyUrl;
        CPIBHttpFailedException fallbackException = new CPIBHttpFailedException();

        assertThrows(CPIBHttpFailedException.class, () ->
                webClientUtil.send(
                        HttpMethod.GET,
                        url,
                        String.class,
                        (retryBackoffSpec, retrySignal) -> {
                            WebClientResponseException webClientResponseException =
                                    (WebClientResponseException) retrySignal.failure();
                            return new CPIBHttpFailedException(
                                    "test", "test", webClientResponseException.getStatusCode());
                        },
                        responseBody -> {},
                        fallbackException
                ));
    }

    @Test
    void givenDummyWireMockStubWithNotRetriableStatusCode_whenGetWebClient_thenWebClientResponseExceptionThrown() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        String dummyUrl = "/dummy/url";
        wireMockServer.stubFor(get(dummyUrl).willReturn(badRequest().withBody("Bad request")));
        AtomicBoolean entered = new AtomicBoolean(false);

        String url = wireMockServer.baseUrl() + dummyUrl;
        CPIBHttpFailedException fallbackException = new CPIBHttpFailedException();

        assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                webClientUtil.send(
                        HttpMethod.GET,
                        url,
                        String.class,
                        (retryBackoffSpec, retrySignal) -> {
                            entered.set(true);
                            WebClientResponseException webClientResponseException =
                                    (WebClientResponseException) retrySignal.failure();
                            return new CPIBHttpFailedException(
                                    "test", "test", webClientResponseException.getStatusCode());
                        },
                        responseBody -> {},
                        fallbackException
                ));

        assertFalse(entered.get());
    }

    @Test
    void givenDummyWireMockStubWithUndefinedUrl_whenGetWebClient_thenWebClientRequestExceptionThrown() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        String dummyUrl = "/dummy/url";
        AtomicBoolean entered = new AtomicBoolean(false); // this boolean to ensure that on the WebClientRequestException it will not enter the retry block

        CPIBHttpFailedException fallbackException = new CPIBHttpFailedException();

        assertThrows(CoodHttpServiceUnavailableException.class, () ->
                webClientUtil.send(
                        HttpMethod.GET,
                        dummyUrl,
                        String.class,
                        (retryBackoffSpec, retrySignal) -> {
                            entered.set(true);
                            WebClientResponseException webClientResponseException =
                                    (WebClientResponseException) retrySignal.failure();
                            return new CPIBHttpFailedException(
                                    "test", "test", webClientResponseException.getStatusCode());
                        },
                        responseBody -> {},
                        fallbackException
                ));

        assertFalse(entered.get());
    }

    @Test
    void givenDummyWireMockStubWithTooManyRequestsHttpStatusCode_whenGetWebClient_thenEnteredOnRetryExhaustedThrow() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        String dummyUrl = "/dummy/url";
        wireMockServer.stubFor(get(dummyUrl).willReturn(aResponse().withStatus(429)));
        AtomicBoolean entered = new AtomicBoolean(false);

        String url = wireMockServer.baseUrl() + dummyUrl;
        CPIBHttpFailedException fallbackException = new CPIBHttpFailedException();

        assertThrows(CPIBHttpFailedException.class, () ->
                webClientUtil.send(
                        HttpMethod.GET,
                        url,
                        String.class,
                        (retryBackoffSpec, retrySignal) -> {
                            entered.set(true);
                            WebClientResponseException webClientResponseException =
                                    (WebClientResponseException) retrySignal.failure();
                            return new CPIBHttpFailedException(
                                    "test", "test", webClientResponseException.getStatusCode());
                        },
                        responseBody -> {},
                        fallbackException
                ));

        assertTrue(entered.get());
    }

    @ParameterizedTest
    @CsvSource({
            "404, Not Found",
            "400, Bad Request",
            "204, No Content"
    })
    void givenDummyWireMockStubWithErrorStatus_whenGetWebClient_thenThrowCoodRecoverableAndNonRetryableException(
            int statusCode, String responseBody) {

        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        String dummyUrl = "/dummy/url";

        wireMockServer.stubFor(get(dummyUrl)
                .willReturn(aResponse()
                        .withBody(responseBody)
                        .withStatus(statusCode)));

        AtomicBoolean entered = new AtomicBoolean(false);

        String url = wireMockServer.baseUrl() + dummyUrl;
        CPIBHttpFailedException fallbackException = new CPIBHttpFailedException();

        assertThrows(CoodRecoverableAndNonRetryableException.class,
                () -> webClientUtil.send(
                        HttpMethod.GET,
                        url,
                        String.class,
                        (retryBackoffSpec, retrySignal) -> {
                            entered.set(true);
                            WebClientResponseException webClientResponseException =
                                    (WebClientResponseException) retrySignal.failure();
                            return new CPIBHttpFailedException(
                                    "test", "test", webClientResponseException.getStatusCode());
                        },
                        response -> {},
                        fallbackException
                ));

        assertFalse(entered.get());
    }

    @Test
    void givenDummyWireMockStubWithInternalServerErrorHttpStatusCode_whenGetWebClient_thenThrowCoodRecoverableAndNonRetryableException() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        String dummyUrl = "/dummy/url";
        wireMockServer.stubFor(get(dummyUrl).willReturn(aResponse().withBody("Internal Server Error").withStatus(500)));
        AtomicBoolean entered = new AtomicBoolean(false);

        String url = wireMockServer.baseUrl() + dummyUrl;
        CPIBHttpFailedException fallbackException = new CPIBHttpFailedException();

        assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                webClientUtil.send(
                        HttpMethod.GET,
                        url,
                        String.class,
                        (retryBackoffSpec, retrySignal) -> {
                            entered.set(true);
                            WebClientResponseException webClientResponseException =
                                    (WebClientResponseException) retrySignal.failure();
                            return new CPIBHttpFailedException(
                                    "test", "test", webClientResponseException.getStatusCode());
                        },
                        responseBody -> {},
                        fallbackException
                ));

        assertFalse(entered.get());
    }
}

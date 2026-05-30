// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.util;

import com.orange.discobole.orderorchestration.exception.CoodHttpServiceUnavailableException;
import com.orange.discobole.orderorchestration.exception.model.CoodDecodingException;
import com.orange.discobole.orderorchestration.exception.model.CoodHttpFailedException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.orange.discobole.orderorchestration.exception.utils.ExceptionUtil.createCoodDLTAndNonRetryableException;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Getter
public class WebClientUtil {

    private static final String EMPTY_RESPONSE_MESSAGE = "";
    private static final String AN_ERROR_OCCURRED_URL_S_MESSAGE_S = "An error occurred, url : %s, message: %s";

    @Value("${retry.maxAttempts}")
    private Long maxAttempts;

    @Value("${retry.maxDelay}")
    private Long maxDelay;

    private final WebClient webClient;

    private static Predicate<HttpStatusCode> isHttpStatusCodeTechnicalException() {
        return httpStatusCode ->
                httpStatusCode.value() == 400 || httpStatusCode.value() == 404 || httpStatusCode.value() == 500;
    }

    private static Predicate<HttpStatusCode> isNoContentStatusCode() {
        return httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatusCode.valueOf(204));
    }

    /**
     * Send a http request
     *
     * @param httpMethod
     * @param uri
     * @param responseBodyClass       response body class type
     * @param retryExhaustedGenerator
     * @param onSuccess
     * @param <T>
     * @return
     */
    public <T> T send(HttpMethod httpMethod, String uri, Class<T> responseBodyClass, BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustedGenerator, Consumer<? super T> onSuccess, CoodHttpFailedException throwable) {
        return webClient
                .method(httpMethod)
                .uri(uri)
                .retrieve()
                .onStatus(isNoContentStatusCode(), clientResponse -> Mono.error(createCoodDLTAndNonRetryableException(clientResponse.statusCode().value(), uri, EMPTY_RESPONSE_MESSAGE, throwable)))
                .onStatus(isHttpStatusCodeTechnicalException(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseBody -> Mono.error(createCoodDLTAndNonRetryableException(clientResponse.statusCode().value(), uri, responseBody, throwable)))
                )
                .bodyToMono(responseBodyClass)
                .retryWhen(Retry.fixedDelay(maxAttempts, Duration.ofMillis(maxDelay))
                        .onRetryExhaustedThrow(retryExhaustedGenerator)
                        .filter(WebClientRetryStrategy::retryStrategy))
                .doOnSuccess(onSuccess)
                .onErrorMap(DecodingException.class, e -> CoodRecoverableAndNonRetryableException.of(new CoodDecodingException(ExceptionCode.COOD_DECODING_EXCEPTION, e.getMessage())))
                .onErrorMap(WebClientRequestException.class, e -> new CoodHttpServiceUnavailableException(uri, e.getMessage()))
                .doOnError(exception -> log.error(AN_ERROR_OCCURRED_URL_S_MESSAGE_S.formatted(uri, exception.getMessage()), exception))
                .block();
    }

    /**
     * Send a http request with body
     *
     * @param httpMethod
     * @param uri
     * @param typeReference
     * @param retryExhaustedGenerator
     * @param onSuccess
     * @param <T>
     * @return
     */

    public <T> T send(HttpMethod httpMethod, String uri, ParameterizedTypeReference<T> typeReference, BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustedGenerator, Consumer<? super T> onSuccess, CoodHttpFailedException throwable) {
        return webClient
                .method(httpMethod)
                .uri(uri)
                .retrieve()
                .onStatus(isNoContentStatusCode(), clientResponse -> Mono.error(createCoodDLTAndNonRetryableException(clientResponse.statusCode().value(), uri, EMPTY_RESPONSE_MESSAGE, throwable)))
                .onStatus(isHttpStatusCodeTechnicalException(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseBody -> Mono.error(createCoodDLTAndNonRetryableException(clientResponse.statusCode().value(), uri, responseBody, throwable)))
                )
                .bodyToMono(typeReference)
                .retryWhen(Retry.fixedDelay(maxAttempts, Duration.ofMillis(maxDelay))
                        .onRetryExhaustedThrow(retryExhaustedGenerator)
                        .filter(WebClientRetryStrategy::retryStrategy))
                .doOnSuccess(onSuccess)
                .onErrorMap(DecodingException.class, e -> CoodRecoverableAndNonRetryableException.of(new CoodDecodingException(ExceptionCode.COOD_DECODING_EXCEPTION, e.getMessage())))
                .onErrorMap(WebClientRequestException.class, e -> new CoodHttpServiceUnavailableException(uri, e.getMessage()))
                .doOnError(exception -> log.error(AN_ERROR_OCCURRED_URL_S_MESSAGE_S.formatted(uri, exception.getMessage()), exception))
                .block();
    }

    /**
     * Send a http request with body
     *
     * @param httpMethod              (Patch, Post, Put)
     * @param uri
     * @param responseBodyClass       response body class type
     * @param body
     * @param retryExhaustedGenerator
     * @param onSuccess
     * @param <T>
     * @return
     */
    public <T> T send(HttpMethod httpMethod, String uri, Class<T> responseBodyClass, String body, BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustedGenerator, Consumer<? super T> onSuccess, CoodHttpFailedException throwable) {
        return webClient
                .method(httpMethod)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(isNoContentStatusCode(), clientResponse -> Mono.error(createCoodDLTAndNonRetryableException(clientResponse.statusCode().value(), uri, EMPTY_RESPONSE_MESSAGE, throwable)))
                .onStatus(isHttpStatusCodeTechnicalException(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseBody -> Mono.error(createCoodDLTAndNonRetryableException(clientResponse.statusCode().value(), uri, responseBody, throwable)))
                )
                .bodyToMono(responseBodyClass)
                .retryWhen(Retry.fixedDelay(maxAttempts, Duration.ofMillis(maxDelay))
                        .onRetryExhaustedThrow(retryExhaustedGenerator)
                        .filter(WebClientRetryStrategy::retryStrategy))
                .doOnSuccess(onSuccess)
                .onErrorMap(DecodingException.class, e -> CoodRecoverableAndNonRetryableException.of(new CoodDecodingException(ExceptionCode.COOD_DECODING_EXCEPTION, e.getMessage())))
                .onErrorMap(WebClientRequestException.class, e -> new CoodHttpServiceUnavailableException(uri, e.getMessage()))
                .doOnError(exception -> log.error(AN_ERROR_OCCURRED_URL_S_MESSAGE_S.formatted(uri, exception.getMessage()), exception))
                .block();
    }

    public <T> Mono<T> sendMono(
            HttpMethod httpMethod,
            String uri,
            ParameterizedTypeReference<T> typeReference,
            String body,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustedGenerator,
            Consumer<? super T> onSuccess,
            CoodHttpFailedException throwable
    ) {
        return webClient.method(httpMethod)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(isNoContentStatusCode(), clientResponse ->
                        Mono.error(createCoodDLTAndNonRetryableException(
                                clientResponse.statusCode().value(),
                                uri,
                                "",
                                throwable
                        ))
                )
                .onStatus(isHttpStatusCodeTechnicalException(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseBody ->
                                        Mono.error(createCoodDLTAndNonRetryableException(
                                                clientResponse.statusCode().value(),
                                                uri,
                                                responseBody,
                                                throwable
                                        ))
                                )
                )
                .bodyToMono(typeReference)
                .retryWhen(
                        Retry.fixedDelay(maxAttempts, Duration.ofMillis(maxDelay))
                                .onRetryExhaustedThrow(retryExhaustedGenerator)
                                .filter(WebClientRetryStrategy::retryStrategy)
                )
                .doOnSuccess(onSuccess)
                .onErrorMap(DecodingException.class, e ->
                        CoodRecoverableAndNonRetryableException.of(
                                new CoodDecodingException(ExceptionCode.COOD_DECODING_EXCEPTION, new Object[]{e.getMessage()})
                        )
                )
                .onErrorMap(WebClientRequestException.class, e ->
                        new CoodHttpServiceUnavailableException(uri, e.getMessage())
                )
                .doOnError(exception ->
                        log.error("An error occurred, url : {}, message: {}", uri, exception.getMessage(), exception)
                );
    }

    public <T> Mono<T> sendMono(
            HttpMethod httpMethod,
            String uri,
            Class<T> responseBodyClass,
            String body,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustedGenerator,
            Consumer<? super T> onSuccess,
            CoodHttpFailedException throwable
    ) {
        return webClient.method(httpMethod)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(isNoContentStatusCode(), clientResponse ->
                        Mono.error(createCoodDLTAndNonRetryableException(
                                clientResponse.statusCode().value(),
                                uri,
                                "",
                                throwable
                        ))
                )
                .onStatus(isHttpStatusCodeTechnicalException(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseBody ->
                                        Mono.error(createCoodDLTAndNonRetryableException(
                                                clientResponse.statusCode().value(),
                                                uri,
                                                responseBody,
                                                throwable
                                        ))
                                )
                )
                .bodyToMono(responseBodyClass)
                .retryWhen(
                        Retry.fixedDelay(maxAttempts, Duration.ofMillis(maxDelay))
                                .onRetryExhaustedThrow(retryExhaustedGenerator)
                                .filter(WebClientRetryStrategy::retryStrategy)
                )
                .doOnSuccess(onSuccess)
                .onErrorMap(DecodingException.class, e ->
                        CoodRecoverableAndNonRetryableException.of(
                                new CoodDecodingException(ExceptionCode.COOD_DECODING_EXCEPTION, new Object[]{e.getMessage()})
                        )
                )
                .onErrorMap(WebClientRequestException.class, e ->
                        new CoodHttpServiceUnavailableException(uri, e.getMessage())
                )
                .doOnError(exception ->
                        log.error("An error occurred, url : {}, message: {}", uri, exception.getMessage(), exception)
                );
    }


}
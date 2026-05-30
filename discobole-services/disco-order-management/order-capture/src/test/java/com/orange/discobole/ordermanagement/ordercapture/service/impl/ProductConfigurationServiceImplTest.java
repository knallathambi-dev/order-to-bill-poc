// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfiguration;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfigurationItem;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile({"test"})
class ProductConfigurationServiceImplTest {

    private static final String CONFIGURATION_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String VALID_PRODUCT_CONFIGURATION_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(5);

    private static final String DEFAULT_GET_PRODUCT_CONFIGURATION_BY_ID_URL = "getProductConfigurationByIdUrl";
    private static final String PRODUCT_CONFIGURATION_URL = "http://localhost:8082/v1/queryProductConfiguration";
    private static final String NOT_FOUND_PRODUCT_CONFIGURATION_URL = "notFoundURL";


    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private ProductConfigurationServiceImpl productConfigurationServiceImpl;

    @Test
    @DisplayName("Given null configuration id, " +
            "when getProductConfigurationById is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullConfigurationId() {
        // Given
        // When
        // Then
        assertThrows(InvalidParameterException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(null));
    }

    @Test
    @DisplayName("Given empty configuration id, " +
            "when getProductConfigurationById is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForEmptyConfigurationId() {
        // Given
        // When
        // Then
        assertThrows(InvalidParameterException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(""));
    }

    @Test
    @DisplayName("Given an invalid product configuration URL, " +
            "when getProductConfigurationById is called, " +
            "then IllegalArgumentException is thrown")
    void shouldThrowIllegalArgumentExceptionForInvalidProductConfigurationUrl() {
        // Given
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(VALID_PRODUCT_CONFIGURATION_ID)).thenThrow(IllegalArgumentException.class);

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(VALID_PRODUCT_CONFIGURATION_ID));
    }

    @Test
    @DisplayName("Given a product configuration URL that returns a NO_CONTENT status, " +
            "when getProductConfigurationById is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForNoContentProductConfiguration() {
        // Given
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(VALID_PRODUCT_CONFIGURATION_ID)).thenReturn(PRODUCT_CONFIGURATION_URL);
        mockWebClientResponse(HttpStatus.NO_CONTENT, null);

        // When
        // Then
        assertThrows(DiscoException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(VALID_PRODUCT_CONFIGURATION_ID));
    }

    @Test
    @DisplayName("Given a product configuration URL that returns a NOT_FOUND status, " +
            "when getProductConfigurationById is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForNotFoundProductConfigurationUrl() {
        // Given
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(VALID_PRODUCT_CONFIGURATION_ID)).thenReturn(NOT_FOUND_PRODUCT_CONFIGURATION_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When
        // Then
        assertThrows(DiscoException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(VALID_PRODUCT_CONFIGURATION_ID));
    }

    @Test
    @DisplayName("Given a product configuration URL that results in an internal server error, " +
            "when getProductConfigurationById is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForInternalServerErrorProductConfiguration() {
        // Given
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(VALID_PRODUCT_CONFIGURATION_ID)).thenReturn(PRODUCT_CONFIGURATION_URL);
        mockWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When
        // Then
        assertThrows(DiscoException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(VALID_PRODUCT_CONFIGURATION_ID));
    }

    @Test
    @DisplayName("Given an empty response body, " +
            "when getProductConfigurationById is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForEmptyResponseBodyProductConfiguration() {
        // Given
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(VALID_PRODUCT_CONFIGURATION_ID)).thenReturn(PRODUCT_CONFIGURATION_URL);
        mockWebClientResponse(HttpStatus.OK, null);

        // When
        // Then
        assertThrows(DiscoException.class, () -> productConfigurationServiceImpl.getProductConfigurationById(VALID_PRODUCT_CONFIGURATION_ID));
    }

    @Test
    @DisplayName("Given a valid request, " +
            "when getProductConfigurationById is called, " +
            "then return a valid response")
    void shouldReturnValidResponseForValidRequest() {
        // Given
        QueryProductConfiguration expectedResult = createProductConfiguration();
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(VALID_PRODUCT_CONFIGURATION_ID)).thenReturn(PRODUCT_CONFIGURATION_URL);
        mockWebClientResponse(HttpStatus.OK, expectedResult);

        // When
        QueryProductConfiguration responseResult = productConfigurationServiceImpl.getProductConfigurationById(VALID_PRODUCT_CONFIGURATION_ID);

        // Then
        assertEquals(expectedResult, responseResult);
    }

    @DisplayName("given Valid request with productIds " +
            "when retrieve product configuration " +
            "then return valid response")
    @Test
    void testValidResponseBodyWithConfigurationId() {
        //given
        QueryProductConfiguration expectedResult = createProductConfiguration();
        String formattedConfigurationId = String.format("%s?id=%s&id=%s", VALID_PRODUCT_CONFIGURATION_ID, PRODUCT_ID_1, PRODUCT_ID_2);
        when(discoServiceUrl.getProductConfigurationItemsByIdUrl(formattedConfigurationId)).thenReturn(DEFAULT_GET_PRODUCT_CONFIGURATION_BY_ID_URL);
        configWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(expectedResult));
        String configurationId = String.format("%s_%s_%s", VALID_PRODUCT_CONFIGURATION_ID, PRODUCT_ID_1, PRODUCT_ID_2);

        //when
        QueryProductConfiguration responseResult = productConfigurationServiceImpl.getProductConfigurationById(configurationId);

        //then
        assertEquals(expectedResult, responseResult);
    }

    private QueryProductConfiguration createProductConfiguration() {
        QueryProductConfiguration expectedResult = new QueryProductConfiguration();
        List<QueryProductConfigurationItem> configurationItems = new ArrayList<>();
        QueryProductConfigurationItem configurationItem = new QueryProductConfigurationItem();
        configurationItem.setId(CONFIGURATION_ID);
        configurationItems.add(configurationItem);
        expectedResult.setComputedProductConfigurationItems(configurationItems);
        expectedResult.setRequestedProductConfigurationItems(configurationItems);
        return expectedResult;
    }

    private void mockWebClientResponse(HttpStatus status, QueryProductConfiguration productConfiguration) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && productConfiguration != null) {
            ResponseEntity<QueryProductConfiguration> responseEntity = new ResponseEntity<>(productConfiguration, status);
            Mono<ResponseEntity<QueryProductConfiguration>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(QueryProductConfiguration.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(QueryProductConfiguration.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }

    void configWebclientResponse(ResponseEntity<QueryProductConfiguration> responseEntity) {
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<ResponseEntity<QueryProductConfiguration>> responseEntityMonoMock = Mono.just(responseEntity);
        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(QueryProductConfiguration.class);
    }
}
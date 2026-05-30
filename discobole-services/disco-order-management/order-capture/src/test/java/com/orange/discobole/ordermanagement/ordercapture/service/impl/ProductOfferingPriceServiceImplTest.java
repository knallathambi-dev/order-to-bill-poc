// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.Money;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.ProductOfferingPrice;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOfferingPriceServiceImplTest {

    private static final String PRODUCT_OFFERING_PRICE_ID = RandomStringUtils.randomAlphabetic(5);
    private static final float PRICE_VALUE = 3f;
    private static final String PRICE_UNIT = "Euro";
    private static final String PRODUCT_OFFERING_PRICE_URL = "http://localhost:8082/productOfferingPrice";

    @Mock
    private WebClient webClient;
    @InjectMocks
    @Spy
    private ProductOfferingPriceServiceImpl productOfferingPriceService;
    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Test
    @DisplayName("Given an empty product offering price ID list, " +
            "when fetchProductOfferingPrices is called, " +
            "then shouldThrowInvalidParameterException")
    void shouldThrowInvalidParameterExceptionForEmptyProductOfferingPriceIdList() {
        // Given
        List<String> productOfferingPriceIds = Collections.emptyList();

        // When & Then
        assertThrows(InvalidParameterException.class,
                () -> productOfferingPriceService.fetchProductOfferingPrices(productOfferingPriceIds));
    }

    @Test
    @DisplayName("Given a valid product offering price ID list, " +
            "when a server error occurs while fetching product offering prices, " +
            "then shouldThrowDiscoException")
    void shouldThrowDiscoExceptionWhenFetchingProductOfferingPrices() {
        // Given
        List<String> productOfferingPriceIds = List.of(PRODUCT_OFFERING_PRICE_ID);
        when(discoServiceUrl.getProductOfferingPriceUrl()).thenReturn(PRODUCT_OFFERING_PRICE_URL);
        mockGetWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, Collections.emptyList());

        // When & Then
        assertThrows(DiscoException.class,
                () -> productOfferingPriceService.fetchProductOfferingPrices(productOfferingPriceIds));
    }

    @Test
    @DisplayName("Given a valid product offering price ID list, " +
            "when fetchProductOfferingPrices is called, " +
            "then shouldReturnProductOfferingPrices")
    void shouldReturnProductOfferingPricesForValidIdList() {
        // Given
        List<String> productOfferingPriceIds = List.of(PRODUCT_OFFERING_PRICE_ID);
        when(discoServiceUrl.getProductOfferingPriceUrl()).thenReturn(PRODUCT_OFFERING_PRICE_URL);
        ProductOfferingPrice productOfferingPrice = createProductOfferingPrice();
        mockGetWebClientResponse(HttpStatus.OK, List.of(productOfferingPrice));

        // When
        List<ProductOfferingPrice> result = productOfferingPriceService.fetchProductOfferingPrices(productOfferingPriceIds);

        // Then
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Given a valid product offering price ID list, " +
            "when fetchProductOfferingPrices is called and no product offering prices are found, " +
            "then shouldThrowDiscoException")
    void shouldThrowDiscoExceptionForValidIdListNotFound() {
        // Given
        List<String> productOfferingPriceIds = List.of(PRODUCT_OFFERING_PRICE_ID);
        when(discoServiceUrl.getProductOfferingPriceUrl()).thenReturn(PRODUCT_OFFERING_PRICE_URL);
        mockGetWebClientResponse(HttpStatus.OK, Collections.emptyList());

        // When & Then
        assertThrows(DiscoException.class,
                () -> productOfferingPriceService.fetchProductOfferingPrices(productOfferingPriceIds));
    }

    private ProductOfferingPrice createProductOfferingPrice() {
        return ProductOfferingPrice.builder()
                .id(PRODUCT_OFFERING_PRICE_ID)
                .price(Money.builder()
                        .unit(PRICE_UNIT)
                        .value(PRICE_VALUE)
                        .build())
                .immediatePayment(Boolean.TRUE)
                .build();
    }

    private void mockGetWebClientResponse(HttpStatus status, List<ProductOfferingPrice> responseBody) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful()) {
            ResponseEntity<List<ProductOfferingPrice>> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<List<ProductOfferingPrice>>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntityList(ProductOfferingPrice.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(ProductOfferingPrice.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString(), any(Function.class));

        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }
}
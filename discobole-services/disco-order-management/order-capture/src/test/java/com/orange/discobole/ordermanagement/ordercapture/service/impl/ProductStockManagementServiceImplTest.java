// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.stock.ProductStock;
import com.orange.discobole.ordermanagement.commons.dto.product.stock.ProductStockReserved;
import com.orange.discobole.ordermanagement.commons.dto.product.stock.ReserveProductStockItem;
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
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockManagementServiceImplTest {
    private static final String DEFAULT_PRODUCT_STOCK_RESERVED = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_PRODUCT_STOCK_URL = "productStockUrl";
    private static final String VALID_RESOURCE_ID = RandomStringUtils.randomAlphabetic(5);

    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @Mock
    private WebClient webClient;

    @InjectMocks
    @Spy
    private ProductStockManagementServiceImpl productStockManagementService;

    @Test
    @DisplayName("Given null product stock, " +
            "when reserveProductStock is called, " +
            "then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullProductStock() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> productStockManagementService.reserveProductStock(null));
    }

    @Test
    @DisplayName("Given invalid URL, " +
            "when reserveProductStock is called, " +
            "then throw IllegalArgumentException")
    void shouldThrowIllegalArgumentExceptionForInvalidUrl() {
        // Given
        when(discoServiceUrl.getProductStockManagementUrl()).thenThrow(IllegalArgumentException.class);
        ProductStock productStock = new ProductStock();

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> productStockManagementService.reserveProductStock(productStock));
    }

    @Test
    @DisplayName("Given server error, " +
            "when reserveProductStock is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForServerErrorOnReserve() {
        // Given
        ProductStock productStock = new ProductStock();
        when(discoServiceUrl.getProductStockManagementUrl()).thenReturn(DEFAULT_PRODUCT_STOCK_URL);
        mockPostWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When & Then
        assertThrows(DiscoException.class, () -> productStockManagementService.reserveProductStock(productStock));
    }

    @Test
    @DisplayName("Given valid product stock, " +
            "when reserveProductStock is called, " +
            "then return reserved product stock")
    void shouldReturnReservedProductStockForValidRequest() {
        // Given
        ProductStock expectedProductStock = createExpectedProductStock();
        when(discoServiceUrl.getProductStockManagementUrl()).thenReturn(DEFAULT_PRODUCT_STOCK_URL);
        mockPostWebClientResponse(HttpStatus.CREATED, expectedProductStock);

        ProductStock productStock = new ProductStock();

        // When
        ProductStock reservedProductStock = productStockManagementService.reserveProductStock(productStock);

        // Then
        assertThat(reservedProductStock).isNotNull();
        assertThat(reservedProductStock.getReserveProductStockItem().get(0).getProductStockReserved().getId())
                .isEqualTo(DEFAULT_PRODUCT_STOCK_RESERVED);
    }

    @Test
    @DisplayName("Given exception on reserve, " +
            "when reserveProductStocksBatch is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionOnReserveProductStocksError() {
        // Given
        Map<String, ProductStock> productOrderItemProductStockMap = Collections.singletonMap("itemId", new ProductStock());
        when(discoServiceUrl.getProductStockManagementUrl()).thenReturn(DEFAULT_PRODUCT_STOCK_URL);
        mockPostWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When & Then
        assertThrows(DiscoException.class, () -> productStockManagementService.reserveProductStocks(productOrderItemProductStockMap));
    }

    @Test
    @DisplayName("Given empty reserved stock map, " +
            "when getReservedProductStocks is called, " +
            "then return empty map")
    void shouldReturnEmptyMapForEmptyReservedProductStockMap() {
        // Given
        Map<String, String> emptyMap = Collections.emptyMap();

        // When
        Map<String, ProductStock> result = productStockManagementService.getReservedProductStocks(emptyMap);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given valid reserved stock map, " +
            "when getReservedProductStocks is called, " +
            "then return reserved stocks map")
    void shouldReturnReservedProductStocksMapForValidInput() {
        // Given
        ProductStock expectedProductStock = createExpectedProductStock();
        when(discoServiceUrl.getProductStockManagementUrl()).thenReturn(DEFAULT_PRODUCT_STOCK_URL);
        mockWebClientResponseList(HttpStatus.OK, Collections.singletonList(expectedProductStock));
        Map<String, String> productOrderItemProductStockReservedMap = Collections.singletonMap("orderId", VALID_RESOURCE_ID);

        // When
        Map<String, ProductStock> reservedStocks = productStockManagementService.getReservedProductStocks(productOrderItemProductStockReservedMap);

        // Then
        assertThat(reservedStocks).containsEntry("orderId", expectedProductStock);
    }


    private void mockWebClientResponseList(HttpStatus status, List<ProductStock> responseBody) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful()) {
            ResponseEntity<List<ProductStock>> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<List<ProductStock>>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntityList(ProductStock.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(ProductStock.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString(), any(Function.class));
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }

    private void mockPostWebClientResponse(HttpStatus status, ProductStock responseBody) {
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        ResponseEntity<ProductStock> responseEntity = new ResponseEntity<>(responseBody, status);
        Mono<ResponseEntity<ProductStock>> responseEntityMonoMock = Mono.just(responseEntity);

        if (status.is2xxSuccessful()) {
            doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(ProductStock.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(ProductStock.class);
        }

        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);

        doReturn(requestBodyUriSpecMock).when(webClient).post();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).contentType(any());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
    }

    private ProductStock createExpectedProductStock() {
        ProductStock productStock = new ProductStock();
        productStock.setId(VALID_RESOURCE_ID);
        ReserveProductStockItem reserveProductStockItem = new ReserveProductStockItem();
        ProductStockReserved productStockReserved = new ProductStockReserved();
        productStockReserved.setId(DEFAULT_PRODUCT_STOCK_RESERVED);
        reserveProductStockItem.setProductStockReserved(productStockReserved);
        productStock.setReserveProductStockItem(Collections.singletonList(reserveProductStockItem));
        return productStock;
    }
}
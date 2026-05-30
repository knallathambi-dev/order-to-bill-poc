// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInventoryServiceImplTest {
    private static final String VALID_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String ORDER_INVENTORY_URL = "http://localhost:8082/productOrderingManagement/v1/productOrder/";

    @InjectMocks
    private OrderInventoryServiceImpl orderInventoryService;

    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Mock
    private WebClient webClient;

    @Test
    @DisplayName("Given a valid product order, " +
            "when createProductOrder is called, " +
            "then should return the created product order")
    void shouldReturnProductOrderForValidInputWhenCreateProductOrder() {
        // Given
        when(discoServiceUrl.getOrderInventoryUrl()).thenReturn(ORDER_INVENTORY_URL);
        ProductOrder validProductOrder = createProductOrder();
        mockPostWebClientResponse(HttpStatus.OK, validProductOrder);

        // When
        ProductOrder productOrder = orderInventoryService.createProductOrder(validProductOrder);

        // Then
        assertNotNull(productOrder);
        assertEquals(validProductOrder.getId(), productOrder.getId());
    }

    @Test
    @DisplayName("Given a product order with an error response from WebClient, " +
            "when createProductOrder is called, " +
            "then should throw DiscoException")
    void shouldThrowDiscoExceptionForWebClientErrorWhenCreateProductOrder() {
        // Given
        when(discoServiceUrl.getOrderInventoryUrl()).thenReturn(ORDER_INVENTORY_URL);
        ProductOrder validProductOrder = createProductOrder();
        mockPostWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, validProductOrder);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> orderInventoryService.createProductOrder(validProductOrder));
        assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, exception.getReason());
    }

    @Test
    @DisplayName("Given a null product order ID, " +
            "when getProductOrderById is called, " +
            "then should throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullProductOrderIdWhenGetProductOrderById() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> orderInventoryService.getProductOrderById(null));
    }

    @Test
    @DisplayName("Given an empty product order ID, " +
            "when getProductOrderById is called, " +
            "then should throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForEmptyProductOrderIdWhenGetProductOrderById() {
        // Given
        String productOrderId = "";

        // When & Then
        assertThrows(InvalidParameterException.class, () -> orderInventoryService.getProductOrderById(productOrderId));
    }

    @Test
    @DisplayName("Given an invalid product order URL, " +
            "when getProductOrderById is called, " +
            "then should throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForInvalidProductOrderUrlWhenGetProductOrderById() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> orderInventoryService.getProductOrderById(null));
    }

    @Test
    @DisplayName("Given a product order URL that returns NOT_FOUND, " +
            "when getProductOrderById is called, " +
            "then should throw DiscoException with SELECTED_PRODUCT_ORDER_DOES_NOT_EXIST reason")
    void shouldThrowDiscoExceptionWithProductOrderNotFoundReasonForNotFoundResponseWhenGetProductOrderById() {
        // Given
        when(discoServiceUrl.getProductOrderInventoryByIdUrl(VALID_PRODUCT_ORDER_ID)).thenReturn(ORDER_INVENTORY_URL + VALID_PRODUCT_ORDER_ID);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> orderInventoryService.getProductOrderById(VALID_PRODUCT_ORDER_ID));
        assertEquals(DescriptionConstants.SELECTED_PRODUCT_ORDER_DOES_NOT_EXIST, exception.getReason());
    }

    @Test
    @DisplayName("Given a server error, " +
            "when getProductOrderById is called, " +
            "then should throw DiscoException with INTERNAL_SERVER_ERROR reason")
    void shouldThrowDiscoExceptionWithCatalogServiceUnreachableReasonForServerErrorWhenGetProductOrderById() {
        // Given
        when(discoServiceUrl.getProductOrderInventoryByIdUrl(VALID_PRODUCT_ORDER_ID)).thenReturn(ORDER_INVENTORY_URL + VALID_PRODUCT_ORDER_ID);
        mockWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> orderInventoryService.getProductOrderById(VALID_PRODUCT_ORDER_ID));
        assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, exception.getReason());
    }

    @Test
    @DisplayName("Given a bad request response, " +
            "when getProductOrderById is called, " +
            "then should throw DiscoException with INTERNAL_SERVER_ERROR reason")
    void shouldThrowDiscoExceptionWithBadRequestReasonForBadRequestWhenGetProductOrderById() {
        // Given
        when(discoServiceUrl.getProductOrderInventoryByIdUrl(VALID_PRODUCT_ORDER_ID)).thenReturn(ORDER_INVENTORY_URL + VALID_PRODUCT_ORDER_ID);
        mockWebClientResponse(HttpStatus.BAD_REQUEST, null);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> orderInventoryService.getProductOrderById(VALID_PRODUCT_ORDER_ID));
        assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, exception.getReason());
    }

    @Test
    @DisplayName("Given a valid product order ID, " +
            "when getProductOrderById is called, " +
            "then should return product order")
    void shouldReturnProductOrderForValidIdWhenGetProductOrderById() {
        // Given
        ProductOrder expectedOrder = createProductOrder();
        when(discoServiceUrl.getProductOrderInventoryByIdUrl(VALID_PRODUCT_ORDER_ID)).thenReturn(ORDER_INVENTORY_URL + VALID_PRODUCT_ORDER_ID);
        mockWebClientResponse(HttpStatus.OK, expectedOrder);

        // When
        Optional<ProductOrder> productOrder = orderInventoryService.getProductOrderById(VALID_PRODUCT_ORDER_ID);

        // Then
        assertTrue(productOrder.isPresent());
        assertEquals(expectedOrder.getId(), productOrder.get().getId());
    }

    private ProductOrder createProductOrder() {
        return ProductOrder.builder()
                .id("order123")
                .href("sampleHref")
                .creationDate(Instant.now())
                .state(ProductOrderStateType.DRAFT)
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, ProductOrder responseBody) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful()) {
            ResponseEntity<ProductOrder> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<ProductOrder>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(ProductOrder.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(ProductOrder.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }

    void mockPostWebClientResponse(HttpStatus status, ProductOrder responseBody) {
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        ResponseEntity<ProductOrder> responseEntity = new ResponseEntity<>(responseBody, status);
        Mono<ResponseEntity<ProductOrder>> responseEntityMonoMock = Mono.just(responseEntity);

        if (status.is2xxSuccessful()) {
            doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(ProductOrder.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );

            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(ProductOrder.class);
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
}
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.ordercapture.service.util.impl.AcquisitionProductOfferingValidator;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOfferingServiceImplTest {

    private static final String VALID_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(9);
    private static final String INVALID_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(9);
    private static final String PRODUCT_OFFERING_BASE_URL = "http://localhost:8082/productOffering/";
    private static final String NOT_CONTRACT_TYPE = "NotContractType";
    private static final String NOT_LAUNCHED = "NotLaunched";
    private static final String LAUNCHED = "launched";
    private static final String CONTRACT = "Contract";

    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private ProductOfferingServiceImpl productOfferingService;

    @Test
    @DisplayName("Given a null product offering ID, " +
            "when getProductOfferingById is called, " +
            "then should throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullProductOfferingIdWhenGetProductOfferingById() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> productOfferingService.getProductOfferingById(null));
    }

    @Test
    @DisplayName("Given a non-existent product offering, " +
            "when getProductOfferingById is called, " +
            "then should throw DiscoException with SELECTED_OFFER_NOT_VALID reason")
    void shouldThrowDiscoExceptionWithSelectedOfferNotValidReasonForNonExistentProductOfferingWhenGetProductOfferingById() {
        // Given
        when(discoServiceUrl.getProductOfferingByIdUrl(VALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + VALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> productOfferingService.getProductOfferingById(VALID_PRODUCT_OFFERING_ID));
        assertEquals(DescriptionConstants.SELECTED_OFFER_NOT_VALID, exception.getReason());
    }

    @Test
    @DisplayName("Given a server error, " +
            "when getProductOfferingById is called, " +
            "then should throw DiscoException with CATALOG_SERVICE_UNREACHABLE reason")
    void shouldThrowDiscoExceptionWithCatalogServiceUnreachableReasonForServerErrorWhenGetProductOfferingById() {
        // Given
        when(discoServiceUrl.getProductOfferingByIdUrl(VALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + VALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> productOfferingService.getProductOfferingById(VALID_PRODUCT_OFFERING_ID));
        assertEquals(DescriptionConstants.CATALOG_SERVICE_UNREACHABLE, exception.getReason());
    }

    @Test
    @DisplayName("Given a valid product offering, " +
            "when getProductOfferingById is called, " +
            "then should return the product offering")
    void shouldReturnProductOfferingForValidProductOfferingIdWhenGetProductOfferingById() {
        // Given
        ProductOffering productOffering = ProductOffering.builder().type(CONTRACT).lifecycleStatus(LAUNCHED).build();
        when(discoServiceUrl.getProductOfferingByIdUrl(VALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + VALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.OK, productOffering);

        // When
        ProductOffering result = productOfferingService.getProductOfferingById(VALID_PRODUCT_OFFERING_ID);

        // Then
        assertEquals(productOffering, result);
    }

    @Test
    @DisplayName("Given a null product offering ID, " +
            "when isProductOfferingExist is called, " +
            "then should throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullProductOfferingIdWhenIsProductOfferingExist() {
        // Given & When & Then
        Executable invocation = () -> productOfferingService.isProductOfferingExist(null, new AcquisitionProductOfferingValidator());
        assertThrows(InvalidParameterException.class, invocation);
    }

    @Test
    @DisplayName("Given a non-existent product offering, " +
            "when isProductOfferingExist is called, " +
            "then should throw DiscoException with SELECTED_OFFER_NOT_VALID reason")
    void shouldThrowDiscoExceptionWithSelectedOfferNotValidReasonForNonExistentProductOfferingWhenIsProductOfferingExist() {
        // Given
        when(discoServiceUrl.getProductOfferingByIdUrl(VALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + VALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        Executable invocation = () -> productOfferingService.isProductOfferingExist(VALID_PRODUCT_OFFERING_ID, new AcquisitionProductOfferingValidator());
        DiscoException exception = assertThrows(DiscoException.class, invocation);
        assertEquals(DescriptionConstants.SELECTED_OFFER_NOT_VALID, exception.getReason());
    }

    @Test
    @DisplayName("Given a server error, " +
            "when isProductOfferingExist is called, " +
            "then should throw DiscoException with CATALOG_SERVICE_UNREACHABLE reason")
    void shouldThrowDiscoExceptionWithCatalogServiceUnreachableReasonForServerErrorWhenIsProductOfferingExist() {
        // Given
        when(discoServiceUrl.getProductOfferingByIdUrl(VALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + VALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When & Then
        Executable invocation = () -> productOfferingService.isProductOfferingExist(VALID_PRODUCT_OFFERING_ID, new AcquisitionProductOfferingValidator());
        DiscoException exception = assertThrows(DiscoException.class, invocation);
        assertEquals(DescriptionConstants.CATALOG_SERVICE_UNREACHABLE, exception.getReason());
    }

    @Test
    @DisplayName("Given a valid product offering with contract type and launched status, " +
            "when isProductOfferingExist is called, " +
            "then should return true")
    void shouldReturnTrueForValidProductOfferingWhenIsProductOfferingExist() {
        // Given
        ProductOffering contractProductOffering = ProductOffering.builder().type(CONTRACT).lifecycleStatus(LAUNCHED).build();
        when(discoServiceUrl.getProductOfferingByIdUrl(VALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + VALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.OK, contractProductOffering);

        // When
        var result = productOfferingService.isProductOfferingExist(VALID_PRODUCT_OFFERING_ID, new AcquisitionProductOfferingValidator());

        // Then
        assertEquals(true, result.getResult());
    }

    @Test
    @DisplayName("Given a product offering with non-contract type, " +
            "when isProductOfferingExist is called, " +
            "then should return false with SELECTED_OFFER_NOT_VALID description")
    void shouldReturnFalseWithSelectedOfferNotValidDescriptionForNonContractProductOfferingWhenIsProductOfferingExist() {
        // Given
        ProductOffering notContractProductOffering = ProductOffering.builder().type(NOT_CONTRACT_TYPE).build();
        when(discoServiceUrl.getProductOfferingByIdUrl(INVALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + INVALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.OK, notContractProductOffering);

        // When
        var result = productOfferingService.isProductOfferingExist(INVALID_PRODUCT_OFFERING_ID, new AcquisitionProductOfferingValidator());

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.SELECTED_OFFER_NOT_VALID, result.getDescription());
    }

    @Test
    @DisplayName("Given a product offering with non-launched status, " +
            "when isProductOfferingExist is called, " +
            "then should return false")
    void shouldReturnFalseForProductOfferingWithNonLaunchedStatusWhenIsProductOfferingExist() {
        // Given
        ProductOffering notLaunchedProductOffering = ProductOffering.builder().lifecycleStatus(NOT_LAUNCHED).build();
        when(discoServiceUrl.getProductOfferingByIdUrl(INVALID_PRODUCT_OFFERING_ID)).thenReturn(PRODUCT_OFFERING_BASE_URL + INVALID_PRODUCT_OFFERING_ID);
        mockWebClientResponse(HttpStatus.OK, notLaunchedProductOffering);

        // When
        var result = productOfferingService.isProductOfferingExist(INVALID_PRODUCT_OFFERING_ID, new AcquisitionProductOfferingValidator());

        // Then
        assertEquals(false, result.getResult());
    }

    private void mockWebClientResponse(HttpStatus status, ProductOffering responseBody) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful()) {
            ResponseEntity<ProductOffering> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<ProductOffering>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(ProductOffering.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(ProductOffering.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }
}
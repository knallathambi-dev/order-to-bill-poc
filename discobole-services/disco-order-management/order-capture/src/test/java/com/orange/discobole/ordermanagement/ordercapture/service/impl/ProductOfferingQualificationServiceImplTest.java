// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.*;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOfferingQualificationServiceImplTest {
    private static final String PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_OFFERING_QUALIFICATION_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_OFFERING_QUALIFICATION_ITEM_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_OFFERING_QUALIFICATION_URL = "http://localhost:8082/productOfferingQualification";
    private static final String QUALIFIED = "Qualified";
    private static final String UNQUALIFIED = "UnQualified";
    private static final String CUSTOMER = "customer";
    private static final String RELATED_PARTY = "RelatedParty";
    private static final String RELATED_PARTY_ID = "relatedPartyId";
    private static final String RELATED_PARTY_REFERRED_TYPE = "individual";

    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private ProductOfferingQualificationServiceImpl productOfferingQualificationService;

    @Test
    @DisplayName("Given a null product offering qualification, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalse")
    void shouldReturnFalseForNullProductOfferingQualification() {
        // Given & When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(null);

        // Then
        assertEquals(false, result.getResult());
    }

    @Test
    @DisplayName("Given an invalid product offering qualification URL, " +
            "when isProductOfferingQualified is called, " +
            "then shouldThrowIllegalArgumentException")
    void shouldThrowIllegalArgumentExceptionForInvalidProductOfferingUrl() {
        // Given
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenThrow(IllegalArgumentException.class);
        ProductOfferingQualification productOfferingQualification = createProductOfferingQualification();

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> productOfferingQualificationService.isProductOfferingQualified(productOfferingQualification));
    }

    @Test
    @DisplayName("Given a not found product offering qualification URL, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithQualificationServiceUnreachableDescription")
    void shouldReturnFalseWithQualificationServiceUnreachableDescriptionForNotFoundProductOfferingQualificationUrl() {
        // Given
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);
        ProductOfferingQualification productOfferingQualification = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualification);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.QUALIFICATION_SERVICE_UNREACHABLE, result.getDescription());
    }

    @Test
    @DisplayName("Given a server error occurs, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithQualificationServiceUnreachableDescription")
    void shouldReturnFalseWithQualificationServiceUnreachableDescriptionForServerError() {
        // Given
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);
        ProductOfferingQualification productOfferingQualification = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualification);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.QUALIFICATION_SERVICE_UNREACHABLE, result.getDescription());
    }

    @Test
    @DisplayName("Given a product offering qualification is not found, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalse")
    void shouldReturnFalseForProductOfferingNotFound() {
        // Given
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.NO_CONTENT, null);
        ProductOfferingQualification productOfferingQualification = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualification);

        // Then
        assertEquals(false, result.getResult());
    }

    @Test
    @DisplayName("Given a null request body, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithConfiguredOfferUnqualifiedDescription")
    void shouldReturnFalseWithConfiguredOfferUnqualifiedDescriptionForNullRequestBody() {
        // Given & When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(null);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.CONFIGURED_OFFER_UNQUALIFIED, result.getDescription());
    }

    @Test
    @DisplayName("Given a null qualification result, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithConfiguredOfferUnqualifiedDescription")
    void shouldReturnFalseWithConfiguredOfferUnqualifiedDescriptionForNullQualificationResult() {
        // Given
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.OK, null);
        ProductOfferingQualification productOfferingQualification = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualification);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.CONFIGURED_OFFER_UNQUALIFIED, result.getDescription());
    }

    @Test
    @DisplayName("Given an unqualified product offering, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithSelectedOfferUnqualifiedDescription")
    void shouldReturnFalseWithSelectedOfferUnqualifiedDescriptionForUnqualifiedProductOffering() {
        // Given
        ProductOfferingQualification productOfferingQualification = new ProductOfferingQualification();
        productOfferingQualification.setQualificationResult(UNQUALIFIED);
        productOfferingQualification.setState(TaskStateType.DONE);
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.OK, productOfferingQualification);
        ProductOfferingQualification productOfferingQualificationCreate = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualificationCreate);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.SELECTED_OFFER_UNQUALIFIED, result.getDescription());
    }

    @Test
    @DisplayName("Given a product offering with terminatedWithError state, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithConfiguredOfferUnqualifiedDescription")
    void shouldReturnFalseWithConfiguredOfferUnqualifiedDescriptionForProductOfferingWithTerminatedWithErrorState() {
        // Given
        ProductOfferingQualification productOfferingQualification = new ProductOfferingQualification();
        productOfferingQualification.setState(TaskStateType.TERMINATEDWITHERROR);
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.OK, productOfferingQualification);
        ProductOfferingQualification productOfferingQualificationCreate = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualificationCreate);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.QUALIFICATION_NOT_FULFILLED, result.getDescription());
    }

    @Test
    @DisplayName("Given a product offering with inProgress state, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnFalseWithQualificationNotFulfilledDescription")
    void shouldReturnFalseWithQualificationNotFulfilledDescriptionForProductOfferingWithInProgressState() {
        // Given
        ProductOfferingQualification productOfferingQualification = createProductOfferingQualification();
        productOfferingQualification.setState(TaskStateType.INPROGRESS);
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.OK, productOfferingQualification);

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualification);

        // Then
        assertEquals(false, result.getResult());
        assertEquals(DescriptionConstants.QUALIFICATION_NOT_FULFILLED, result.getDescription());
    }

    @Test
    @DisplayName("Given a valid product offering qualification, " +
            "when isProductOfferingQualified is called, " +
            "then shouldReturnTrue")
    void shouldReturnTrueForValidProductOffering() {
        // Given
        ProductOfferingQualification productOfferingQualification = new ProductOfferingQualification();
        productOfferingQualification.setId(PRODUCT_OFFERING_QUALIFICATION_ID);
        productOfferingQualification.setQualificationResult(QUALIFIED);
        productOfferingQualification.setState(TaskStateType.DONE);
        when(discoServiceUrl.getProductOfferingQualificationUrl()).thenReturn(PRODUCT_OFFERING_QUALIFICATION_URL);
        mockWebClientResponse(HttpStatus.OK, productOfferingQualification);
        ProductOfferingQualification productOfferingQualificationCreate = createProductOfferingQualification();

        // When
        ResponseResult result = productOfferingQualificationService.isProductOfferingQualified(productOfferingQualificationCreate);

        // Then
        assertEquals(true, result.getResult());
    }

    private ProductOfferingQualification createProductOfferingQualification() {
        RelatedParty relatedParty = RelatedParty.builder()
                .id(RELATED_PARTY_ID)
                .role(CUSTOMER)
                .type(RELATED_PARTY)
                .referredType(RELATED_PARTY_REFERRED_TYPE)
                .build();

        ProductOfferingRef productOffering = ProductOfferingRef.builder()
                .id(PRODUCT_OFFERING_ID)
                .build();

        ProductOfferingQualificationItem productOfferingQualificationItem = ProductOfferingQualificationItem.builder()
                .id(PRODUCT_OFFERING_QUALIFICATION_ITEM_ID)
                .productOffering(productOffering)
                .build();

        return ProductOfferingQualification.builder()
                .description("check offer eligibility for acquisition")
                .relatedParties(Collections.singletonList(relatedParty))
                .productOfferingQualificationItems(Collections.singletonList(productOfferingQualificationItem))
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, ProductOfferingQualification responseBody) {
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful()) {
            ResponseEntity<ProductOfferingQualification> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<ProductOfferingQualification>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(ProductOfferingQualification.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(ProductOfferingQualification.class);
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
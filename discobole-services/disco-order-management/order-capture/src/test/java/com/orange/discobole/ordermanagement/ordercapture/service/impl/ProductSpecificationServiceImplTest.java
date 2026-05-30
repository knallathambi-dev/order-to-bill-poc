// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecCharacteristic;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.RelatedResource;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.mongodb.assertions.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationServiceImplTest {
    public static final String VALID_PRODUCT_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(9);
    public static final String DEFAULT_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_MOBILE_LINE = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_RESOURCES_ID = "SOM3";
    public static final String DEFAULT_RELATED_RESOURCES_NAME = "SOM Application";
    public static final String DEFAULT_RELATED_RESOURCES_ROLE = "Service delivery";
    private static final String VALID_PRODUCT_SPECIFICATION_URL = "http://localhost:8082/productSpecification";
    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private ProductSpecificationServiceImpl productSpecificationService;

    @DisplayName("Given empty product specification Id list, " +
            "when fetching product specifications, " +
            "then throw InvalidParameterException")
    @Test
    void shouldThrowInvalidParameterExceptionForEmptyProductSpecificationIdList() {
        // Given
        List<String> specificationIds = Collections.emptyList();

        // When
        // Then
        assertThrows(InvalidParameterException.class, () -> productSpecificationService.fetchProductSpecifications(specificationIds));
    }

    @DisplayName("Given valid product specification ID list, when fetch product specifications, then return product specifications")
    @Test
    void shouldReturnProductSpecificationsForValidIdList() {
        // Given
        List<String> specificationIds = List.of(VALID_PRODUCT_SPECIFICATION_ID);

        // When
        when(discoServiceUrl.getProductSpecificationUrl()).thenReturn(VALID_PRODUCT_SPECIFICATION_URL);
        ProductSpecification productSpecification = createProductSpecification();
        mockWebClientResponse(HttpStatus.OK, List.of(productSpecification), VALID_PRODUCT_SPECIFICATION_ID);
        List<ProductSpecification> result = productSpecificationService.fetchProductSpecifications(specificationIds);

        // Then
        assertFalse(CollectionUtils.isEmpty(result));
    }

    private ProductSpecification createProductSpecification() {
        return ProductSpecification.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_MOBILE_LINE)
                .productSpecCharacteristic(List.of(ProductSpecCharacteristic.builder()
                        .id("123")
                        .name("ICCID")
                        .build()))
                .relatedResource(List.of(createRelatedResources()))
                .build();
    }

    private RelatedResource createRelatedResources() {
        return RelatedResource.builder()
                .id(DEFAULT_RELATED_RESOURCES_ID)
                .name(DEFAULT_RELATED_RESOURCES_NAME)
                .role(DEFAULT_RELATED_RESOURCES_ROLE)
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, Object responseBody, String... uriVariables) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        if (status.is2xxSuccessful() && responseBody != null) {
            if (responseBody instanceof List) {
                @SuppressWarnings("unchecked")
                List<ProductSpecification> productSpecList = (List<ProductSpecification>) responseBody;
                ResponseEntity<List<ProductSpecification>> responseEntity = new ResponseEntity<>(productSpecList, status);
                Mono<ResponseEntity<List<ProductSpecification>>> responseMono = Mono.just(responseEntity);
                doReturn(responseMono).when(responseSpecMock).toEntityList(ProductSpecification.class);
            } else if (responseBody instanceof ProductSpecification) {
                ResponseEntity<ProductSpecification> responseEntity = new ResponseEntity<>((ProductSpecification) responseBody, status);
                Mono<ResponseEntity<ProductSpecification>> responseMono = Mono.just(responseEntity);
                doReturn(responseMono).when(responseSpecMock).toEntity(ProductSpecification.class);
            }
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            if (responseBody instanceof List) {
                doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(ProductSpecification.class);
            } else {
                doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(ProductSpecification.class);
            }
        }
        doReturn(uriSpecMock).when(webClient).get();
        if (uriVariables.length > 0) {
            doReturn(headersSpecMock).when(uriSpecMock).uri(anyString(), ArgumentMatchers.<Function<UriBuilder, URI>>any());
        } else {
            doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        }
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }
}
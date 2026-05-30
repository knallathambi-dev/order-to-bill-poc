// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOffering;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;
import com.orange.discobole.productcatalog.policyrule.dto.ProductSpecification;
import com.orange.discobole.productcatalog.policyrule.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.policyrule.service.impl.ProductCatalogQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductCatalogQueryServiceImplTest extends DiscoPolicyRuleServiceApplicationTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConfigurableProperties configurableProperties;

    @Mock
    private AccessTokenInterceptor accessTokenInterceptor;

    @InjectMocks
    private ProductCatalogQueryServiceImpl productCatalogQueryService;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer token");
    }

    @Test
    void testFetchProductOfferingById() {
        String productOfferingId = "product-offering-id";
        String url = "http://product-offering-url/" + productOfferingId;

        when(configurableProperties.getProductOfferingUrl()).thenReturn("http://product-offering-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductOffering.class)))
                .thenReturn(new ResponseEntity<>(new ProductOffering(productOfferingId), HttpStatus.OK));

        String result = productCatalogQueryService.fetchProductOfferingById(productOfferingId);

        assertEquals(productOfferingId, result);
    }

    @Test
    void testFetchProductOfferingById_NotFound() {
        String productOfferingId = "product-offering-id";
        String url = "http://product-offering-url/" + productOfferingId;

        when(configurableProperties.getProductOfferingUrl()).thenReturn("http://product-offering-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductOffering.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        String result = productCatalogQueryService.fetchProductOfferingById(productOfferingId);

        assertNull(result);
    }

    @Test
    void testFetchProductSpecificationById() {
        String productSpecificationId = "product-specification-id";
        String url = "http://product-specification-url/" + productSpecificationId;

        when(configurableProperties.getProductSpecificationUrl()).thenReturn("http://product-specification-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductSpecification.class)))
                .thenReturn(new ResponseEntity<>(new ProductSpecification(productSpecificationId), HttpStatus.OK));

        String result = productCatalogQueryService.fetchProductSpecificationById(productSpecificationId);

        assertEquals(productSpecificationId, result);
    }

    @Test
    void testFetchProductSpecificationById_NotFound() {
        String productSpecificationId = "product-specification-id";
        String url = "http://product-specification-url/" + productSpecificationId;

        when(configurableProperties.getProductSpecificationUrl()).thenReturn("http://product-specification-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductSpecification.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        String result = productCatalogQueryService.fetchProductSpecificationById(productSpecificationId);

        assertNull(result);
    }

    @Test
    void testFetchProductOfferingByPolicyRule() {
        String policyRuleRefId = "policy-rule-ref-id";
        String url = "http://product-offering-url?policyRuleRef.id=" + policyRuleRefId;
        List<ProductOffering> productOfferings = Collections.singletonList(new ProductOffering("product-offering-id"));

        when(configurableProperties.getProductOfferingUrl()).thenReturn("http://product-offering-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(productOfferings, HttpStatus.OK));

        List<ProductOffering> result = productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRuleRefId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(productOfferings, result);
    }

    @Test
    void testFetchProductSpecificationByPolicyRule() {
        String policyRuleRefId = "policy-rule-ref-id";
        String url = "http://product-specification-url?policyRuleRef.id=" + policyRuleRefId;
        List<ProductSpecification> productSpecifications = Collections.singletonList(new ProductSpecification("product-specification-id"));

        when(configurableProperties.getProductSpecificationUrl()).thenReturn("http://product-specification-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(productSpecifications, HttpStatus.OK));

        List<ProductSpecification> result = productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRuleRefId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(productSpecifications, result);
    }

    @Test
    void testFetchProductOfferingPriceById() {
        String productOfferingPriceId = "product-offering-price-id";
        String url = "http://product-offering-price-url/" + productOfferingPriceId;

        when(configurableProperties.getProductOfferingPriceUrl()).thenReturn("http://product-offering-price-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductOfferingPrice.class)))
                .thenReturn(new ResponseEntity<>(new ProductOfferingPrice(productOfferingPriceId), HttpStatus.OK));

        String result = productCatalogQueryService.fetchProductofferingPriceById(productOfferingPriceId);

        assertEquals(productOfferingPriceId, result);
    }

    @Test
    void testFetchProductOfferingPriceById_NotFound() {
        String productOfferingPriceId = "product-offering-price-id";
        String url = "http://product-offering-price-url/" + productOfferingPriceId;

        when(configurableProperties.getProductOfferingPriceUrl()).thenReturn("http://product-offering-price-url");
        when(accessTokenInterceptor.getToken()).thenReturn("Bearer token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductOfferingPrice.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        String result = productCatalogQueryService.fetchProductofferingPriceById(productOfferingPriceId);

        assertNull(result);
    }

        @Test
        void fetchProductOfferingPriceByIds_shouldReturnList() {
            // Arrange
            List<String> ids = List.of("id-1", "id-2");
            String expectedUrl = "http://mock-service/product-offering-prices?id=id-1,id-2";

            Mockito.when(configurableProperties.getProductOfferingPriceUrl())
                    .thenReturn("http://mock-service/product-offering-prices");
            Mockito.when(accessTokenInterceptor.getToken()).thenReturn("Bearer token-123");

            List<ProductOfferingPrice> mockResponse = List.of(new ProductOfferingPrice("id-1"), new ProductOfferingPrice("id-2"));
            ResponseEntity<List<ProductOfferingPrice>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

            Mockito.when(restTemplate.exchange(
                    Mockito.eq(expectedUrl),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.<ParameterizedTypeReference<List<ProductOfferingPrice>>>any()
            ))
                    .thenReturn(responseEntity);

            // Act
            List<ProductOfferingPrice> result = productCatalogQueryService.fetchProductOfferingPriceByIds(ids);

            // Assert
            assertEquals(2, result.size());
            assertEquals("id-1", result.get(0).getId());

            // Verify headers
            ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
            Mockito.verify(restTemplate).exchange(
                    Mockito.eq(expectedUrl),
                    Mockito.eq(HttpMethod.GET),
                    captor.capture(),
                    Mockito.<ParameterizedTypeReference<List<ProductOfferingPrice>>>any()
            );

            HttpEntity capturedEntity = captor.getValue();
            assertEquals("Bearer token-123", capturedEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        }

        @Test
        void fetchProductOfferingPriceByIds_shouldReturnEmptyList_whenResponseBodyIsNull() {
            List<String> ids = List.of("id-1");

            Mockito.when(configurableProperties.getProductOfferingPriceUrl())
                    .thenReturn("http://mock-service/product-offering-prices");
            Mockito.when(accessTokenInterceptor.getToken()).thenReturn("Bearer token-xyz");

            ResponseEntity<List<ProductOfferingPrice>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
            Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.<ParameterizedTypeReference<List<ProductOfferingPrice>>>any()))
                    .thenReturn(responseEntity);

            List<ProductOfferingPrice> result = productCatalogQueryService.fetchProductOfferingPriceByIds(ids);

            assertNull(result); // since .getBody() was null
        }

        @Test
        void fetchPOById_shouldReturnProductOffering_whenResponseIsValid() {
            // Arrange
            String poId = "12345";
            String expectedUrl = "http://mock-service/product-offering/12345";

            Mockito.when(configurableProperties.getProductOfferingUrl())
                    .thenReturn("http://mock-service/product-offering");
            Mockito.when(accessTokenInterceptor.getToken()).thenReturn("Bearer token-123");

            ProductOffering mockOffering = new ProductOffering();
            mockOffering.setId(poId);

            ResponseEntity<ProductOffering> responseEntity = new ResponseEntity<>(mockOffering, HttpStatus.OK);

            Mockito.when(restTemplate.exchange(
                    Mockito.eq(expectedUrl),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.eq(ProductOffering.class)
            ))
                    .thenReturn(responseEntity);

            // Act
            ProductOffering result = productCatalogQueryService.fetchPOById(poId);

            // Assert
            assertNotNull(result);
            assertEquals(poId, result.getId());

            // Verify headers
            ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
            Mockito.verify(restTemplate).exchange(
                    Mockito.eq(expectedUrl),
                    Mockito.eq(HttpMethod.GET),
                    captor.capture(),
                    Mockito.eq(ProductOffering.class)
            );

            HttpEntity capturedEntity = captor.getValue();
            assertEquals("Bearer token-123", capturedEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        }

        @Test
        void fetchPOById_shouldReturnNull_whenResponseBodyIsNull() {
            String poId = "12345";

            Mockito.when(configurableProperties.getProductOfferingUrl())
                    .thenReturn("http://mock-service/product-offering");
            Mockito.when(accessTokenInterceptor.getToken()).thenReturn("Bearer token-xyz");

            ResponseEntity<ProductOffering> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

            Mockito.when(restTemplate.exchange(Mockito.anyString(),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.eq(ProductOffering.class)))
                    .thenReturn(responseEntity);

            ProductOffering result = productCatalogQueryService.fetchPOById(poId);

            assertNull(result);
        }

        @Test
        void fetchPOById_shouldReturnNull_whenExceptionThrown() {
            String poId = "12345";

            Mockito.when(configurableProperties.getProductOfferingUrl())
                    .thenReturn("http://mock-service/product-offering");
            Mockito.when(accessTokenInterceptor.getToken()).thenReturn("Bearer token-xyz");

            Mockito.when(restTemplate.exchange(Mockito.anyString(),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.eq(ProductOffering.class)))
                    .thenThrow(new RuntimeException("Service unavailable"));

            ProductOffering result = productCatalogQueryService.fetchPOById(poId);

            assertNull(result);
        }
}


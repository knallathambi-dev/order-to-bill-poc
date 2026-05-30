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
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productinventory.dto.v1.*;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.mongodb.assertions.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductInventoryServiceImplTest {
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_MANAGEMENT_URL = "http://localhost:8082/productInventoryManagement/v1/product";
    private static final String PRODUCT_REF_TYPE = "ProductRef";
    private static final String VALID_PRODUCT_ID = RandomStringUtils.randomAlphabetic(9);

    @Mock
    private WebClient webClient;
    @InjectMocks
    @Spy
    private ProductInventoryServiceImpl productManagementService;
    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Test
    @DisplayName("Given a valid JSON Patch request, " +
            "when updateProducts is called, " +
            "then return a list of modified products")
    void shouldReturnListOfModifiedProductsForValidJsonPatchRequest() {
        // Given
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        Product product = Product.builder().id(PRODUCT_ID_1).build();
        List<Product> expectedProducts = List.of(product);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(expectedProducts));

        // When
        List<Product> actualProducts = productManagementService.updateProducts("[]");

        // Then
        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    @DisplayName("Given a product order, " +
            "when getProductsIdsByProductOrder is called, " +
            "then return a list of product IDs")
    void shouldReturnListOfProductIdsForSuccessfulRequest() {
        // Given
        ProductOrder productOrder = ProductOrder.builder().id(PRODUCT_ORDER_ID).build();
        Product product1 = Product.builder().id(PRODUCT_ID_1).build();
        List<Product> products = List.of(product1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockGetWebClientResponse(HttpStatus.OK, products, productOrder.getId());

        // When
        List<String> result = productManagementService.getProductsIdsByProductOrder(productOrder, product -> true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(PRODUCT_ID_1));
    }

    @Test
    @DisplayName("Given a contract product id " +
            "and product specification ids" +
            "when getProductsIdsByRelationship is called, " +
            "then return a list of product IDs")
    void shouldReturnListOfProductsForSuccessfulRequest() {
        // Given
        Product product1 = Product.builder().id(PRODUCT_ID_1).build();
        List<Product> products = List.of(product1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockGetWebClientResponse(HttpStatus.OK, products, "123", "Sold");

        // When
        List<Product> result = productManagementService.getProductsByRelationship("123");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PRODUCT_ID_1, result.get(0).getId());
    }

    @Test
    @DisplayName("Given a valid contract product id, " +
            "when getProductsByRelationship is called and no products are found, " +
            "then a DiscoException is thrown")
    void shouldThrowDiscoExceptionWhenGetProductsByRelationshipReturnsEmptyList() {
        // Given
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockGetWebClientResponse(HttpStatus.NOT_FOUND, Collections.emptyList(), "123", "Sold");
        // When & Then
        assertThrows(DiscoException.class, () -> productManagementService.getProductsByRelationship(OrderCaptureConstants.CONTRACT_PRODUCT_ID));
    }

    @Test
    @DisplayName("Given an exception on updating products, " +
            "when cancelProducts is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForUpdateProductsException() {
        // Given
        List<String> productIds = List.of(PRODUCT_ID_1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productManagementService.cancelProducts(productIds));
    }

    @Test
    @DisplayName("Given valid product IDs, " +
            "when cancelProducts is called, " +
            "then products are canceled")
    void shouldCancelProductsSuccessfullyForValidProductIds() {
        // Given
        List<String> productIds = List.of(PRODUCT_ID_1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(Product.builder().id(PRODUCT_ORDER_ID).build())));

        // When
        productManagementService.cancelProducts(productIds);

        // Then
        verify(webClient).patch();
    }

    @Test
    @DisplayName("Given valid product IDs, " +
            "when confirmProducts is called, " +
            "then products are confirmed")
    void shouldConfirmProductsSuccessfullyForValidProductIds() {
        // Given
        List<String> productIds = List.of(PRODUCT_ID_1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(Product.builder().id(PRODUCT_ORDER_ID).build())));

        // When
        productManagementService.confirmProducts(productIds);

        // Then
        verify(webClient).patch();
    }

    @Test
    @DisplayName("Given an error occurs while updating products during confirmation, " +
            "when confirmProducts is called, " +
            "then a DiscoException is thrown")
    void shouldThrowDiscoExceptionWhenUpdateProductsFailsDuringConfirmation() {
        // Given
        List<String> productIds = List.of(PRODUCT_ID_1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When
        assertThrows(DiscoException.class, () -> productManagementService.confirmProducts(productIds));
    }

    @Test
    @DisplayName("Given null product ID, " +
            "when getProductById is called, " +
            "then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullProductId() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> productManagementService.getProductById(null));
    }

    @Test
    @DisplayName("Given a not found request, " +
            "when getProductById is called, " +
            "then throw DiscoException with SELECTED_PRODUCT_DOES_NOT_EXIST")
    void shouldThrowDiscoExceptionForNotFoundRequest() {
        // Given
        when(discoServiceUrl.getProductManagementByIdUrl(PRODUCT_ID_1)).thenReturn(PRODUCT_MANAGEMENT_URL + PRODUCT_ID_1);
        mockGetWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        DiscoException exception = assertThrows(DiscoException.class, () -> productManagementService.getProductById(PRODUCT_ID_1));
        assertEquals(DescriptionConstants.SELECTED_PRODUCT_DOES_NOT_EXIST, exception.getReason());
    }

    @Test
    @DisplayName("Given a valid product ID, " +
            "when getProductById is called, " +
            "then the result is true")
    void shouldReturnTrueResultForValidProductId() {
        // Given
        when(discoServiceUrl.getProductManagementByIdUrl(PRODUCT_ID_1)).thenReturn(PRODUCT_MANAGEMENT_URL + PRODUCT_ID_1);
        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE).build())
                .build();
        mockGetWebClientResponse(HttpStatus.OK, product);

        // When
        Product extractedPproduct = productManagementService.getProductById(PRODUCT_ID_1);

        // Then
        assertNotNull(extractedPproduct);
    }

    @Test
    @DisplayName("Given a valid product, " +
            "when createProducts is called, " +
            "then the created product is returned")
    void shouldReturnCreatedProductForValidProduct() {
        // Given
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        Product product = Product.builder()
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE).build())
                .build();
        Product expectedProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE).build())
                .build();
        mockCreateProductWebClientResponse(ResponseEntity.status(HttpStatus.CREATED).body(expectedProduct));

        // When
        Product actualProduct = productManagementService.createProducts(product);

        // Then
        assertNotNull(actualProduct);
        assertEquals(PRODUCT_ID_1, actualProduct.getId());
    }

    @Test
    @DisplayName("Given a valid product order ID, " +
            "when getProductsByProductOrderId is called, " +
            "then the list of products is returned")
    void shouldReturnProductsForValidProductOrderId() {
        // Given
        Product product = Product.builder()
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE).build())
                .build();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        List<Product> expectedProducts = List.of(product);
        mockGetWebClientResponse(HttpStatus.OK, expectedProducts, PRODUCT_ORDER_ID);

        // When
        List<Product> actualProducts = productManagementService.getProductsByProductOrderId(PRODUCT_ORDER_ID);

        // Then
        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    @DisplayName("Given a bundled product containing only physical physical, " +
            "when terminateProducts is called, " +
            "then products are terminated")
    void shouldTerminateProductsSuccessfullyForValidProducts() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.DELETE)
                .build();
        Product childProduct = Product.builder()
                .id(PRODUCT_ID_2)
                .productOrderItem(List.of(relatedProductOrderItem))
                .status(ProductStatusType.SOLD)
                .build();
        Product parentProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        List<Product> products = List.of(parentProduct, childProduct);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(parentProduct)));

        // When
        productManagementService.terminateProducts(products, products, PRODUCT_ORDER_ID);

        // Then
        verify(webClient).patch();
    }

    @Test
    @DisplayName("Given a valid products with multi-level bundled containing only physical physical, " +
            "when terminateProducts is called, " +
            "then products are terminated")
    void shouldTerminateProductsSuccessfullyForValidProductsWithMultipleLevelBundled() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.DELETE)
                .build();
        Product childProduct = Product.builder()
                .id(PRODUCT_ID_3)
                .productOrderItem(List.of(relatedProductOrderItem))
                .status(ProductStatusType.SOLD)
                .build();
        Product parentLevel2Product = Product.builder()
                .id(PRODUCT_ID_2)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_3)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();

        Product parentLevel1Product = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        List<Product> products = List.of(parentLevel1Product, parentLevel2Product, childProduct);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(parentLevel2Product)));

        // When
        productManagementService.terminateProducts(products, products, PRODUCT_ORDER_ID);

        // Then
        verify(webClient).patch();
    }

    @Test
    @DisplayName("Given a valid products with multi-level bundled  containing empty atomic products, " +
            "when terminateProducts is called, " +
            "then products are terminated")
    void shouldTerminateProductsSuccessfullyForValidProductsWithMultipleLevelBundledAndEmptyAtomicProduct() {
        // Given
        Product parentLevel2Product = Product.builder()
                .id(PRODUCT_ID_2)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_3)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();

        Product parentLevel1Product = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        List<Product> products = List.of(parentLevel1Product, parentLevel2Product);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(parentLevel2Product)));

        // When
        productManagementService.terminateProducts(products, products, PRODUCT_ORDER_ID);

        // Then
        verify(webClient).patch();
    }

    @Test
    @DisplayName("Given a list of products with no bundled products, " +
            "when terminateProducts is called, " +
            "then no products are terminated")
    void shouldNotTerminateProductsWhenNoBundledProducts() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.DELETE)
                .build();
        Product product1 = Product.builder()
                .id("product1")
                .productOrderItem(List.of(relatedProductOrderItem))
                .status(ProductStatusType.SOLD)
                .build();
        Product product2 = Product.builder()
                .id("product2")
                .productOrderItem(List.of(relatedProductOrderItem))
                .status(ProductStatusType.SOLD)
                .build();
        List<Product> products = List.of(product1, product2);

        // When
        productManagementService.terminateProducts(products, products, PRODUCT_ORDER_ID);

        // Then
        verify(webClient, never()).patch();
    }

    @Test
    @DisplayName("Given a bundled product with a child product not in SOLD status, " +
            "when terminateProducts is called, " +
            "then the bundled product is not terminated")
    void shouldNotTerminateBundledProductWhenChildProductNotInSoldStatus() {
        // Given
        Product childProduct = Product.builder()
                .id(PRODUCT_ID_2)
                .status(ProductStatusType.ABORTED)
                .build();
        Product parentProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        List<Product> products = List.of(parentProduct, childProduct);

        // When
        productManagementService.terminateProducts(products, products, PRODUCT_ORDER_ID);

        // Then
        verify(webClient, never()).patch();
    }

    @Test
    @DisplayName("Given an empty list of products, " +
            "when terminateProducts is called, " +
            "then no products are terminated")
    void shouldNotTerminateProductsWhenProductListIsEmpty() {
        // Given
        List<Product> products = Collections.emptyList();

        // When
        productManagementService.terminateProducts(products, Collections.emptyList(), PRODUCT_ORDER_ID);

        // Then
        verify(webClient, never()).patch();
    }

    @Test
    @DisplayName("Given a null list of products, " +
            "when terminateProducts is called, " +
            "then no products are terminated")
    void shouldNotTerminateProductsWhenProductListIsNull() {
        // Given & When
        productManagementService.terminateProducts(null, null, null);

        // Then
        verify(webClient, never()).patch();
    }

    @Test
    @DisplayName("Given an error occurs while updating products during termination, " +
            "when terminateProducts is called, " +
            "then a DiscoException is thrown")
    void shouldThrowDiscoExceptionWhenUpdateProductsFailsDuringTermination() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.DELETE)
                .build();
        Product childProduct = Product.builder()
                .id(PRODUCT_ID_2)
                .productOrderItem(List.of(relatedProductOrderItem))
                .status(ProductStatusType.SOLD)
                .build();
        Product parentProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        List<Product> products = List.of(parentProduct, childProduct);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productManagementService.terminateProducts(products, products, PRODUCT_ORDER_ID));
    }

    @Test
    @DisplayName("Given a null product, " +
            "when createProducts is called, " +
            "then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullProduct() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> productManagementService.createProducts(null));
    }

    @Test
    @DisplayName("Given an error occurs while creating a product, " +
            "when createProducts is called, " +
            "then a DiscoException is thrown")
    void shouldThrowDiscoExceptionWhenCreateProductsFails() {
        // Given
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        Product product = Product.builder()
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE).build())
                .build();
        mockCreateProductWebClientResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productManagementService.createProducts(product));
    }

    @DisplayName("Given empty product  Id list, " +
            "when fetching products , " +
            "then throw InvalidParameterException")
    @Test
    void shouldThrowInvalidParameterExceptionForEmptyProductIdList() {
        // Given
        List<String> productIds = Collections.emptyList();

        // When
        // Then
        assertThrows(InvalidParameterException.class, () -> productManagementService.getProductByIds(productIds));
    }

    @DisplayName("Given valid product  ID list, when fetch product , then return product ")
    @Test
    void shouldReturnProductsForValidIdList() {
        // Given
        List<String> productIds = List.of(VALID_PRODUCT_ID);

        // When
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        Product product = createProduct();
        mockGetWebClientResponse(HttpStatus.OK, List.of(product), VALID_PRODUCT_ID);
        List<Product> result = productManagementService.getProductByIds(productIds);

        // Then
        assertFalse(CollectionUtils.isEmpty(result));
    }

    private Product createProduct() {
        return Product.builder()
                .id(PRODUCT_ID_1)
                .atType("Product")
                .build();
    }
    @Test
    @DisplayName("Given an exception on updating products, " +
            "when abortProducts is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForUpdateProductsExceptionWhenAborting() {
        // Given
        List<String> productIds = List.of(PRODUCT_ID_1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productManagementService.abortProducts(productIds));
    }

    @Test
    @DisplayName("Given valid product IDs, " +
            "when abortProducts is called, " +
            "then products are aborted")
    void shouldAbortProductsSuccessfullyForValidProductIds() {
        // Given
        List<String> productIds = List.of(PRODUCT_ID_1);
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_MANAGEMENT_URL);
        mockPatchWebClientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(Product.builder().id(PRODUCT_ORDER_ID).build())));

        // When
        productManagementService.abortProducts(productIds);

        // Then
        verify(webClient).patch();
    }

    private <T> void mockGetWebClientResponse(HttpStatus status, T responseBody, String... uriVariables) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && responseBody != null) {
            ResponseEntity<T> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<T>> responseMono = Mono.just(responseEntity);

            if (responseBody instanceof List) {
                doReturn(responseMono).when(responseSpecMock).toEntityList(Product.class);
            } else {
                doReturn(responseMono).when(responseSpecMock).toEntity(Product.class);
            }
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            if (responseBody instanceof List) {
                doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(Product.class);
            } else {
                doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(Product.class);
            }
        }

        doReturn(uriSpecMock).when(webClient).get();

        if (uriVariables.length > 0) {
            doReturn(headersSpecMock).when(uriSpecMock).uri(anyString(), any(Function.class));
        } else {
            doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        }

        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }

    private void mockPatchWebClientResponse(ResponseEntity<List<Product>> responseEntity) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<ResponseEntity<List<Product>>> responseEntityMonoMock = Mono.just(responseEntity);
        doReturn(requestBodyUriSpecMock).when(webClient).patch();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).header(any(), anyString());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntityList(Product.class);
    }

    private void mockCreateProductWebClientResponse(ResponseEntity<Product> responseEntity) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<ResponseEntity<Product>> responseEntityMonoMock = Mono.just(responseEntity);
        doReturn(requestBodyUriSpecMock).when(webClient).post();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).contentType(any());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(Product.class);
    }
}
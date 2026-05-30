// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductDateHelper;
import com.orange.discobole.ordermanagement.orderfollowup.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productinventory.dto.v1.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType.ADD;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage.ERROR_UPDATING_PRODUCTS_IN_INVENTORY;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage.PRODUCTS_UPDATE_FAILED_IN_INVENTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductInventoryServiceImplTest {
    public static final String PRODUCT_REF = "ProductRef";
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_6 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_7 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_8 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_INVENTORY_URL = "http://product-inventory/productInventoryManagement/v1/product";
    private static final String PRODUCT_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_10 = RandomStringUtils.randomAlphabetic(10);
    private static final String PATH = "path";
    private static final String VALUE = "value";
    private static final String MONTH = "month";
    private static final float AMOUNT = 3f;
    private static final String PATCH_TO_STRING_VALUE = "patchToStringValue";

    @Mock
    private WebClient webClient;

    @InjectMocks
    @Spy
    private ProductInventoryServiceImpl productInventoryService;

    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Mock
    private ProductDateHelper productDateHelper;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Given a valid product order ID, " +
            "when retrieving products, " +
            "then the products are returned successfully")
    void shouldGetProductsByProductOrderIdWhenSuccessful() {
        // Given
        ProductOrder createdProductToFetch = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .build();
        List<Product> expectedProducts = createProducts();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(createProducts()));

        // When
        List<Product> result = productInventoryService.getProductsByProductOrderById(createdProductToFetch.getId());

        // Then
        assertEquals(expectedProducts.size(), result.size());
        assertEquals(expectedProducts.get(0).getId(), result.get(0).getId());
        assertEquals(expectedProducts.get(1).getId(), result.get(1).getId());
    }

    @Test
    @DisplayName("Given an invalid product order, " +
            "when retrieving products, " +
            "then a DiscoException is thrown")
    void shouldGetProductsByProductOrderIdWhenInvalidProduct() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .build();
        String productOrderId = productOrderDTO.getId();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productInventoryService.getProductsByProductOrderById(productOrderId));
    }

    @Test
    @DisplayName("Given a product order ID, " +
            "when a server error occurs while retrieving products, " +
            "then a DiscoException is thrown")
    void shouldGetProductsByProductOrderIdWhenUnsuccessful() {
        //Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .build();
        String productOrderId = productOrderDTO.getId();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productInventoryService.getProductsByProductOrderById(productOrderId));
    }

    @Test
    @DisplayName("Given a valid patch request, " +
            "when updating products, " +
            "then the products are updated successfully")
    void shouldUpdateProductRequestWhenSuccessful() throws JsonProcessingException {
        // Given
        PatchDTOList statusPatchRequestList = buildStatusPatchRequest();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        List<Product> patchedProducts = List.of(createPatchedProduct());
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(patchedProducts));

        // When
        List<Product> productDTOlist = productInventoryService.updateProducts(statusPatchRequestList.toJsonString());

        // Then
        assertEquals(1, productDTOlist.size());
    }

    @Test
    @DisplayName("Given a null product in the patch request, " +
            "when updating products, " +
            "then a DiscoException is thrown")
    void shouldUpdateProductRequestWhenNullProduct() throws JsonProcessingException {
        // Given
        PatchDTOList statusPatchRequestList = buildStatusPatchRequest();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).build());

        // When & Then
        String jsonPatch = statusPatchRequestList.toJsonString();
        assertThrows(DiscoException.class, () -> productInventoryService.updateProducts(jsonPatch));
    }

    @Test
    @DisplayName("Given a server error, " +
            "when updating products, " +
            "then a DiscoException is thrown")
    void shouldUpdateProductRequestWhenUnsuccessful() throws JsonProcessingException {
        // Given
        PatchDTOList statusPatchRequestList = buildStatusPatchRequest();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When & Then
        String jsonPatch = statusPatchRequestList.toJsonString();
        assertThrows(DiscoException.class, () -> productInventoryService.updateProducts(jsonPatch));
    }

    @Test
    @DisplayName("Given an internal server error on patch, " +
            "when updating products, " +
            "then a DiscoException is thrown")
    void shouldThrowExceptionWhenInternalServerErrorOnUpdatingProducts() throws JsonProcessingException {
        // Given
        PatchDTOList statusPatchRequestList = buildStatusPatchRequest();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);

        WebClientResponseException webClientResponseException = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null, null, null
        );
        configPatchWebclientResponseWithError(webClientResponseException);

        // When
        String jsonPatch = statusPatchRequestList.toJsonString();
        DiscoException exception =
                assertThrows(DiscoException.class, () -> productInventoryService.updateProducts(jsonPatch));

        // Then
        assertEquals(ERROR_UPDATING_PRODUCTS_IN_INVENTORY, exception.getReason());
    }

    @Test
    @DisplayName("Given a bad request error on patch, " +
            "when updating products, " +
            "then a DiscoException is thrown")
    void shouldThrowExceptionWhenBadRequestOnUpdatingProducts() throws JsonProcessingException {
        // Given
        PatchDTOList statusPatchRequestList = buildStatusPatchRequest();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);

        WebClientResponseException webClientResponseException = WebClientResponseException.create(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                null, null, null
        );
        configPatchWebclientResponseWithError(webClientResponseException);

        // When
        String jsonPatch = statusPatchRequestList.toJsonString();
        DiscoException exception =
                assertThrows(DiscoException.class, () -> productInventoryService.updateProducts(jsonPatch));

        // Then
        assertEquals(ERROR_UPDATING_PRODUCTS_IN_INVENTORY, exception.getReason());
    }

    @Test
    @DisplayName("Given a service unavailable error on patch, " +
            "when updating products, " +
            "then a DiscoException is thrown")
    void shouldThrowExceptionWhenServiceUnavailableOnUpdatingProducts() throws JsonProcessingException {
        // Given
        PatchDTOList statusPatchRequestList = buildStatusPatchRequest();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);

        WebClientResponseException webClientResponseException = WebClientResponseException.create(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                null, null, null
        );
        configPatchWebclientResponseWithError(webClientResponseException);

        // When
        String jsonPatch = statusPatchRequestList.toJsonString();
        DiscoException exception =
                assertThrows(DiscoException.class, () -> productInventoryService.updateProducts(jsonPatch));

        // Then
        assertEquals(PRODUCTS_UPDATE_FAILED_IN_INVENTORY, exception.getReason());
    }

    @Test
    @DisplayName("Given a null product ID, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenProductIdIsNull() {
        // Given
        // When
        productInventoryService.updateProductsHierarchy(null, PRODUCT_ITEM_ID_1, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a null product item ID, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenProductItemIdIsNull() {
        // Given & When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, null, ProductOrderStateType.INPROGRESS);

        //Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given an empty product list, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenProductListIsEmpty() {
        // Given
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>()));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_1, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given an invalid product item ID, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenProductItemIdIsInvalid() {
        // Given
        RelatedProductOrderItem productOrderItemDTO = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_2)
                .build();
        Product createdProductToFetch = Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(Collections.singletonList(productOrderItemDTO))
                .build();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(createdProductToFetch)));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_1, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a valid product item ID with no parent product, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenParentProductIsNull() {
        // Given
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItemDTO = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_2)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        Product createdProductToFetch = Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(atomicProductOfferingRef)
                .productOrderItem(Collections.singletonList(productOrderItemDTO))
                .build();

        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(createdProductToFetch)));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_2, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ABORTED, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsAborted() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.ABORTED, ProductStatusType.CREATED),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.ABORTED, ProductOperationalStatusType.CONFIRMED)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ABORTED and SOLD, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsAbortedAndSold() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.ABORTED, ProductStatusType.SOLD),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.ABORTED, ProductOperationalStatusType.SOLD)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        //Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status CREATED, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsCreated() {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.CREATED, ProductStatusType.CREATED),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.CONFIRMED)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status CREATED and SOLD, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsCreatedAndSold() {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.CREATED, ProductStatusType.SOLD),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.SOLD)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsActive() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.ACTIVE, ProductStatusType.ACTIVE),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.ACTIVE)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }


    @Test
    @DisplayName("Given a product with status ACTIVE, " +
            "and the product price has recurring charge, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsActiveAndProductPriceHasRecurringCharge() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.ACTIVE, ProductStatusType.ACTIVE),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.ACTIVE)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));

        PatchDTO patchDTO = PatchDTO.builder()
                .op(ADD)
                .path(PATH)
                .value(VALUE)
                .build();
        when(productDateHelper.updateProductPriceDate(any(), anyString())).thenReturn(Collections.singletonList(patchDTO));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);
        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(2)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and SOLD, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsActiveAndSold() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.ACTIVE, ProductStatusType.SOLD),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.SOLD)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status TERMINATED, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsTerminated() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.ACTIVE, ProductStatusType.TERMINATED, ProductStatusType.TERMINATED),
                List.of(ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.TERMINATED, ProductOperationalStatusType.TERMINATED)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status TERMINATED and the order delivery completed, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsTerminatedAndOrderDeliveryCompleted() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.ACTIVE, ProductStatusType.TERMINATED, ProductStatusType.TERMINATED),
                List.of(ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.TERMINATED, ProductOperationalStatusType.TERMINATED)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status TERMINATED and SOLD, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsTerminatedAndSold() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.ACTIVE, ProductStatusType.TERMINATED, ProductStatusType.SOLD),
                List.of(ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.TERMINATED, ProductOperationalStatusType.SOLD)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given products all having status SOLD, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenAllChildStatusIsSold() throws JsonProcessingException {
        // Given
        List<Product> products = createProducts(
                List.of(ProductStatusType.CREATED, ProductStatusType.SOLD, ProductStatusType.SOLD),
                List.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.SOLD, ProductOperationalStatusType.SOLD)
        );
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and a unique product order, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenStatusIsActiveAndProductOrderIsUnique() {
        // Given
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);

        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(relatedProductOrderItem1)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_1, ProductOrderStateType.INPROGRESS);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and a unique product order, " +
            "and the order delivery completed, " +
            "when updating products hierarchy, " +
            "then no product is updated")
    void shouldNotUpdateProductsHierarchyWhenStatusIsActiveAndProductOrderIsUniqueAndDeliveryCompleted() {
        // Given
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);

        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(relatedProductOrderItem1)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_1, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(0)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and a unique product order, " +
            "and the order delivery completed, " +
            "and the product price has recurring charge, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsActiveAndProductOrderIsUniqueAndDeliveryCompleted() throws JsonProcessingException {
        // Given
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);

        MeasuredValue measuredValue = MeasuredValue.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        ProductPrice productPrice = ProductPrice.builder()
                .recurringChargePeriod(measuredValue)
                .build();

        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(relatedProductOrderItem1)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .productPrice(Collections.singletonList(productPrice))
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        PatchDTO patchDTO = PatchDTO.builder()
                .op(ADD)
                .path(PATH)
                .value(VALUE)
                .build();
        when(productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID)).thenReturn(Collections.singletonList(patchDTO));
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_1, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and a modification product order, " +
            "and the order delivery completed, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsActiveAndModificationOrderAndDeliveryCompleted() throws JsonProcessingException {
        // Given
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);

        ProductRef productRef1 = ProductRef.builder()
                .id(PRODUCT_ID_1)
                .atType(PRODUCT_REF)
                .build();
        ProductRelationship productRelationshipRootProduct = ProductRelationship.builder()
                .relationshipType(ServiceConstants.ROOT_PRODUCT)
                .product(productRef1)
                .build();

        List<ProductRelationship> productRelationship = Collections.singletonList(productRelationshipRootProduct);

        Product product = Product.builder()
                .id(PRODUCT_ID_2)
                .productOrderItem(relatedProductOrderItem1)
                .productRelationship(productRelationship)
                .status(ProductStatusType.ACTIVE)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));

        Product contractProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_TYPE).build())
                .operationalStatus(ProductOperationalStatusType.PENDINGMODIFICATION)
                .build();
        doReturn(contractProduct).when(productInventoryService).getProductById(any());
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_1, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and a modification bundled product order, " +
            "and the order delivery completed, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsActiveAndModificationBundledOrderAndDeliveryCompleted() throws JsonProcessingException {
        // Given
        List<Product> products = createBundleProducts();
        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));
        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);
        Product contractProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_TYPE).build())
                .productOrderItem(relatedProductOrderItem1)
                .operationalStatus(ProductOperationalStatusType.PENDINGMODIFICATION)
                .build();
        doReturn(contractProduct).when(productInventoryService).getProductById(any());
        when(objectMapper.writeValueAsString(any())).thenReturn(PATCH_TO_STRING_VALUE);
        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a valid product ID, " +
            "when getting product by ID, " +
            "then the product is returned successfully")
    void shouldReturnProductWhenProductIdIsValid() {
        // Given
        Product expectedProduct = Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(ProductOfferingRef.builder().atType(ServiceConstants.CONTRACT_TYPE).build())
                .build();
        when(discoServiceUrl.getProductInventoryByIdUrl(PRODUCT_ID_1))
                .thenReturn(PRODUCT_INVENTORY_URL + "/" + PRODUCT_ID_1);
        configGetProductWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(expectedProduct));

        // When
        Product result = productInventoryService.getProductById(PRODUCT_ID_1);

        // Then
        assertEquals(expectedProduct.getId(), result.getId());
    }

    @Test
    @DisplayName("Given a blank product ID, " +
            "when getting product by ID, " +
            "then an InvalidParameterException is thrown")
    void shouldThrowExceptionWhenProductIdIsBlank() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> productInventoryService.getProductById(""));
    }

    @Test
    @DisplayName("Given a non-existent product ID, " +
            "when getting product by ID, " +
            "then a DiscoException is thrown")
    void shouldThrowExceptionWhenProductIdDoesNotExist() {
        // Given
        when(discoServiceUrl.getProductInventoryByIdUrl(PRODUCT_ID_1))
                .thenReturn(PRODUCT_INVENTORY_URL + "/" + PRODUCT_ID_1);
        configGetProductWebclientResponse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        // When & Then
        assertThrows(DiscoException.class, () -> productInventoryService.getProductById(PRODUCT_ID_1));
    }

    @Test
    @DisplayName("Given a product with status ACTIVE and the order delivery completed, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsCompletedAndOrderDeliveryCompletedForMigrationFor() {
        // Given
        List<Product> products = createProductsForMigration();


        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));


        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ITEM_ID_3, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(1)).updateProducts(any());
    }
    @Test
    @DisplayName("Given a product with status ACTIVE and the order delivery completed and has all noChange action, " +
            "when updating products hierarchy, " +
            "then the product is updated")
    void shouldUpdateProductsHierarchyWhenStatusIsCompletedAndOrderDeliveryCompletedForMigrationNoChange() {
        // Given
        List<Product> products = createProductsForMigration();


        when(discoServiceUrl.getProductInventoryUrl()).thenReturn(PRODUCT_INVENTORY_URL);
        configGetListWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(products));


        configPatchWebclientResponse(ResponseEntity.status(HttpStatus.OK).body(List.of(createPatchedProduct())));

        // When
        productInventoryService.updateProductsHierarchy(PRODUCT_ORDER_ID, PRODUCT_ORDER_ITEM_10, ProductOrderStateType.COMPLETED);

        // Then
        verify(productInventoryService, times(2)).updateProducts(any());
    }
    private Product createPatchedProduct() {
        ProductRef productRef2 = ProductRef.builder()
                .id(PRODUCT_ID_2)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef9 = ProductRef.builder()
                .id(PRODUCT_ID_8)
                .atType(PRODUCT_REF)
                .build();
        ProductRelationship productRelationshipDTO1 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef2)
                .build();
        ProductRelationship productRelationship9 = ProductRelationship.builder()
                .relationshipType("migrateFrom")
                .product(productRef9)
                .build();
        List<ProductRelationship> productRelationship1 = List.of(productRelationshipDTO1, productRelationship9);
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction("add")
                .orderItemId(PRODUCT_ITEM_ID_2)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);
        ProductOfferingRef contractProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.CONTRACT_TYPE)
                .build();
        return Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(productRelationship1)
                .productOrderItem(relatedProductOrderItem1)
                .status(ProductStatusType.ACTIVE)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .productOffering(contractProductOfferingRef)
                .build();
    }

    private List<Product> createProductsForMigration() {
        List<Product> products = new ArrayList<>();

        // contract migrate product
        ProductRef productRef2 = ProductRef.builder()
                .id(PRODUCT_ID_2)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef9 = ProductRef.builder()
                .id(PRODUCT_ID_8)
                .atType(PRODUCT_REF)
                .build();
        ProductRelationship productRelationshipDTO1 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef2)
                .build();
        ProductRelationship productRelationship9 = ProductRelationship.builder()
                .relationshipType("migrateFrom")
                .product(productRef9)
                .build();
        List<ProductRelationship> productRelationship1 = List.of(productRelationshipDTO1, productRelationship9);
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction("migrate")
                .orderItemId(PRODUCT_ITEM_ID_2)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);
        ProductOfferingRef contractProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.CONTRACT_TYPE)
                .build();
        Product product1 = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(productRelationship1)
                .productOrderItem(relatedProductOrderItem1)
                .status(ProductStatusType.CREATED)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productOffering(contractProductOfferingRef)
                .build();
        products.add(product1);

        // bundle migrate product
        ProductOfferingRef bundledProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.BUNDLE_PRODUCT_OFFERING)
                .build();
        ProductRef productRef4 = ProductRef.builder()
                .id(PRODUCT_ID_4)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef3 = ProductRef.builder()
                .id(PRODUCT_ID_3)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef5 = ProductRef.builder()
                .id(PRODUCT_ID_5)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef6 = ProductRef.builder()
                .id(PRODUCT_ID_6)
                .atType(PRODUCT_REF)
                .build();
        RelatedProductOrderItem productOrderItem2 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_3)
                .orderItemAction("migrate")
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem2 = Collections.singletonList(productOrderItem2);
        ProductRelationship productRelationship23 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef3)
                .build();
        ProductRelationship productRelationship24 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef4)
                .build();
        ProductRelationship productRelationship25 = ProductRelationship.builder()
                .relationshipType("migrateFrom")
                .product(productRef5)
                .build();
        ProductRelationship productRelationship26 = ProductRelationship.builder()
                .relationshipType("bundlesMigrate")
                .product(productRef6)
                .build();
        List<ProductRelationship> productRelationship2 = new ArrayList<>();
        productRelationship2.add(productRelationship23);
        productRelationship2.add(productRelationship24);
        productRelationship2.add(productRelationship25);
        productRelationship2.add(productRelationship26);
        Product product2 = Product.builder()
                .id(PRODUCT_ID_2)
                .productRelationship(productRelationship2)
                .productOrderItem(relatedProductOrderItem2)
                .status(ProductStatusType.ACTIVE)
                .productOffering(bundledProductOfferingRef)
                .status(ProductStatusType.CREATED)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .build();
        products.add(product2);

        //atomic  migrate product
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItem4 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_5)
                .orderItemAction("migrate")
                .build();

        ProductRef productRef7 = ProductRef.builder()
                .id(PRODUCT_ID_7)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef8 = ProductRef.builder()
                .id(PRODUCT_ID_1)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef11 = ProductRef.builder()
                .id("product11")
                .atType(PRODUCT_REF)
                .build();
        ProductRelationship productRelationship28 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.ROOT_PRODUCT)
                .product(productRef8)
                .build();
        ProductRelationship productRelationship34 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.RELIES_ON)
                .product(productRef11)
                .build();
        ProductRelationship productRelationship38 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.MIGRATE_FROM)
                .product(productRef7)
                .build();
        List<ProductRelationship> productRelationship21 = new ArrayList<>();
        productRelationship21.add(productRelationship28);
        productRelationship21.add(productRelationship34);
        productRelationship21.add(productRelationship38);

        List<RelatedProductOrderItem> relatedProductOrderItem4 = Collections.singletonList(productOrderItem4);
        Product product4 = Product.builder()
                .id(PRODUCT_ID_4)
                .productOrderItem(relatedProductOrderItem4)
                .productRelationship(productRelationship21)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        products.add(product4);

        //atomic noChange product for reliesOn and reliesOnMigrate relationship

        RelatedProductOrderItem productOrderItem5 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ORDER_ITEM_10)
                .orderItemAction("noChange")
                .build();
        ProductRelationship productRelationship31 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.RELIES_ON_MIGRATE)
                .product(productRef4)
                .build();
        ProductRelationship productRelationship32 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.RELIES_ON)
                .product(productRef7)
                .build();
        List<ProductRelationship> productRelationships11 = new ArrayList<>();
        productRelationships11.add(productRelationship28);
        productRelationships11.add(productRelationship31);
        productRelationships11.add(productRelationship32);
        List<RelatedProductOrderItem> relatedProductOrderItem5 = Collections.singletonList(productOrderItem5);
        Product product10 = Product.builder()
                .id(PRODUCT_ID_6)
                .productOrderItem(relatedProductOrderItem5)
                .productRelationship(productRelationships11)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        products.add(product10);

        // atomic noChange product for reliesFrom


        ProductRelationship productRelationship33 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.RELIES_FROM)
                .product(productRef4)
                .build();
        List<ProductRelationship> productRelationships12 = new ArrayList<>();
        productRelationships12.add(productRelationship33);


        Product product11 = Product.builder()
                .id("product11")
                .productOrderItem(relatedProductOrderItem5)
                .productRelationship(productRelationships12)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        products.add(product11);


        //Atomic product
        ProductRelationship productRelationship36 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.RELIES_ON)
                .product(productRef11)
                .build();
        List<ProductRelationship> productRelationship22 = new ArrayList<>();
        productRelationship22.add(productRelationship28);
        productRelationship22.add(productRelationship33);
        productRelationship22.add(productRelationship36);


        Product product7 = Product.builder()
                .id(PRODUCT_ID_7)
                .productOrderItem(relatedProductOrderItem4)
                .productRelationship(productRelationship22)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        products.add(product7);

        // bundle product migrate to


        ProductRelationship productRelationship29 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef7)
                .build();
        ProductRelationship productRelationship35 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef11)
                .build();

        List<ProductRelationship> productRelationships29 = new ArrayList<>();
        productRelationships29.add(productRelationship29);
        productRelationships29.add(productRelationship35);
        Product product5 = Product.builder()
                .id(PRODUCT_ID_5)
                .productRelationship(productRelationships29)
                .productOrderItem(relatedProductOrderItem2)
                .status(ProductStatusType.ACTIVE)
                .productOffering(bundledProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();
        products.add(product5);

        // contract product migrate to

        ProductRef productRef12 = ProductRef.builder()
                .id(PRODUCT_ID_5)
                .atType(PRODUCT_REF)
                .build();
        ProductRelationship productRelationship10 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef12)
                .build();
        List<ProductRelationship> productRelationships10 = List.of(productRelationship10);
        Product product6 = Product.builder()
                .id(PRODUCT_ID_8)
                .productRelationship(productRelationships10)
                .productOrderItem(relatedProductOrderItem1)
                .status(ProductStatusType.ACTIVE)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .productOffering(contractProductOfferingRef)
                .build();
        products.add(product6);


        return products;
    }

    private List<Product> createBundleProducts() {
        // contract root product
        ProductOfferingRef contractProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.CONTRACT_TYPE)
                .build();
        ProductRef productRef1 = ProductRef.builder()
                .id(PRODUCT_ID_1)
                .atType(PRODUCT_REF)
                .build();

        ProductRelationship productRelationshipRootProduct = ProductRelationship.builder()
                .relationshipType(ServiceConstants.ROOT_PRODUCT)
                .product(productRef1)
                .build();

        // bundle product
        ProductRef productRef3 = ProductRef.builder()
                .id(PRODUCT_ID_3)
                .atType(PRODUCT_REF)
                .build();

        RelatedProductOrderItem productOrderItem2 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_2)
                .orderItemAction(ServiceConstants.ADD)
                .build();

        List<RelatedProductOrderItem> relatedProductOrderItem2 = Collections.singletonList(productOrderItem2);

        ProductRelationship productRelationship23 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef3)
                .build();

        List<ProductRelationship> productRelationship2 = new ArrayList<>();
        productRelationship2.add(productRelationship23);
        productRelationship2.add(productRelationshipRootProduct);

        Product product1 = Product.builder()
                .id(PRODUCT_ID_2)
                .productRelationship(productRelationship2)
                .productOrderItem(relatedProductOrderItem2)
                .status(ProductStatusType.CREATED)
                .productOffering(contractProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .build();

        // Atomic Product
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_3)
                .orderItemAction(ServiceConstants.ADD)
                .build();

        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);
        List<ProductRelationship> productRelationship = Collections.singletonList(productRelationshipRootProduct);

        Product product2 = Product.builder()
                .id(PRODUCT_ID_3)
                .productOrderItem(relatedProductOrderItem1)
                .productRelationship(productRelationship)
                .status(ProductStatusType.ACTIVE)
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        return products;
    }

    private List<Product> createProducts() {
        return Arrays.asList(
                Product.builder().id(PRODUCT_ID_1).build(),
                Product.builder().id(PRODUCT_ID_2).build()
        );
    }

    List<Product> createProducts(List<ProductStatusType> status, List<ProductOperationalStatusType> operationalStatus) {
        ProductOfferingRef atomicProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING)
                .build();
        ProductRef productRef1 = ProductRef.builder()
                .id(PRODUCT_ID_1)
                .atType(PRODUCT_REF)
                .build();
        ProductRelationship productRelationshipRootProduct = ProductRelationship.builder()
                .relationshipType(ServiceConstants.ROOT_PRODUCT)
                .product(productRef1)
                .build();

        ProductRef productRef4 = ProductRef.builder()
                .id(PRODUCT_ID_4)
                .atType(PRODUCT_REF)
                .build();
        ProductRef productRef3 = ProductRef.builder()
                .id(PRODUCT_ID_3)
                .atType(PRODUCT_REF)
                .build();

        RelatedProductOrderItem productOrderItem2 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(ServiceConstants.ADD)
                .orderItemId(PRODUCT_ITEM_ID_2)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem2 = Collections.singletonList(productOrderItem2);

        ProductRelationship productRelationship23 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef3)
                .build();
        ProductRelationship productRelationship24 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef4)
                .build();

        List<ProductRelationship> productRelationship2 = new ArrayList<>();
        productRelationship2.add(productRelationship23);
        productRelationship2.add(productRelationship24);
        productRelationship2.add(productRelationshipRootProduct);

        Product product2 = Product.builder()
                .id(PRODUCT_ID_2)
                .productRelationship(productRelationship2)
                .productOrderItem(relatedProductOrderItem2)
                .status(status.get(0))
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(operationalStatus.get(0))
                .build();

        ProductRef productRef2 = ProductRef.builder()
                .id(PRODUCT_ID_2)
                .atType(PRODUCT_REF)
                .build();

        ProductOfferingRef contractProductOfferingRef = ProductOfferingRef.builder()
                .atType(ServiceConstants.CONTRACT_TYPE)
                .build();


        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_2)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem1 = Collections.singletonList(productOrderItem1);

        ProductRelationship productRelationshipDTO1 = ProductRelationship.builder()
                .relationshipType(ServiceConstants.BUNDLES)
                .product(productRef2)
                .build();
        List<ProductRelationship> productRelationship1 = Collections.singletonList(productRelationshipDTO1);

        Product product1 = Product.builder()
                .id(PRODUCT_ID_1)
                .productRelationship(productRelationship1)
                .productOrderItem(relatedProductOrderItem1)
                .status(status.get(0))
                .operationalStatus(operationalStatus.get(0))
                .productOffering(contractProductOfferingRef)
                .build();

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        RelatedProductOrderItem productOrderItem3 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_3)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem3 = Collections.singletonList(productOrderItem3);
        List<ProductRelationship> productRelationship3 = Collections.singletonList(productRelationshipRootProduct);

        Product product3 = Product.builder()
                .id(PRODUCT_ID_3)
                .productOrderItem(relatedProductOrderItem3)
                .productRelationship(productRelationship3)
                .status(status.get(1))
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(operationalStatus.get(1))
                .build();
        products.add(product3);

        RelatedProductOrderItem productOrderItem4 = RelatedProductOrderItem.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemId(PRODUCT_ITEM_ID_4)
                .orderItemAction(ServiceConstants.ADD)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItem4 = Collections.singletonList(productOrderItem4);
        List<ProductRelationship> productRelationship4 = Collections.singletonList(productRelationshipRootProduct);

        Product product4 = Product.builder()
                .id(PRODUCT_ID_4)
                .productOrderItem(relatedProductOrderItem4)
                .productRelationship(productRelationship4)
                .status(status.get(2))
                .productOffering(atomicProductOfferingRef)
                .operationalStatus(operationalStatus.get(2))
                .build();
        products.add(product4);

        return products;
    }

    private PatchDTOList buildStatusPatchRequest() {
        return PatchDTOList.builder()
                .list(Collections.singletonList(PatchDTO.builder()
                        .op(PatchOperationType.REPLACE)
                        .path(ServiceConstants.PRODUCT_INVENTORY_URI.concat(PRODUCT_ID_1 + ServiceConstants.STATUS_URI))
                        .value(ProductStatusType.ACTIVE)
                        .build()))
                .build();
    }

    void configGetListWebclientResponse(ResponseEntity<List<Product>> responseEntity) {
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<ResponseEntity<List<Product>>> responseEntityMonoMock = Mono.just(responseEntity);

        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock)
                .when(requestHeadersUriSpecMock)
                .uri(anyString(), ArgumentMatchers.<Function<UriBuilder, URI>>any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntityList(Product.class);
    }

    void configPatchWebclientResponse(ResponseEntity<List<Product>> responseEntity) {
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

    void configPatchWebclientResponseWithError(WebClientResponseException webClientResponseException) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(requestBodyUriSpecMock).when(webClient).patch();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).header(any(), anyString());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(Mono.error(webClientResponseException)).when(responseSpecMock).toEntityList(Product.class);
    }

    private void configGetProductWebclientResponse(ResponseEntity<Product> responseEntity) {
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        Mono<ResponseEntity<Product>> responseEntityMonoMock = Mono.just(responseEntity);
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(Product.class);
    }
}


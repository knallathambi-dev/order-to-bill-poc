// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl.ProductSpecificationServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ProductSpecificationServiceImpl.class})
@ExtendWith(SpringExtension.class)
@Disabled
class ProductSpecificationServiceImplTest {
    @MockBean
    private DiscoServiceUrl discoServiceUrl;

    @Autowired
    private ProductSpecificationServiceImpl productSpecificationServiceImpl;


    @MockBean
    private WebClientUtil webClientUtil;

    /**
     * Method under test:
     * {@link ProductSpecificationServiceImpl#retrieveProductSpecificationsFromProductOrderItem(Set, String)}
     */
    @Test
    void testRetrieveProductSpecificationsFromProductOrderItem() {
        // Arrange, Act and Assert
        List<ProductOrderItem> productOrderItems = new ArrayList<>();
        assertThrows(CoodRecoverableAndNonRetryableException.class,
                () -> productSpecificationServiceImpl.retrieveProductSpecificationsFromProductOrderItem(productOrderItems, "42"));
    }

    /**
     * Method under test:
     * {@link ProductSpecificationServiceImpl#retrieveProductSpecificationsFromProductOrderItem(Set, String)}
     */
    @Test
    void testRetrieveProductSpecificationsFromProductOrderItem2() {
        // Arrange
        when(discoServiceUrl.getProductSpecificationsByIdsUrl(Mockito.any()))
                .thenReturn("https://example.org/example");
        when(webClientUtil.send(eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<List<ProductSpecification>>() {
        }), any(), any(), any())).thenReturn(List.of());

        List<ProductOrderItem> productOrderItems = new ArrayList<>();
        ProductOrderItem.ProductOrderItemBuilder actionResult = ProductOrderItem.builder()
                .action(ItemActionType.ADD);
        AppointmentRef appointmentRef = AppointmentRef.builder()
                .atBaseType("Base Type")
                .description("The characteristics of someone or something")
                .href("Href")
                .id("42")
                .atReferredType("Referred Type")
                .atSchemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .atType("Type")
                .build();
        ProductOrderItem.ProductOrderItemBuilder baseTypeResult = actionResult.appointment(appointmentRef)
                .atBaseType("Base Type");
        BillingAccountRef billingAccount = BillingAccountRef.builder()
                .atBaseType("Base Type")
                .href("Href")
                .id("42")
                .name("Name")
                .atReferredType("Referred Type")
                .atSchemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .atType("Type")
                .build();
        ProductOrderItem.ProductOrderItemBuilder idResult = baseTypeResult.billingAccount(billingAccount).id("42");
        ProductOrderItem.ProductOrderItemBuilder itemPriceResult = idResult.itemPrice(new ArrayList<>());
        ProductOrderItem.ProductOrderItemBuilder itemTermResult = itemPriceResult.itemTerm(new ArrayList<>());
        ProductOrderItem.ProductOrderItemBuilder paymentResult = itemTermResult.payment(new ArrayList<>());
        Product.ProductBuilder nameResult = Product.builder()
                .atType("Product")
                .description("The characteristics of someone or something")
                .href("Href")
                .id("42")
                .name("Name");
        Product.ProductBuilder orderDateResult = nameResult
                .orderDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Product.ProductBuilder productCharacteristicResult = orderDateResult
                .productCharacteristic(new ArrayList<>());
        Product.ProductBuilder productSerialNumberResult = productCharacteristicResult
                .productRelationship(new ArrayList<>())
                .productSerialNumber("42");
        ProductSpecificationRef productSpecification = ProductSpecificationRef.builder()
                .atBaseType("Base Type")
                .href("Href")
                .id("42")
                .name("Name")
                .atReferredType("Referred Type")
                .atSchemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .atType("Type")
                .build();
        Product.ProductBuilder productSpecificationResult = productSerialNumberResult
                .productSpecification(productSpecification);
        Product.ProductBuilder productTermResult = productSpecificationResult
                .productTerm(new ArrayList<>());
        Product.ProductBuilder realizingResourceResult = productTermResult
                .realizingResource(new ArrayList<>());

        Product.ProductBuilder schemaLocationResult = realizingResourceResult
                .atSchemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri());
        Product.ProductBuilder statusResult = schemaLocationResult
                .startDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())
                .status(ProductStatusType.ABORTED);
        ProductRefOrValue product = statusResult
                .terminationDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())
                .atType("Type")
                .build();
        ProductOrderItem.ProductOrderItemBuilder productResult = paymentResult.product(product);
        ProductOfferingRef productOffering = ProductOfferingRef.builder()
                .atBaseType("Base Type")
                .href("Href")
                .id("42")
                .name("Name")
                .atReferredType("Referred Type")
                .atSchemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .atType("Type")
                .build();
        ProductOrderItem.ProductOrderItemBuilder productOfferingResult = productResult.productOffering(productOffering);
        ProductOrderItem buildResult = productOfferingResult.productOrderItemRelationship(new ArrayList<>())
                .quantity(1)
                .atSchemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .state(ProductOrderItemStateType.ACKNOWLEDGED)
                .atType("Type")
                .build();
        productOrderItems.add(buildResult);

        // Act
        Map<String, ProductSpecification> actualRetrieveProductSpecificationsFromProductOrderItemResult = productSpecificationServiceImpl
                .retrieveProductSpecificationsFromProductOrderItem(productOrderItems, "42");

        // Assert
        verify(discoServiceUrl).getProductSpecificationsByIdsUrl(Mockito.<List<String>>any());
        assertTrue(actualRetrieveProductSpecificationsFromProductOrderItemResult.isEmpty());
    }
}

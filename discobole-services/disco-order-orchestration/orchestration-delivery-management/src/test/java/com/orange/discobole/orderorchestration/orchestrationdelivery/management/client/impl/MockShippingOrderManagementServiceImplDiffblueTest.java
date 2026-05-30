// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MockShippingOrderManagementServiceImpl.class})
@ExtendWith(SpringExtension.class)
class MockShippingOrderManagementServiceImplDiffblueTest {
    @MockBean
    private DiscoServiceUrl discoServiceUrl;

    @MockBean
    private ObjectMapper objectMapper;

    @Autowired
    private MockShippingOrderManagementServiceImpl mockShippingOrderManagementServiceImpl;

    @MockBean
    private WebClientUtil webClientUtil;

    /**
     * Method under test:
     * {@link MockShippingOrderManagementServiceImpl#createShippingOrder(ShippingOrderCreate)}
     */
    @Test
    void testCreateShippingOrder() throws JsonProcessingException {
        // Arrange
        when(discoServiceUrl.getShippingOrderUrl()).thenReturn("https://example.org/example");
        ShippingOrder.ShippingOrderBuilder typeResult = ShippingOrder.builder()
                .baseType("At Base Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type");
        ShippingOrder.ShippingOrderBuilder idResult = typeResult.creationDate(Instant.now())
                .href("Href")
                .id("42");
        ShippingOrder.ShippingOrderBuilder lastUpdateDateResult = idResult
                .lastUpdateDate(Instant.now());
        ShippingOrder.ShippingOrderBuilder noteResult = lastUpdateDateResult.note(new ArrayList<>());
        RelatedPlaceRefOrValue placeFrom = RelatedPlaceRefOrValue.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href("Href")
                .id("42")
                .name("Name")
                .role("Role")
                .build();
        ShippingOrder.ShippingOrderBuilder placeFromResult = noteResult.placeFrom(placeFrom);
        RelatedPlaceRefOrValue placeTo = RelatedPlaceRefOrValue.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href("Href")
                .id("42")
                .name("Name")
                .role("Role")
                .build();
        ShippingOrder.ShippingOrderBuilder placeToResult = placeFromResult.placeTo(placeTo);
        ProductOrderRef productOrder = ProductOrderRef.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42")
                .name("Name")
                .build();
        ShippingOrder.ShippingOrderBuilder productOrderResult = placeToResult.productOrder(productOrder);
        ShippingOrder.ShippingOrderBuilder relatedPartyResult = productOrderResult.relatedParty(new ArrayList<>());
        RelatedShippingOrder relatedShippingOrder = RelatedShippingOrder.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42")
                .name("Name")
                .role("Role")
                .build();
        ShippingOrder.ShippingOrderBuilder relatedShippingOrderResult = relatedPartyResult
                .relatedShippingOrder(relatedShippingOrder);
        ShippingInstruction.ShippingInstructionBuilder deliverySpeedResult = ShippingInstruction.builder()
                .baseType("At Base Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .carrierId("42")
                .carrierName("Carrier Name")
                .carrierServiceCode("Carrier Service Code")
                .deliveryAttempts(1)
                .deliverySpeed("Delivery Speed");
        TimePeriod.TimePeriodBuilder builderResult = TimePeriod.builder();
        TimePeriod.TimePeriodBuilder endDateTimeResult = builderResult.endDateTime(Instant.now());
        TimePeriod deliveryTimeSlot = endDateTimeResult.startDateTime(Instant.now()).build();
        ShippingInstruction.ShippingInstructionBuilder idResult2 = deliverySpeedResult.deliveryTimeSlot(deliveryTimeSlot)
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42");
        ShippingInstruction.ShippingInstructionBuilder instructionCharacteristicResult = idResult2
                .instructionCharacteristic(new ArrayList<>());
        Money insuredValue = Money.builder().unit("Unit").value(10.0f).build();
        ShippingInstruction.ShippingInstructionBuilder labelMessageResult = instructionCharacteristicResult
                .insuredValue(insuredValue)
                .labelMessage("Label Message");
        ShippingInstruction shippingInstruction = labelMessageResult.note(new ArrayList<>())
                .packageType("java.text")
                .receiptConfirmation("Receipt Confirmation")
                .shippingType("Shipping Type")
                .signatureRequired(true)
                .signatureRequiredBy(ShippingInstruction.SignatureRequiredByType.ADULT)
                .warehouseId("42")
                .build();
        ShippingOrder.ShippingOrderBuilder shippingInstructionResult = relatedShippingOrderResult
                .shippingInstruction(shippingInstruction);
        ShippingOrder.ShippingOrderBuilder shippingOrderCharacteristicResult = shippingInstructionResult
                .shippingOrderCharacteristic(new ArrayList<>());
        ShippingOrder.ShippingOrderBuilder shippingOrderItemResult = shippingOrderCharacteristicResult
                .shippingOrderItem(new ArrayList<>());
        ProductOfferingRef shippingOrderOffering = ProductOfferingRef.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42")
                .name("Name")
                .build();
        ShippingOrder.ShippingOrderBuilder shippingOrderOfferingResult = shippingOrderItemResult
                .shippingOrderOffering(shippingOrderOffering);
        ProductPrice.ProductPriceBuilder typeResult2 = ProductPrice.builder()
                .baseType("At Base Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type");
        BillingAccountRef billingAccount = BillingAccountRef.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href("Href")
                .id("42")
                .name("Name")
                .ratingType("Rating Type")
                .build();
        ProductPrice.ProductPriceBuilder nameResult = typeResult2.billingAccount(billingAccount)
                .description("The characteristics of someone or something")
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42")
                .name("Name");
        Price.PriceBuilder typeResult3 = Price.builder()
                .baseType("At Base Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type");
        Money dutyFreeAmount = Money.builder().unit("Unit").value(10.0f).build();
        Price.PriceBuilder percentageResult = typeResult3.dutyFreeAmount(dutyFreeAmount)
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42")
                .percentage(10.0f);
        Money taxIncludedAmount = Money.builder().unit("Unit").value(10.0f).build();
        Price price = percentageResult.taxIncludedAmount(taxIncludedAmount).taxRate(10.0f).build();
        ProductPrice.ProductPriceBuilder priceTypeResult = nameResult.price(price).priceType("Price Type");
        ProductOfferingPriceRef productOfferingPrice = ProductOfferingPriceRef.builder()
                .baseType("At Base Type")
                .referredType("At Referred Type")
                .schemaLocation(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .type("At Type")
                .href(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri())
                .id("42")
                .name("Name")
                .build();
        ProductPrice.ProductPriceBuilder productOfferingPriceResult = priceTypeResult
                .productOfferingPrice(productOfferingPrice);
        ProductPrice shippingOrderPrice = productOfferingPriceResult.productPriceAlteration(new ArrayList<>())
                .recurringChargePeriod("Recurring Charge Period")
                .unitOfMeasure("Unit Of Measure")
                .build();
        ShippingOrder buildResult = shippingOrderOfferingResult.shippingOrderPrice(shippingOrderPrice)
                .status("Status")
                .build();
        when(webClientUtil.send(Mockito.<HttpMethod>any(), Mockito.<String>any(), Mockito.<Class<ShippingOrder>>any(),
                Mockito.<String>any(), any(), any(), any())).thenReturn(buildResult);
        when(objectMapper.writeValueAsString(Mockito.<Object>any())).thenReturn("42");

        // Act
        mockShippingOrderManagementServiceImpl.createShippingOrder(new ShippingOrderCreate());

        // Assert
        verify(objectMapper).writeValueAsString(Mockito.<Object>any());
        verify(discoServiceUrl).getShippingOrderUrl();
        verify(webClientUtil).send(Mockito.<HttpMethod>any(), eq("https://example.org/example"),
                Mockito.<Class<ShippingOrder>>any(), eq("42"), any(), any(), any());
    }
}

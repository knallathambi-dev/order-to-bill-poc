// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator;


import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderCreate;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.RelatedPartyMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ShippingOrderCreator.class})
@ExtendWith(SpringExtension.class)
class ShippingOrderCreatorDiffblueTest {

    @MockBean
    RelatedPartyMapper relatedPartyMapper;

    @MockBean
    CharacteristicMapper characteristicMapper;

    @Autowired
    ShippingOrderCreator shippingOrderCreator;

    @Test
    void testPrepareShippingOrderRequest_happyPath() {
        // Arrange
        OffsetDateTime deliveryDate = OffsetDateTime.parse("2025-01-06T10:22:23.155Z");

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("100")
                .action("add")
                .quantity(1)
                .orderItemCharacteristics(new ArrayList<>(List.of(
                        DateCharacteristic.builder()
                                .name("requested delivery date")
                                .value(deliveryDate)
                                .build()
                )))
                .build();

        List<RelatedParty> relatedParties = List.of(
                RelatedParty.builder().id("42").name("Name").role("Role").build()
        );
        when(relatedPartyMapper.map(anyList())).thenReturn(new ArrayList<>());
        when(characteristicMapper.from(any(Set.class))).thenReturn(new HashSet<>());

        // Act
        ShippingOrderCreate result = shippingOrderCreator.prepareShippingOrderRequest(
                List.of(orderItemRef), relatedParties, "7800");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getShippingOrderItem().size());

        ShippingOrderItem item = result.getShippingOrderItem().get(0);
        assertEquals(ShippingOrderItem.ShippingOrderItemActionType.ADD, item.getAction());
        assertEquals("100", item.getProductOrderItem().getId());
        assertEquals("7800", item.getProductOrderItem().getProductOrderId());
        assertEquals("1", item.getQuantity());
        assertNotNull(item.getShipment());
        assertEquals(deliveryDate.toInstant(), item.getShipment().getRequestedDeliveryDate());

        assertNotNull(result.getShippingOrderCharacteristic());
        verify(relatedPartyMapper).map(anyList());
        verify(characteristicMapper).from(any(Set.class));
    }

    @Test
    void testPrepareShippingOrderRequest_multipleOrderItems() {
        // Arrange
        OffsetDateTime deliveryDate1 = OffsetDateTime.parse("2025-01-06T10:00:00Z");
        OffsetDateTime deliveryDate2 = OffsetDateTime.parse("2025-02-10T14:30:00Z");

        OrderItemRef orderItemRef1 = OrderItemRef.builder()
                .productOrderItemId("100")
                .action("add")
                .quantity(1)
                .orderItemCharacteristics(new ArrayList<>(List.of(
                        DateCharacteristic.builder()
                                .name("requested delivery date")
                                .value(deliveryDate1)
                                .build()
                )))
                .build();

        OrderItemRef orderItemRef2 = OrderItemRef.builder()
                .productOrderItemId("200")
                .action("modify")
                .quantity(2)
                .orderItemCharacteristics(new ArrayList<>(List.of(
                        DateCharacteristic.builder()
                                .name("requested delivery date")
                                .value(deliveryDate2)
                                .build()
                )))
                .build();

        when(relatedPartyMapper.map(anyList())).thenReturn(new ArrayList<>());
        when(characteristicMapper.from(any(Set.class))).thenReturn(new HashSet<>());

        // Act
        ShippingOrderCreate result = shippingOrderCreator.prepareShippingOrderRequest(
                List.of(orderItemRef1, orderItemRef2), new ArrayList<>(), "7800");

        // Assert
        assertEquals(2, result.getShippingOrderItem().size());

        ShippingOrderItem item1 = result.getShippingOrderItem().get(0);
        assertEquals("100", item1.getProductOrderItem().getId());
        assertEquals(ShippingOrderItem.ShippingOrderItemActionType.ADD, item1.getAction());
        assertEquals("1", item1.getQuantity());
        assertEquals(deliveryDate1.toInstant(), item1.getShipment().getRequestedDeliveryDate());

        ShippingOrderItem item2 = result.getShippingOrderItem().get(1);
        assertEquals("200", item2.getProductOrderItem().getId());
        assertEquals(ShippingOrderItem.ShippingOrderItemActionType.MODIFY, item2.getAction());
        assertEquals("2", item2.getQuantity());
        assertEquals(deliveryDate2.toInstant(), item2.getShipment().getRequestedDeliveryDate());
    }

    @Test
    void testPrepareShippingOrderRequest_missingRequestedDeliveryDate() {
        // Arrange: characteristic with wrong name — "requested delivery date" not found
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("100")
                .action("add")
                .quantity(1)
                .orderItemCharacteristics(new ArrayList<>(List.of(
                        DateCharacteristic.builder()
                                .name("some other date")
                                .value(OffsetDateTime.now())
                                .build()
                )))
                .build();

        // Act & Assert
        List<OrderItemRef> orderItemRefs = List.of(orderItemRef);
        ArrayList<RelatedParty> relatedParties = new ArrayList<>();

        assertThrows(CoodRecoverableAndNonRetryableException.class,
                () -> shippingOrderCreator.prepareShippingOrderRequest(
                        orderItemRefs, relatedParties, "7800"));
    }

    @Test
    void testPrepareShippingOrderRequest_emptyCharacteristics() {
        // Arrange: empty characteristics list — no shipment data available
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("100")
                .action("add")
                .quantity(1)
                .orderItemCharacteristics(new ArrayList<>())
                .build();

        // Act & Assert
        List<OrderItemRef> orderItemRefs = List.of(orderItemRef);
        ArrayList<RelatedParty> relatedParties = new ArrayList<>();

        assertThrows(CoodRecoverableAndNonRetryableException.class,
                () -> shippingOrderCreator.prepareShippingOrderRequest(
                        orderItemRefs, relatedParties, "7800"));
    }

    @Test
    void testPrepareShippingOrderRequest_invalidAction() {
        // Arrange: valid characteristic but invalid action type
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("100")
                .action("invalid")
                .quantity(1)
                .orderItemCharacteristics(new ArrayList<>(List.of(
                        DateCharacteristic.builder()
                                .name("requested delivery date")
                                .value(OffsetDateTime.parse("2025-01-06T10:22:23.155Z"))
                                .build()
                )))
                .build();

        // Act & Assert
        List<OrderItemRef> orderItemRefs = List.of(orderItemRef);
        ArrayList<RelatedParty> relatedParties = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> shippingOrderCreator.prepareShippingOrderRequest(
                        orderItemRefs, relatedParties, "7800"));
    }
}

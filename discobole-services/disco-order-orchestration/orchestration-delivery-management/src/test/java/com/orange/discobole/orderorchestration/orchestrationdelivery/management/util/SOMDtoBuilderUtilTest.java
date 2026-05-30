// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.util;


import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.ProductSpecificationRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.ServiceSpecificationRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.util.SOMDtoBuilderUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SOMDtoBuilderUtilTest {

    @Test
    void givenDefaultServiceOrderBuilder_whenBuild_thenReturnsServiceOrderWithDefaultValues() {
        // When
        ServiceOrder serviceOrder = SOMDtoBuilderUtil.getServiceOrderBuilder().build();

        // Then
        assertEquals("4126c7sd0z567z501372l7784", serviceOrder.getId());
        assertNotNull(serviceOrder.getOrderDate());
        assertEquals(ServiceOrder.State.COMPLETED, serviceOrder.getState());
        assertEquals("ServiceOrder", serviceOrder.getType());
        assertNotNull(serviceOrder.getRelatedParty());
        assertEquals(1, serviceOrder.getRelatedParty().size());
        assertNotNull(serviceOrder.getNote());
        assertEquals(1, serviceOrder.getNote().size());
    }

    @Test
    void givenDefaultRelatedPartyBuilder_whenBuild_thenReturnsRelatedPartyWithDefaultValues() {
        // When
        ServiceOrderRelatedParty relatedParty = SOMDtoBuilderUtil.getRelatedPartyBuilder().build();

        // Then
        assertEquals("231-mf4", relatedParty.getId());
        assertEquals("Abir", relatedParty.getName());
        assertEquals("customer", relatedParty.getRole());
        assertEquals("customer", relatedParty.getReferredType());
    }

    @Test
    void givenDefaultNoteBuilder_whenBuild_thenReturnsNoteWithDefaultValues() {
        // When
        Note note = SOMDtoBuilderUtil.getNoteBuilder().build();

        // Then
        assertEquals("1243", note.getId());
        assertEquals("string", note.getText());
        assertEquals("2023-06-13T09:11:59.120Z", note.getDate());
        assertEquals("string", note.getAuthor());
    }

    @Test
    void givenDeliveryOrderWithHeldId_whenGetServiceOrderItemBuilder_thenReturnsServiceOrderItemWithHeldState() {
        // Given
        DeliveryOrder deliveryOrder = mock(DeliveryOrder.class);

        when(deliveryOrder.getFactoryOrderId()).thenReturn("held-123");
        when(deliveryOrder.getOrderItemRef()).thenReturn(List.of(OrderItemRef.builder()
                .orchestrationNodeId("node1")
                .action("add")
                .build()));

        // When
        ServiceOrderItem serviceOrderItem = SOMDtoBuilderUtil.getServiceOrderItemBuilder(deliveryOrder).build();

        // Then
        assertEquals("1243", serviceOrderItem.getId());
        assertEquals(ServiceOrderItem.State.HELD, serviceOrderItem.getState());
        assertNotNull(serviceOrderItem.getErrorMessage());
        assertEquals(1, serviceOrderItem.getErrorMessage().size());
        assertEquals("RESOURCE_UNAVAILABLE", serviceOrderItem.getErrorMessage().get(0).getCode());
    }

    @Test
    void givenDeliveryOrderWithNonHeldId_whenGetServiceOrderItemBuilder_thenReturnsServiceOrderItemWithCompletedState() {
        // Given
        DeliveryOrder deliveryOrder = mock(DeliveryOrder.class);

        when(deliveryOrder.getFactoryOrderId()).thenReturn("complete-123");
        when(deliveryOrder.getOrderItemRef()).thenReturn(List.of(OrderItemRef.builder()
                .orchestrationNodeId("node1")
                .action("add")
                .build()));
        // When
        ServiceOrderItem serviceOrderItem = SOMDtoBuilderUtil.getServiceOrderItemBuilder(deliveryOrder).build();

        // Then
        assertEquals("1243", serviceOrderItem.getId());
        assertEquals(ServiceOrderItem.State.COMPLETED, serviceOrderItem.getState());
        assertNull(serviceOrderItem.getErrorMessage());
    }


    @Test
    void givenDeliveryOrderItemWithValidAction_whenGetActionEnum_thenReturnsCorrespondingAction() {
        // Given
        OrderItemRef orderItemRef = mock(OrderItemRef.class);
        when(orderItemRef.getAction()).thenReturn("add");

        // When
        ServiceOrderItem.Action action = SOMDtoBuilderUtil.getActionEnum(orderItemRef);

        // Then
        assertEquals(ServiceOrderItem.Action.ADD, action);
    }

    @Test
    void givenDeliveryOrderItemWithMigrateValidAction_whenGetActionEnum_thenReturnsModifyAction() {
        // Given
        OrderItemRef orderItemRef = mock(OrderItemRef.class);
        when(orderItemRef.getAction()).thenReturn("migrate");

        // When
        ServiceOrderItem.Action action = SOMDtoBuilderUtil.getActionEnum(orderItemRef);

        // Then
        assertEquals(ServiceOrderItem.Action.MODIFY, action);
    }

    @Test
    void givenDeliveryOrderItemWithNullAction_whenGetActionEnum_thenReturnsNull() {
        // Given
        OrderItemRef orderItemRef = mock(OrderItemRef.class);
        when(orderItemRef.getAction()).thenReturn(null);

        // When
        ServiceOrderItem.Action action = SOMDtoBuilderUtil.getActionEnum(orderItemRef);

        // Then
        assertNull(action);
    }

    @Test
    void givenDeliveryOrderItemWithValidProduct_whenGetServiceBuilder_thenReturnsServiceWithCharacteristics() {
        // Given
        OrderItemRef orderItemRef = mock(OrderItemRef.class);
        ProductSpecificationRef productSpecification = mock(ProductSpecificationRef.class);
        ServiceSpecificationRef serviceSpecification = mock(ServiceSpecificationRef.class);

        when(serviceSpecification.getName()).thenReturn("Product Name");

        when(orderItemRef.getProductSpecificationRef()).thenReturn(productSpecification);
        when(orderItemRef.getProductSpecificationRef().getServiceSpecificationRef()).thenReturn(List.of(serviceSpecification));

        // When
        Service service = SOMDtoBuilderUtil.getServiceBuilder(orderItemRef).build();

        // Then
        assertEquals("3153", service.getId());
        assertEquals("http://serverlocation:port/serviceInventory/3153", service.getHref());
        assertEquals("Product Name", service.getDescription());
        assertEquals("Product Name", service.getName());
        assertEquals("CFS", service.getServiceType());
        assertNotNull(service.getServiceCharacteristic());
        assertNotNull(service.getServiceSpecification());
        assertNotNull(service.getServiceRelationship());
        assertEquals(Service.ServiceStateType.ACTIVE, service.getState());
    }

    @Test
    void givenDefaultServiceCharacteristicRingBuilder_whenBuild_thenReturnsServiceCharacteristicWithDefaultValues() {
        // When
        StringCharacteristic characteristic = SOMDtoBuilderUtil.getServiceCharacteristicRingBuilder();

        // Then
        assertEquals("006", characteristic.getId());
        assertEquals("Tone", characteristic.getName());
        assertEquals("ToneVIP", characteristic.getValue());
        assertEquals("string", characteristic.getValueType());
    }

    @Test
    void givenDefaultServiceCharacteristicSmsOptionBuilder_whenBuild_thenReturnsServiceCharacteristicWithDefaultValues() {
        // When
        StringCharacteristic characteristic = SOMDtoBuilderUtil.getServiceCharacteristicSmsOptionBuilder();

        // Then
        assertEquals("003", characteristic.getId());
        assertEquals("SMS_Number", characteristic.getName());
        assertEquals("500-cood", characteristic.getValue());
        assertEquals("string", characteristic.getValueType());
    }

    @Test
    void givenDefaultServiceCharacteristicTimeBundleBuilder_whenBuild_thenReturnsServiceCharacteristicWithDefaultValues() {
        // When
        StringCharacteristic characteristic = SOMDtoBuilderUtil.getServiceCharacteristicTimeBundleBuilder();

        // Then
        assertEquals("003", characteristic.getId());
        assertEquals("volume", characteristic.getName());
        assertEquals("10h-cood", characteristic.getValue());
        assertEquals("string", characteristic.getValueType());
    }

    @Test
    void givenDefaultServiceCharacteristicMobileLineBuilder_whenBuild_thenReturnsServiceCharacteristicWithDefaultValues() {
        // When
        StringCharacteristic characteristic = SOMDtoBuilderUtil.getServiceCharacteristicMobileLineBuilder();

        // Then
        assertEquals("001", characteristic.getId());
        assertEquals("MSISDN", characteristic.getName());
        assertEquals("4152797439-cood", characteristic.getValue());
        assertEquals("string", characteristic.getValueType());
    }

    @Test
    void givenDefaultServiceCharacteristicConnectivityBuilder_whenBuild_thenReturnsServiceCharacteristicWithDefaultValues() {
        // When
        StringCharacteristic characteristic = SOMDtoBuilderUtil.getServiceCharacteristicConnectivityBuilder();

        // Then
        assertEquals("002", characteristic.getId());
        assertEquals("ICCID", characteristic.getName());
        assertEquals("891004234814455936-cood", characteristic.getValue());
        assertEquals("string", characteristic.getValueType());
    }

    @Test
    void givenDefaultServiceRelationshipBuilder_whenBuild_thenReturnsServiceRelationshipWithDefaultValues() {
        // When
        ServiceRelationship relationship = SOMDtoBuilderUtil.getServiceRelationshipBuilder().build();

        // Then
        assertEquals("4321", relationship.getId());
        assertEquals("requires", relationship.getServiceRelationshipType());
    }

    @Test
    void givenDefaultServiceSpecificationBuilder_whenBuild_thenReturnsServiceSpecificationWithDefaultValues() {
        // When
        ServiceSpecification specification = SOMDtoBuilderUtil.getServiceSpecificationBuilder().build();

        // Then
        assertEquals("5e18402d-c964-4d52-b362-22ef79c27c01", specification.getId());
        assertEquals("Connectivity", specification.getName());
        assertEquals("ServiceSpecificationRef", specification.getType());
    }
}

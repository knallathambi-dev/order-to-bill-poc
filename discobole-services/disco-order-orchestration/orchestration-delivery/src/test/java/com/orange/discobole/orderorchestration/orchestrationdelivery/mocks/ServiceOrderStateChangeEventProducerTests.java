// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mocks;

import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.mocks.producer.impl.ServiceOrderStateChangeEventProducerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


class ServiceOrderStateChangeEventProducerTests {

    @MockBean
    private final KafkaTemplate<String, ServiceOrderEvent> kafkaTemplate = Mockito.mock(KafkaTemplate.class);

    @InjectMocks
    private ServiceOrderStateChangeEventProducerImpl serviceOrderStateChangeEventProducer;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceOrderStateChangeWithGetEnabled_ThenServiceOrderStateChangeEventProducerIsCalled() {
        ServiceOrder serviceOrder = createServiceOrderRequest();
        String topicName = "disco.service-order-management.serviceOrderStateChange-event";

        serviceOrderStateChangeEventProducer.publishEvent(serviceOrder);

        ArgumentCaptor<String> topic = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<ServiceOrderEvent> messageCaptor = ArgumentCaptor.forClass(ServiceOrderEvent.class);

        // Verify that the send method was called with the expected topic and message
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(topic.capture(), any(), messageCaptor.capture());

        // Extract the captured values
        ServiceOrderEvent message = messageCaptor.getValue();
        ServiceOrderPayloadEvent serviceOrderPayloadEvent = message.getEvent();
        ServiceOrder sentServiceOrder = serviceOrderPayloadEvent.getServiceOrder();
        Assertions.assertEquals(topicName, topic.getValue());
        Assertions.assertEquals(EventType.SERVICE_ORDER_STATE_CHANGE_EVENT.getValue(), message.getEventType());
        Assertions.assertEquals(serviceOrder, sentServiceOrder);
    }


    public ServiceOrder createServiceOrderRequest() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification serviceSpecification = com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification.builder()
                .id("MobileLine")
                .build();
        Characteristic serviceCharacteristic = StringCharacteristic.builder()
                .id("5243")
                .name("volume")
                .valueType("string")
                .value("1h")
                .build();
        List<Characteristic> serviceCharacteristicList = new ArrayList<>();
        serviceCharacteristicList.add(serviceCharacteristic);
        Service service = Service.builder()
                .serviceType("CFS")
                .serviceCharacteristic(serviceCharacteristicList)
                .serviceSpecification(serviceSpecification)
                .build();
        ServiceOrderItem serviceOrderItem = ServiceOrderItem.builder()
                .id("1")
                .action(ServiceOrderItem.Action.ADD)
                .service(service)
                .build();
        List<ServiceOrderItem> serviceOrderItemList = new ArrayList<>();
        serviceOrderItemList.add(serviceOrderItem);
        ServiceOrderRelatedParty relatedParty = ServiceOrderRelatedParty.builder()
                .id("231-mf4")
                .name("Abir")
                .role("customer")
                .referredType("individual")
                .build();
        List<ServiceOrderRelatedParty> relatedPartyList = new ArrayList<>();
        relatedPartyList.add(relatedParty);

        return ServiceOrder.builder()
                .id("12")
                .description("Service order Time Bundle")
                .category("string")
                .requestedCompletionDate(Instant.ofEpochSecond(1626436900L))
                .type("ServiceOrder")
                .relatedParty(relatedPartyList)
                .serviceOrderItem(serviceOrderItemList)
                .build();

    }
}

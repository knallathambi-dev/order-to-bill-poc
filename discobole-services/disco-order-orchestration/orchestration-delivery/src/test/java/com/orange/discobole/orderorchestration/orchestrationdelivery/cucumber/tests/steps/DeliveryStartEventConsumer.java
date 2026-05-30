// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.OrchestrationPlanNodeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.ProductCharacteristicRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class DeliveryStartEventConsumer {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    private DeliveryOrderPayloadEvent deliveryOrderPayloadEvent;

    private final Duration TIMEOUT = Duration.ofSeconds(20L);

    @Then("the system will fire delivery start event on topic {string} with {string} delivery factory type, {string} start date and the following node ids:")
    @Then("the system will fire bulk delivery start event on topic {string} with {string} delivery factory type, {string} start date and the following node ids:")
    public void theSystemWillFireBulkDeliveryStartEventOnTopicWithTheFollowingNodeIds(String topicName, String deliveryFactoryType, String orderStartDate, List<OrchestrationPlanNodeRecord> nodesList) {
        Awaitility.await().atMost(TIMEOUT).untilAsserted(() -> {
            ArgumentCaptor<DeliveryOrderPayloadEvent> tangiblePayloadEventArgumentCaptor = ArgumentCaptor.forClass(DeliveryOrderPayloadEvent.class);
            Mockito.verify(eventPublisher).publishEvent(eq(CDCEvent.fromTopicName(topicName)), tangiblePayloadEventArgumentCaptor.capture());
            deliveryOrderPayloadEvent = tangiblePayloadEventArgumentCaptor.getValue();

            List<String> actualNodeIds = deliveryOrderPayloadEvent.getDeliveryOrder().getOrderItemRef().stream().map(OrderItemRef::getOrchestrationNodeId).toList();
            List<String> expectedNodeIds = nodesList.stream().map(OrchestrationPlanNodeRecord::id).toList();
            assertThat(expectedNodeIds).isEqualTo(actualNodeIds);

            assertThat(DeliveryFactoryRef.DeliveryFactoryEnum.fromValue(deliveryFactoryType))
                    .isEqualTo(deliveryOrderPayloadEvent.getDeliveryOrder().getDeliveryFactoryRef().getDeliveryFactoryType());

            assertThat(Instant.parse(orderStartDate))
                    .isEqualTo(deliveryOrderPayloadEvent.getDeliveryOrder().getStartDate());
        });
    }

    @Then("the system will not fire bulk delivery start event on topic {string}")
    public void theSystemWillNotFireBulkDeliveryStartEventOnTopic(String topicName) {
        Awaitility.await().atMost(TIMEOUT).untilAsserted(() -> {
            Mockito.verify(eventPublisher, Mockito.never()).publishEvent(eq(CDCEvent.fromTopicName(topicName)), any());
        });
    }

    @And("the system marks the following nodes as part of a bulk delivery batch:")
    public void theEligibleNodesAreMarkedAsPartOfABulkDeliveryBatch(List<OrchestrationPlanNodeRecord> nodesList) {
        Awaitility.await().atMost(TIMEOUT).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodesList.get(0).id()).orElseThrow(() -> new IllegalArgumentException("No orchestration plan with id: " + nodesList.get(0).id()));
            Map<String, OrchestrationPlanNode> orchestrationPlanNodeMap = orchestrationPlan.getOrchestrationPlanNodes().stream().collect(Collectors.toMap(OrchestrationPlanNode::getId, Function.identity()));

            nodesList.forEach(node -> {
                assertThat(orchestrationPlanNodeMap.get(node.id()).isDeliveryBatched()).isTrue();
            });
        });
    }

    @And("the delivery start event will have the following characteristics for node id {string}")
    public void theDeliveryStartEventWillHaveTheFollowingCharacteristicsForNodeId(String nodeId, List<ProductCharacteristicRecord> productCharacteristics) {
        List<Characteristic> characteristicList = productCharacteristics.stream().map(productCharacteristic ->
                switch (Objects.nonNull(productCharacteristic.type()) ? productCharacteristic.type() : StringCharacteristic.class.getSimpleName()) {
                    case "StringCharacteristic" -> StringCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .value(productCharacteristic.value())
                            .atType(StringCharacteristic.class.getSimpleName())
                            .valueType(productCharacteristic.valueType() != null ? productCharacteristic.valueType() : null)
                            .build();
                    case "ValidityCharacteristic" -> ValidityCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(ValidityCharacteristic.class.getSimpleName())
                            .value(ValidityValue.builder()
                                    .unitOfMeasure(productCharacteristic.unitOfMeasure())
                                    .value(Objects.nonNull(productCharacteristic.value()) ? Integer.valueOf(productCharacteristic.value()) : null)
                                    .validFrom(Objects.nonNull(productCharacteristic.validFrom()) ? OffsetDateTime.parse(productCharacteristic.validFrom()) : null)
                                    .validTo(Objects.nonNull(productCharacteristic.validTo()) ? OffsetDateTime.parse(productCharacteristic.validTo()) : null)
                                    .build())
                            .build();
                    case "AddressCharacteristic" -> AddressCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(AddressCharacteristic.class.getSimpleName())
                            .addressId(productCharacteristic.addressId())
                            .country(productCharacteristic.country())
                            .city(productCharacteristic.city())
                            .streetName(productCharacteristic.streetName())
                            .postcode(productCharacteristic.postCode())
                            .build();
                    case "ObjectCharacteristic" -> ObjectCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(ObjectCharacteristic.class.getSimpleName())
                            .value(Map.of(
                                    "value", productCharacteristic.value(),
                                    "unitOfMeasure", productCharacteristic.unitOfMeasure()
                            )).build();
                    case "DateCharacteristic" -> DateCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(DateCharacteristic.class.getSimpleName())
                            .value(Objects.nonNull(productCharacteristic.value()) ? OffsetDateTime.parse(productCharacteristic.value()) : null)
                            .build();
                    default -> throw new IllegalStateException("Unexpected value: " + productCharacteristic.valueType());
                }
        ).toList();

       OrderItemRef orderItemRef = deliveryOrderPayloadEvent.getDeliveryOrder().getOrderItemRef().stream().filter(orderItemRef1 ->
                        orderItemRef1.getOrchestrationNodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No order item ref found with the given node id"));

        // Sort characteristics by name and compare them as strings because test fails in HELD case
        List<Characteristic> actualCharacteristics =
                orderItemRef.getOrderItemCharacteristics()
                        .stream()
                        .sorted(Comparator.comparing(Characteristic::getName))
                        .toList();

        List<Characteristic> expectedCharacteristics =
                characteristicList.stream()
                        .sorted(Comparator.comparing(Characteristic::getName))
                        .toList();

        assertThat(actualCharacteristics).hasSameSizeAs(expectedCharacteristics);
        assertThat(actualCharacteristics.toString()).isEqualTo(expectedCharacteristics.toString());
    }
}

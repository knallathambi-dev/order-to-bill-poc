// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.controller;

import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl.ProductSpecificationServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.mocks.producer.ProductOrderStateChangeEventProducer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DeepCopyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/api/cood/test")
@RequiredArgsConstructor
public class ControllerTest {
    private final ProductOrderStateChangeEventProducer producer;
    private final ProductSpecificationServiceImpl productSpecificationService;
    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private static ProductOrderStateChangeEvent getProductOrderEvent(ProductOrder productOrder, EventType eventType) {
        ProductOrderPayloadEvent productOrderStateChangePayloadEvent = ProductOrderPayloadEvent
                .builder()
                .productOrder(productOrder)
                .build();

        return ProductOrderStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType.getValue())
                .event(productOrderStateChangePayloadEvent)
                .build();
    }

    @GetMapping("/productSpecification/{id}")
    public ResponseEntity<ProductSpecification> getProductSpecification(@PathVariable String id) {
        ProductSpecification resultFromCatalog = productSpecificationService.retrieveProductSpecById(id);
        return ResponseEntity.ok().body(resultFromCatalog);
    }

    // get the String message via HTTP, publish it to broker using spring cloud stream
    @PostMapping(value = "/productOrderStatChange/sendMessage")
    public String publishMessageString(@RequestBody ProductOrder productOrder) {
        log.info("trigger the orchestrationPlan event");
        ProductOrderStateChangeEvent productOrderStateChangeEvent = getProductOrderEvent(productOrder, EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT);
        String partitionKey = productOrder.getId();

        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderStateChangeEvent)
                .setHeader(MessageHeaders.CONTENT_TYPE, "application/json") // Set the content type header
                .setHeader("partitionKey", partitionKey)
                .build();

        producer.publishEvent(message.getPayload());
        log.info("Publish message productOrderStatChange succeeded");
        return productOrderStateChangeEvent.getEventId();
    }

    @PostMapping(value = "/plan/{id}")
    public String updatePlanReceivedDate(@PathVariable String id, @RequestBody OrchestrationPlan orchestrationPlan) {
        OrchestrationPlan savedPlan = orchestrationPlanRepository.findOrchestrationPlanById(id).orElseThrow(() -> new CoodNotFoundException(ORCHESTRATION_PLAN_NOT_FOUND, id));
        orchestrationPlan.setId(savedPlan.getId());
        orchestrationPlanRepository.save(orchestrationPlan);

        return "Plan updated";
    }

    @PostMapping(value = "/plan/deep-clone")
    public OrchestrationPlan updatePlanDeepClone(@RequestBody OrchestrationPlan orchestrationPlan) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        OrchestrationPlan savedPlan = orchestrationPlanRepository.findOrchestrationPlanById(orchestrationPlan.getId()).orElseThrow(() -> new CoodNotFoundException(ORCHESTRATION_PLAN_NOT_FOUND, orchestrationPlan.getId()));
        DeepCopyUtil.copyNonNullProperties(orchestrationPlan, savedPlan);
        orchestrationPlanRepository.save(savedPlan);

        return savedPlan;
    }
}

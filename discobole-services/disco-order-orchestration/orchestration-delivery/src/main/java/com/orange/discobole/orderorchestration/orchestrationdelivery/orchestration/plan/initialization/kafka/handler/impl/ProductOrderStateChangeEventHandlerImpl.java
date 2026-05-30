// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.kafka.handler.impl;

import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.kafka.handler.ProductOrderStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanBuilderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanInitService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.validator.ProductOrderStateChangeEventValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
 import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State.HELD;
import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.PRODUCT_ORDER_INVALID;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.validator.ProductOrderStateChangeEventValidator.isProductOrderStatusAccepted;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ProductOrderStateChangeEventHandlerImpl implements ProductOrderStateChangeEventHandler {
    private final OrchestrationPlanService orchestrationPlanService;
    private final OrchestrationPlanInitService orchestrationPlanInitService;
    private final DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;
    private final OrchestrationPlanBuilderService orchestrationPlanBuilderService;
    private final OrchestrationDeliveryFalloutManagement orchestrationDeliveryFalloutManagement;
    private final OrchestrationPlanModificationService orchestrationPlanModificationService;
    private final ErrorMessageMapper errorMessageMapper;
    private final EventPublisher eventPublisher;

    @KafkaSessionScope
    public void handleEvent(ProductOrderStateChangeEvent productOrderStateChangeEvent) {
        log.info("Consuming productOrderStateChange event: {}", productOrderStateChangeEvent.getEvent().getProductOrder().getId());
        log.debug("Consuming productOrderStateChange event: {}", productOrderStateChangeEvent.getEvent());
        ProductOrderStateChangeEventValidator.checkProductOrderEvent(productOrderStateChangeEvent);
        if (!isProductOrderStatusAccepted(productOrderStateChangeEvent)) {
            log.debug("Product order status is not accepted for productOrderId: {}", productOrderStateChangeEvent.getEvent().getProductOrder().getId());
            return;
        }
        ProductOrder productOrder = productOrderStateChangeEvent.getEvent().getProductOrder();
        OrchestrationPlan orchestrationPlan = orchestrationPlanInitService.createOrchestrationPlan(productOrder);
        dataPersistenceKafkaSessionService.setPlanToBePersisted(orchestrationPlan);
        dataPersistenceKafkaSessionService.persistAll();

        orchestrationPlanBuilderService.buildOrchestrationPlan(orchestrationPlan, productOrder);
        dataPersistenceKafkaSessionService.persistAll();
        log.info("ProductOrderStateChangeEventHandlerImpl | handleEvent | finished triggering the productOrderStateChange event consumer for productOrderId: {}", productOrderStateChangeEvent.getEvent().getProductOrder().getId());
    }

    @Override
    public void handleDeadLetter(ProductOrderStateChangeEvent message, FalloutCharacteristicWrapper characteristicWrapper) {
        ProductOrder productOrder = message.getEvent().getProductOrder();
        OrchestrationPlan orchestrationPlan = orchestrationPlanInitService.getOrchestrationPlanByRelatedProductOrderId(productOrder.getId());
        orchestrationDeliveryFalloutManagement.createFalloutProcessFromDLT(orchestrationPlan, characteristicWrapper);
        orchestrationPlan.addErrorMessage(errorMessageMapper.mapToPlanError(characteristicWrapper.getCoodError()));
        orchestrationPlanModificationService.updatePlanStateAndAddErrorMessageById(orchestrationPlan.getId(),
                orchestrationPlan.getErrorMessage(),
                HELD.equals(orchestrationPlan.getState()) ? orchestrationPlan.getPreviousState() : orchestrationPlan.getState(),
                HELD);
        orchestrationPlan.setState(HELD);
        eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, orchestrationPlan);
    }

}

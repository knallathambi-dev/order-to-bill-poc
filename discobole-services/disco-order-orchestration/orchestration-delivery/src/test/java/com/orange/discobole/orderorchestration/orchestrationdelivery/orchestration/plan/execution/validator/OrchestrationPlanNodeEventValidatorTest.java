// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.validator;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType.DELIVERS;

class OrchestrationPlanNodeEventValidatorTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenOrchestrationPlanNodeStateChangeEventWithoutEventIdOrEventWhichCausesViolations_WhenValidateSanityOrchestrationPlanNodeEvent_ThenExceptionThrown(){
        OrchestrationPlanNodeStateChangeEvent orchestrationPlanNodeStateChangeEvent = OrchestrationPlanNodeStateChangeEvent
                .builder()
                .eventType("type")
                .eventTime(Instant.MAX)
                .correlationId("correlation")
                .build();
        Assertions.assertThrows(CoodNonRecoverableAndNonRetryableException.class, () ->
                OrchestrationPlanNodeEventValidator.validateSanityOrchestrationPlanNodeEvent(orchestrationPlanNodeStateChangeEvent));

    }

    @Test
    void givenOrchestrationPlanNodeStateChangeEventWithoutViolations_WhenValidateSanityOrchestrationPlanNodeEvent_ThenExceptionThrown(){
        OrchestrationPlanNodeStateChangeEvent orchestrationPlanNodeStateChangeEvent = OrchestrationPlanNodeStateChangeEvent
                .builder()
                .eventId("id")
                .eventType("type")
                .eventTime(Instant.MAX)
                .correlationId("correlation")
                .event(OrchestrationPlanNodeStateChangePayloadEvent.builder().orchestrationPlanNode(
                        initOrchestrationPlanNode()
                ).build())
                .build();
        Assertions.assertDoesNotThrow(() ->
                OrchestrationPlanNodeEventValidator.validateSanityOrchestrationPlanNodeEvent(orchestrationPlanNodeStateChangeEvent));

    }

    private static @NotNull OrchestrationPlanNode initOrchestrationPlanNode() {
        return new OrchestrationPlanNode("1", OrchestrationPlanNodeState.COMPLETED, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("2", "add", 1)), new RelatedSupplyChainOrder("2", "2"), List.of(RelatedProduct.builder().id("id1").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("relatedNodeId", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
    }

}

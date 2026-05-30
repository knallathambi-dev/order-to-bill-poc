// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.validator;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

class OrchestrationPlanEventValidatorTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenOrchestrationPlanStateChangeEventWithoutEventId_WhenCheckOrchestrationPlanEvent_ThenExceptionThrown(){
        OrchestrationPlanStateChangeEvent orchestrationPlanStateChangeEvent = OrchestrationPlanStateChangeEvent
                .builder()
                .eventType("type")
                .eventTime(Instant.MAX)
                .correlationId("correlation")
                .build();
        Assertions.assertThrows(CoodNonRecoverableAndNonRetryableException.class, () ->
                OrchestrationPlanEventValidator.checkOrchestrationPlanEvent(orchestrationPlanStateChangeEvent));

    }

}

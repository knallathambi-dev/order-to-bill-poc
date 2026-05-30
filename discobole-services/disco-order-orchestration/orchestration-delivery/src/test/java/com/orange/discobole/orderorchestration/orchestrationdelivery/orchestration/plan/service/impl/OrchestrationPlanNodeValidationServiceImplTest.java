// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.service.impl;


import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.validations.OrchestrationPlanNodeValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl.OrchestrationPlanNodeValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;
import static org.junit.jupiter.api.Assertions.*;

class OrchestrationPlanNodeValidationServiceImplTest {

    private OrchestrationPlanNodeValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        validationService = new OrchestrationPlanNodeValidationServiceImpl();
    }

    @Test
    void testValidTransitionFromInitializedToAcknowledged() {
        OrchestrationPlanNode node = new OrchestrationPlanNode();
        node.setState(INITIALIZED);

        assertDoesNotThrow(() ->
                validationService.validateStateUpdate(node, ACKNOWLEDGED));
    }

    @Test
    void testInvalidTransitionFromInitializedToInProgress() {
        OrchestrationPlanNode node = new OrchestrationPlanNode();
        node.setState(INITIALIZED);

        OrchestrationPlanNodeValidationException ex = assertThrows(OrchestrationPlanNodeValidationException.class, () ->
                validationService.validateStateUpdate(node, IN_PROGRESS));

        assertEquals(ExceptionCode.ORCHESTRATION_PLAN_NODE_STATE_NOT_VALID.getCode(), ex.getCode());
    }

    @Test
    void testValidTransitionFromInProgressToCompleted() {
        OrchestrationPlanNode node = new OrchestrationPlanNode();
        node.setState(IN_PROGRESS);

        assertDoesNotThrow(() ->
                validationService.validateStateUpdate(node, COMPLETED));
    }

    @Test
    void testInvalidTransitionFromCompletedToAcknowledged() {
        OrchestrationPlanNode node = new OrchestrationPlanNode();
        node.setState(COMPLETED);

        OrchestrationPlanNodeValidationException ex = assertThrows(OrchestrationPlanNodeValidationException.class, () ->
                validationService.validateStateUpdate(node, ACKNOWLEDGED));

        assertEquals(ExceptionCode.ORCHESTRATION_PLAN_NODE_STATE_NOT_VALID.getCode(), ex.getCode());
    }
}


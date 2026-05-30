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
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.validations.OrchestrationPlanNodeValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.OrchestrationPlanValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class OrchestrationPlanNodeEventValidator {

    public static final String ORCHESTRATION_PLAN_NODE_VALID_EVENT = "OrchestrationPlanNodeEventValidator | checkOrchestrationPlanNodeEvent | Valid Orchestration Plan Node event id {}";
    public static final String ORCHESTRATION_PLAN_NODE_VALIDATING_EVENT = "OrchestrationPlanNodeEventValidator | checkOrchestrationPlanNodeEvent | Validating Orchestration Plan Node event with event id: {}";

    private OrchestrationPlanNodeEventValidator() {
    }

    public static void validateSanityOrchestrationPlanNodeEvent(OrchestrationPlanNodeStateChangeEvent event) throws OrchestrationPlanValidationException {
        log.info(ORCHESTRATION_PLAN_NODE_VALIDATING_EVENT, event.getEventId());
        log.debug("OrchestrationPlanNodeEventValidator | checkOrchestrationPlanNodeEvent | Validating event {}", event);
        Set<ConstraintViolation<OrchestrationPlanNodeStateChangeEvent>> violations = ValidationUtil.getViolations(event);
        if (violations.isEmpty()) {
            log.info(ORCHESTRATION_PLAN_NODE_VALID_EVENT, event.getEventId());
        } else {
            log.error("OrchestrationPlanNodeEventValidator | checkOrchestrationPlanNodeEvent | Validating {}", violations);
            throw new CoodNonRecoverableAndNonRetryableException(new OrchestrationPlanNodeValidationException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, violations, "Invalid Orchestration Plan Node event"));
        }
    }
}

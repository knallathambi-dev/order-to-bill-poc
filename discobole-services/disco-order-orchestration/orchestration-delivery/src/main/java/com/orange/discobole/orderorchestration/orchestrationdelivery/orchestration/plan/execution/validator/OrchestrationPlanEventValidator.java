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
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.OrchestrationPlanValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class OrchestrationPlanEventValidator {

    public static final String ORCHESTRATION_PLAN_VALID_EVENT = "OrchestrationPlanEventValidator | checkOrchestrationPlanEvent | Valid Orchestration Plan event id {}";
    public static final String ORCHESTRATION_PLAN_VALIDATING_EVENT = "OrchestrationPlanEventValidator | checkOrchestrationPlanEvent | Validating Orchestration Plan event with event id: {}";

    private OrchestrationPlanEventValidator() {
    }

    public static void checkOrchestrationPlanEvent(OrchestrationPlanStateChangeEvent event) throws OrchestrationPlanValidationException {
        log.info(ORCHESTRATION_PLAN_VALIDATING_EVENT, event.getEventId());
        log.debug("OrchestrationPlanEventValidator | checkOrchestrationPlanEvent | Validating event {}", event);
        Set<ConstraintViolation<OrchestrationPlanStateChangeEvent>> violations = ValidationUtil.getViolations(event);
        if (violations.isEmpty()) {
            log.info(ORCHESTRATION_PLAN_VALID_EVENT, event.getEventId());
        } else {
            log.error("OrchestrationPlanEventValidator | checkOrchestrationPlanEvent | Validating {}", violations);
            throw new CoodNonRecoverableAndNonRetryableException(new OrchestrationPlanValidationException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, violations, "Invalid Orchestration Plan event"));
        }
    }
}

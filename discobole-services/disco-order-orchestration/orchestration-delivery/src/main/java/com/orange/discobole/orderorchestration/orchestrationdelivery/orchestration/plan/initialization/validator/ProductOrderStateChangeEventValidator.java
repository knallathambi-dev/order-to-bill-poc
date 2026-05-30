// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.validator;

import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import java.util.Objects;
import java.util.Set;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.PRODUCT_ORDER_ITEM_INVALID;

@Slf4j
public class ProductOrderStateChangeEventValidator {

    private ProductOrderStateChangeEventValidator() {
    }

    public static void checkProductOrderEvent(ProductOrderStateChangeEvent event) throws ProductOrderValidationException {
        Set<ConstraintViolation<ProductOrderStateChangeEvent>> violations = ValidationUtil.getViolations(event);
        if (violations.isEmpty()) {
            log.info("ProductOrderStateChangeEventValidator | checkProductOrderEvent | Product Order Event with id: {} is valid", event.getEventId());
        } else {
            //TODO switch cases based on violations
            log.info("ProductOrderStateChangeEventValidator | checkProductOrderEvent | Product Order Event with id: {} is invalid", event.getEventId());
            log.error("ProductOrderStateChangeEventValidator | checkProductOrderEvent | Product Order Event with violations {}", violations);
            throw new CoodNonRecoverableAndNonRetryableException(new ProductOrderValidationException(PRODUCT_ORDER_ITEM_INVALID, violations, event.getEventId()));
        }
    }

    public static boolean isProductOrderStatusAccepted(ProductOrderStateChangeEvent event) {
        boolean isProductOrderStateNotNull = Objects.nonNull(event.getEvent().getProductOrder().getState());
        boolean isProductStateEqualAccepted = event.getEvent().getProductOrder().getState().equals(ProductOrderStateType.ACCEPTED);
        boolean isAccepted = isProductOrderStateNotNull && isProductStateEqualAccepted;
        log.debug("ProductOrderStateChangeEventValidator | isProductOrderStatusAccepted | isProductOrderStatusAccepted result: {}, isProductOrderStateNotNull: {}," +
                " isProductStateEqualAccepted: {}", isAccepted, isProductOrderStateNotNull, isProductStateEqualAccepted);
        return isAccepted;
    }

}


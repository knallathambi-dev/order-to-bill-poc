// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.exception;


import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class OrchestrationPlanNodeErrorMessagesException extends RuntimeException {
    private final transient List<OrchestrationNodeErrorMessage> errors;

    public OrchestrationPlanNodeErrorMessagesException(List<OrchestrationNodeErrorMessage> errors) {
        this.errors = errors;
    }

    public static OrchestrationPlanNodeErrorMessagesException of(List<OrchestrationNodeErrorMessage> errors) {
        return new OrchestrationPlanNodeErrorMessagesException(errors);
    }

}

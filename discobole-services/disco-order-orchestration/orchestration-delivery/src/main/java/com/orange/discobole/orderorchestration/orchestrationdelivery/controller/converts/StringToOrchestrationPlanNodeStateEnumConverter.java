// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.controller.converts;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlanNodeStateEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToOrchestrationPlanNodeStateEnumConverter implements Converter<String, OrchestrationPlanNodeStateEnum> {
    @Override
    public OrchestrationPlanNodeStateEnum convert(String source) {
        return OrchestrationPlanNodeStateEnum.fromValue(source);
    }
}

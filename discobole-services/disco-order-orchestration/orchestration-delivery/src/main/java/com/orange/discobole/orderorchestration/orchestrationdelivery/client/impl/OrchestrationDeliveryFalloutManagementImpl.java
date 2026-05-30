// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProcessFlowManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ProcessFlowCreateMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class OrchestrationDeliveryFalloutManagementImpl implements OrchestrationDeliveryFalloutManagement {

    private final ProcessFlowCreateMapper processFlowCreateMapper;

    private final ProcessFlowManagement processFlowManagement;

    @Override
    public void createFalloutProcess(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan) {
        log.info("start fallout process flow node id {}", orchestrationPlanNode.getId());
        ProcessFlowCreate processFlow = processFlowCreateMapper.from(orchestrationPlan, orchestrationPlanNode);
        processFlowManagement.createProcessFlow(processFlow);
    }

    @Override
    public void createFalloutProcessFromDLT(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan, FalloutCharacteristicWrapper characteristicWrapper) {
        ProcessFlowCreate processFlow = processFlowCreateMapper.from(orchestrationPlanNode, orchestrationPlan, characteristicWrapper);
        processFlowManagement.createProcessFlow(processFlow);
    }

    @Override
    public void createFalloutProcessFromDLT(OrchestrationPlan orchestrationPlan, FalloutCharacteristicWrapper characteristicWrapper) {
        log.info("start fallout process flow plan id {}", orchestrationPlan.getId());
        ProcessFlowCreate processFlow = processFlowCreateMapper.from(orchestrationPlan, characteristicWrapper);
        processFlowManagement.createProcessFlow(processFlow);
    }
}

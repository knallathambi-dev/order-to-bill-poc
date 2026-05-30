// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.RelatedEntity;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.RelatedEntityRole;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = {
        ProcessFlowCreateMapperImpl.class
})
class ProcessFlowMapperTest {

    @Autowired
    ProcessFlowCreateMapper processFlowCreateMapper;

    @Test
    void givenOrchestrationPlanAndFalloutCharacteristicWrapper_whenMapToProcessFlow_thenReturnProcessFlow() {
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);
        FalloutCharacteristicWrapper characteristicWrapper = Instancio.create(FalloutCharacteristicWrapper.class);

        ProcessFlowCreate processFlowCreate = processFlowCreateMapper.from(orchestrationPlan, characteristicWrapper);

        assertThat(processFlowCreate.getRelatedEntity()).hasSize(1);
        assertThat(processFlowCreate.getRelatedEntity().get(0).getId()).isEqualTo(orchestrationPlan.getId());
        assertThat(processFlowCreate.getRelatedEntity().get(0).getReferredType()).isEqualTo(OrchestrationPlan.class.getSimpleName());
        assertThat(processFlowCreate.getCharacteristic()).hasSize(6);
        assertThat(processFlowCreate.getCharacteristic().stream().filter(c -> c.getValueType().equals("Fallout")).count()).isEqualTo(1);
        ObjectCharacteristic characteristic = (ObjectCharacteristic) processFlowCreate.getCharacteristic().stream().filter(c -> c.getValueType().equals("Fallout")).findFirst().get();
        assertThat(characteristic.getValue().getClass().getSimpleName()).isEqualTo(FalloutIncident.class.getSimpleName());
        FalloutIncident fallout = (FalloutIncident) characteristic.getValue();
        assertThat(fallout.getRelatedEntity()).hasSize(3);
        assertThat(fallout.getRelatedEntity().get(0)).isEqualTo(RelatedEntity.builder()
                .id(orchestrationPlan.getId())
                .atReferredType(OrchestrationPlan.class.getSimpleName())
                .role(RelatedEntityRole.INITIATOR.getValue())
                .href("/orchestrationPlan/" + orchestrationPlan.getId())
                .build());
        assertThat(fallout.getRelatedEntity().get(1)).isEqualTo(RelatedEntity.builder()
                .id(orchestrationPlan.getId())
                .atReferredType(OrchestrationPlan.class.getSimpleName())
                .href("/orchestrationPlan/" + orchestrationPlan.getId())
                .role(RelatedEntityRole.RELATED_ORCHESTRATION_PLAN.getValue())
                .build());
        assertThat(fallout.getRelatedEntity().get(2)).isEqualTo(RelatedEntity.builder()
                .id(orchestrationPlan.getRelatedProductOrder().getId())
                .atReferredType(ProductOrder.class.getSimpleName())
                .role(RelatedEntityRole.RELATED_PRODUCT_ORDER.getValue())
                .build());
    }

    @Test
    void givenOrchestrationPlanNodeAndOrchestrationPlanAndFalloutCharacteristicWrapper_whenMapToProcessFlow_thenReturnProcessFlow() {
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);
        OrchestrationPlanNode orchestrationPlanNode = Instancio.create(OrchestrationPlanNode.class);
        FalloutCharacteristicWrapper characteristicWrapper = Instancio.create(FalloutCharacteristicWrapper.class);

        ProcessFlowCreate processFlowCreate = processFlowCreateMapper.from(orchestrationPlanNode, orchestrationPlan, characteristicWrapper);

        assertThat(processFlowCreate.getRelatedEntity()).hasSize(1);
        assertThat(processFlowCreate.getRelatedEntity().get(0).getId()).isEqualTo(orchestrationPlanNode.getId());
        assertThat(processFlowCreate.getRelatedEntity().get(0).getReferredType()).isEqualTo(OrchestrationPlanNode.class.getSimpleName());
        assertThat(processFlowCreate.getCharacteristic()).hasSize(6);
        assertThat(processFlowCreate.getCharacteristic().stream().filter(c -> c.getValueType().equals("Fallout")).count()).isEqualTo(1);
        ObjectCharacteristic characteristic = (ObjectCharacteristic) processFlowCreate.getCharacteristic().stream().filter(c -> c.getValueType().equals("Fallout")).findFirst().get();
        assertThat(characteristic.getValue().getClass().getSimpleName()).isEqualTo(FalloutIncident.class.getSimpleName());
        FalloutIncident fallout = (FalloutIncident) characteristic.getValue();
        assertThat(fallout.getRelatedEntity()).hasSize(3);
        assertThat(fallout.getRelatedEntity().get(0)).isEqualTo(RelatedEntity.builder()
                .id(orchestrationPlanNode.getId())
                .atReferredType(OrchestrationPlanNode.class.getSimpleName())
                .role(RelatedEntityRole.INITIATOR.getValue())
                .build());
        assertThat(fallout.getRelatedEntity().get(1)).isEqualTo(RelatedEntity.builder()
                .id(orchestrationPlan.getId())
                .atReferredType(OrchestrationPlan.class.getSimpleName())
                .href("/orchestrationPlan/" + orchestrationPlan.getId())
                .role(RelatedEntityRole.RELATED_ORCHESTRATION_PLAN.getValue())
                .build());
        assertThat(fallout.getRelatedEntity().get(2)).isEqualTo(RelatedEntity.builder()
                .id(orchestrationPlan.getRelatedProductOrder().getId())
                .atReferredType(ProductOrder.class.getSimpleName())
                .role(RelatedEntityRole.RELATED_PRODUCT_ORDER.getValue())
                .build());
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.user.actions;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.Resolution;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.characteristic.SelectResolutionStateWithReason;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.CharacteristicUtil;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.CharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.NotFoundException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component("Fallout.SelectResolveOrUnresolvedUserAction")
@Slf4j
@RequiredArgsConstructor
public class SelectResolveOrUnresolvedUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecificationList;
    private List<CharacteristicValueSpecification> specificationValueList;
    private final EventPublisher eventPublisher;
    private final FalloutRepository falloutRepository;

    @PostConstruct
    public void init() {
        try {
            characteristicSpecificationList = new ArrayList<>();
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(SelectResolutionStateWithReason.class.getSimpleName());
            CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification("1", SelectResolutionStateWithReason.class.getSimpleName(), 1, 1, specificationValueList, null, "Object");
            characteristicSpecificationList.add(characteristicSpecification);
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Perform select resolved or unresolved product user action");
        SelectResolutionStateWithReason selectResolutionStateWithReason = CharacteristicUtil.getCharacteristicValue(taskFlowUpdate.getCharacteristic(), characteristicSpecificationList, SelectResolutionStateWithReason.class);
        FalloutIncident fallout = falloutRepository.findById(stateMachine.getProcessInstanceId())
                .orElseThrow(() -> new NotFoundException("Fallout not found with id: %s".formatted(stateMachine.getProcessInstanceId())));
        Resolution resolution = Resolution.builder().status(selectResolutionStateWithReason.getResolutionState())
                .comment(selectResolutionStateWithReason.getReason()).build();
        fallout.setResolution(resolution);
        falloutRepository.save(fallout);
        Map<String, Object> result = new HashMap<>();
        result.put("reason", selectResolutionStateWithReason.getReason());
        result.put("resolutionState", selectResolutionStateWithReason.getResolutionState());
        taskFlowUpdate.addCharacteristicItem(taskFlowUpdate.getCharacteristic().get(0));
        eventPublisher.publishEvent(CDCEvent.FALLOUT_STATE_CHANGE_EVENT, fallout);
        return result;
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), "Completed");
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, "Completed", 1, 1, specificationValueList, null, "Object");
        return Collections.singletonList(characteristicSpecification);
    }
}

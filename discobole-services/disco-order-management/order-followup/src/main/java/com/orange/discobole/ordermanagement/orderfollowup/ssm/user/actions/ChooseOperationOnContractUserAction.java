// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.user.actions;

import com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants;
import com.orange.discobole.ordermanagement.orderfollowup.util.CharacteristicUtil;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.CharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("OrderFollowUp.chooseOperationalOnContract")
@Slf4j
public class ChooseOperationOnContractUserAction implements UserAction {
    private List<CharacteristicValueSpecification> specificationValueList;

    @PostConstruct
    public void init() {
        log.info("Initializing ChooseOperationOnContractUserAction - loading characteristic specification values for [{}]",
                FollowUpConstants.CHOOSE_OPERATION_CLASS);
        try {
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(FollowUpConstants.CHOOSE_OPERATION_CLASS);
            log.info("Successfully loaded [{}] characteristic specification values for [{}]",
                    specificationValueList.size(),
                    FollowUpConstants.CHOOSE_OPERATION_CLASS);
        } catch (Exception e) {
            log.error("Failed to initialize ChooseOperationOnContractUserAction - unable to load specification values for [{}]: {}",
                    FollowUpConstants.CHOOSE_OPERATION_CLASS, e.getMessage(), e);
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Performing choose operational on contract user action for taskDefinitionId: [{}]",
                stateMachineTransition.getTaskDefinitionId());
        log.debug("StateMachineTransition: [{}], TaskFlowUpdate: [{}]", stateMachineTransition, taskFlowUpdate);
        return new HashMap<>();
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.debug("Building required characteristics for taskDefinitionId: [{}]",
                stateMachineTransition.getTaskDefinitionId());
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), FollowUpConstants.CHOOSE_OPERATION_CLASS);
        log.debug("Generated characteristicId: [{}]", characteristicId);
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, FollowUpConstants.CHOOSE_OPERATION_CLASS, FollowUpConstants.DEFAULT_MIN_CARDINALITY, FollowUpConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, FollowUpConstants.OBJECT_NAME);
        return Collections.singletonList(characteristicSpecification);
    }
}
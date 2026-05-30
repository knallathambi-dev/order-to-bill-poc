// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.user.actions;

import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.enums.ConfigurationState;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.ConfirmConfigurationIsProcessed;
import com.orange.discobole.ordermanagement.ordercapture.util.CharacteristicUtil;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.CharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("OrderCapture.confirmConfiguration")
@Slf4j
public class ConfirmConfigurationUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecificationList;
    private List<CharacteristicValueSpecification> specificationValueList;

    @PostConstruct
    public void init() {
        try {
            characteristicSpecificationList = new ArrayList<>();
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(OrderCaptureConstants.CONFIRM_CONFIGURATION_IS_PROCESSED_CLASS);
            CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(OrderCaptureConstants.DEFAULT_ID, OrderCaptureConstants.CONFIRM_CONFIGURATION_IS_PROCESSED_CLASS, OrderCaptureConstants.DEFAULT_MIN_CARDINALITY, OrderCaptureConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, null, OrderCaptureConstants.OBJECT_NAME);
            characteristicSpecificationList.add(characteristicSpecification);
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Perform confirm configuration user action");
        ConfirmConfigurationIsProcessed confirmedConfiguration = CharacteristicUtil.getCharacteristicValue(taskFlowUpdate.getCharacteristic(), characteristicSpecificationList, ConfirmConfigurationIsProcessed.class);
        checkConfigurationStatus(confirmedConfiguration);
        return getConfigurationVariables(confirmedConfiguration);
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.info("Creating the required characteristics for confirm configuration");
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), OrderCaptureConstants.CONFIRM_CONFIGURATION_IS_PROCESSED_CLASS);
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, OrderCaptureConstants.CONFIRM_CONFIGURATION_IS_PROCESSED_CLASS, OrderCaptureConstants.DEFAULT_MIN_CARDINALITY, OrderCaptureConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, null, OrderCaptureConstants.OBJECT_NAME);
        return Collections.singletonList(characteristicSpecification);
    }

    private void checkConfigurationStatus(ConfirmConfigurationIsProcessed confirmConfiguration) {
        if (!confirmConfiguration.getState().equals(ConfigurationState.VALIDATED) && !confirmConfiguration.getState().equals(ConfigurationState.MODIFIED)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_CHARACTERISTICS_INPUT);
        }
    }

    private Map<String, Object> getConfigurationVariables(ConfirmConfigurationIsProcessed confirmConfigurationIsProcessed) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(OrderCaptureConstants.CONFIGURATION_ID, confirmConfigurationIsProcessed.getId());
        variables.put(OrderCaptureConstants.CONFIGURATION_STATE, confirmConfigurationIsProcessed.getState());
        return variables;
    }
}
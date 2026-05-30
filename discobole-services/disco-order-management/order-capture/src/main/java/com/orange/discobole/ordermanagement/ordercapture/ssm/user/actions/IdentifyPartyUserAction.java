// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.user.actions;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PartyIdentifier;
import com.orange.discobole.ordermanagement.ordercapture.util.CharacteristicUtil;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.CharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("OrderCapture.identifyParty")
@Slf4j
public class IdentifyPartyUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecificationList;
    private List<CharacteristicValueSpecification> specificationValueList;

    @PostConstruct
    public void init() {
        try {
            characteristicSpecificationList = new ArrayList<>();
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(OrderCaptureConstants.PARTY_IDENTIFIER_CLASS);
            CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(OrderCaptureConstants.DEFAULT_ID, OrderCaptureConstants.PARTY_IDENTIFIER_CLASS, OrderCaptureConstants.DEFAULT_MIN_CARDINALITY, OrderCaptureConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, null, OrderCaptureConstants.OBJECT_NAME);
            characteristicSpecificationList.add(characteristicSpecification);
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Perform identify party user action");
        PartyIdentifier partyIdentifier = CharacteristicUtil.getCharacteristicValue(taskFlowUpdate.getCharacteristic(), characteristicSpecificationList, PartyIdentifier.class);
        return getIdentifyPartyVariables(partyIdentifier);
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.info("Creating the required characteristics for identify party");
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), OrderCaptureConstants.PARTY_IDENTIFIER_CLASS);
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, OrderCaptureConstants.PARTY_IDENTIFIER_CLASS, OrderCaptureConstants.DEFAULT_MIN_CARDINALITY, OrderCaptureConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, null, OrderCaptureConstants.OBJECT_NAME);
        return Collections.singletonList(characteristicSpecification);
    }

    private Map<String, Object> getIdentifyPartyVariables(PartyIdentifier partyIdentifier) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(OrderCaptureConstants.PARTY_ID, partyIdentifier.getPartyId());
        if (StringUtils.isNotBlank(partyIdentifier.getPartyName())) {
            variables.put(OrderCaptureConstants.PARTY_NAME, partyIdentifier.getPartyName());
        }
        variables.put(OrderCaptureConstants.PARTY_REFERRED_TYPE, partyIdentifier.getReferredType());
        return variables;
    }
}
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
import com.orange.discobole.ordermanagement.ordercapture.enums.ValidationStatus;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.ValidateOrderByCustomer;
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

@Component("OrderCapture.validateOrder")
@Slf4j
public class ValidateOrderUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecificationList;
    private List<CharacteristicValueSpecification> specificationValueList;

    @PostConstruct
    public void init() {
        try {
            characteristicSpecificationList = new ArrayList<>();
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(OrderCaptureConstants.VALIDATE_ORDER_BY_CUSTOMER_CLASS);
            CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(OrderCaptureConstants.DEFAULT_ID, OrderCaptureConstants.VALIDATE_ORDER_BY_CUSTOMER_CLASS, OrderCaptureConstants.DEFAULT_MIN_CARDINALITY, OrderCaptureConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, null, OrderCaptureConstants.OBJECT_NAME);
            characteristicSpecificationList.add(characteristicSpecification);
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Perform validate order user action");
        ValidateOrderByCustomer validateOrder = CharacteristicUtil.getCharacteristicValue(taskFlowUpdate.getCharacteristic(), characteristicSpecificationList, ValidateOrderByCustomer.class);
        checkOrderValidationStatus(validateOrder);
        return getValidateOrderVariables(validateOrder);
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.info("Creating the required characteristics for validate order");
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), OrderCaptureConstants.VALIDATE_ORDER_BY_CUSTOMER_CLASS);
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, OrderCaptureConstants.VALIDATE_ORDER_BY_CUSTOMER_CLASS, OrderCaptureConstants.DEFAULT_MIN_CARDINALITY, OrderCaptureConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, null, OrderCaptureConstants.OBJECT_NAME);
        return Collections.singletonList(characteristicSpecification);
    }

    private void checkOrderValidationStatus(ValidateOrderByCustomer validateOrder) {
        if (!validateOrder.getStatus().equals(ValidationStatus.VALIDATED)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_CHARACTERISTICS_INPUT);
        }
    }

    private Map<String, Object> getValidateOrderVariables(ValidateOrderByCustomer validateOrder) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(OrderCaptureConstants.PRODUCT_ORDER_ID, validateOrder.getId());
        return variables;
    }
}
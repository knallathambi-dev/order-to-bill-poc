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
import com.orange.discobole.ordermanagement.orderfollowup.pojo.spec.characteristic.operation.ProductDeliveredEvent;
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

import java.util.*;

@Component("OrderFollowUp.receiveNewProductStateChangeEvent")
@Slf4j
public class ReceiveNewProductStateChangeEventUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecificationList;
    private List<CharacteristicValueSpecification> specificationValueList;

    @PostConstruct
    public void init() {
        log.info("Initializing ReceiveNewProductStateChangeEventUserAction - loading specification values for [{}]",
                FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS);
        try {
            characteristicSpecificationList = new ArrayList<>();
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS);
            log.debug("Loaded [{}] specification values for [{}]",
                    specificationValueList.size(),
                    FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS);
            CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(FollowUpConstants.DEFAULT_ID, FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS, FollowUpConstants.DEFAULT_MIN_CARDINALITY, FollowUpConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, FollowUpConstants.OBJECT_NAME);
            characteristicSpecificationList.add(characteristicSpecification);
            log.info("Successfully initialized ReceiveNewProductStateChangeEventUserAction with [{}] characteristic specifications",
                    characteristicSpecificationList.size());
        } catch (Exception e) {
            log.error("Failed to initialize ReceiveNewProductStateChangeEventUserAction - unable to load specification values for [{}]: {}",
                    FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS, e.getMessage(), e);
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Performing receive new product state change event user action for taskDefinitionId: [{}]",
                stateMachineTransition.getTaskDefinitionId());
        log.debug("StateMachineTransition: [{}], TaskFlowUpdate characteristics count: [{}]",
                stateMachineTransition,
                taskFlowUpdate.getCharacteristic() != null ? taskFlowUpdate.getCharacteristic().size() : 0);
        ProductDeliveredEvent productDeliveredEvent = CharacteristicUtil.getCharacteristicValue(taskFlowUpdate.getCharacteristic(), characteristicSpecificationList, ProductDeliveredEvent.class);
        log.debug("Extracted ProductDeliveredEvent - productOrderId: [{}], productOrderItemId: [{}], relatedProductOrderState: [{}]",
                productDeliveredEvent.getProductOrderId(),
                productDeliveredEvent.getProductOrderItemId(),
                productDeliveredEvent.getRelatedProductOrderState());
        Map<String, Object> variables = getProductEventVariables(productDeliveredEvent);
        log.info("Successfully extracted [{}] product event variables from ProductDeliveredEvent", variables.size());
        return variables;
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.debug("Building required characteristics for taskDefinitionId: [{}]",
                stateMachineTransition.getTaskDefinitionId());
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS);
        log.debug("Generated characteristicId: [{}]", characteristicId);
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS, FollowUpConstants.DEFAULT_MIN_CARDINALITY, FollowUpConstants.DEFAULT_MAX_CARDINALITY, specificationValueList, FollowUpConstants.OBJECT_NAME);
        return Collections.singletonList(characteristicSpecification);
    }

    private Map<String, Object> getProductEventVariables(ProductDeliveredEvent productDeliveredEvent) {
        log.debug("Building product event variables from ProductDeliveredEvent");
        Map<String, Object> variables = new HashMap<>();
        variables.put(FollowUpConstants.PRODUCT_ORDER_ID, productDeliveredEvent.getProductOrderId());
        variables.put(FollowUpConstants.PRODUCT_ORDER_ITEM_ID, productDeliveredEvent.getProductOrderItemId());
        variables.put(FollowUpConstants.PRODUCT_ORDER_STATE, productDeliveredEvent.getRelatedProductOrderState());
        variables.put(FollowUpConstants.PRODUCT_ORDER_ITEM_EVENT_ID, productDeliveredEvent.getProductOrderItemEventId());
        log.debug("Product event variables - productOrderId: [{}], productOrderItemId: [{}], productOrderState: [{}], productOrderItemEventId: [{}]",
                productDeliveredEvent.getProductOrderId(),
                productDeliveredEvent.getProductOrderItemId(),
                productDeliveredEvent.getRelatedProductOrderState(),
                productDeliveredEvent.getProductOrderItemEventId());
        return variables;
    }
}
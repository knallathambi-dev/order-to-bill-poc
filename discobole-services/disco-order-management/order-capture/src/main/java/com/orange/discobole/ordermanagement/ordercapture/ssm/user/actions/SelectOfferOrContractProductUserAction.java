// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.user.actions;

import com.orange.discobole.ordermanagement.ordercapture.enums.ReferredType;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PickMainOfferOrContractProduct;
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

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.util.MiscUtil.getFirstChannelId;

@Component("OrderCapture.selectOfferOrContract")
@Slf4j
public class SelectOfferOrContractProductUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecList;
    private List<CharacteristicValueSpecification> specificationValueList;

    @PostConstruct
    public void init() {
        try {
            characteristicSpecList = new ArrayList<>();
            specificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT_CLASS);
            CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(DEFAULT_ID, PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT_CLASS, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY, specificationValueList, null, OBJECT_NAME);
            characteristicSpecList.add(characteristicSpecification);
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Perform select offer or contract product user action");
        PickMainOfferOrContractProduct pickMainOfferOrContractProduct = CharacteristicUtil.getCharacteristicValue(taskFlowUpdate.getCharacteristic(), characteristicSpecList, PickMainOfferOrContractProduct.class);
        return getSelectOfferOrContractProductVariables(taskFlowUpdate, pickMainOfferOrContractProduct);
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.info("Creating the required characteristics for selecting an offer or contract product");
        String characteristicId = CharacteristicUtil.createCharacteristicSpecificationId(stateMachineTransition.getTaskDefinitionId(), PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT_CLASS);
        CharacteristicSpecification characteristicSpecification = CharacteristicUtil.createCharacteristicSpecification(characteristicId, PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT_CLASS, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY, specificationValueList, null, OBJECT_NAME);
        return Collections.singletonList(characteristicSpecification);
    }

    private Map<String, Object> getSelectOfferOrContractProductVariables(TaskFlowUpdate taskFlowUpdate, PickMainOfferOrContractProduct pickMainOfferOrContractProduct) {
        final Map<String, Object> variables = new HashMap<>();

        ReferredType referredType = pickMainOfferOrContractProduct.getReferredType();
        variables.put(REFERRED_TYPE, referredType);

        variables.put(referredType == ReferredType.PRODUCT_OFFERING ? PRODUCT_OFFERING_ID : CONTRACT_PRODUCT_ID, pickMainOfferOrContractProduct.getId());

        if (referredType == ReferredType.PRODUCT_OFFERING) {
            variables.put(CONTRACT_PRODUCT_ID, StringUtils.EMPTY);
        } else {
            variables.put(PRODUCT_OFFERING_ID, StringUtils.EMPTY);
        }

        String optionalProductOfferingId = pickMainOfferOrContractProduct.getOptionalProductOfferingId();

        if (StringUtils.isNotBlank(optionalProductOfferingId)) {
            variables.put(OPTIONAL_PRODUCT_OFFERING_ID, optionalProductOfferingId);
        } else {
            variables.put(OPTIONAL_PRODUCT_OFFERING_ID, StringUtils.EMPTY);
        }

        variables.put(CHANNEL_ID, getFirstChannelId(taskFlowUpdate.getChannel()));

        return variables;
    }
}
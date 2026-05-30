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
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PickMainOfferOrContractProduct;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class IdentifyPartyUserActionTest {
    public static final String TASK_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PARTY_NAME = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PARTY_REFERRED_TYPE = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_REFERRED_TYPE = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_ROLE = "customer";
    public static final String NOT_PARTY_IDENTIFIER = "NotPartyIdentifier";
    public static final String PARTY_IDENTIFIER = "PartyIdentifier";

    @InjectMocks
    private IdentifyPartyUserAction identifyPartyUserAction;

    @BeforeEach
    void setUp() {
        identifyPartyUserAction.init();
    }

    @DisplayName("given null characteristics " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(null);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty characteristics " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.emptyList());

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null  characteristics name " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristicsName() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        Characteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(null);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given  characteristics name not PickMainOfferFromCommercialCatalog " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testCharacteristicsNameNotPickMainOfferFromCommercialCatalog() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        Characteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(NOT_PARTY_IDENTIFIER);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null characteristics value " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristicsValue() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PARTY_IDENTIFIER);
        characteristic.setValue(null);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null product offering id " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullProductOfferingId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PARTY_IDENTIFIER);
        PickMainOfferOrContractProduct pickMainOfferOrContractProduct = new PickMainOfferOrContractProduct();
        pickMainOfferOrContractProduct.setId(null);
        characteristic.setValue(pickMainOfferOrContractProduct);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty product offering id " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyProductOfferingId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PARTY_IDENTIFIER);
        PickMainOfferOrContractProduct commercialCatalog = new PickMainOfferOrContractProduct();
        commercialCatalog.setId("");
        characteristic.setValue(commercialCatalog);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given duplicate Characteristic name " +
            "when perform identify party user action " +
            "then throw InvalidParameterException")
    @Test
    void testDuplicateCharacteristicName() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PARTY_IDENTIFIER);
        PickMainOfferOrContractProduct commercialCatalog = new PickMainOfferOrContractProduct();
        commercialCatalog.setId("");
        characteristic.setValue(commercialCatalog);
        List<Characteristic> characteristicList = new ArrayList<>();
        characteristicList.add(characteristic);
        characteristicList.add(characteristic);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(characteristicList);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> identifyPartyUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given a valid characteristic for party " +
            "when perform identify party user action " +
            "then return variable list")
    @Test
    void testValidCharacteristicForProductOffering() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PARTY_IDENTIFIER);
        PartyIdentifier partyIdentifier = new PartyIdentifier();
        partyIdentifier.setPartyId(DEFAULT_PARTY_ID);
        partyIdentifier.setPartyName(DEFAULT_PARTY_NAME);
        partyIdentifier.setReferredType(DEFAULT_PARTY_REFERRED_TYPE);
        characteristic.setValue(partyIdentifier);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        Map<String, Object> responseResult = identifyPartyUserAction.perform(null, taskFlowUpdate);

        //then
        Assertions.assertEquals(DEFAULT_PARTY_ID, responseResult.get(OrderCaptureConstants.PARTY_ID));
        Assertions.assertEquals(DEFAULT_PARTY_NAME, responseResult.get(OrderCaptureConstants.PARTY_NAME));
    }

    @DisplayName("given a valid task definition id " +
            "when get required Characteristics " +
            "then return required characteristics list")
    @Test
    void testRequiredCharacteristics() {
        //given
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setTaskDefinitionId(TASK_ID);

        //when
        List<CharacteristicSpecification> requiredCharacteristics = identifyPartyUserAction.requiredCharacteristics(stateMachineTransition, new HashMap<>());
        //then
        assertThat(requiredCharacteristics).isNotEmpty();
        Assertions.assertEquals(OrderCaptureConstants.PARTY_IDENTIFIER_CLASS, requiredCharacteristics.get(0).getName());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMaxCardinality());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMinCardinality());
    }

    private TaskFlowUpdate getTaskFlowUpdate() {
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId(DEFAULT_RELATED_PARTY_ID);
        relatedParty.setReferredType(DEFAULT_REFERRED_TYPE);
        relatedParty.setRole(DEFAULT_ROLE);
        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        taskFlowUpdate.setRelatedParty(Collections.singletonList(relatedParty));
        return taskFlowUpdate;
    }
}
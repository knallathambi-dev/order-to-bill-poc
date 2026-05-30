// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.user.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.enums.ConfigurationState;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.ConfirmConfigurationIsProcessed;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import lombok.Getter;
import lombok.Setter;
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
class ConfirmConfigurationUserActionTest {

    public static final String DEFAULT_CONFIGURATION_ID = RandomStringUtils.randomAlphabetic(5);
    public static final ConfigurationState DEFAULT_CONFIGURATION_STATE = ConfigurationState.VALIDATED;
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_CHANNEL_NAME = RandomStringUtils.randomAlphabetic(5);
    public static final String NOT_PROVIDE_VALID_CONFIGURATION = "NotProvideValidConfiguration";
    public static final String CONFIRM_CONFIGURATION_IS_PROCESSED = "ConfirmConfigurationIsProcessed";
    public static final String DEFAULT_ROLE = "customer";
    public static final String NOT_VALID_STATUS = "Not_Valid_status";
    public static final String DEFAULT_REFERRED_TYPE = "referredType";
    public static final String TASK_ID = RandomStringUtils.randomAlphabetic(8);

    @InjectMocks
    private ConfirmConfigurationUserAction confirmConfigurationUserAction;

    @BeforeEach
    void setUp() {
        confirmConfigurationUserAction.init();
    }

    @DisplayName("given null characteristics " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(null);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty characteristics " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.emptyList());

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null  characteristics name " +
            "when perform confirm configuration user action " +
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
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given characteristics name not ProvideValidConfiguration " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testCharacteristicsNameNotProvideValidConfiguration() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        Characteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(NOT_PROVIDE_VALID_CONFIGURATION);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null characteristics value " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristicsValue() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        characteristic.setValue(null);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null configuration id " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullConfigurationId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        ConfirmConfigurationIsProcessed confirmConfigurationIsProcessed = new ConfirmConfigurationIsProcessed();
        confirmConfigurationIsProcessed.setId(null);
        characteristic.setValue(confirmConfigurationIsProcessed);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty configuration id " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyConfigurationId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        ConfirmConfigurationIsProcessed confirmConfigurationIsProcessed = new ConfirmConfigurationIsProcessed();
        confirmConfigurationIsProcessed.setId("");
        characteristic.setValue(confirmConfigurationIsProcessed);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null  configuration status  " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullConfigurationStatus() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        ConfirmConfigurationIsProcessed confirmConfigurationIsProcessed = new ConfirmConfigurationIsProcessed();
        confirmConfigurationIsProcessed.setId(DEFAULT_CONFIGURATION_ID);
        characteristic.setValue(confirmConfigurationIsProcessed);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));
        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given invalid configuration status  " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testInvalidConfigurationStatus() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        ConfirmConfigurationIsProcessedTest invalidConfigurationStatus = new ConfirmConfigurationIsProcessedTest();
        invalidConfigurationStatus.setId(DEFAULT_CONFIGURATION_ID);
        invalidConfigurationStatus.setState(NOT_VALID_STATUS);
        characteristic.setValue(invalidConfigurationStatus);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given duplicate characteristic name " +
            "when perform confirm configuration user action " +
            "then throw InvalidParameterException")
    @Test
    void testDuplicateCharacteristicName() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        ConfirmConfigurationIsProcessed confirmConfigurationIsProcessed = new ConfirmConfigurationIsProcessed();
        confirmConfigurationIsProcessed.setId("");
        characteristic.setValue(confirmConfigurationIsProcessed);
        List<Characteristic> characteristicList = new ArrayList<>();
        characteristicList.add(characteristic);
        characteristicList.add(characteristic);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(characteristicList);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> confirmConfigurationUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given a valid characteristic " +
            "when perform confirm configuration user action " +
            "then return variable list")
    @Test
    void testValidCharacteristic() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(CONFIRM_CONFIGURATION_IS_PROCESSED);
        ConfirmConfigurationIsProcessed confirmConfigurationIsProcessed = new ConfirmConfigurationIsProcessed();
        confirmConfigurationIsProcessed.setId(DEFAULT_CONFIGURATION_ID);
        confirmConfigurationIsProcessed.setState(DEFAULT_CONFIGURATION_STATE);
        characteristic.setValue(confirmConfigurationIsProcessed);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        Map<String, Object> responseResult = confirmConfigurationUserAction.perform(null, taskFlowUpdate);

        //then
        Assertions.assertEquals(DEFAULT_CONFIGURATION_ID, responseResult.get(OrderCaptureConstants.CONFIGURATION_ID));
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
        List<CharacteristicSpecification> requiredCharacteristics = confirmConfigurationUserAction.requiredCharacteristics(stateMachineTransition, new HashMap<>());

        //then
        assertThat(requiredCharacteristics).isNotEmpty();
        Assertions.assertEquals(OrderCaptureConstants.CONFIRM_CONFIGURATION_IS_PROCESSED_CLASS, requiredCharacteristics.get(0).getName());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMaxCardinality());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMinCardinality());
    }

    private TaskFlowUpdate getTaskFlowUpdate() {
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId(DEFAULT_RELATED_PARTY_ID);
        ChannelRef channelRef = new ChannelRef();
        channelRef.setName(DEFAULT_CHANNEL_NAME);
        relatedParty.setReferredType(DEFAULT_REFERRED_TYPE);
        relatedParty.setRole(DEFAULT_ROLE);
        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        taskFlowUpdate.setRelatedParty(Collections.singletonList(relatedParty));
        taskFlowUpdate.setChannel(Collections.singletonList(channelRef));
        return taskFlowUpdate;
    }

    @Getter
    @Setter
    private static class ConfirmConfigurationIsProcessedTest {
        @JsonProperty("configuration.id")
        private String id;

        @JsonProperty("configuration.state")
        private String state;
    }
}
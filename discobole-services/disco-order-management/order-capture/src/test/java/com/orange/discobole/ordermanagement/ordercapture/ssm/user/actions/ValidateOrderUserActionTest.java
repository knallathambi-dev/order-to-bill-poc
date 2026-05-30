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
import com.orange.discobole.ordermanagement.ordercapture.enums.ValidationStatus;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.ValidateOrderByCustomer;
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
class ValidateOrderUserActionTest {
    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(5);
    public static final ValidationStatus DEFAULT_VALIDATION_STATUS = ValidationStatus.VALIDATED;
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_CHANNEL_NAME = RandomStringUtils.randomAlphabetic(5);
    public static final String NOT_PROVIDE_VALID_VALIDATION = "NotProvideValidValidation";
    public static final String VALIDATE_ORDER_BY_CUSTOMER = "ValidateOrderByCustomer";
    public static final String DEFAULT_ROLE = "customer";
    public static final String NOT_VALID_STATUS = "Not_Valid_status";
    public static final String DEFAULT_REFERRED_TYPE = "referredType";
    public static final String TASK_ID = RandomStringUtils.randomAlphabetic(8);
    @InjectMocks
    private ValidateOrderUserAction validateOrderUserAction;


    @BeforeEach
    void setUp() {
        validateOrderUserAction.init();
    }

    @DisplayName("given null characteristics " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(null);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty characteristics " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.emptyList());

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null  characteristics name " +
            "when perform validate order user action " +
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
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given  characteristics name not Provide Valid Validation " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testCharacteristicsNameNotProvideValidValidation() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        Characteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(NOT_PROVIDE_VALID_VALIDATION);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null characteristics value " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristicsValue() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        characteristic.setValue(null);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null product Order id " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullProductOrderIdId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        ValidateOrderByCustomer validateOrderByCustomer = new ValidateOrderByCustomer();
        validateOrderByCustomer.setId(null);
        characteristic.setValue(validateOrderByCustomer);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty product order id " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyProductOrderId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        ValidateOrderByCustomer validateOrderByCustomer = new ValidateOrderByCustomer();
        validateOrderByCustomer.setId("");
        characteristic.setValue(validateOrderByCustomer);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null  order validation status  " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullOrderValidationStatus() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        ValidateOrderByCustomer validateOrderByCustomer = new ValidateOrderByCustomer();
        validateOrderByCustomer.setId(DEFAULT_PRODUCT_ORDER_ID);
        characteristic.setValue(validateOrderByCustomer);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given Invalid  order validation status  " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testInvalidOrderValidationStatus() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        ValidateOrderByCustomerTest invalidOrderStatus = new ValidateOrderByCustomerTest();
        invalidOrderStatus.setId(DEFAULT_PRODUCT_ORDER_ID);
        invalidOrderStatus.setStatus(NOT_VALID_STATUS);
        characteristic.setValue(invalidOrderStatus);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given duplicate Characteristic name " +
            "when perform validate order user action " +
            "then throw InvalidParameterException")
    @Test
    void testDuplicateCharacteristicName() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        ValidateOrderByCustomer validateOrderByCustomer = new ValidateOrderByCustomer();
        validateOrderByCustomer.setId("");
        characteristic.setValue(validateOrderByCustomer);
        List<Characteristic> characteristicList = new ArrayList<>();
        characteristicList.add(characteristic);
        characteristicList.add(characteristic);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(characteristicList);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> validateOrderUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given a valid Characteristic " +
            "when perform validate order user action " +
            "then return variable list")
    @Test
    void testValidCharacteristic() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(VALIDATE_ORDER_BY_CUSTOMER);
        ValidateOrderByCustomer validateOrderByCustomer = new ValidateOrderByCustomer();
        validateOrderByCustomer.setId(DEFAULT_PRODUCT_ORDER_ID);
        validateOrderByCustomer.setStatus(DEFAULT_VALIDATION_STATUS);
        characteristic.setValue(validateOrderByCustomer);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        Map<String, Object> responseResult = validateOrderUserAction.perform(null, taskFlowUpdate);

        //then
        Assertions.assertEquals(DEFAULT_PRODUCT_ORDER_ID, responseResult.get(OrderCaptureConstants.PRODUCT_ORDER_ID));
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
        List<CharacteristicSpecification> requiredCharacteristics = validateOrderUserAction.requiredCharacteristics(stateMachineTransition, new HashMap<>());
        //then
        assertThat(requiredCharacteristics).isNotEmpty();
        Assertions.assertEquals(OrderCaptureConstants.VALIDATE_ORDER_BY_CUSTOMER_CLASS, requiredCharacteristics.get(0).getName());
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
    public static class ValidateOrderByCustomerTest {
        @JsonProperty("productOrderId")
        private String id;
        @JsonProperty("orderValidationStatus")
        private String status;
    }
}
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
import com.orange.discobole.ordermanagement.ordercapture.enums.ReferredType;
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
class SelectOfferOrContractProductUserActionTest {
    public static final String TASK_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_CHANNEL_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String NOT_PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT = "NotPickMainOfferOrContractProduct";
    public static final String PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT = "PickMainOfferOrContractProduct";

    @InjectMocks
    private SelectOfferOrContractProductUserAction selectOfferOrContractProductUserAction;

    @BeforeEach
    void setUp() {
        selectOfferOrContractProductUserAction.init();
    }

    @DisplayName("given null characteristics " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(null);

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty characteristics " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyCharacteristics() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.emptyList());

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null  characteristics name " +
            "when perform select offer user action " +
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
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given  characteristics name not PickMainOfferFromCommercialCatalog " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testCharacteristicsNameNotPickMainOfferFromCommercialCatalog() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        Characteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(NOT_PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null characteristics value " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullCharacteristicsValue() {
        //given
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
        characteristic.setValue(null);
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given null product offering id " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testNullProductOfferingId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
        PickMainOfferOrContractProduct pickMainOfferOrContractProduct = new PickMainOfferOrContractProduct();
        pickMainOfferOrContractProduct.setId(null);
        characteristic.setValue(pickMainOfferOrContractProduct);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given empty product offering id " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testEmptyProductOfferingId() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
        PickMainOfferOrContractProduct commercialCatalog = new PickMainOfferOrContractProduct();
        commercialCatalog.setId("");
        characteristic.setValue(commercialCatalog);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        //then
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given duplicate Characteristic name " +
            "when perform select offer user action " +
            "then throw InvalidParameterException")
    @Test
    void testDuplicateCharacteristicName() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
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
        Assertions.assertThrows(InvalidParameterException.class, () -> selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate));
    }

    @DisplayName("given a valid Characteristic for product " +
            "when perform select offer user action " +
            "then return variable list")
    @Test
    void testValidCharacteristicForProduct() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
        PickMainOfferOrContractProduct pickMainOfferOrContractProduct = new PickMainOfferOrContractProduct();
        pickMainOfferOrContractProduct.setId(DEFAULT_PRODUCT_ID);
        pickMainOfferOrContractProduct.setReferredType(ReferredType.PRODUCT);
        pickMainOfferOrContractProduct.setOptionalProductOfferingId(DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID);
        characteristic.setValue(pickMainOfferOrContractProduct);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        Map<String, Object> responseResult = selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate);

        //then
        Assertions.assertEquals(DEFAULT_PRODUCT_ID, responseResult.get(OrderCaptureConstants.CONTRACT_PRODUCT_ID));
        Assertions.assertEquals(ReferredType.PRODUCT, responseResult.get(OrderCaptureConstants.REFERRED_TYPE));
        Assertions.assertEquals(DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID, responseResult.get(OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID));
        Assertions.assertEquals(DEFAULT_CHANNEL_ID, responseResult.get(OrderCaptureConstants.CHANNEL_ID));
    }

    @DisplayName("given a valid Characteristic for product offering " +
            "when perform select offer user action " +
            "then return variable list")
    @Test
    void testValidCharacteristicForProductOffering() {
        //given
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setName(PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT);
        PickMainOfferOrContractProduct pickMainOfferOrContractProduct = new PickMainOfferOrContractProduct();
        pickMainOfferOrContractProduct.setId(DEFAULT_PRODUCT_OFFERING_ID);
        pickMainOfferOrContractProduct.setReferredType(ReferredType.PRODUCT_OFFERING);
        pickMainOfferOrContractProduct.setOptionalProductOfferingId(DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID);
        characteristic.setValue(pickMainOfferOrContractProduct);
        TaskFlowUpdate taskFlowUpdate = getTaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(Collections.singletonList(characteristic));

        //when
        Map<String, Object> responseResult = selectOfferOrContractProductUserAction.perform(null, taskFlowUpdate);

        //then
        Assertions.assertEquals(DEFAULT_PRODUCT_OFFERING_ID, responseResult.get(OrderCaptureConstants.PRODUCT_OFFERING_ID));
        Assertions.assertEquals(ReferredType.PRODUCT_OFFERING, responseResult.get(OrderCaptureConstants.REFERRED_TYPE));
        Assertions.assertEquals(DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID, responseResult.get(OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID));
        Assertions.assertEquals(DEFAULT_CHANNEL_ID, responseResult.get(OrderCaptureConstants.CHANNEL_ID));
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
        List<CharacteristicSpecification> requiredCharacteristics = selectOfferOrContractProductUserAction.requiredCharacteristics(stateMachineTransition, new HashMap<>());
        //then
        assertThat(requiredCharacteristics).isNotEmpty();
        Assertions.assertEquals(OrderCaptureConstants.PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT_CLASS, requiredCharacteristics.get(0).getName());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMaxCardinality());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMinCardinality());
    }

    private TaskFlowUpdate getTaskFlowUpdate() {
        ChannelRef channelRef = new ChannelRef();
        channelRef.setId(DEFAULT_CHANNEL_ID);
        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        taskFlowUpdate.setChannel(Collections.singletonList(channelRef));
        return taskFlowUpdate;
    }
}
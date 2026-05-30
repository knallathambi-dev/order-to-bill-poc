// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.user.actions;

import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.AppointmentRef;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.BillingAccountRef;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PaymentRef;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PaymentRefIdentifier;
import com.orange.discobole.ordermanagement.ordercapture.util.CharacteristicUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;

@Component("OrderCapture.completeOrder")
@Slf4j
public class CompleteOrderUserAction implements UserAction {
    private List<CharacteristicSpecification> characteristicSpecificationList;
    private List<CharacteristicValueSpecification> paymentRefSpecificationValueList;
    private List<CharacteristicValueSpecification> baRefSpecificationValueList;
    private List<CharacteristicValueSpecification> appointmentSpecificationValueList;

    @PostConstruct
    public void init() {
        try {
            characteristicSpecificationList = new ArrayList<>();
            paymentRefSpecificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(PAYMENT_REF_CLASS);
            baRefSpecificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(BILLING_ACCOUNT_REF_CLASS);
            appointmentSpecificationValueList = CharacteristicUtil.getCharacteristicSpecificationValue(APPOINTMENT_REF_CLASS);

            characteristicSpecificationList.add(
                    CharacteristicUtil.createCharacteristicSpecification(
                            DEFAULT_ID, PAYMENT_REF_CLASS, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY,
                            paymentRefSpecificationValueList, null, OBJECT_NAME));

            characteristicSpecificationList.add(
                    CharacteristicUtil.createCharacteristicSpecification(
                            DEFAULT_ID, BILLING_ACCOUNT_REF_CLASS, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY,
                            baRefSpecificationValueList, null, OBJECT_NAME));

            characteristicSpecificationList.add(
                    CharacteristicUtil.createCharacteristicSpecification(
                            DEFAULT_ID, APPOINTMENT_REF_CLASS, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY,
                            appointmentSpecificationValueList, null, OBJECT_NAME));
        } catch (Exception e) {
            log.error("Error initializing CompleteOrderUserAction", e);
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        log.info("Perform complete order user action");
        Map<String, List<String>> paymentRefCharacteristics = new HashMap<>();
        Map<String, String> baRefCharacteristics = new HashMap<>();
        Map<String, String> appointmentRefCharacteristics = new HashMap<>();

        for (Characteristic characteristic : taskFlowUpdate.getCharacteristic()) {
            if (PAYMENT_REF.equals(characteristic.getName())) {
                processPaymentRefItems(characteristic, taskFlowUpdate, paymentRefCharacteristics);
            } else if (BILLING_ACCOUNT_REF.equals(characteristic.getName())) {
                processBaRefItems(characteristic, taskFlowUpdate, baRefCharacteristics);
            } else if (APPOINTMENT_REF.equals(characteristic.getName())) {
                processAppointmentRefItems(characteristic, taskFlowUpdate, appointmentRefCharacteristics);
            }
        }

        return Map.of(
                ORDER_ITEM_PAYMENT_REF_MAP, paymentRefCharacteristics,
                ORDER_ITEM_BILLING_ACCOUNT_REF_MAP, baRefCharacteristics,
                ORDER_ITEM_APPOINTMENT_REF_MAP, appointmentRefCharacteristics
        );
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        log.info("Creating the required characteristics for complete order");
        List<CharacteristicSpecification> characteristicSpecifications = new ArrayList<>();
        Map<String, String> processedOrderItems = new HashMap<>();

        if (contextVariables == null || contextVariables.isEmpty()) {
            log.warn("Context variables are empty or null");
            return characteristicSpecifications;
        }

        List<String> unpaidOrderItemIds = StateMachineUtil.getListValue(contextVariables, UNPAID_ORDER_ITEM_IDS, String.class);
        if (!CollectionUtils.isEmpty(unpaidOrderItemIds)) {
            unpaidOrderItemIds.forEach(orderItemId -> characteristicSpecifications.addAll(createCharacteristics(orderItemId, PAYMENT_REF_CLASS, processedOrderItems, characteristicSpecifications, paymentRefSpecificationValueList)));
        }

        List<String> orderItemIdsRequiringBARef = StateMachineUtil.getListValue(contextVariables, ORDER_ITEM_IDS_REQUIRING_BA_REF, String.class);
        if (!CollectionUtils.isEmpty(orderItemIdsRequiringBARef)) {
            orderItemIdsRequiringBARef.forEach(orderItemId -> characteristicSpecifications.addAll(createCharacteristics(orderItemId, BILLING_ACCOUNT_REF_CLASS, processedOrderItems, characteristicSpecifications, baRefSpecificationValueList)));
        }

        List<String> orderItemsIdsRequiringAppointmentRef = StateMachineUtil.getListValue(contextVariables, ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, String.class);
        if (!CollectionUtils.isEmpty(orderItemsIdsRequiringAppointmentRef)) {
            orderItemsIdsRequiringAppointmentRef
                    .forEach(orderItemId -> characteristicSpecifications
                            .addAll(createCharacteristics(
                                    orderItemId, APPOINTMENT_REF_CLASS, processedOrderItems,
                                    characteristicSpecifications, appointmentSpecificationValueList)));
        }

        return characteristicSpecifications;
    }

    private void processPaymentRefItems(Characteristic characteristic, TaskFlowUpdate taskFlowUpdate, Map<String, List<String>> characteristics) {
        List<String> ids = getPaymentRefIds(characteristic, characteristicSpecificationList);
        for (Characteristic orderItemCharacteristic : taskFlowUpdate.getCharacteristic()) {
            if (isOrderItem(characteristic, orderItemCharacteristic)) {
                characteristics.put((String) orderItemCharacteristic.getValue(), ids);
                break;
            }
        }
    }

    private void processBaRefItems(Characteristic characteristic, TaskFlowUpdate taskFlowUpdate, Map<String, String> characteristics) {
        String id = getBARefIds(characteristic, characteristicSpecificationList);
        for (Characteristic orderItemCharacteristic : taskFlowUpdate.getCharacteristic()) {
            if (isOrderItem(characteristic, orderItemCharacteristic)) {
                characteristics.put((String) orderItemCharacteristic.getValue(), id);
                break;
            }
        }
    }

    private void processAppointmentRefItems(Characteristic characteristic, TaskFlowUpdate taskFlowUpdate, Map<String, String> orderItemIdAppointmentRefMap) {
        String id = getAppointmentRefIds(characteristic, characteristicSpecificationList);
        for (Characteristic orderItemCharacteristic : taskFlowUpdate.getCharacteristic()) {
            if (isOrderItem(characteristic, orderItemCharacteristic)) {
                orderItemIdAppointmentRefMap.put((String) orderItemCharacteristic.getValue(), id);
                break;
            }
        }
    }

    private List<CharacteristicSpecification> createCharacteristics(String orderItemId, String className, Map<String, String> processedOrderItems, List<CharacteristicSpecification> characteristicSpecifications, List<CharacteristicValueSpecification> specificationValueList) {
        List<CharacteristicSpecification> characteristics = new ArrayList<>();
        String characteristicId = UUID.randomUUID().toString();

        CharacteristicSpecification characteristic = CharacteristicUtil.createCharacteristicSpecification(
                characteristicId, className, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY, specificationValueList, null, OBJECT_NAME);
        characteristics.add(characteristic);

        String existingOrderItemCharacteristicId = processedOrderItems.get(orderItemId);
        if (StringUtils.isNotBlank(existingOrderItemCharacteristicId)) {
            Optional<CharacteristicSpecification> orderItemCharacteristic = characteristicSpecifications.stream()
                    .filter(c -> c.getId().equals(existingOrderItemCharacteristicId))
                    .findFirst();

            if (orderItemCharacteristic.isPresent()) {
                List<CharacteristicSpecificationRelationship> relationships = new ArrayList<>(orderItemCharacteristic.get().getCharacteristicSpecificationRelationship());

                relationships.add(new CharacteristicSpecificationRelationship()
                        .id(characteristicId)
                        .relationshipType(REQUIRES));
                orderItemCharacteristic.get().setCharacteristicSpecificationRelationship(relationships);
            } else {
                log.warn("OrderItem characteristic not found for orderItemId: {}", orderItemId);
            }
        } else {
            String orderItemCharacteristicId = UUID.randomUUID().toString();
            processedOrderItems.put(orderItemId, orderItemCharacteristicId);

            characteristics.add(CharacteristicUtil.createCharacteristicSpecification(
                    orderItemCharacteristicId, ORDER_ITEM, DEFAULT_MIN_CARDINALITY, DEFAULT_MAX_CARDINALITY,
                    List.of(new StringCharacteristicValueSpecification().value(orderItemId).type(StringCharacteristicValueSpecification.class.getSimpleName())),
                    List.of(new CharacteristicSpecificationRelationship().id(characteristicId).relationshipType(REQUIRES)),
                    OBJECT_NAME));
        }

        return characteristics;
    }

    private List<String> getPaymentRefIds(Characteristic characteristic, List<CharacteristicSpecification> characteristicSpecificationList) {
        return CharacteristicUtil.getCharacteristicValue(
                        List.of(characteristic),
                        characteristicSpecificationList,
                        PaymentRef.class,
                        characteristic.getName())
                .getPaymentRefIdentifier()
                .stream()
                .map(PaymentRefIdentifier::getId)
                .toList();
    }

    private String getBARefIds(Characteristic characteristic, List<CharacteristicSpecification> characteristicSpecificationList) {
        return CharacteristicUtil.getCharacteristicValue(
                        List.of(characteristic),
                        characteristicSpecificationList,
                        BillingAccountRef.class,
                        characteristic.getName())
                .getBillingAccountRefIdentifier();
    }

    private String getAppointmentRefIds(Characteristic characteristic, List<CharacteristicSpecification> characteristicSpecificationList) {
        return CharacteristicUtil.getCharacteristicValue(
                        List.of(characteristic),
                        characteristicSpecificationList,
                        AppointmentRef.class,
                        characteristic.getName())
                .getAppointmentRefIdentifier();
    }

    private boolean isOrderItem(Characteristic characteristic, Characteristic orderItemCharacteristic) {
        return (PAYMENT_REF.equals(characteristic.getName()) ||
                BILLING_ACCOUNT_REF.equals(characteristic.getName()) ||
                APPOINTMENT_REF.equals(characteristic.getName())) &&
                ORDER_ITEM.equals(orderItemCharacteristic.getName()) && orderItemCharacteristic.getCharacteristicRelationship().stream()
                .anyMatch(rel -> rel.getId().equals(characteristic.getId()));
    }
}
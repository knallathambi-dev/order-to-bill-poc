// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toast} from "react-toastify";
import {patchTaskFlow} from "../api/processFlowService";
import {generatePrefixedRandomId, generateRandomId} from "../../utlis/helpers";
import {createCompleteOrderRequest} from "../../utlis/processFlowRequestBodyUtils";
import {hasErrorDescription} from "../../utlis/utils";

const createCharacteristicPair = (refName, refValue, orderItemId) => {
    const characteristicId = generateRandomId();

    return [
        {
            name: refName,
            id: characteristicId,
            valueType: "Object",
            value: refValue,
            "@type": "ObjectCharacteristic"
        },
        {
            name: "OrderItem",
            value: orderItemId,
            characteristicRelationship: [{id: characteristicId, relationshipType: "requires"}],
            valueType: "Object",
            "@type": "ObjectCharacteristic"
        }
    ];
};

const buildPaymentCharacteristics = (paymentRefs) => {
    if (!paymentRefs?.length) return [];

    return paymentRefs.flatMap(orderItemId => {
        const paymentRefId = generatePrefixedRandomId('12');
        return createCharacteristicPair(
            "PaymentRef",
            {paymentRefId: [{id: paymentRefId}]},
            orderItemId
        );
    });
};

const buildBillingCharacteristics = (billingAccountRefs, billingAccountId) => {
    if (!billingAccountRefs?.length) return [];

    return billingAccountRefs.flatMap(orderItemId =>
        createCharacteristicPair(
            "BillingAccountRef",
            {baRefId: billingAccountId},
            orderItemId
        )
    );
};

const buildAppointmentCharacteristics = (appointmentRefs) => {
    if (!appointmentRefs?.length) return [];

    return appointmentRefs.flatMap(orderItemId => {
        const appointmentRefId = generatePrefixedRandomId('14');
        return createCharacteristicPair(
            "AppointmentRef",
            {appRefId: appointmentRefId},
            orderItemId
        );
    });
};

const buildCharacteristics = (paymentRefs, billingAccountRefs, billingAccountId, appointmentRefs) => {
    return [
        ...buildPaymentCharacteristics(paymentRefs),
        ...buildBillingCharacteristics(billingAccountRefs, billingAccountId),
        ...buildAppointmentCharacteristics(appointmentRefs)
    ];
};

const isOrderCompletionFailed = (response) => {
    if (!response) return true;

    const nextTasks = response?.data?._links?.nextTaskstoBePerformed ?? [];

    return nextTasks.length !== 0 || hasErrorDescription(response);
};

export const submitOrder = async ({
                                      paymentRefItems,
                                      billingAccountRefItems,
                                      billingAccountId,
                                      appointmentRefItems,
                                      relatedParty,
                                      nextTaskToBePerformed,
                                      navigate,
                                      dispatch,
                                      toggleLoading,
                                      tNotification
                                  }) => {
    dispatch(toggleLoading(true));

    const characteristics = buildCharacteristics(
        paymentRefItems,
        billingAccountRefItems,
        billingAccountId,
        appointmentRefItems
    );
    try {

        const reqBody = createCompleteOrderRequest(characteristics, relatedParty);
        const response = await patchTaskFlow(nextTaskToBePerformed, reqBody);

        if (isOrderCompletionFailed(response)) {
            toast.error(tNotification("completeOrder.completeFailed"));
            return;
        }

        navigate("/orderStatusUpdate", {state: {orderStatus: "confirmed"}});

    } catch (error) {
        toast.error(tNotification("completeOrder.completeFailed"));
    } finally {
        dispatch(toggleLoading(false));
    }
};
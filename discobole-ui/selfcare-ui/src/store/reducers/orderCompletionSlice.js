// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    SET_APPOINTMENT_REF_ITEMS,
    SET_BILLING_ACCOUNT_ID,
    SET_BILLING_ACCOUNT_REF_ITEMS,
    SET_PAYMENT_REF_ITEMS,
} from "../actions/orderCompletionActions";

const initialState = {
    paymentRefItems: [],
    billingAccountRefItems: [],
    appointmentRefItems: [],
    billingAccountId: {},
};

export default function orderCompletionSlice(state = initialState, action) {
    switch (action.type) {
        case SET_PAYMENT_REF_ITEMS:
            return {...state, paymentRefItems: action.payload};
        case SET_BILLING_ACCOUNT_REF_ITEMS:
            return {...state, billingAccountRefItems: action.payload};
        case SET_APPOINTMENT_REF_ITEMS:
            return {...state, appointmentRefItems: action.payload};
        case SET_BILLING_ACCOUNT_ID:
            return {
                ...state,
                billingAccountId: {
                    ...state.billingAccountId,
                    [action.payload.relatedPartyId]: action.payload.billingAccountId,
                },
            };
        default:
            return state;
    }
}
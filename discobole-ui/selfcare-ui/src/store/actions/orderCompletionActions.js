// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_PAYMENT_REF_ITEMS = 'SET_PAYMENT_REF_ITEMS';
export const SET_BILLING_ACCOUNT_REF_ITEMS = 'SET_BILLING_ACCOUNT_REF_ITEMS';
export const SET_APPOINTMENT_REF_ITEMS = 'SET_APPOINTMENT_REF_ITEMS';
export const SET_BILLING_ACCOUNT_ID = 'SET_BILLING_ACCOUNT_ID';

export const setPaymentRefItems = (items) => ({
    type: SET_PAYMENT_REF_ITEMS,
    payload: items,
});

export const setBillingAccountRefItems = (items) => ({
    type: SET_BILLING_ACCOUNT_REF_ITEMS,
    payload: items,
});

export const setAppointmentRefItems = (items) => ({
    type: SET_APPOINTMENT_REF_ITEMS,
    payload: items,
});

export const setBillingAccountId = (relatedPartyId, billingAccountId) => ({
    type: SET_BILLING_ACCOUNT_ID,
    payload: {relatedPartyId, billingAccountId},
});
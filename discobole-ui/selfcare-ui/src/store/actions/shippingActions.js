// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_SHIPPING_INFO = 'SET_SHIPPING_INFO';
export const SET_SHIPPING_METHOD = 'SET_SHIPPING_METHOD';
export const SET_SHIPPING_CONFIGURATIONS_ITEMS = 'SET_SHIPPING_CONFIGURATIONS_ITEMS';

export const setShippingInfo = (address, deliveryDate) => ({
    type: SET_SHIPPING_INFO,
    payload: {address, deliveryDate},
});

export const setShippingMethod = (value) => ({
    type: SET_SHIPPING_METHOD,
    value,
});

export const setShippingConfigItems = (value) => ({
    type: SET_SHIPPING_CONFIGURATIONS_ITEMS,
    payload: value,
});
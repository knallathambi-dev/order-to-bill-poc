// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_PRICING = 'SET_PRICING';
export const SET_SHIPPING_PRICE = 'SET_SHIPPING_PRICE';

export const setPricing = (pricing) => ({
    type: SET_PRICING,
    payload: pricing,
});

export const setShippingPrice = (shippingPrice) => ({
    type: SET_SHIPPING_PRICE,
    payload: shippingPrice,
});
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_PRICING, SET_SHIPPING_PRICE} from '../actions/pricingActions';

const INITIAL_STATE = {
    pricing: null,
    shippingPrice: null,
};

export default function pricingSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case SET_PRICING:
            return {
                ...state,
                pricing: action.payload,
            };
        case SET_SHIPPING_PRICE:
            return {
                ...state,
                shippingPrice: action.payload,
            };
        default:
            return state;
    }
}
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_SHIPPING_CONFIGURATIONS_ITEMS, SET_SHIPPING_INFO, SET_SHIPPING_METHOD} from "../actions/shippingActions";

const initialState = {
    shippingType: 2,
    address: '',
    deliveryDate: '',
    itemsRequiringShipping: [],
};

const shippingSlice = (state = initialState, action) => {
    switch (action.type) {
        case SET_SHIPPING_METHOD:
            return {
                ...state,
                shippingType: action.value,
            };
        case SET_SHIPPING_INFO:
            return {
                ...state,
                address: action.payload.address,
                deliveryDate: action.payload.deliveryDate,
            };
        case SET_SHIPPING_CONFIGURATIONS_ITEMS:
            return {
                ...state,
                shippingConfigItems: action.payload,
            };
        default:
            return state;
    }
};

export default shippingSlice;
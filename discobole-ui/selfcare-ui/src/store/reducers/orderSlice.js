// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {ORDER_TYPE, SET_PRODUCT_ORDER_ID} from "../actions/orderActions";

const INITIAL_STATE = {
    productOrderId: "",
    orderType: ""
};
export default function orderSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case SET_PRODUCT_ORDER_ID:
            return {
                ...state,
                productOrderId: action.value
            }
        case ORDER_TYPE:
            return {
                ...state,
                orderType: action.value
            }
        default:
            return state
    }
}
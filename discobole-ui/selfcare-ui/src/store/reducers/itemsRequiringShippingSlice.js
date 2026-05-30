// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_ITEMS_REQUIRING_SHIPPING} from "../actions/itemsRequiringShipping";

const initialState = {
    itemsRequiringShipping: [],
};

const itemsRequiringShippingSlice = (state = initialState, action) => {
    switch (action.type) {
        case SET_ITEMS_REQUIRING_SHIPPING:
            return {
                ...state,
                itemsRequiringShipping: [...action.payload],
            };
        default:
            return state;
    }
};

export default itemsRequiringShippingSlice;
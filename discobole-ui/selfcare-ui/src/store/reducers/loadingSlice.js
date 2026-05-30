// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {TOGGLE_LOADING} from "../actions/loadingActions";

const INITIAL_STATE = {
    isLoading: false
};
export default function loadingSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case TOGGLE_LOADING:
            return {
                ...state,
                isLoading: action.value
            }
        default:
            return state
    }
}
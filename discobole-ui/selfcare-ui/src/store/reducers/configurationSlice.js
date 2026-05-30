// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_CONFIGURATION_ID,} from "../actions/configurationActions";

const INITIAL_STATE = {
    configurationId: "",
};

export default function configurationSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case SET_CONFIGURATION_ID:
            return {...state, configurationId: action.payload};
        default:
            return state;
    }
}
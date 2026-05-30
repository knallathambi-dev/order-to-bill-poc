// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_IS_EDIT} from "../actions/editActions";

const initialState = {
    isEdit: false,
};

const editSlice = (state = initialState, action) => {
    switch (action.type) {
        case SET_IS_EDIT:
            return {
                ...state,
                isEdit: action.payload,
            };
        default:
            return state;
    }
};

export default editSlice;
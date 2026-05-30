// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {ADD_BREADCRUMB, RESET_BREADCRUMB} from "../actions/breadcrumbActions";

const INITIAL_STATE = {
    history: [],
};

export default function breadcrumbSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case ADD_BREADCRUMB: {
            const title = action.title;

            if (!title) {
                return state;
            }

            // If this is the first item being added after a page refresh/navigation
            // and it already exists in history, find where it should be and truncate
            const existingIndex = state.history.indexOf(title);
            if (existingIndex !== -1) {
                // Keep history up to this point and don't add duplicate
                return {
                    ...state,
                    history: state.history.slice(0, existingIndex + 1)
                };
            }

            // Prevent duplicate consecutive items
            const lastItem = state.history[state.history.length - 1];
            if (lastItem === title) {
                return state;
            }

            return {
                ...state,
                history: [...state.history, title]
            };
        }

        case RESET_BREADCRUMB: {
            return INITIAL_STATE;
        }

        default:
            return state;
    }
}
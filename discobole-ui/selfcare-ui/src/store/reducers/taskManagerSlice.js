// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {CANCEL_TASK_FLOW, UPDATE_NEXT_TASK} from "../actions/taskManagerActions";

const INITIAL_STATE = {
    nextTaskToBePerformed: '',
    cancelTaskFlow: ''
};

export default function taskManagerSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case UPDATE_NEXT_TASK:
            return {
                ...state,
                nextTaskToBePerformed: action.value,
            };
        case CANCEL_TASK_FLOW:
            return {
                ...state,
                cancelTaskFlow: action.value,
            };
        default:
            return state;
    }
}
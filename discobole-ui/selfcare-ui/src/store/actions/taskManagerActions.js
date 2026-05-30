// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {removeBaseUrl} from "../../utlis/helpers";

export const UPDATE_NEXT_TASK = "UPDATE_NEXT_TASK";
export const CANCEL_TASK_FLOW = "CANCEL_TASK_FLOW";

export const updateNextTaskAction = (value) => {
    return {
        type: UPDATE_NEXT_TASK,
        value: removeBaseUrl(value)
    };
};

export const cancelTaskFlowAction = (value) => {
    return {
        type: CANCEL_TASK_FLOW,
        value: removeBaseUrl(value)
    };
};
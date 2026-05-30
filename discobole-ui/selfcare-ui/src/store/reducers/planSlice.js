// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_CURRENT_PLAN} from "../actions/planAction";

const INITIAL_STATE = {
    planId: "",
    planName: "",
    planProductOfferingId: "",
    planDetails: null,
};

export default function planSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case SET_CURRENT_PLAN: {
            const v = action.value ?? {};
            return {
                ...state,
                ...(Object.prototype.hasOwnProperty.call(v, "planId") ? {planId: v.planId} : {}),
                ...(Object.prototype.hasOwnProperty.call(v, "planName") ? {planName: v.planName} : {}),
                ...(Object.prototype.hasOwnProperty.call(v, "planProductOfferingId") ? {planProductOfferingId: v.planProductOfferingId} : {}),
                ...(Object.prototype.hasOwnProperty.call(v, "planDetails") ? {planDetails: v.planDetails} : {}),
            };
        }
        default:
            return state;
    }
}
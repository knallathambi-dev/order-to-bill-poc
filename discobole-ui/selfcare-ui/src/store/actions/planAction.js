// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_CURRENT_PLAN = "SET_CURRENT_PLAN";

export const setCurrentPlan = (value) => ({
    type: SET_CURRENT_PLAN,
    value,
});
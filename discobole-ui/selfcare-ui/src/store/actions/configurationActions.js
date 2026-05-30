// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_CONFIGURATION_ID = "SET_CONFIGURATION_ID";

export const setConfigurationId = (id) => ({
    type: SET_CONFIGURATION_ID,
    payload: id,
});
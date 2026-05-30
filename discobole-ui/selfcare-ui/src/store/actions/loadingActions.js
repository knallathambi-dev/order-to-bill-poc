// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const TOGGLE_LOADING = "TOGGLE_LOADING"

export const toggleLoading = (value) => {
    return {type: TOGGLE_LOADING, value};
}
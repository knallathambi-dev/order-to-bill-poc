// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const ADD_BREADCRUMB = "ADD_BREADCRUMB";
export const RESET_BREADCRUMB = "RESET_BREADCRUMB";

export const addBreadcrumb = (title) => ({
    type: ADD_BREADCRUMB,
    title,
});

export const resetBreadcrumb = () => ({
    type: RESET_BREADCRUMB,
});
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_IS_EDIT = 'SET_IS_EDIT';

export const setIsEdit = (isEdit) => ({
    type: SET_IS_EDIT,
    payload: isEdit,
});
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_PRODUCT_ORDER_ID = "SET_PRODUCT_ORDER_ID"
export const ORDER_TYPE = "ORDER_TYPE"

export const setProductOrderId = (value) => {
    return {type: SET_PRODUCT_ORDER_ID, value};
}

export const setOrderType = (value) => {
    return {type: ORDER_TYPE, value};
}
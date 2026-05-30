// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import apiClient from "../api/apiClient";

export const fetchOrder = async (orderId) => {
    const {data} = await apiClient.get(
        `${process.env.REACT_APP_ORDER_INVENTORY_URL}/${orderId}`
    );
    return data;
};
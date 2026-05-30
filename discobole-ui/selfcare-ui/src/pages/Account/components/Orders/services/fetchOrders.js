// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toast} from "react-toastify";
import apiClient from "../../../../../services/api/apiClient";

const orderInventoryURL = process.env.REACT_APP_ORDER_INVENTORY_URL;

export const fetchAllOrders = async (relatedPartyId, tNotification) => {
    const params = new URLSearchParams({
        limit: 5,
        sort: '-creationDate',
        offset: 0,
        'relatedParty.id': relatedPartyId,
    });

    try {
        const response = await apiClient.get(`${orderInventoryURL}?${params}`);
        return response.data;
    } catch (error) {
        toast.error(tNotification("myOrders.fetchOrdersFailed"));
    }
};
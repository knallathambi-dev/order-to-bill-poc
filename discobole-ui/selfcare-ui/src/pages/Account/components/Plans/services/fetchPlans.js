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

const fetchPlans = async (relatedPartyId, tNotification) => {
    const productInventoryURL = process.env.REACT_APP_PRODUCT_INVENTORY_URL;

    const params = new URLSearchParams({
        limit: 3,
        offset: 0,
        sort: '-creationDate',
        'productOffering.@type': 'Contract',
        'relatedParty.partyOrPartyRole.id': relatedPartyId,
    });

    try {
        const response = await apiClient.get(`${productInventoryURL}?${params}`);
        return response.data.map(item => ({
            id: item.id,
            name: item.productOffering.name,
            contractOfferId: item.productOffering.id,
            creationDate: item.creationDate,
            terminationDate: item.terminationDate,
            status: item.status,
            operationalStatus: item.operationalStatus,
            productRelationship: item.productRelationship
        }));
    } catch (error) {
        toast.error(tNotification("myPlans.fetchPlansFailed"));
    }
};

export default fetchPlans;
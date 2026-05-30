// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {ATOMIC_TYPE} from "../../../../../utlis/constants";
import {toast} from "react-toastify";
import apiClient from "../../../../../services/api/apiClient";

const fetchPlansDetailsByIds = async (ids, tNotification) => {
    const queryParams = new URLSearchParams({
        'productRelationship.relationshipType': 'rootProduct',
        'productOffering.@type': `${ATOMIC_TYPE},ProductOfferingRef`,
    });

    ids.forEach(id => {
        queryParams.append('productRelationship.product.id', id);
    });

    const productInventoryURL = `${process.env.REACT_APP_PRODUCT_INVENTORY_URL}?${queryParams.toString()}`;

    try {
        const response = await apiClient.get(productInventoryURL);
        const products = response.data;

        return ids.reduce((acc, id) => {
            acc[id] = products.filter(product =>
                product.productRelationship?.some(rel => rel.product?.id === id)
            );
            return acc;
        }, {});
    } catch (error) {
        toast.error(tNotification("myPlans.fetchPlanDetailsFailed"));
        return null;
    }
};

export default fetchPlansDetailsByIds;
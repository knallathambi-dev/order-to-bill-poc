// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import api from "./OrderInventoryAPI.js";

class OrderInventoryService {

    /* ---- Orders ---- */

    fetchOrders = async ({fields, limit, sort, offset, filters}) => {
        const params = new URLSearchParams({fields, limit, sort, offset});

        if (filters) {
            Object.entries(filters).forEach(([key, value]) => {
                if (value) params.set(key, value);
            });
        }

        return api.getAllProductOrders(params);
    };

    /* ---- Cross-service Lookups ---- */

    getContractProductId = async (orderId) => {
        try {
            const products = await api.getProductsByOrderId(orderId);

            if (products.length === 1) return products[0].id;

            const rootRel = products
                .flatMap((product) => product.productRelationship ?? [])
                .find((rel) => rel.relationshipType === "rootProduct");

            return rootRel?.product.id ?? null;
        } catch {
            return null;
        }
    };

    getOrchestrationPlanId = async (orderId) => {
        try {
            const plans = await api.getOrchestrationPlans(orderId);
            return plans[0]?.id ?? null;
        } catch {
            return null;
        }
    };
}

export default new OrderInventoryService();
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import api from './OrchestrationDeliveryAPI';

class OrchestrationDeliveryService {

    /* ---- Cross-service lookups ---- */

    getOrderId = async (orderId) => {
        try {
            const order = await api.getProductOrder(orderId);
            return order?.id ?? null;
        } catch {
            return null;
        }
    };

    getContractProductId = async (orderId) => {
        try {
            const products = await api.getProductsByOrderId(orderId);

            if (products.length === 1) return products[0].id;

            const rootRel = products
                .flatMap((product) => product.productRelationship ?? [])
                .find((rel) => rel.relationshipType === 'rootProduct');

            return rootRel?.product.id ?? null;
        } catch {
            return null;
        }
    };
}

export default new OrchestrationDeliveryService();
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {httpClient} from "@discobole/common-ui";
import {ORCHESTRATION_SERVICE_URL, ORDER_INVENTORY_URL, PRODUCT_INVENTORY_URL,} from '../utils/constants.js';

const ENDPOINTS = {
    productOrder: `${ORDER_INVENTORY_URL}/v1/productOrder`,
    product: `${PRODUCT_INVENTORY_URL}/v1/product`,
    orchestrationPlan: `${ORCHESTRATION_SERVICE_URL}/orchestrationPlan`,
};

class OrchestrationDeliveryAPI {

    /* ---- Generic ---- */

    get = async (url, params) => {
        const fullUrl = params ? `${url}?${params}` : url;
        return httpClient.get(fullUrl);
    };

    patch = async (url, data) => {
        return httpClient.patch(url, data);
    };

    /* ---- Orders ---- */

    getProductOrder = async (orderId) => {
        const {data} = await httpClient.get(`${ENDPOINTS.productOrder}/${orderId}`);
        return data;
    };

    /* ---- Products ---- */

    getProductsByOrderId = async (orderId) => {
        const {data} = await httpClient.get(
            `${ENDPOINTS.product}?productOrderItem.productOrderId=${orderId}`,
        );
        return data;
    };
}

export default new OrchestrationDeliveryAPI();
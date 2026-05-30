// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {httpClient} from "@discobole/common-ui";
import {
    ORCHESTRATION_SERVICE_URL,
    ORDER_CAPTURE_URL,
    ORDER_INVENTORY_URL,
    PRODUCT_INVENTORY_URL,
} from "../utils/constants.js";

const API = {
    productOrder: `${ORDER_INVENTORY_URL}/v1/productOrder`,
    taskSettings: `${ORDER_CAPTURE_URL}/v1/setting`,
    product: `${PRODUCT_INVENTORY_URL}/v1/product`,
    orchestrationPlan: `${ORCHESTRATION_SERVICE_URL}/orchestrationPlan`,
};

class OrderInventoryAPI {

    /* ---- Orders ---- */

    getProductOrder = async (id) => {
        const {data} = await httpClient.get(`${API.productOrder}/${id}`);
        return data;
    };

    getAllProductOrders = async (params) => {
        const {data, headers} = await httpClient.get(API.productOrder, {params});
        return {data, totalCount: parseInt(headers["x-total-count"] ?? "0", 10)};
    };

    /* ---- Task Settings ---- */

    getTaskSettings = async () => {
        const {data} = await httpClient.get(API.taskSettings);
        return data;
    };

    saveTaskSettings = async (taskSettings) => {
        const {data} = await httpClient.post(API.taskSettings, taskSettings);
        return data;
    };

    /* ---- Cross-service Lookups ---- */

    getProductsByOrderId = async (orderId) => {
        const {data} = await httpClient.get(
            `${API.product}?productOrderItem.productOrderId=${orderId}`
        );
        return data;
    };

    getOrchestrationPlans = async (orderId) => {
        const {data} = await httpClient.get(API.orchestrationPlan, {
            params: {"relatedProductOrder.id": orderId, fields: "id"},
        });
        return data;
    };
}

export default new OrderInventoryAPI();
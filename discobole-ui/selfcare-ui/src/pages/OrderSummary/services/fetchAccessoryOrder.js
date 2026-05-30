// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {calculateItemPricing} from "../../../utlis/helpers";
import {SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE} from "../../../utlis/constants";
import {toast} from "react-toastify";
import {fetchOrder} from "../../../services";

const fetchAccessoryOrder = async (productOrderId, tNotification) => {
    try {
        const data = await fetchOrder(productOrderId, tNotification);
        const relatedParty = data.relatedParty?.[0];

        const shippingItem = data.productOrderItem?.find(
            (item) => item.product?.productSpecification?.["@baseType"] === SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE);
        const shippingCharacteristics = shippingItem?.product?.productCharacteristic || [];

        const items = data.productOrderItem
            ?.filter(item => item.product?.productSpecification?.["@baseType"] !== SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE)
            .map(item => {
                const existingCharacteristics = item.product?.productCharacteristic || [];
                return {
                    ...item,
                    product: {
                        ...item.product,
                        productCharacteristic: [
                            ...existingCharacteristics,
                            ...shippingCharacteristics,
                        ],
                    },
                };
            }) || [];

        const shippingPrice = calculateItemPricing(shippingItem?.itemPrice);

        return {
            ...data,
            relatedParty,
            items,
            shippingItem,
            shippingPrice,
        };
    } catch (error) {
        toast.error(tNotification("common.fetchOrderFailed"));
    }
};

export default fetchAccessoryOrder;
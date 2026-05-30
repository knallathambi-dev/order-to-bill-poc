// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE} from "../../../../utlis/constants";

export const extractDeviceConfiguration = (configs) => {
    let configurationItem = null;
    let shippingConfigurationItem = null;

    for (const item of configs.computedProductConfigurationItem) {
        const baseType = item.productConfiguration?.productSpecification?.["@baseType"];

        if (baseType === SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE) {
            if (!shippingConfigurationItem) {
                shippingConfigurationItem = item;
            }
        } else {
            if (!configurationItem) {
                configurationItem = item;
            }
        }

        if (configurationItem && shippingConfigurationItem) break;
    }

    if (!configurationItem) {
        return {
            configurationItem: null,
            configurationPrice: [],
            productOffering: {},
            configurableCharacteristics: [],
            shippingConfigurationItem: shippingConfigurationItem || null,
        };
    }

    const {productConfiguration = {}, id} = configurationItem;
    const {
        configurationPrice = [],
        productOffering = {},
        configurationCharacteristic = [],
    } = productConfiguration;

    const configurableCharacteristics = configurationCharacteristic.filter(c => c.isConfigurable);

    return {
        configurationItem,
        id,
        configurationPrice,
        productOffering,
        configurableCharacteristics,
        shippingConfigurationItem: shippingConfigurationItem || null,
    };
};
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createChannel, createRelatedPartyArray} from "../../../utlis/utils";

const findCharacteristicId = (characteristics, name) => {
    const characteristic = characteristics.find(cc => cc.name.toLowerCase() === name.toLowerCase());
    return characteristic ? characteristic.id : null;
};

const createRequestBodyShipping = (
    configurationId,
    relatedParty,
    shippingItems,
    shippingMode,
    shippingAddress,
    shippingDate
) => {
    const relatedPartyArray = createRelatedPartyArray(relatedParty);

    const requestProductConfigurationItems = shippingItems.map(item => {
        const {id, productConfiguration} = item;
        const {configurationCharacteristic} = productConfiguration;

        const shippingModeId = findCharacteristicId(configurationCharacteristic, "shipping mode");
        const shippingAddressId = findCharacteristicId(configurationCharacteristic, "shipping address");
        const deliveryDateId = findCharacteristicId(configurationCharacteristic, "requested delivery date");

        const configurationCharacteristics = [];

        if (shippingMode) {
            configurationCharacteristics.push({
                id: shippingModeId,
                configurationCharacteristicValues: [
                    {
                        isSelected: true,
                        characteristic: {
                            id: shippingModeId,
                            value: `${shippingMode}`,
                            "@type": "StringCharacteristic"
                        },
                        "@type": "StringCharacteristic"
                    }
                ]
            });
        }

        if (shippingAddress) {
            configurationCharacteristics.push({
                id: shippingAddressId,
                configurationCharacteristicValues: [
                    {
                        isSelected: true,
                        characteristic: {
                            id: shippingAddressId,
                            value: `${shippingAddress}`,
                            "@type": "StringCharacteristic"
                        },
                        "@type": "StringCharacteristic"
                    }
                ]
            });
        }

        if (shippingDate) {
            configurationCharacteristics.push({
                id: deliveryDateId,
                configurationCharacteristicValues: [
                    {
                        isSelected: true,
                        characteristic: {
                            id: deliveryDateId,
                            value: `${shippingDate}`,
                            "@type": "DateCharacteristic"
                        },
                        "@type": "DateCharacteristic"
                    }
                ]
            });
        }

        return {
            id: `${id}`,
            productConfiguration: {
                id: `${productConfiguration.id}`,
                isSelected: true,
                configurationAction: [
                    {
                        action: "add",
                        isSelected: true,
                        "@type": "ConfigurationAction",
                    }
                ],
                configurationCharacteristic: configurationCharacteristics,
                "@type": "ProductConfiguration"
            },
            "@type": "QueryProductConfigurationItem"
        };
    });

    return {
        id: `${configurationId}`,
        channel: createChannel(),
        ...(relatedPartyArray.length > 0 && {relatedParty: relatedPartyArray}),
        requestProductConfigurationItem: requestProductConfigurationItems
    };
};

export default createRequestBodyShipping;
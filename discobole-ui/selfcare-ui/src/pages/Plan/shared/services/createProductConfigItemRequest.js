// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createChannel, createRelatedPartyArray} from "../../../../utlis/utils";

const createRequestBodyConfigurationItem = (
    configId,
    configurationItemId,
    characteristics,
    relatedParty,
    isSelected,
    action
) => {
    const relatedPartyArray = createRelatedPartyArray(relatedParty);

    const characteristicsArray = Array.isArray(characteristics)
        ? characteristics
        : characteristics
            ? [characteristics]
            : [];

    const configurationCharacteristic = characteristicsArray.length > 0
        ? characteristicsArray.map((char) => ({
            id: char.id,
            configurationCharacteristicValues: [
                {
                    isSelected: true,
                    characteristic: {
                        id: char.id,
                        value: char.value,
                        "@type": char.type,
                    },
                },
            ],
        }))
        : [];

    return {
        id: `${configId}`,
        channel: createChannel(),
        ...(relatedPartyArray.length > 0 && {relatedParty: relatedPartyArray}),
        requestProductConfigurationItem: [
            {
                id: `${configurationItemId}`,
                productConfiguration: {
                    isSelected: isSelected,
                    ...(characteristicsArray.length > 0 && {configurationCharacteristic: configurationCharacteristic}),
                    configurationAction: [
                        {
                            action: action,
                            isSelected: isSelected,
                            "@type": "ConfigurationAction",
                        },
                    ],
                    "@type": "ProductConfiguration",
                },
                "@type": "QueryProductConfigurationItem",
            },
        ],
    };
};

export default createRequestBodyConfigurationItem;
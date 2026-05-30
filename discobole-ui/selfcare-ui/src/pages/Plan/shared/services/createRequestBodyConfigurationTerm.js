// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createChannel, createRelatedPartyArray} from "../../../../utlis/utils";

const createRequestBodyConfigurationTerm = (
    configurationId,
    configurationItemId,
    configurationTermDuration,
    relatedParty
) => {
    const relatedPartyArray = createRelatedPartyArray(relatedParty);

    return {
        id: `${configurationId}`,
        channel: createChannel(),
        ...(relatedPartyArray.length > 0 && {relatedParty: relatedPartyArray}),
        requestProductConfigurationItem: [
            {
                id: configurationItemId,
                productConfiguration: {
                    isSelected: true,
                    configurationAction: [
                        {
                            action: "add",
                            isSelected: true,
                            "@type": "ConfigurationAction",
                        }
                    ],
                    configurationTerm: [
                        {
                            duration: configurationTermDuration,
                            isSelected: true,
                        }
                    ],
                    "@type": "ProductConfiguration"
                },
                "@type": "QueryProductConfigurationItem"
            }
        ]
    };
};

export default createRequestBodyConfigurationTerm;
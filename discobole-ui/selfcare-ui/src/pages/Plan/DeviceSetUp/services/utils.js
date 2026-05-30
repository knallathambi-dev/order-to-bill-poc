// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const getProductType = (productOffering) => {
    const productName = productOffering.name?.toLowerCase() || '';

    const productMappings = [
        {keywords: ["smartview wallet galaxy a55 case"], type: "A55case"},
        {keywords: ["mobile phone eco case for samsung s10"], type: "phone"},
        {keywords: ["samsung galaxy buds2 ear buds"], type: "buds"},
        {keywords: ["vr"], type: "VR"},
    ];

    for (const {keywords, type} of productMappings) {
        if (keywords.some(keyword => productName.includes(keyword))) {
            return type;
        }
    }

    return "Default";
};
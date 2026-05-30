// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    buildPricingResult,
    createCurrencyManager,
    createPricingState,
    processCurrentPrice
} from "../../../../utlis/utils";

export const calculatePricingForProducts = (products = []) => {
    const state = createPricingState();
    const currencyManager = createCurrencyManager();

    products.forEach(product => {
        const productPrices = product.productPrice || [];

        productPrices.forEach(productPrice => {
            processCurrentPrice(productPrice, state, currencyManager.updateCurrency);
        });
    });

    return buildPricingResult(state, currencyManager.getCurrency());
};
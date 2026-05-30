// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {formatRecurringChargePeriod, roundAmount} from "../../../utlis/helpers";
import {
    addToRecurringChargeMap,
    buildPricingResult,
    createCurrencyManager,
    createPricingState,
    processCurrentPrice,
    processDiscount
} from "../../../utlis/utils";
import {INSTALLMENT_CHARGE_TYPE} from "../../../utlis/constants";

const processBasePriceForOrder = (priceItem, state, updateCurrency) => {
    const productOfferingPrice = priceItem.productOfferingPrice;
    if (!productOfferingPrice?.price) return;

    const {price} = productOfferingPrice;
    const currencyCode = price.unit;
    const totalPrice = roundAmount(price.value, currencyCode);

    updateCurrency(currencyCode);

    if (priceItem.recurringChargePeriod) {
        const formattedPeriod = formatRecurringChargePeriod(priceItem.recurringChargePeriod);
        addToRecurringChargeMap(state.baseRc, formattedPeriod, totalPrice);
    } else {
        state.baseNrc += totalPrice;
    }
};

const processBasePriceForOrderPreview = (priceItem, state, updateCurrency) => {
    const productOfferingPrice = priceItem.productOfferingPrice;
    if (!productOfferingPrice?.price) return;

    const {price} = productOfferingPrice;
    const isInstallmentCharge = productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE;

    let totalPrice;
    if (isInstallmentCharge) {
        const taxIncludedAmount = priceItem?.price?.taxIncludedAmount?.value;
        if (taxIncludedAmount != null) {
            const interestRate = productOfferingPrice?.interestRate || 0;
            totalPrice = roundAmount(taxIncludedAmount * (1 + interestRate / 100));
            updateCurrency(priceItem?.price?.taxIncludedAmount?.unit || price.unit);
        } else {
            const basePriceWithTax = roundAmount(price.value);
            const interestRate = productOfferingPrice?.interestRate || 0;
            totalPrice = roundAmount(basePriceWithTax * (1 + interestRate / 100));
            updateCurrency(price.unit);
        }
    } else {
        totalPrice = roundAmount(price.value);
        updateCurrency(price.unit);
    }

    if (priceItem.recurringChargePeriod) {
        const formattedPeriod = formatRecurringChargePeriod(priceItem.recurringChargePeriod);
        addToRecurringChargeMap(state.baseRc, formattedPeriod, totalPrice);
    } else {
        state.baseNrc += totalPrice;
    }
};

export const calculatePricingForOrder = (order = {}) => {
    const state = createPricingState();
    const currencyManager = createCurrencyManager();

    const orderTotalPrices = order.orderTotalPrice || [];

    orderTotalPrices.forEach(orderPrice => {
        processCurrentPrice(orderPrice, state, currencyManager.updateCurrency);
    });

    const productOrderItems = order.productOrderItem || [];
    productOrderItems.forEach(orderItem => {
        const itemPrices = orderItem.itemPrice || [];
        itemPrices.forEach(itemPrice => {
            processBasePriceForOrder(itemPrice, state, currencyManager.updateCurrency);
            processDiscount(itemPrice, state, currencyManager.updateCurrency);
        });
    });

    return buildPricingResult(state, currencyManager.getCurrency());
};

export const getOrderPreviewPricing = (order = {}) => {
    const state = createPricingState();
    const currencyManager = createCurrencyManager();

    const orderTotalPrices = order.orderTotalPrice || [];
    const productOrderItems = order.productOrderItem || [];
    const hasItemPrices = productOrderItems.some(
        (item) => Array.isArray(item.itemPrice) && item.itemPrice.length > 0
    );

    if (hasItemPrices) {
        productOrderItems.forEach((orderItem) => {
            (orderItem.itemPrice || []).forEach((itemPrice) => {
                processCurrentPrice(itemPrice, state, currencyManager.updateCurrency);
            });
        });
    } else {
        orderTotalPrices.forEach((orderPrice) => {
            processCurrentPrice(orderPrice, state, currencyManager.updateCurrency);
        });
    }

    productOrderItems.forEach((orderItem) => {
        (orderItem.itemPrice || []).forEach((itemPrice) => {
            processBasePriceForOrderPreview(itemPrice, state, currencyManager.updateCurrency);
            processDiscount(itemPrice, state, currencyManager.updateCurrency);
        });
    });

    return buildPricingResult(state, currencyManager.getCurrency());
};
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    formatCurrencyAmount,
    getCurrencyFractionDigits,
    getCurrencySymbol,
    roundCurrencyAmount,
} from "@discobole/common-ui";
import {INSTALLMENT_CHARGE_TYPE} from "../../../utils/constants.js";

// Re-export so existing consumers don't break
export {getCurrencyFractionDigits, getCurrencySymbol, formatCurrencyAmount, roundCurrencyAmount};

const PRICE_TYPES = {
    NON_RECURRING_CHARGE: "nonRecurringCharge",
    RECURRING_CHARGE: "recurringCharge",
};

const ALTERATION_TYPES = {
    NON_RECURRING_DISCOUNT: "nonRecurringDiscount",
    RECURRING_DISCOUNT: "recurringDiscount",
    TAX: "TaxProductOfferingPriceAlteration",
};

const UNIT_ABBREVIATIONS = {
    month: "M",
    months: "M",
    week: "W",
    weeks: "W",
    day: "D",
    days: "D",
};

export const ORDER_STATUSES = {
    accepted: "Accepted",
    acknowledged: "Acknowledged",
    assessingCancellation: "Assessing cancellation",
    cancelled: "Cancelled",
    completed: "Completed",
    draft: "Draft",
    failed: "Failed",
    held: "Held",
    inProgress: "In progress",
    partial: "Partial",
    pending: "Pending",
    pendingCancellation: "Pending cancellation",
    rejected: "Rejected",
};

export const DELETE_ACTION = {
    delete: "terminate",
};

const toNumber = (value) => {
    const n = Number(value);
    return Number.isFinite(n) ? n : 0;
};

export const roundAmount = (amount, currencyCode) =>
    roundCurrencyAmount(amount, currencyCode);

const capitalizeFirstLetter = (text) =>
    text ? text.charAt(0).toUpperCase() + text.slice(1) : text;

export const formatActionName = (action) => {
    const mappedAction = DELETE_ACTION[action.toLowerCase()] || action;
    return capitalizeFirstLetter(mappedAction);
};

export const getFormattedSelectedDuration = (orderTerms) => {
    if (!Array.isArray(orderTerms) || orderTerms.length === 0) return null;

    const duration = orderTerms[0].duration;
    if (!duration || typeof duration.amount !== "number") return "";

    const {amount, units} = duration;
    const normalizedUnits = units?.toLowerCase();
    const abbreviation = UNIT_ABBREVIATIONS[normalizedUnits] || units?.charAt(0).toUpperCase() || "";

    return `${amount} ${abbreviation}`;
};

export const formatRecurringChargePeriod = (recurringChargePeriod) => {
    if (!recurringChargePeriod) return '';

    const {amount, units} = recurringChargePeriod;
    if (amount == null || units == null || amount === 0) return '';

    const normalizedUnits = units.toLowerCase();

    if (amount === 1) return normalizedUnits;
    if (amount === 30 && normalizedUnits === 'day') return 'month';
    if (amount === 7 && normalizedUnits === 'day') return 'week';

    return `${amount} ${normalizedUnits}`;
};

export const formatApplicationDuration = (applicationDuration, withParentheses = true) => {
    if (!applicationDuration) return '';
    const {amount, units} = applicationDuration;
    const normalized = units.toLowerCase();
    const text = amount === 1
        ? `for 1 ${normalized}`
        : `for ${amount} ${normalized}${amount > 1 ? 's' : ''}`;
    return withParentheses ? `(${text})` : text;
};

export const extractShippingItem = (orderData) =>
    orderData.productOrderItem?.find(
        (item) => item.product?.productSpecification?.["@baseType"] === "ShippingProductSpecification"
    );

export const extractShippingDetails = (orderData) => {
    const shippingItem = extractShippingItem(orderData);
    if (!shippingItem) return null;

    const findCharacteristic = (name) =>
        shippingItem.product?.productCharacteristic?.find(
            (char) => char.name.toLowerCase() === name.toLowerCase()
        )?.value;

    return {
        shippingMode: findCharacteristic("shipping mode"),
        shippingAddress: findCharacteristic("shipping address"),
        requestedDeliveryDay: findCharacteristic("requested delivery date"),
    };
};

export const renderPrices = (pricing, currency = "") => {
    const hasChargesData = pricing && (pricing.nrc > 0 || (Array.isArray(pricing.rc) && pricing.rc.length > 0));
    if (!hasChargesData) return currency ? `${currency} 0` : "0";

    const symbol = getCurrencySymbol(currency);
    const priceComponents = [];

    if (pricing.nrc !== 0) {
        priceComponents.push(`${symbol} ${formatCurrencyAmount(pricing.nrc, currency)}`);
    }

    if (Array.isArray(pricing.rc)) {
        pricing.rc.forEach((charge) => {
            priceComponents.push(`${symbol} ${formatCurrencyAmount(charge.total, currency)}/${charge.recurringChargePeriod}`);
        });
    }

    return priceComponents.join(" + ");
};

const hasValidApplicationDuration = (duration) => duration?.amount > 0;

export const extractCurrentPricesAndDiscounts = (itemPrices) => {
    if (!Array.isArray(itemPrices)) return {currentPrices: [], discounts: []};

    const priceGroups = {};
    const discounts = [];

    for (const item of itemPrices) {
        const taxIncluded = item.price?.taxIncludedAmount;
        if (taxIncluded?.value != null) {
            const key = `${item.recurringChargePeriod}_${item.applicationDuration}`;
            priceGroups[key] ??= {
                totalAmount: 0,
                currency: taxIncluded.unit,
                chargePeriod: item.recurringChargePeriod,
                duration: item.applicationDuration,
            };
            priceGroups[key].totalAmount += toNumber(taxIncluded.value);
        }

        const alterations = Array.isArray(item.priceAlterations)
            ? item.priceAlterations
            : Array.isArray(item.priceAlteration) ? item.priceAlteration : [];
        if (!alterations.length) continue;

        const basePrice = item.productOfferingPrice?.price;
        const totalPrice =
            basePrice?.value != null && Number.isFinite(toNumber(basePrice.value))
                ? toNumber(basePrice.value)
                : undefined;

        for (const alt of alterations) {
            if (alt?.["@type"] === ALTERATION_TYPES.TAX) continue;

            const taxAmt = alt.price?.taxIncludedAmount;
            const percentage = alt.price?.percentage;
            const altValue = taxAmt?.value != null ? toNumber(taxAmt.value) : undefined;
            const altCurrency = taxAmt?.unit || basePrice?.unit;
            const symbol = getCurrencySymbol(altCurrency);
            const showPeriod = !hasValidApplicationDuration(alt.applicationDuration);
            const suffix = (v) =>
                v > 0 && alt.recurringChargePeriod && showPeriod
                    ? `/${formatRecurringChargePeriod(alt.recurringChargePeriod)}`
                    : "";

            let formatted = "";
            if (percentage != null && totalPrice != null) {
                const amt = (totalPrice * toNumber(percentage)) / 100;
                formatted = `${percentage}% (${symbol} ${formatCurrencyAmount(amt, altCurrency)}${suffix(amt)})`;
            } else if (altValue != null) {
                formatted = `${symbol} ${formatCurrencyAmount(altValue, altCurrency)}${suffix(altValue)}`;
            }
            if (!formatted) continue;

            const hasOffset = typeof alt.applicationOffset === "number" && alt.applicationOffset > 0;
            discounts.push({
                totalPrice: formatted,
                applicationDuration: formatApplicationDuration(alt.applicationDuration, !hasOffset),
                applicationOffset: hasOffset
                    ? {offset: alt.applicationOffset, unit: item.recurringChargePeriod?.units}
                    : undefined,
            });
        }
    }

    let groups = Object.values(priceGroups);
    const hasRecurring = groups.some((g) => Boolean(g.chargePeriod));
    if (hasRecurring && groups.some((g) => !g.chargePeriod)) {
        groups = groups.filter((g) => g.totalAmount !== 0);
    }

    const currentPrices = groups.map(({totalAmount, currency, chargePeriod, duration}) => {
        const pfx = totalAmount > 0 && chargePeriod ? `/${formatRecurringChargePeriod(chargePeriod)}` : "";
        return {
            totalPrice: `${getCurrencySymbol(currency)} ${formatCurrencyAmount(totalAmount, currency)}${pfx}`,
            applicationDuration: formatApplicationDuration(duration),
        };
    });

    return {currentPrices, discounts};
};

const createRecurringChargeArray = (chargeMap) => {
    const charges = [];
    chargeMap.forEach((total, period) => charges.push({recurringChargePeriod: period, total}));
    return charges;
};

const addToRecurringChargeMap = (chargeMap, period, value) => {
    if (value) chargeMap.set(period, (chargeMap.get(period) || 0) + value);
};

const processPrice = (priceItem, state, updateCurrency) => {
    const taxIncludedAmount = priceItem?.price?.taxIncludedAmount;
    if (!taxIncludedAmount?.value) return;

    const unit = taxIncludedAmount.unit;
    updateCurrency(unit);
    const amount = roundAmount(taxIncludedAmount.value, unit);

    if (priceItem.priceType === PRICE_TYPES.NON_RECURRING_CHARGE) {
        state.currentNrc += amount;
    } else if (priceItem.priceType === PRICE_TYPES.RECURRING_CHARGE) {
        addToRecurringChargeMap(state.currentRc, formatRecurringChargePeriod(priceItem.recurringChargePeriod || ""), amount);
    }
};

export const processDiscount = (priceItem, state, updateCurrency) => {
    const basePrice = priceItem.productOfferingPrice?.price;
    if (!basePrice) return;

    const basePriceWithTax = toNumber(basePrice.value ?? 0);

    (priceItem.priceAlteration || [])
        .filter((alt) => alt?.["@type"] !== ALTERATION_TYPES.TAX && alt?.price)
        .forEach(({price: altPrice, priceType, recurringChargePeriod}) => {
            const isPercentage = altPrice.percentage !== undefined;
            const currency = isPercentage
                ? altPrice.taxIncludedAmount?.unit || basePrice.unit || ""
                : altPrice.taxIncludedAmount?.unit;

            const rawAmount = isPercentage
                ? (basePriceWithTax * toNumber(altPrice.percentage)) / 100
                : toNumber(altPrice.taxIncludedAmount?.value);

            const amount = roundAmount(rawAmount, currency);
            if (!amount) return;

            updateCurrency(currency);

            if (priceType === ALTERATION_TYPES.NON_RECURRING_DISCOUNT) {
                state.discountNrc += amount;
            } else if (priceType === ALTERATION_TYPES.RECURRING_DISCOUNT) {
                addToRecurringChargeMap(state.discountRc, formatRecurringChargePeriod(recurringChargePeriod), amount);
            }
        });
};

export const calculatePricingForOrder = (order = {}) => {
    const state = {
        currentNrc: 0,
        currentRc: new Map(),
        discountNrc: 0,
        discountRc: new Map(),
    };

    let currency = "";
    const updateCurrency = (unit) => {
        if (!currency && unit) currency = unit;
    };

    (order.orderTotalPrice || []).forEach((orderPrice) => {
        processPrice(orderPrice, state, updateCurrency);
        processDiscount(orderPrice, state, updateCurrency);
    });

    (order.productOrderItem || []).forEach((orderItem) => {
        (orderItem.itemPrice || []).forEach((itemPrice) => {
            processDiscount(itemPrice, state, updateCurrency);
        });
    });

    return {
        current: {nrc: state.currentNrc, rc: createRecurringChargeArray(state.currentRc)},
        discount: {nrc: state.discountNrc, rc: createRecurringChargeArray(state.discountRc)},
        currency,
    };
};

export const isContractType = (item) => item.productOffering?.["@type"] === "Contract";

export const hasCharges = (priceObject) =>
    !!priceObject && (priceObject.nrc > 0 || (priceObject.rc && priceObject.rc.length > 0));

const hasMigrateTo = (item) =>
    Array.isArray(item?.productOrderItemRelationship) &&
    item.productOrderItemRelationship.some((rel) => rel?.relationshipType === "migrateTo");

export const byName = (a, b) =>
    (a?.name || "").localeCompare(b?.name || "", undefined, {sensitivity: "base"});

export const byMigrateToThenName = (a, b) => {
    const aFirst = hasMigrateTo(a) ? 0 : 1;
    const bFirst = hasMigrateTo(b) ? 0 : 1;
    if (aFirst !== bFirst) return aFirst - bFirst;
    return byName(a, b);
};

export const getOperation = (productOrderItems) => {
    if (!Array.isArray(productOrderItems) || productOrderItems.length === 0) return "";

    const contractItems = productOrderItems.filter((item) => item.productOffering?.["@type"] === "Contract");

    if (contractItems.length > 0) {
        const contractActions = contractItems.map((item) => item.action?.toLowerCase()).filter(Boolean);
        if (contractActions.includes("delete")) return "Termination";
        if (contractActions.includes("migrate")) return "Migration";
        if (contractActions.includes("add")) return "Acquisition";
    }

    const hasProductRelationships = productOrderItems.some((item) => {
        const rels = item?.product?.productRelationship;
        if (!rels) return false;
        return (Array.isArray(rels) ? rels : [rels]).length > 0;
    });

    const hasModifyAction = productOrderItems.some((item) => item.action?.toLowerCase() === "modify");
    const hasDeleteAction = productOrderItems.some((item) => item.action?.toLowerCase() === "delete");

    return (hasProductRelationships || hasModifyAction || hasDeleteAction) ? "Modification" : "Acquisition";
};

const getInstallmentPeriodValue = (item) => {
    const characteristics = item?.characteristics ?? item?.product?.productCharacteristic ?? [];
    const char = characteristics.find(
        (c) => String(c?.name || "").toLowerCase() === "installment period"
    );
    const value = char && (char.value ?? char.value?.value);
    return typeof value === "string" ? value.trim() : "";
};

export const extractInstallmentInfo = (item) => {
    const itemPrices = item?.itemPrice;
    if (!Array.isArray(itemPrices) || itemPrices.length === 0) return null;

    const installmentPeriod = getInstallmentPeriodValue(item);
    if (String(installmentPeriod).toLowerCase() === "upfront payment") return null;

    const orderPriceEntry = itemPrices.find(
        (p) => p?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE
    );
    if (!orderPriceEntry) return null;

    const pop = orderPriceEntry.productOfferingPrice;
    const price = orderPriceEntry.price;
    const taxIncluded = price?.taxIncludedAmount;
    if (taxIncluded?.value == null) return null;

    const downPayment = pop?.downPayment;
    const interestRate = pop?.interestRate;
    const currency = taxIncluded.unit || "EUR";
    const symbol = getCurrencySymbol(currency);
    const totalValue = parseFloat(taxIncluded.value);
    if (!Number.isFinite(totalValue)) return null;

    const downPaymentFormatted =
        downPayment != null && Number.isFinite(Number(downPayment))
            ? `${symbol} ${formatCurrencyAmount(Number(downPayment), currency)}`
            : null;

    const totalWithInstallmentsFormatted =
        `${symbol} ${formatCurrencyAmount(totalValue, currency)}` +
        (interestRate != null && Number.isFinite(Number(interestRate))
            ? ` + ${Number(interestRate)}%`
            : "");

    return {
        downPaymentFormatted,
        totalWithInstallmentsFormatted,
    };
};
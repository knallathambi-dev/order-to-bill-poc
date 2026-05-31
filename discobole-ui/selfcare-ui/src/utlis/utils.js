// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    formatAmount,
    formatApplicationDuration,
    formatRecurringChargePeriod,
    generatePrefixedRandomId,
    getCurrencySymbol,
    roundAmount,
} from "./helpers";
import {
    ALTERATION_TYPES,
    ATOMIC_TYPE,
    CONTRACT_TYPE,
    INSTALLMENT_CHARGE_TYPE,
    PRICE_TYPES,
    SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE,
    STATIC_BILLING_IDS,
    UNQUALIFIED_OFFER_MESSAGE,
} from "./constants";
import {setBillingAccountId} from "../store/actions/orderCompletionActions";

export const hasCharges = (priceObject) =>
    !!priceObject && (priceObject.nrc > 0 || (priceObject.rc?.length > 0));

export const shouldRenderPricing = (pricing) =>
    !!pricing && (hasCharges(pricing.current) || hasCharges(pricing.beforeDiscount) || hasCharges(pricing.discount));

export const addToRecurringChargeMap = (map, period, value) => {
    if (value) map.set(period, (map.get(period) || 0) + value);
};

export const convertRecurringChargeMapToArray = (map) => {
    const result = [];
    map.forEach((total, period) => result.push({recurringChargePeriod: period, total}));
    return result;
};

export const createPricingState = () => ({
    currentNrc: 0,
    currentRc: new Map(),
    baseNrc: 0,
    baseRc: new Map(),
    discountNrc: 0,
    discountRc: new Map(),
});

export const createCurrencyManager = () => {
    let currency = '';
    return {
        getCurrency: () => currency,
        updateCurrency: (unit) => {
            if (!currency && unit) currency = unit;
        },
    };
};

export const buildPricingResult = (state, currency) => ({
    current: {nrc: state.currentNrc, rc: convertRecurringChargeMapToArray(state.currentRc)},
    beforeDiscount: {nrc: state.baseNrc, rc: convertRecurringChargeMapToArray(state.baseRc)},
    discount: {nrc: state.discountNrc, rc: convertRecurringChargeMapToArray(state.discountRc)},
    currency,
});

export const renderPrices = (pricing, currencyCode = '') => {
    if (!hasCharges(pricing)) {
        const symbol = currencyCode ? getCurrencySymbol(currencyCode) : '';
        const roundedZero = formatAmount(roundAmount(0, currencyCode), currencyCode);
        return symbol ? `${symbol} ${roundedZero}` : roundedZero;
    }

    const symbol = getCurrencySymbol(currencyCode);
    const parts = [];

    if (pricing.nrc !== 0) {
        parts.push(`${symbol} ${formatAmount(pricing.nrc, currencyCode)}`);
    }

    if (Array.isArray(pricing.rc)) {
        for (const charge of pricing.rc) {
            parts.push(`${symbol} ${formatAmount(charge.total, currencyCode)}/${charge.recurringChargePeriod}`);
        }
    }

    return parts.join(' + ');
};

export const processCurrentPrice = (priceItem, state, updateCurrency) => {
    const taxIncluded = priceItem?.price?.taxIncludedAmount;
    if (!taxIncluded?.value) return;

    const currencyCode = taxIncluded.unit || '';
    updateCurrency(currencyCode);

    const pop = priceItem?.productOfferingPrice;
    const isInstallment = pop?.['@type'] === INSTALLMENT_CHARGE_TYPE;

    const amount = isInstallment
        ? roundAmount(taxIncluded.value * (1 + (pop?.interestRate || 0) / 100), currencyCode)
        : roundAmount(taxIncluded.value, currencyCode);

    if (priceItem.priceType === PRICE_TYPES.NON_RECURRING_CHARGE || isInstallment) {
        state.currentNrc += amount;
    } else if (priceItem.priceType === PRICE_TYPES.RECURRING_CHARGE) {
        addToRecurringChargeMap(
            state.currentRc,
            formatRecurringChargePeriod(priceItem.recurringChargePeriod || ''),
            amount,
        );
    }
};

export const processDiscount = (priceItem, state, updateCurrency) => {
    const basePrice = priceItem.productOfferingPrice?.price;
    if (!basePrice) return;

    const alterations = priceItem.priceAlterations || priceItem.priceAlteration || [];
    const basePriceWithTax = roundAmount(basePrice.value, basePrice.unit || '');

    for (const alt of alterations) {
        if (alt?.['@type'] === ALTERATION_TYPES.TAX) continue;

        const altPrice = alt?.price;
        if (!altPrice) continue;

        let discountAmount = 0;
        let currency = '';

        if (altPrice.percentage !== undefined) {
            currency = altPrice.taxIncludedAmount?.unit || basePrice.unit || '';
            discountAmount = roundAmount((basePriceWithTax * altPrice.percentage) / 100, currency);
        } else if (altPrice.taxIncludedAmount?.value !== undefined) {
            currency = altPrice.taxIncludedAmount.unit || basePrice.unit || '';
            discountAmount = roundAmount(altPrice.taxIncludedAmount.value, currency);
        }

        if (!discountAmount) continue;
        updateCurrency(currency);

        if (alt.priceType === ALTERATION_TYPES.NON_RECURRING_DISCOUNT) {
            state.discountNrc += discountAmount;
        } else if (alt.priceType === ALTERATION_TYPES.RECURRING_DISCOUNT) {
            addToRecurringChargeMap(
                state.discountRc,
                formatRecurringChargePeriod(alt.recurringChargePeriod),
                discountAmount,
            );
        }
    }
};

export const getCurrentPrices = (items) => {
    if (!Array.isArray(items) || items.length === 0) return [];

    const groups = {};

    for (const item of items) {
        const pop = item?.productOfferingPrice;
        const isInstallment = pop?.['@type'] === INSTALLMENT_CHARGE_TYPE;
        const taxIncluded = item?.price?.taxIncludedAmount;

        const rawValue = isInstallment
            ? (taxIncluded?.value || 0) * (1 + (pop?.interestRate || 0) / 100)
            : taxIncluded?.value;

        if (rawValue == null) continue;
        const value = parseFloat(rawValue);
        if (!isFinite(value)) continue;

        const currency = taxIncluded?.unit;
        const chargePeriod = item?.recurringChargePeriod;
        const duration = item?.applicationDuration;
        const key = `${chargePeriod || 'one-time'}_${duration || 'indefinite'}`;

        if (!groups[key]) {
            groups[key] = {totalAmount: 0, currency, chargePeriod, duration};
        }
        groups[key].totalAmount += value;
    }

    let grouped = Object.values(groups);
    const hasRecurring = grouped.some((g) => Boolean(g.chargePeriod));
    const hasNonRecurring = grouped.some((g) => !g.chargePeriod);

    if (hasRecurring && hasNonRecurring) {
        grouped = grouped.filter((g) => g.totalAmount !== 0);
    }

    return grouped.map((g) => {
        const periodSuffix = g.totalAmount > 0 && g.chargePeriod
            ? `/${formatRecurringChargePeriod(g.chargePeriod)}`
            : '';
        const symbol = getCurrencySymbol(g.currency);
        return {
            totalPrice: `${symbol} ${formatAmount(g.totalAmount, g.currency || '')}${periodSuffix}`,
            applicationDuration: formatApplicationDuration(g.duration),
        };
    });
};

export const createRelatedPartyArray = (relatedParty) => {
    if (!relatedParty?.id || !relatedParty?.name || !relatedParty?.role) return [];
    return [{
        role: relatedParty.role,
        partyOrPartyRole: {
            id: relatedParty.id,
            name: relatedParty.name,
            "@referredType": "individual",
            "@type": "PartyRef",
        },
        "@type": "RelatedPartyRefOrPartyRoleRef",
    }];
};

export const createChannel = () => ({id: "Selfcare", name: "Selfcare"});

export function hasOnlyContractInConfiguration(configurationStructure) {
    const {includedItems, optionalItems, nestedBundles, configItem} = configurationStructure;
    return (
        includedItems.length === 0 &&
        optionalItems.length === 0 &&
        nestedBundles.length === 0 &&
        configItem?.productConfiguration?.productOffering?.["@referredType"] === CONTRACT_TYPE
    );
}

export const getFormattedValue = (inputValue, t) =>
    inputValue >= 9999 ? t("common.unlimited") : inputValue;

export const isFiberOrConvergentOffer = (offer) => {
    const offerName = (offer?.name ?? '').trim().toLowerCase();
    if (offerName.includes('fiber') || offerName.includes('broadband')) return true;

    const categories = offer?.category;
    if (!Array.isArray(categories)) return false;
    return categories.some((cat) => {
        const name = (cat?.name ?? '').trim().toLowerCase();
        return name === 'fiber' || name === 'convergent';
    });
};

export const getDisplayableOrderItems = (order) => {
    if (!order?.productOrderItem?.length) return [];
    return order.productOrderItem.filter((item) => {
        if (item?.productOffering?.["@type"] !== ATOMIC_TYPE) return false;
        if (item.product?.productSpecification?.["@baseType"] === SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE) return false;
        return !item?.productOrderItemRelationship?.some(
            (rel) => rel?.relationshipType === "migrateTo"
        );
    });
};

export const getBillingAccountId = (dispatch, relatedParty, storeBillingIds) => {
    const normalizedName = relatedParty.name.toLowerCase();
    const staticId = STATIC_BILLING_IDS[normalizedName];
    if (staticId) return staticId;

    if (storeBillingIds?.[relatedParty.id]) return storeBillingIds[relatedParty.id];

    const generated = generatePrefixedRandomId('13', 20);
    dispatch(setBillingAccountId(relatedParty.id, generated));
    return generated;
};

export const hasInstallment = (prices) =>
    prices?.some(price => price?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE);

export const hasErrorDescription = (response) => {
    const description = (response?.data?.description ?? '').trim();
    const isUnqualifiedOffer = description === UNQUALIFIED_OFFER_MESSAGE;

    return description.length > 0 && !isUnqualifiedOffer;
};

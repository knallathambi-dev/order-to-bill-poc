// SPDX-FileCopyrightText: 2026 Orange SA
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
    getCurrencySymbol,
    roundAmount
} from "../../../../../utlis/helpers";
import {performActionAndGetCharacteristics} from "../productConfigurationService";
import {
    ADDRESS_CHARACTERISTIC_TYPE,
    ALTERATION_TYPES,
    CONTRACT_TYPE,
    UNIT_OF_MEASURE_INTERVAL
} from "../../../../../utlis/constants";
import {
    addToRecurringChargeMap,
    buildPricingResult,
    createCurrencyManager,
    createPricingState,
    getCurrentPrices,
    processCurrentPrice,
    processDiscount
} from "../../../../../utlis/utils";
import {toast} from "react-toastify";

export function countSelectedItemsByOfferingName(configs, offeringName, configurationItemId) {
    if (!configs?.computedProductConfigurationItem || !offeringName || !configurationItemId) {
        return 0;
    }

    const allItems = configs.computedProductConfigurationItem;

    const parentItem = allItems.find(item => {
        const relationships = item.productConfigurationItemRelationship || [];
        return relationships.some(
            rel => rel.id === configurationItemId && rel.relationshipType === "bundles"
        );
    });

    if (!parentItem) {
        return 0;
    }

    const bundledChildIds = (parentItem.productConfigurationItemRelationship || [])
        .filter(rel => rel.relationshipType === "bundles")
        .map(rel => rel.id);

    return allItems.filter(item => {
        if (!bundledChildIds.includes(item.id)) return false;
        const productConfig = item?.productConfiguration;
        const matchedOfferingName = productConfig?.productOffering?.name?.trim();
        return productConfig?.isSelected && matchedOfferingName === offeringName;
    }).length;
}

export const addFirstSelectableItem = async (
    items,
    isConfigItem,
    configurationId,
    relatedParty,
    dispatch,
    tNotification,
    onConfigurationChange
) => {
    const getProductConfig = (item) =>
        isConfigItem ? item.configItem.productConfiguration : item.productConfiguration;

    const getItemId = (item) =>
        isConfigItem ? item.configItem.id : item.id;

    const firstUnselected = items.find(item => getProductConfig(item).isSelected === false);
    if (!firstUnselected) return;

    const result = await performActionAndGetCharacteristics({
        configurationId,
        configurationItemId: getItemId(firstUnselected),
        characteristic: null,
        isSelected: true,
        relatedParty,
        actionType: 'add',
        dispatch,
        tNotification,
        onConfigurationChange,
    });

    if (!result) {
        toast.error(tNotification("plan.updateCharacteristicFailed"));
    }
};

function hasPositiveApplicationDuration(duration) {
    return duration && duration.amount > 0;
}

function buildDiscountDisplay(alteration, basePrice, priceAlterations, defaultCurrency) {
    const {applicationDuration, recurringChargePeriod, price = {}, applicationOffset} = alteration;
    const {taxIncludedAmount, percentage} = price;

    const discountValue = taxIncludedAmount?.value;
    const currencyCode = taxIncludedAmount?.unit || defaultCurrency || '';

    let formattedPrice = '';

    if (percentage !== undefined) {
        formattedPrice = `${percentage}%`;
    } else if (discountValue !== undefined && Number.isFinite(Number(discountValue))) {
        const showPeriodSuffix = !hasPositiveApplicationDuration(applicationDuration);
        const periodSuffix = recurringChargePeriod && showPeriodSuffix
            ? `/${formatRecurringChargePeriod(recurringChargePeriod)}`
            : '';

        formattedPrice = `${getCurrencySymbol(currencyCode)} ${formatAmount(roundAmount(discountValue, currencyCode), currencyCode)}${periodSuffix}`;
    }

    if (!formattedPrice) return null;

    const hasOffset = typeof applicationOffset === 'number' && applicationOffset > 0;

    return {
        totalPrice: formattedPrice,
        applicationDuration: formatApplicationDuration(applicationDuration, !hasOffset),
        applicationOffset: hasOffset
            ? {offset: applicationOffset, unit: alteration?.recurringChargePeriod?.units}
            : undefined,
    };
}

export const getDiscounts = (items) => {
    if (!Array.isArray(items) || items.length === 0) return [];

    const discounts = [];

    for (const item of items) {
        const priceAlterations = Array.isArray(item?.priceAlterations)
            ? item.priceAlterations
            : Array.isArray(item?.priceAlteration)
                ? item.priceAlteration
                : [];

        if (priceAlterations.length === 0) continue;

        const defaultCurrency = item?.productOfferingPrice?.price?.unit;
        const basePrice = item?.productOfferingPrice?.price;

        const nonTaxAlterations = priceAlterations.filter(
            alteration => alteration?.['@type'] !== ALTERATION_TYPES?.TAX
        );

        for (const alteration of nonTaxAlterations) {
            const discount = buildDiscountDisplay(alteration, basePrice, priceAlterations, defaultCurrency);
            if (discount) {
                discounts.push(discount);
            }
        }
    }

    return discounts;
};

export const summarizeConfigurationPrices = (configurationPrices) => {
    if (!Array.isArray(configurationPrices) || configurationPrices.length === 0) {
        return {currentPrices: [], basePrices: '', discounts: []};
    }

    const recurringTotalsByPeriod = new Map();
    let nonRecurringTotal = 0;
    let detectedCurrency = null;

    for (const priceConfig of configurationPrices) {
        const offeringPrice = priceConfig?.productOfferingPrice;
        const basePrice = offeringPrice?.price;
        if (!basePrice) continue;

        if (!detectedCurrency && basePrice.unit) {
            detectedCurrency = basePrice.unit;
        }

        const baseValue = Number(basePrice?.value) || 0;

        const periodLength = offeringPrice?.recurringChargePeriodLength;
        const periodType = offeringPrice?.recurringChargePeriodType;

        if (periodLength && periodType) {
            const periodKey = formatRecurringChargePeriod({amount: periodLength, units: periodType});
            recurringTotalsByPeriod.set(periodKey, (recurringTotalsByPeriod.get(periodKey) || 0) + baseValue);
        } else {
            nonRecurringTotal += baseValue;
        }
    }

    const currencyCode = detectedCurrency || "";
    const currencySymbol = getCurrencySymbol(currencyCode);

    const formatPrice = (amount, suffix = '') => {
        const rounded = roundAmount(amount, currencyCode);
        const formatted = formatAmount(rounded, currencyCode);
        const price = currencySymbol ? `${currencySymbol} ${formatted}` : formatted;
        return `${price}${suffix}`;
    };

    const priceParts = [];

    if (nonRecurringTotal > 0) {
        priceParts.push(formatPrice(nonRecurringTotal));
    }

    for (const [period, total] of recurringTotalsByPeriod) {
        if (total > 0) {
            priceParts.push(formatPrice(total, `/${period}`));
        }
    }

    return {
        currentPrices: getCurrentPrices(configurationPrices),
        basePrices: priceParts.join(' + '),
        discounts: getDiscounts(configurationPrices),
    };
};

export function getConfigurationProperty(productConfiguration, property) {
    const addAction = productConfiguration.configurationAction
        ?.find(action => action.action === 'add');

    if (addAction && addAction[property]) {
        return addAction[property];
    }

    return productConfiguration[property];
}

export function getActiveConfigurationAction(productConfiguration) {
    const EXCLUDED_ACTIONS = ['noChange', 'terminate'];

    const isActionValid = (action) =>
        action && !EXCLUDED_ACTIONS.includes(action.action);

    const selectableAction = productConfiguration.configurationAction?.find(
        action => action.isSelectable && isActionValid(action)
    );

    if (selectableAction) {
        return selectableAction.action;
    }

    const selectedAction = productConfiguration.configurationAction?.find(
        action => action.isSelected && isActionValid(action)
    );

    return selectedAction?.action;
}

export function checkAncestorsSelection(itemId, configItems) {
    const findItemById = (id) => configItems.find(item => item.id === id);

    const findParentItem = (childId) => {
        for (const item of configItems) {
            const relationships = item.productConfigurationItemRelationship || [];
            const isBundleParent = relationships.some(
                rel => rel.relationshipType === 'bundles' && rel.id === childId
            );
            if (isBundleParent) return item;
        }
        return null;
    };

    let currentItem = findItemById(itemId);

    while (currentItem) {
        const isContractType =
            currentItem.productConfiguration?.productOffering?.["@referredType"] === CONTRACT_TYPE;

        if (isContractType) return true;

        currentItem = findParentItem(currentItem.id);

        if (currentItem && !currentItem.productConfiguration?.isSelected) {
            return false;
        }
    }

    return false;
}

export const getSelectedConfigurationAction = (productConfiguration) => {
    return productConfiguration?.configurationAction
        ?.find(actionItem => actionItem.isSelected === true)
        ?.action || null;
};

const resolveInstallmentPrice = (productOfferingPrice, priceItem, priceAlterations, currencyCode) => {
    const interestRate = productOfferingPrice?.interestRate || 0;
    const taxIncludedAmount = priceItem?.price?.taxIncludedAmount?.value;

    if (taxIncludedAmount != null) {
        return roundAmount(
            taxIncludedAmount * (1 + interestRate / 100),
            currencyCode
        );
    }

    const basePrice = productOfferingPrice.price;
    const basePriceWithTax = roundAmount(basePrice.value, currencyCode);

    return roundAmount(
        basePriceWithTax * (1 + interestRate / 100),
        currencyCode
    );
};

const processBasePriceForConfigurationItem = (priceItem, state, updateCurrency) => {
    const productOfferingPrice = priceItem.productOfferingPrice;
    if (!productOfferingPrice?.price) return;

    const {price, recurringChargePeriodLength, recurringChargePeriodType} = productOfferingPrice;
    const priceAlterations = priceItem.priceAlterations || [];
    const isInstallmentCharge = productOfferingPrice?.['@type'] === 'InstallmentCharge';
    const currencyCode = priceItem?.price?.taxIncludedAmount?.unit || price?.unit || "";

    const totalPrice = isInstallmentCharge
        ? resolveInstallmentPrice(productOfferingPrice, priceItem, priceAlterations, currencyCode)
        : roundAmount(price.value, currencyCode);

    const currencyUnit = isInstallmentCharge
        ? (priceItem?.price?.taxIncludedAmount?.unit || price.unit)
        : price.unit;

    updateCurrency(currencyUnit);

    if (recurringChargePeriodLength && recurringChargePeriodType) {
        const formattedPeriod = formatRecurringChargePeriod({
            amount: recurringChargePeriodLength,
            units: recurringChargePeriodType
        });
        addToRecurringChargeMap(state.baseRc, formattedPeriod, totalPrice);
    } else {
        state.baseNrc += totalPrice;
    }
};

export const calculatePricingForConfigurationItems = (configurationItems = []) => {
    const state = createPricingState();
    const currencyManager = createCurrencyManager();

    const selectedPrices = configurationItems
        .filter(({productConfiguration}) =>
            getConfigurationProperty(productConfiguration, 'isSelected') === true
        )
        .flatMap(({productConfiguration}) =>
            productConfiguration?.configurationPrice ?? []
        );

    for (const configPrice of selectedPrices) {
        processCurrentPrice(configPrice, state, currencyManager.updateCurrency);
        processBasePriceForConfigurationItem(configPrice, state, currencyManager.updateCurrency);
        processDiscount(configPrice, state, currencyManager.updateCurrency);
    }

    return buildPricingResult(state, currencyManager.getCurrency());
};

export const hasValidUnitOfMeasure = (values) => {
    return values.every(value =>
        UNIT_OF_MEASURE_INTERVAL.includes(value.characteristic?.unitOfMeasure?.toLowerCase())
    );
};

export const shouldShowCharacteristicsSection = (characteristics) => {
    if (!Array.isArray(characteristics) || characteristics.length === 0) return false;

    return characteristics.some(characteristic => {
        if (!characteristic?.isConfigurable) return false;

        const valueOptions = characteristic.configurationCharacteristicValues;
        if (!Array.isArray(valueOptions) || valueOptions.length === 0) return false;

        const optionTypes = valueOptions.map(v => v.characteristic?.['@type']);
        if (optionTypes.includes(ADDRESS_CHARACTERISTIC_TYPE)) return true;

        return valueOptions.length >= 2;
    });
};
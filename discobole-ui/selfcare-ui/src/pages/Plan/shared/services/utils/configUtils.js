// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {formatDuration} from "../../../../../utlis/helpers";
import {
    CONTRACT_TYPE,
    INSTALLMENT_PERIOD_CHARACTERISTIC_NAME,
    PARTNER_CHARACTERISTIC_NAME,
    TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM,
    UPFRONT_PAYMENT_VALUE
} from "../../../../../utlis/constants";

export function findContractItem(configurationItems) {
    return configurationItems.find((ci) => {
        const typeCondition = ci["@type"] ? ci["@type"] === TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM : true;
        const referredTypeCondition = ci.productConfiguration?.productOffering?.["@referredType"] === CONTRACT_TYPE;
        return typeCondition && referredTypeCondition;
    });
}

export function hasChildren(node) {
    if (!node) return false;
    return (
        (node.includedItems && node.includedItems.length > 0) ||
        (node.optionalItems && node.optionalItems.length > 0) ||
        (node.nestedBundles && node.nestedBundles.length > 0)
    );
}

export function findById(items, id) {
    return items.find((itm) => itm.id === id);
}

export function isIncluded(lower, upper) {
    return lower >= 1 && (upper >= 1);
}

export function isOptional(lower, upper) {
    return lower === 0 && (upper >= 1);
}

export function getCardinality(parentItem, offeringId) {
    const groupOfferings = parentItem?.productConfiguration?.bundledGroupProductOffering?.bundledProductOfferings || [];
    const found = groupOfferings.find((b) => b.productOfferingRef?.id === offeringId);
    return found?.bundledProductOfferingOption
        ? [
            found.bundledProductOfferingOption.numberRelOfferLowerLimit ?? 0,
            found.bundledProductOfferingOption.numberRelOfferUpperLimit ?? 0
        ]
        : [0, 0];
}

export function groupItemsByOffering(items) {
    const groupedItems = {};
    items.forEach(item => {
        if (hasSelectableOrSelectedActionAndVisible(item)) {
            const offeringName = item.productConfiguration?.productOffering?.name;
            if (!groupedItems[offeringName]) {
                groupedItems[offeringName] = {
                    offeringName,
                    items: [],
                    lowerLimit: item.lowerLimit,
                    upperLimit: item.upperLimit
                };
            }
            groupedItems[offeringName].items.push(item);
        }
    });
    return Object.values(groupedItems);
}

export function filterVisibleItems(items) {
    return items.filter(item => item.productConfiguration?.isVisible);
}

export function hasSelectableOrSelectedActionAndVisible(item) {
    return (
        item?.productConfiguration?.configurationAction?.some(
            (action => action.isSelectable || action.isSelected) ?? false
        ) && item?.productConfiguration?.isVisible
    );
}

export function isActionSelectedAndVisible(item) {
    return (
        item?.productConfiguration?.configurationAction?.some(
            (action => action.isSelected) ?? false
        ) && item?.productConfiguration?.isVisible
    );
}

export function getFormattedSelectedDuration(configurationTerms) {
    if (
        !configurationTerms ||
        !Array.isArray(configurationTerms) ||
        configurationTerms.length === 0
    ) {
        return null;
    }

    const selectedConfigurationTerm = configurationTerms.find(configurationTerm => configurationTerm.isSelected === true);
    if (!selectedConfigurationTerm || !selectedConfigurationTerm.duration) {
        return null;
    }

    return formatDuration(selectedConfigurationTerm.duration);
}

export function isTerminateSelectable(configItem) {
    const actions = configItem?.productConfiguration?.configurationAction;
    if (!Array.isArray(actions)) return false;

    for (const a of actions) {
        const actionName = (a?.action ?? a?.name ?? a?.code ?? "").toString().toLowerCase();
        if (actionName === "terminate" && a?.isSelectable === true) {
            return true;
        }
    }

    return false;
}

export function getDisplayableSelectedCharacteristics(item) {
    const configurationCharacteristic = item?.productConfiguration?.configurationCharacteristic;
    const characteristics =
        configurationCharacteristic
            ?.flatMap((char) => char?.configurationCharacteristicValues?.filter((val) => val?.isSelected) || [])
            ?.map((val) => ({
                name: val?.characteristic?.name || "",
                value: val?.characteristic?.value || "",
                unit: val?.characteristic?.unitOfMeasure || ""
            })) || [];

    const selectedInstallmentPeriod = configurationCharacteristic
        ?.find((char) => char.name === INSTALLMENT_PERIOD_CHARACTERISTIC_NAME)
        ?.configurationCharacteristicValues?.find((val) => val?.isSelected)
        ?.characteristic?.value;

    if (selectedInstallmentPeriod === UPFRONT_PAYMENT_VALUE) {
        return characteristics.filter((char) => char.name !== PARTNER_CHARACTERISTIC_NAME);
    }
    return characteristics;
}
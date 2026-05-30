// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    filterVisibleItems,
    findById,
    findContractItem,
    getCardinality,
    groupItemsByOffering,
    hasChildren,
    isIncluded,
    isOptional,
    isTerminateSelectable
} from './utils/configUtils.js';
import {BUNDLE_TYPE, CONTRACT_TYPE, TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM} from "../../../../utlis/constants";

export default function processConfiguration(configItems) {
    const allConfigItems = configItems?.computedProductConfigurationItem || [];
    let visibleItems = filterVisibleItems(allConfigItems);

    const contractItem = findContractItem(visibleItems);

    if (!contractItem) {
        return {configurationStructure: null};
    }

    function buildConfiguration(item, parentItem) {
        const isTargetType = item["@type"] ? item["@type"] === TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM : true;

        if (!isTargetType) {
            return null;
        }

        const {productConfiguration, productConfigurationItemRelationship} = item;
        const offeringId = productConfiguration?.productOffering?.id;
        const [lowerLimit, upperLimit] = getCardinality(parentItem, offeringId);
        const isBundle = productConfiguration?.productOffering?.["@referredType"] === BUNDLE_TYPE;
        const isContract = productConfiguration?.productOffering?.["@referredType"] === CONTRACT_TYPE;

        const structure = {
            configItem: item,
            includedItems: [],
            optionalItems: [],
            nestedBundles: [],
            bundleType: "",
            parent: {
                configItem: parentItem,
                includedItems: [],
                optionalItems: [],
                nestedBundles: [],
                bundleType: "",
                parent: null
            },
            lowerLimit,
            upperLimit
        };

        if (isBundle) {
            structure.bundleType = isIncluded(lowerLimit, upperLimit)
                ? "includedBundle"
                : isOptional(lowerLimit, upperLimit)
                    ? "optionalBundle"
                    : "";
        }

        if (isContract) {
            structure.bundleType = CONTRACT_TYPE;
        }

        const includedItems = [];
        const optionalItems = [];
        const bundleItems = []

        productConfigurationItemRelationship?.forEach(rel => {
            const childItem = findById(allConfigItems, rel.id);
            if (!childItem) return;

            const childOfferingId = childItem.productConfiguration?.productOffering?.id;
            const [childLower, childUpper] = getCardinality(item, childOfferingId);

            if (childItem.productConfiguration?.productOffering?.["@referredType"] === BUNDLE_TYPE && childItem?.productConfiguration?.isVisible) {
                const childBundle = buildConfiguration(childItem, item);

                if (childBundle) {
                    childBundle.lowerLimit = childLower;
                    childBundle.upperLimit = childUpper;
                    bundleItems.push(childBundle);
                }
            } else {
                if (isIncluded(childLower, childUpper)) {
                    includedItems.push({...childItem, lowerLimit: childLower, upperLimit: childUpper});
                } else if (isOptional(childLower, childUpper)) {
                    optionalItems.push({...childItem, lowerLimit: childLower, upperLimit: childUpper});
                }
            }
        });

        structure.includedItems = groupItemsByOffering(includedItems);
        structure.optionalItems = groupItemsByOffering(optionalItems);

        const validBundles = bundleItems.filter(
            b => hasChildren(b) || isTerminateSelectable(b.configItem)
        );

        structure.nestedBundles = Object.values(groupBundlesByOffering(validBundles, item));

        return structure;
    }

    const configurationStructure = buildConfiguration(contractItem, contractItem);

    return {configurationStructure};
}

function groupBundlesByOffering(bundles, parentItem) {
    const grouped = {};
    bundles.forEach(bundle => {
        const offeringName = bundle.configItem.productConfiguration?.productOffering?.name;
        const offeringId = bundle.configItem.productConfiguration?.productOffering?.id;
        const [lowerLimit, upperLimit] = getCardinality(parentItem, offeringId);

        if (!grouped[offeringName]) {
            grouped[offeringName] = {
                offeringName,
                items: [],
                lowerLimit,
                upperLimit
            };
        }
        grouped[offeringName].items.push(bundle);
    });
    return Object.values(grouped);
}
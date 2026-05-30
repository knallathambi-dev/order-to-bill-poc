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
    hasChildren,
    isActionSelectedAndVisible,
    isIncluded,
    isOptional
} from "./utils/configUtils";
import {
    BUNDLE_TYPE,
    CONTRACT_TYPE,
    SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE,
    SOURCE_QUERY_PRODUCT_CONFIGURATION_ITEM,
    TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM
} from "../../../../utlis/constants";
import {getConfigurationProperty} from "./utils/utils";

export default function processConfigurationPlanPreview(configItems) {
    const allConfigItems = configItems?.computedProductConfigurationItem || [];
    let visibleItems = filterVisibleItems(allConfigItems);

    const shippingConfigItems = visibleItems.filter(
        (item) =>
            item.productConfiguration?.productSpecification?.["@baseType"] ===
            SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE
    );

    const shippingConfigItemIds = shippingConfigItems.map(item => item.id);

    const allItemsThatRequireShipping = allConfigItems.filter((item) => {
        const relationships = item.productConfigurationItemRelationship || [];
        return relationships.some(
            (rel) => rel.relationshipType === "requires" && shippingConfigItemIds.includes(rel.id)
        );
    });

    const terminatedConfigurationOffers = visibleItems.filter(item => {
        const hasTerminateActionSelected = item?.productConfiguration?.configurationAction?.some(
            configAction => configAction.action === "terminate" && configAction.isSelected
        );

        const isSourceQueryProductConfigurationItemType = item?.["@type"] === SOURCE_QUERY_PRODUCT_CONFIGURATION_ITEM;

        return hasTerminateActionSelected && isSourceQueryProductConfigurationItemType;
    });

    const contractItem = findContractItem(visibleItems);

    if (!contractItem) {
        return {
            configurationStructure: null,
            itemsRequiringShipping: allItemsThatRequireShipping,
            shippingConfigItems
        };
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
            }
        };

        if (isBundle) {
            structure.bundleType = isIncluded(lowerLimit, upperLimit)
                ? "includedBundle"
                : isOptional(lowerLimit, upperLimit)
                    ? "optionalBundle"
                    : "";
        }

        productConfigurationItemRelationship?.forEach((rel) => {
            const childItem = findById(visibleItems, rel.id);
            if (!childItem) return;

            const childOfferingId = childItem.productConfiguration?.productOffering?.id;
            const [childLower, childUpper] = getCardinality(item, childOfferingId);

            if (childItem.productConfiguration?.productOffering?.["@referredType"] === BUNDLE_TYPE) {
                if (!getConfigurationProperty(childItem.productConfiguration, 'isSelected')) return;

                const processedSubBundle = buildConfiguration(childItem, item);

                if (processedSubBundle && !hasChildren(processedSubBundle)) {
                    return;
                }

                if (processedSubBundle) {
                    structure.nestedBundles.push(processedSubBundle);
                }
            } else {
                if (!isActionSelectedAndVisible(childItem)) return;

                const collection = isIncluded(childLower, childUpper)
                    ? structure.includedItems
                    : isOptional(childLower, childUpper)
                        ? structure.optionalItems
                        : null;

                collection && collection.push(childItem);
            }
        });

        return structure;
    }

    function isItemAndAncestorsSelected(itemId, items) {
        const findByIdInItems = (id) => items.find(itm => itm.id === id);
        const findParentByRelationship = (id) => {
            for (const item of items) {
                const relationships = item.productConfigurationItemRelationship || [];
                for (const rel of relationships) {
                    if (rel.relationshipType === 'bundles' && rel.id === id) {
                        return item;
                    }
                }
            }
            return null;
        };

        let currentItem = findByIdInItems(itemId);
        while (currentItem) {
            if (!currentItem.productConfiguration?.isSelected) return false;
            if (currentItem.productConfiguration?.productOffering?.["@referredType"] === CONTRACT_TYPE) return true;
            currentItem = findParentByRelationship(currentItem.id);
        }
        return false;
    }

    const configurationStructure = buildConfiguration(contractItem, contractItem);

    return {
        configurationStructure,
        itemsRequiringShipping: allItemsThatRequireShipping.filter(item => isItemAndAncestorsSelected(item.id, allConfigItems)),
        shippingConfigItems,
        terminatedConfigurationOffers
    };
}
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {getCurrentPrices} from "../../../../../utlis/utils";
import {getFormattedSelectedDuration} from "./configUtils";
import {getActiveConfigurationAction, getConfigurationProperty, shouldShowCharacteristicsSection} from "./utils";

/**
 * Extracts all commonly-needed derived properties from a productConfiguration.
 * Eliminates the repeated 10-line extraction block that was copy-pasted ~8 times.
 */
export const extractConfigItemProperties = (productConfiguration) => {
    const {configurationCharacteristic, configurationPrice} = productConfiguration || {};

    const isSelected = getConfigurationProperty(productConfiguration, 'isSelected');
    const isSelectable = getConfigurationProperty(productConfiguration, 'isSelectable');
    const activeAction = getActiveConfigurationAction(productConfiguration);
    const currentPrices = getCurrentPrices(configurationPrice);
    const formattedDuration = getFormattedSelectedDuration(productConfiguration?.configurationTerm);

    const terminateAction = productConfiguration?.configurationAction?.find(
        (action) => action.action === 'terminate'
    );
    const canTerminate = terminateAction?.isSelectable ?? false;

    const hasModifyAction = productConfiguration?.configurationAction?.some(
        (action) => action.action === 'modify' && action.isSelectable
    ) ?? false;

    const showCharacteristics = shouldShowCharacteristicsSection(configurationCharacteristic);

    return {
        isSelected,
        isSelectable,
        activeAction,
        currentPrices,
        formattedDuration,
        canTerminate,
        hasModifyAction,
        showCharacteristics,
    };
};

/**
 * Splits nested bundles into included and optional categories.
 * Eliminates the repeated filter logic that appeared in ConfigItemsTab, NestedBundles, etc.
 */
export const splitBundlesByType = (nestedBundles = []) => {
    const includedBundles = nestedBundles.filter((bundle) =>
        bundle?.items?.some((item) => item.bundleType === "includedBundle")
    );
    const optionalBundles = nestedBundles.filter((bundle) =>
        bundle?.items?.some((item) => item.bundleType === "optionalBundle")
    );
    return {includedBundles, optionalBundles};
};

/**
 * Collects nested bundles from an array of bundle items and splits them.
 */
export const collectAndSplitNestedBundles = (items = []) => {
    const allNested = items.flatMap((item) => item.nestedBundles || []);
    return splitBundlesByType(allNested);
};
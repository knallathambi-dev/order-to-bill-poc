// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const buildBreadcrumbs = (config) => {
    const items = [];

    Object.entries(config).forEach(([key, value]) => {
        if (value != null && value !== '') {
            items.push(value);
        }
    });

    return items;
};

export const getBreadcrumbsByOrderType = (orderType, currentPageKey, options = {}) => {
    const {selectedOfferName, includeOffers = true} = options;

    const FLOW_CONFIG = {
        Acquisition: {
            root: includeOffers ? 'offers' : null,
            offer: selectedOfferName,
            page: currentPageKey
        },
        Migration: {
            root: null,
            offer: selectedOfferName,
            page: currentPageKey
        }
    };

    const config = FLOW_CONFIG[orderType];

    if (!config) {
        return null;
    }

    return buildBreadcrumbs(config);
};
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {formatDateTimeValue} from '@discobole/common-ui';
import {
    AVERAGE_LEAD_TIME,
    CUSTOM_PERIOD,
    MIN_MAX_LEAD_TIME,
    NODE_STATUS,
    ORDER_ID,
    STATUS,
    STATUS_ABORTED,
    STATUS_FAILED,
    STATUS_HELD,
    STATUS_IN_DELIVERY,
    STATUS_IN_PROGRESS,
    STATUS_REJECTED,
    TIME_PERIOD_END,
    TIME_PERIOD_START,
} from '../utils/constants.js';

// ---------------------------------------------------------------------------
// Date filter helpers
// ---------------------------------------------------------------------------

const createDateFilter = (compareFn) => (time, formikDate) => {
    const selected = new Date(time);
    const target = new Date(formikDate);

    if (selected.toDateString() === target.toDateString()) {
        return compareFn(selected.getTime(), target.getTime());
    }
    return true;
};

export const filterStartTime = (time, dateStart) =>
    createDateFilter((a, b) => a > b)(time, dateStart);

export const filterEndTime = (time, dateEnd) =>
    createDateFilter((a, b) => a < b)(time, dateEnd);

// ---------------------------------------------------------------------------
// Form / filter helpers
// ---------------------------------------------------------------------------

const toISOString = (date) => (date ? new Date(date).toISOString() : '');

const resolveInitialValue = (key, value) => {
    if (key.toLowerCase().includes('date')) {
        return value ? new Date(value) : null;
    }
    if (key === STATUS || key === NODE_STATUS) {
        if (!value) return '';
        const upper = value.toUpperCase();
        if (upper === STATUS_IN_PROGRESS.toUpperCase() || upper === STATUS_IN_DELIVERY.toUpperCase()) {
            return 'In' + value[2] + value.substring(3).toLowerCase();
        }
        return value[0] + value.substring(1).toLowerCase();
    }
    return value || '';
};

export const getInitialValues = (filterParams, inputsData) => {
    const initialValues = {};
    for (const key of Object.keys(inputsData)) {
        const value = filterParams.get(inputsData[key]);
        initialValues[key] = resolveInitialValue(key, value);
    }
    return initialValues;
};

export const getFormattedValue = (key, value) => {
    if ([CUSTOM_PERIOD, AVERAGE_LEAD_TIME, MIN_MAX_LEAD_TIME].includes(key)) {
        return value === 'true' || value === true;
    }
    if ([TIME_PERIOD_START, TIME_PERIOD_END].includes(key) && value && value !== 'null') {
        return toISOString(value);
    }
    return value && value !== 'null' ? value : '';
};

export const isFormEmpty = (formikValues) =>
    Object.values(formikValues).every((val) => !val);

export const setFiltersToParams = (inputsData, filterParams, params) => {
    for (const key of Object.keys(inputsData)) {
        const paramKey = inputsData[key];
        const paramValue = filterParams?.get(paramKey);

        if (paramValue) {
            params.set(
                paramKey,
                paramKey.toLowerCase().includes('date')
                    ? formatDateTimeValue(paramValue)
                    : paramValue,
            );
            if (key === ORDER_ID) {
                params.set('relatedEntity.role', 'relatedProductOrder');
            }
        } else {
            params.delete(paramKey);
        }
    }
};

export const formatFilterValue = (key, value) => {
    if (key.toLowerCase().includes('date')) return formatDateTimeValue(value);
    if (key === STATUS || key === NODE_STATUS) return value.toUpperCase().trim();
    return value.trim();
};

export const getFilterValues = (values, inputsData) => {
    const filteredValues = {};

    for (const [key, val] of Object.entries(values)) {
        if (val == null || val === '' || !inputsData[key]) continue;

        filteredValues[inputsData[key]] = formatFilterValue(key, val);

        if (key === ORDER_ID) {
            filteredValues['relatedEntity.role'] = 'relatedProductOrder';
        }
    }
    return filteredValues;
};

export const clearForm = (setCurrentPage, setFilterParams) => {
    const newFilterParams = new URLSearchParams();
    newFilterParams.set('page', 1);
    setCurrentPage(1);
    setFilterParams(newFilterParams);
};

// ---------------------------------------------------------------------------
// Node / action helpers
// ---------------------------------------------------------------------------

const ACTION_ICON_MAP = {
    add: 'modifier_add',
    modify: 'done_modifier',
    terminate: 'Modifier_delete',
    delete: 'Modifier_delete',
    migrate: 'modifier_synchro',
};

export const getActionIconClassName = (action) =>
    ACTION_ICON_MAP[action] || 'anti_spam';

const collectProductIds = (node, relationshipType) => {
    const ids = {};
    for (const product of node?.relatedProduct || []) {
        if (
            product.productOrderItemId &&
            product['@type']?.toLowerCase() !== 'shipmentproduct' &&
            product.relationshipType === relationshipType
        ) {
            ids[product.productOrderItemId] = true;
        }
    }
    return ids;
};

export const getRelatedProductOrderItem = (node, relationshipType) => {
    const productIds = collectProductIds(node, relationshipType);

    const match = node?.relatedProductOrderItem?.find(
        (item) => productIds[item.id],
    );
    return match?.id ?? null;
};

export const getNodeActionType = (node) => {
    const productIds = collectProductIds(node, 'delivers');

    const match = node?.relatedProduct && node.relatedProductOrderItem?.find(
        (item) => productIds[item.id],
    );
    return match?.action ?? null;
};

// ---------------------------------------------------------------------------
// Status helpers
// ---------------------------------------------------------------------------

const NON_RESOLVED_STATES = new Set([
    STATUS_HELD.toLowerCase(),
    STATUS_ABORTED.toLowerCase(),
    STATUS_FAILED.toLowerCase(),
    STATUS_REJECTED.toLowerCase(),
]);

export const isResolvedErrorMessage = (state) =>
    !NON_RESOLVED_STATES.has(state);

// ---------------------------------------------------------------------------
// Lead time formatting
// ---------------------------------------------------------------------------

const LEAD_TIME_UNITS = [
    {divisor: 86400, suffix: 'd'},
    {divisor: 3600, suffix: 'h'},
    {divisor: 60, suffix: 'm'},
    {divisor: 1, suffix: 's'},
];

export const getLeadTime = (timestampInSeconds) => {
    if (!timestampInSeconds) return undefined;

    let remaining = timestampInSeconds;
    const parts = [];

    for (const {divisor, suffix} of LEAD_TIME_UNITS) {
        const val = divisor === 1
            ? (remaining % 1 === 0 ? remaining : Number(remaining.toFixed(2)))
            : Math.floor(remaining / divisor);

        if (val > 0) {
            parts.push(`${val}${suffix}`);
        }
        if (divisor > 1) remaining %= divisor;
    }

    return parts.join(' ') || undefined;
};
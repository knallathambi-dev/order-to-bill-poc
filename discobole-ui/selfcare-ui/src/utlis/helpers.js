// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import dayjs from './dayjs-setup';

export function formatToLocalDateTime(inputDate) {
    if (!inputDate) return '';
    const date = dayjs(inputDate);
    if (!date.isValid()) return '';
    return date.tz(dayjs.tz.guess()).format('DD/MM/YYYY HH:mm');
}

export function getCurrencyFractionDigits(currencyCode) {
    if (!currencyCode) return 2;
    try {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currencyCode,
        }).resolvedOptions().maximumFractionDigits;
    } catch {
        return 2;
    }
}

export function getCurrencySymbol(currencyCode) {
    if (!currencyCode) return '';
    try {
        const parts = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currencyCode,
            currencyDisplay: 'narrowSymbol',
        }).formatToParts(0);
        return parts.find((p) => p.type === 'currency')?.value || currencyCode;
    } catch {
        return currencyCode;
    }
}

export function formatAmount(amount, currencyCode = '') {
    const n = Number(amount);
    if (!Number.isFinite(n)) return '0';

    const decimals = getCurrencyFractionDigits(currencyCode);

    const formatted = new Intl.NumberFormat('en-US', {
        minimumFractionDigits: decimals,
        maximumFractionDigits: decimals,
        useGrouping: true,
    }).format(Math.abs(n));

    const withSpaces = formatted.replace(/,/g, ' ');

    const sign = n < 0 ? "-" : "";
    return sign + withSpaces;
}

export function roundAmount(amount, currencyCode = '') {
    const n = Number(amount);
    if (!Number.isFinite(n)) return 0;

    const decimals = getCurrencyFractionDigits(currencyCode);
    const factor = Math.pow(10, decimals);
    return Math.round((n + Number.EPSILON) * factor) / factor;
}

export const capitalizeFirstLetter = (value) => {
    const str = String(value);
    return str.length === 0 ? str : str.charAt(0).toUpperCase() + str.slice(1);
};

export const formatActionName = (action) => {
    const actionMapper = {delete: 'terminate'};
    const mapped = actionMapper[action?.toLowerCase()] || action;
    return capitalizeFirstLetter(mapped);
};

export const getActionColor = (action) => {
    const colors = {
        add: 'green',
        delete: 'red',
        terminate: 'red',
        modify: 'blue',
        migrate: 'yellow',
        noChange: 'grey',
    };
    return colors[action];
};

const UNIT_ABBREVIATIONS = {
    month: 'M', months: 'M',
    week: 'W', weeks: 'W',
    day: 'D', days: 'D',
};

export function formatDuration(duration) {
    if (!duration || typeof duration.amount !== 'number') return '';
    const {amount, units} = duration;
    const normalized = units?.toLowerCase();
    const abbreviation = UNIT_ABBREVIATIONS[normalized] || units?.charAt(0).toUpperCase() || '';
    return `${amount} ${abbreviation}`;
}

export const formatRecurringChargePeriod = (recurringChargePeriod) => {
    if (!recurringChargePeriod) return '';
    const {amount, units} = recurringChargePeriod;
    if (amount == null || amount === 0) return '';

    const normalized = units.toLowerCase();
    if (amount === 1) return normalized;
    if (amount === 30 && normalized === 'day') return 'month';
    if (amount === 7 && normalized === 'day') return 'week';
    return `${amount} ${normalized}`;
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

export const calculateItemPricing = (itemPrices) => {
    if (!itemPrices) return {nrc: 0, rc: [], currency: ''};

    let totalNrc = 0;
    let currencyCode = '';
    const rcMap = new Map();

    for (const itemPrice of itemPrices) {
        const taxIncluded = itemPrice?.price?.taxIncludedAmount;
        if (taxIncluded?.value === undefined) continue;

        const unit = taxIncluded.unit;
        if (!currencyCode && unit) currencyCode = unit;

        const amount = roundAmount(taxIncluded.value, currencyCode);
        const period = itemPrice?.recurringChargePeriod;

        if (period) {
            const key = formatRecurringChargePeriod(period);
            rcMap.set(key, (rcMap.get(key) || 0) + amount);
        } else {
            totalNrc += amount;
        }
    }

    const rc = [];
    rcMap.forEach((total, period) => rc.push({recurringChargePeriod: period, total}));
    return {nrc: totalNrc, rc, currency: currencyCode};
};

export const responsive = {
    superLargeDesktop: {breakpoint: {max: 4000, min: 3000}, items: 4},
    desktop: {breakpoint: {max: 3000, min: 1024}, items: 4},
    tablet: {breakpoint: {max: 1024, min: 464}, items: 2},
    mobile: {breakpoint: {max: 464, min: 0}, items: 1},
};

export const responsiveCarouselDevice = {
    desktop: {breakpoint: {max: 3000, min: 1024}, items: 1},
    tablet: {breakpoint: {max: 1024, min: 464}, items: 1},
    mobile: {breakpoint: {max: 464, min: 0}, items: 1},
};

const CHARS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

const randomString = (length) => {
    let result = '';
    for (let i = 0; i < length; i++) {
        result += CHARS.charAt(Math.floor(Math.random() * CHARS.length));
    }
    return result;
};

export const generateRandomId = (length = 12) => randomString(length);

export const generatePrefixedRandomId = (prefix, length = 12) => prefix + randomString(length);

export const removeBaseUrl = (url) => {
    if (!url) return '';
    const match = url.match(/https?:\/\/[^/]+(.*)/) || url.match(/(.+)/);
    return match ? match[1] : url;
};
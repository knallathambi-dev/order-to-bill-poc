// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

const toNumber = (value) => {
    const n = Number(value);
    return Number.isFinite(n) ? n : 0;
};

export const getCurrencyFractionDigits = (currencyCode) => {
    try {
        const fmt = new Intl.NumberFormat('en-US', {
            style: "currency",
            currency: currencyCode,
        });
        return fmt.resolvedOptions().maximumFractionDigits;
    } catch {
        return 2;
    }
};

export const getCurrencySymbol = (currencyCode) => {
    if (!currencyCode) return "";
    try {
        const parts = new Intl.NumberFormat('en-US', {
            style: "currency",
            currency: currencyCode,
            currencyDisplay: "narrowSymbol",
        }).formatToParts(0);
        return parts.find((p) => p.type === "currency")?.value || currencyCode;
    } catch {
        return currencyCode;
    }
};

export const formatCurrencyAmount = (amount, currencyCode) => {
    const n = toNumber(amount);
    const fractionDigits = getCurrencyFractionDigits(currencyCode);

    const formatted = new Intl.NumberFormat('en-US', {
        minimumFractionDigits: fractionDigits,
        maximumFractionDigits: fractionDigits,
        useGrouping: true,
    }).format(Math.abs(n));

    const withSpaces = formatted.replace(/,/g, ' ');

    const sign = n < 0 ? "-" : "";
    return sign + withSpaces;
};

export const roundCurrencyAmount = (amount, currencyCode) => {
    const n = toNumber(amount);
    const fractionDigits = getCurrencyFractionDigits(currencyCode);
    const factor = 10 ** fractionDigits;
    return Math.round(n * factor) / factor;
};

export const formatPriceAmount = (value, unit) => {
    if (value === undefined || value === null || value === "") return "_";
    if (unit) {
        try {
            const symbol = getCurrencySymbol(unit);
            const formatted = formatCurrencyAmount(value, unit);
            return `${symbol} ${formatted}`;
        } catch {
        }
    }
    return `${value}${unit ? ` ${unit}` : ""}`;
};
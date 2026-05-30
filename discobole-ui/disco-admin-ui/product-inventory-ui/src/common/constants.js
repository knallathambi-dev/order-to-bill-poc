// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import numeral from "numeral";

export const DEFAULT_FILTER = {
    "isRoot": true,
};

export const STATUS_COLOR_MAP = {
    "CREATED": "#707C83",
    "ACTIVE": "#67CB67",
    "SOLD": "#527EDB",
    "CANCELLED": "#FD8D8D",
    "TERMINATED": "#FD6161",
    "ABORTED": "#FD3A3A",
};

export function getStatusColor(status) {
    const statusUpper = status?.toUpperCase();
    return STATUS_COLOR_MAP[statusUpper] || null;
}

const getRandomColor = () => {
    const randomColor = Math.floor(Math.random() * 16777215).toString(16);
    return `#${randomColor}`;
};

export const getColorFromStatus = (status) => {
    return getStatusColor(status) || getRandomColor();
};

export const formatChartValue = (value) => numeral(value).format("0.[0]a");

export const REPORT_TYPES = {
    ReportProductByStatus: "ReportProductByStatus",
    ReportProductByOffer: "ReportProductByOffer",
};

export const PRODUCT_STATUSES = {
    Created: "Created",
    Cancelled: "Cancelled",
    Active: "Active",
    Terminated: "Terminated",
    Sold: "Sold",
    Aborted: "Aborted",
};

export const PRODUCT_OPERATIONAL_STATUSES = {
    Aborted: "Aborted",
    Active: "Active",
    Cancelled: "Cancelled",
    Confirmed: "Confirmed",
    Created: "Created",
    InDisturbance: "In disturbance",
    Locked: "Locked",
    LockedActive: "Locked active",
    PendingActive: "Pending active",
    PendingCancel: "Pending cancel",
    PendingModification: "Pending modification",
    PendingTerminate: "Pending terminate",
    Sold: "Sold",
    Terminated: "Terminated",
    PendingMigrate: "Pending migrate",
    PendingDelivery: "Pending delivery",
};

export const MIGRATION_RELATIONSHIPS = {
    bundlesMigrate: "bundlesMigrate",
    reliesOnMigrate: "reliesOnMigrate",
    migrateFrom: "migrateFrom",
};

export const MIGRATION_RELATIONSHIP = Object.values(MIGRATION_RELATIONSHIPS);

export const INSTALLMENT_CHARGE_TYPE = "InstallmentCharge";
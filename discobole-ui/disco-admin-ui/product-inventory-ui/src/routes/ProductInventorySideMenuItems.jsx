// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const ProductInventorySideMenuItems = [
    {
        name: "Monitoring",
        path: "/product-inventory/monitoring",
        requires: "canViewProducts",
        children: [
            {name: "Products", path: "/products"},
        ],
    },
    {
        name: "Administration",
        path: "/product-inventory/administration",
        requires: "canViewProducts",
        children: [
            {name: "Job Specifications", path: "/jobSpecification"},
            {name: "Jobs", path: "/jobs"},
        ],
    },
    {
        name: "Exporting",
        path: "/product-inventory/exporting",
        requires: "canViewProducts",
        children: [
            {name: "Products", path: "/products"},
        ],
    },
    {
        name: "Reporting",
        path: "/product-inventory/reporting",
        requires: "canViewProducts",
        children: [
            {name: "Daily Reporting", path: "/reports/daily"},
            {name: "Periodic Reporting", path: "/reports/periodic"},
        ],
    },
];
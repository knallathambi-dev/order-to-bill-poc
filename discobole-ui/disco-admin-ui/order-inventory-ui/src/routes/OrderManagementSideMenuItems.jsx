// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const OrderManagementSideMenuItems = [
    {
        name: "Monitoring",
        path: "/order-inventory/monitoring",
        requires: "canViewOrders",
        children: [
            {name: "Orders", path: "/orders"},
        ],
    },
    {
        name: "Settings",
        path: "/order-inventory/settings",
        requires: "canManageOrderSettings",
        children: [
            {name: "Process tasks", path: "/process-tasks"},
        ],
    },
];
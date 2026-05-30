// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createAuthContext, ENTITLEMENTS} from "@discobole/common-ui";

export const {AuthProvider, useAuth,ProtectedRoute} = createAuthContext({
    canViewOrders: ENTITLEMENTS.VIEW_ORDERS,
    canManageOrderSettings: ENTITLEMENTS.MANAGE_ORDER_SETTINGS,
});
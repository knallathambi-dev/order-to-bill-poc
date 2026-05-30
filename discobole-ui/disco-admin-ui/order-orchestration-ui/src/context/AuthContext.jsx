// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createAuthContext, ENTITLEMENTS} from "@discobole/common-ui";

export const {AuthProvider, useAuth, ProtectedRoute} = createAuthContext({
    canViewOrchestrationPlans: ENTITLEMENTS.VIEW_ORCHESTRATION_PLANS,
    canViewProducts: ENTITLEMENTS.VIEW_PRODUCTS,
    canViewOrders: ENTITLEMENTS.VIEW_ORDERS,
    canViewFalloutIncidents: ENTITLEMENTS.VIEW_FALLOUT_INCIDENTS,
    canModifyFalloutIncidents: ENTITLEMENTS.MODIFY_FALLOUT_INCIDENTS,
});
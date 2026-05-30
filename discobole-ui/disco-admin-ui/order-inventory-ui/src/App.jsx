// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {Navigate, Route, Routes} from "react-router-dom";
import {PageNotFound} from "@discobole/common-ui";
import {AuthProvider, ProtectedRoute} from "./context/AuthContext.jsx";
import {LayoutWrapper} from "./layout/layoutWrapper.jsx";
import {OrdersDetailsPage, OrdersMonitoring} from "./Views/index.js";
import ProcessTasksSettings from "./Views/ProcessTasksSettings.jsx";

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            refetchOnWindowFocus: false,
            retry: 1,
            staleTime: 5 * 60 * 1000,
        },
    },
});

const App = () => (
    <QueryClientProvider client={queryClient}>
        <AuthProvider>
            <Routes>
                <Route index element={<Navigate to="/order-inventory/monitoring/orders" replace/>}/>
                <Route path="/order-inventory" element={<LayoutWrapper/>}>
                    <Route index element={<Navigate to="monitoring/orders" replace/>}/>
                    <Route path="monitoring/orders" element={
                        <ProtectedRoute requires="canViewOrders">
                            <OrdersMonitoring/>
                        </ProtectedRoute>
                    }/>
                    <Route path="orders-details-page/:id" element={
                        <ProtectedRoute requires="canViewOrders">
                            <OrdersDetailsPage/>
                        </ProtectedRoute>
                    }/>
                    <Route path="settings/process-tasks" element={
                        <ProtectedRoute requires="canManageOrderSettings">
                            <ProcessTasksSettings/>
                        </ProtectedRoute>
                    }/>
                    <Route path="*" element={<PageNotFound/>}/>
                </Route>
            </Routes>
        </AuthProvider>
    </QueryClientProvider>
);

export default App;
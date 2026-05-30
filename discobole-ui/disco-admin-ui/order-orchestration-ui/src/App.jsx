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
import {AuthProvider, ProtectedRoute} from "./context/AuthContext";
import {LayoutWrapper} from "./layout/layoutWrapper.jsx";
import {
    ContractStatisticsReport,
    DeliveryFactoryStatisticsReport,
    FalloutIncidents,
    FalloutIncidentsDetailsPage,
    HistoricalReport,
    OrchestrationMonitoringArchivedPlans,
    OrchestrationMonitoringPlans,
    OrchestrationPlanDetailsPage,
    OrchestrationReporting,
    PlanArchiveReport,
    ProductStatisticsReport,
} from "./views/index.js";

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
                <Route
                    index
                    element={<Navigate to="/orchestration-delivery/monitoring/orchestration-plans" replace/>}
                />
                <Route path="/orchestration-delivery" element={<LayoutWrapper/>}>
                    <Route index element={<Navigate to="monitoring/orchestration-plans" replace/>}/>

                    {/* Monitoring Routes */}
                    <Route path="monitoring/orchestration-plans" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <OrchestrationMonitoringPlans/>
                        </ProtectedRoute>
                    }/>
                    <Route path="monitoring/archived-orchestration-plans" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <OrchestrationMonitoringArchivedPlans/>
                        </ProtectedRoute>
                    }/>

                    <Route path="monitoring/orchestration-plans/orchestration-details-page/:id" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <OrchestrationPlanDetailsPage/>
                        </ProtectedRoute>
                    }/>
                    <Route path="monitoring/archived-orchestration-plans/orchestration-details-page/:id" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <OrchestrationPlanDetailsPage/>
                        </ProtectedRoute>
                    }/>

                    <Route path="monitoring/fallout-incidents-details-page/:id" element={
                        <ProtectedRoute requires="canViewFalloutIncidents">
                            <FalloutIncidentsDetailsPage/>
                        </ProtectedRoute>
                    }/>

                    {/* Reporting Routes */}
                    <Route path="reporting/plans-statuses-reporting" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <OrchestrationReporting/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/plans-archivement-reporting" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <PlanArchiveReport/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/plans-historical-report" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <HistoricalReport/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/contract-statistics-report" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <ContractStatisticsReport/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/product-statistics-report" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <ProductStatisticsReport/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/delivery-factory-statistics-report" element={
                        <ProtectedRoute requires="canViewOrchestrationPlans">
                            <DeliveryFactoryStatisticsReport/>
                        </ProtectedRoute>
                    }/>

                    {/* Administration Routes */}
                    <Route path="administration/fallout-incidents" element={
                        <ProtectedRoute requires="canViewFalloutIncidents">
                            <FalloutIncidents/>
                        </ProtectedRoute>
                    }/>

                    <Route path="*" element={<PageNotFound/>}/>
                </Route>
            </Routes>
        </AuthProvider>
    </QueryClientProvider>
);

export default App;
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
import {ProductsDetailsPage, ProductsMonitoring} from "./views/index.js";
import JobsMonitoring from "./views/JobsViews/JobsMonitoring.jsx";
import JobSpecificationMonitoring from "./views/JobsViews/JobSpecificationMonitoring.jsx";
import JobsDetails from "./components/Administration/Jobs/JobsDetails.jsx";
import JobSpecificationDetails from "./components/Administration/JobSpecification/JobSpecificationDetails.jsx";
import CreateJobSpecification from "./views/JobsViews/CreateJobSpecification.jsx";
import ProductsExporting from "./views/ProductsViews/ProductsExporting.jsx";
import DailyReports from "./views/ProductsViews/Reports/DailyReports.jsx";
import PeriodicReports from "./views/ProductsViews/Reports/PeriodicReports.jsx";

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
                <Route index element={<Navigate to="/product-inventory/monitoring/products" replace/>}/>
                <Route path="/product-inventory" element={<LayoutWrapper/>}>
                    <Route index element={<Navigate to="monitoring/products" replace/>}/>

                    {/* Monitoring */}
                    <Route path="monitoring/products" element={
                        <ProtectedRoute requires="canViewProducts">
                            <ProductsMonitoring/>
                        </ProtectedRoute>
                    }/>
                    <Route path="products-details-page/:id" element={
                        <ProtectedRoute requires="canViewProducts">
                            <ProductsDetailsPage/>
                        </ProtectedRoute>
                    }/>

                    {/* Administration */}
                    <Route path="administration/jobs" element={
                        <ProtectedRoute requires="canViewProducts">
                            <JobsMonitoring/>
                        </ProtectedRoute>
                    }/>
                    <Route path="administration/jobSpecification" element={
                        <ProtectedRoute requires="canViewProducts">
                            <JobSpecificationMonitoring/>
                        </ProtectedRoute>
                    }/>
                    <Route path="jobs-details-page/:id" element={
                        <ProtectedRoute requires="canViewProducts">
                            <JobsDetails/>
                        </ProtectedRoute>
                    }/>
                    <Route path="jobSpecification-details-page/:id" element={
                        <ProtectedRoute requires="canViewProducts">
                            <JobSpecificationDetails/>
                        </ProtectedRoute>
                    }/>
                    <Route path="create-jobSpecification-page" element={
                        <ProtectedRoute requires="canViewProducts">
                            <CreateJobSpecification/>
                        </ProtectedRoute>
                    }/>

                    {/* Exporting & Reporting */}
                    <Route path="exporting/products" element={
                        <ProtectedRoute requires="canViewProducts">
                            <ProductsExporting/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/reports/daily" element={
                        <ProtectedRoute requires="canViewProducts">
                            <DailyReports/>
                        </ProtectedRoute>
                    }/>
                    <Route path="reporting/reports/periodic" element={
                        <ProtectedRoute requires="canViewProducts">
                            <PeriodicReports/>
                        </ProtectedRoute>
                    }/>

                    <Route path="*" element={<PageNotFound/>}/>
                </Route>
            </Routes>
        </AuthProvider>
    </QueryClientProvider>
);

export default App;
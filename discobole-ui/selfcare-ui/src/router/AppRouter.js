// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createBrowserRouter, Navigate} from "react-router-dom";
import {AppLayout} from "../layouts";
import {
    Account,
    CompleteOrder,
    EditPlan,
    Home,
    OrderStatusUpdate,
    OrderSummary,
    PageNotFound,
    SetUpPlan,
    Shipping,
    TrackOrder
} from "../pages";
import AuthenticationTabs from "../components/AccessManagement/AuthenticationTabs";
import Eligibility from "../pages/Eligibility/Eligibility";
import DeviceSetUp from "../pages/Plan/DeviceSetUp/components/DeviceSetUp";
import EligiblePlans from "../pages/Plan/MigratePlan/EligiblePlans";
import BookAppointment from "../pages/BookAppointment/BookAppointment";
import RequireConnected from "../pages/AccessRequired/RequireConnected";
import RootLayout from "./RootLayout";

const AppRouter = createBrowserRouter([
    {
        element: <RootLayout/>,
        children: [
            {
                path: "/",
                element: <AppLayout/>,
                children: [
                    {index: true, element: <Navigate to="/home" replace/>},
                    {path: "home", element: <Home/>},
                    {
                        path: "my-account", element: (
                            <RequireConnected>
                                <Account/>
                            </RequireConnected>
                        )
                    },
                    {path: "set-up-plan", element: <SetUpPlan/>},
                    {path: "set-up-device", element: <DeviceSetUp/>},
                    {path: "eligibility", element: <Eligibility/>},
                    {path: "shipping", element: <Shipping/>},
                    {path: "order-summary", element: <OrderSummary/>},
                    {path: "complete-order", element: <CompleteOrder/>},
                    {path: "book-appointment", element: <BookAppointment/>},
                    {path: "orderStatusUpdate", element: <OrderStatusUpdate/>},
                    {path: "login", element: <AuthenticationTabs/>},
                    {path: "track-order", element: <TrackOrder/>},
                    {path: "edit-plan", element: <EditPlan/>},
                    {path: "terminate-plan", element: <EditPlan/>},
                    {path: "eligible-plans", element: <EligiblePlans/>},
                    {path: "migrate-plan", element: <SetUpPlan/>},
                    {path: "*", element: <PageNotFound/>}
                ],
            },
        ],
    },
]);

export default AppRouter;
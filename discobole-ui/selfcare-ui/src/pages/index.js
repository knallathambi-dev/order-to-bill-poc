// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import Account from "./Account/Account";
import SetUpPlan from "./Plan/SetUpPlan/SetUpPlan";
import Home from "./Home/Home";
import PageNotFound from "./PageNotFound/PageNotFound";
import Shipping from "./Shipping/Shipping";
import OrderSummary from "./OrderSummary/OrderSummary";
import CompleteOrder from "./CompleteOrder/CompleteOrder";
import OrderStatusUpdate from "./OrderStatusUpdate/OrderStatusUpdate";
import TrackOrder from "./TrackOrder/TrackOrder";
import EditPlan from "./Plan/EditPlan/EditPlan";
import DeviceSetUp from "./Plan/DeviceSetUp/components/DeviceSetUp";
import Eligibility from "./Eligibility/Eligibility";
import BookAppointment from "./BookAppointment/BookAppointment";
import EligiblePlans from "./Plan/MigratePlan/EligiblePlans";
import AuthenticationTabs from "../components/AccessManagement/AuthenticationTabs";

export {
    Account,
    SetUpPlan,
    EditPlan,
    DeviceSetUp,
    Home,
    PageNotFound,
    Shipping,
    OrderSummary,
    CompleteOrder,
    OrderStatusUpdate,
    TrackOrder,
    Eligibility,
    BookAppointment,
    EligiblePlans,
    AuthenticationTabs
};
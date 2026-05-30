// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {combineReducers} from "redux";
import loadingSlice from "./loadingSlice";
import offersSlice from "./offerSlice";
import shippingSlice from "./shippingSlice";
import pricingSlice from "./pricingSlice";
import authReducer from "./authReducer";
import taskManagerSlice from "./taskManagerSlice";
import orderSlice from "./orderSlice";
import planSlice from "./planSlice";
import orderCompletionSlice from "./orderCompletionSlice";
import breadcrumbSlice from "./breadcrumbSlice";
import itemsRequiringShippingSlice from "./itemsRequiringShippingSlice";
import editSlice from "./editSlice";
import configurationSlice from "./configurationSlice";

const Reducers = combineReducers({
    offer: offersSlice,
    configuration: configurationSlice,
    shipping: shippingSlice,
    loading: loadingSlice,
    auth: authReducer,
    pricing: pricingSlice,
    taskManager: taskManagerSlice,
    order: orderSlice,
    plan: planSlice,
    orderCompletion: orderCompletionSlice,
    breadcrumb: breadcrumbSlice,
    itemsRequiringShipping: itemsRequiringShippingSlice,
    edit: editSlice
})

export default Reducers
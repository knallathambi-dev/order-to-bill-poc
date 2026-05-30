// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import ReactDOM from "react-dom/client";
import {Provider} from "react-redux";
import {PersistGate} from 'redux-persist/integration/react';
import {ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import App from "./App";
import "./index.css";
import "leaflet/dist/leaflet.css";
import reportWebVitals from "./reportWebVitals";
import {persistor, store} from "./store";
import 'boosted/dist/js/boosted.min.js';

function boot() {
    const root = ReactDOM.createRoot(document.getElementById("root"));
    root.render(
        <Provider store={store}>
            <PersistGate loading={<div>Loading...</div>} persistor={persistor}>
                <App/>
                <ToastContainer/>
            </PersistGate>
        </Provider>
    );
    reportWebVitals();
}

boot();
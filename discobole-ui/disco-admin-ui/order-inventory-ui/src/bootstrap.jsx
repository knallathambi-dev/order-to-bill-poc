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
import {BrowserRouter} from "react-router-dom";
import {AuthService, ToastContainer} from "@discobole/common-ui";
import {env} from "./utils/env-helper.js";
import App from "./App.jsx";

let root = null;

export const mount = async (el, {standaloneMode} = {}) => {
    if (!el) throw new Error("[MFE] Mount failed: no DOM element provided");

    if (standaloneMode) {
        await AuthService.init(env.GATEWAY_URL);

        if (!AuthService.initialized) return;

        if (root) root.unmount();
        root = ReactDOM.createRoot(el);
        root.render(
            <React.StrictMode>
                <BrowserRouter>
                    <App/>
                    <ToastContainer/>
                </BrowserRouter>
            </React.StrictMode>
        );
        return root;
    }

    return <App/>;
};
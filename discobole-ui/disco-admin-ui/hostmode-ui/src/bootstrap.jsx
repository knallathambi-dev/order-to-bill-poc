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
import App from "./App";
import {AuthService} from "@discobole/common-ui";
import {env} from "./utils/env-helper.js";

export const mount = async (el) => {
    await AuthService.init(env.GATEWAY_URL);

    if (!AuthService.initialized) return;

    ReactDOM.createRoot(el).render(
        <React.StrictMode>
            <App/>
        </React.StrictMode>
    );
};
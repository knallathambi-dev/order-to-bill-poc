// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {mount} from "./bootstrap.jsx";
import {env} from "./utils/env-helper.js";
import "@discobole/common-ui/dist/common-ui.css";
import "./index.css";

if (env.STANDALONE_MODE === "true") {
    mount(document.getElementById("root"), {standaloneMode: true});
}
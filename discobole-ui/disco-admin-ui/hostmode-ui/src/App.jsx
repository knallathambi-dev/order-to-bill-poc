// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {LayoutWrapper} from "./layout/layoutWrapper";
import {RouterProvider} from "./context/RouterContext";

function App() {
    return (
        <BrowserRouter>
            <RouterProvider>
                <Routes>
                    <Route path="/*" element={<LayoutWrapper/>}/>
                </Routes>
            </RouterProvider>
        </BrowserRouter>
    );
}

export default App;
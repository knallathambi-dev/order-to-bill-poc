// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {createContext, useMemo} from "react";
import {useLocation, useNavigate} from "react-router-dom";

const RouterContext = createContext(null);

export const RouterProvider = ({children}) => {
    const navigate = useNavigate();
    const location = useLocation();

    const value = useMemo(() => ({
        navigate,
        location,
    }), [navigate, location]);

    return (
        <RouterContext.Provider value={value}>
            {children}
        </RouterContext.Provider>
    );
};
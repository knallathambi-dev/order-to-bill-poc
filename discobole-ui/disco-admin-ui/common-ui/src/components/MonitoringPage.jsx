// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useSideMenu} from "../context/SideMenuContext";

export const MonitoringPage = ({title = "", actions, children}) => {
    const {isActiveNav} = useSideMenu();
    const hasTitle = title.trim() !== "";

    return (
        <div className={`py-1 content-wrapper ${isActiveNav ? "active-cont" : ""}`}>
            <div className={`container-fluid${hasTitle ? " py-3" : ""}`}>
                <div className="d-flex justify-content-between align-items-center mb-1">
                    {hasTitle && <h1 className="display-3 mb-0">{title}</h1>}
                    {actions}
                </div>
                <div className={hasTitle ? "py-3" : ""}>
                    {children}
                </div>
            </div>
        </div>
    );
};
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {Outlet} from "react-router-dom";
import Header from "./Header";
import SideMenu from "./SideMenu";
import {SideMenuProvider} from "../context/SideMenuContext";
import BffAuthService from "../service/BffAuthService";

export const MfeLayoutWrapper = ({link, sideMenuItems, standalone = false, auth}) => (
    <SideMenuProvider>
        {standalone && (
            <Header
                logout={BffAuthService.logout}
                userInfo={BffAuthService.getUserInfo()}
                link={link}
            />
        )}
        <div className="container-fluid mt-1">
            <SideMenu sideMenuItems={sideMenuItems} auth={auth}/>
            <div>
                <Outlet/>
            </div>
        </div>
    </SideMenuProvider>
);
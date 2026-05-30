// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import {Outlet} from "react-router-dom";
import Header from "../components/Header";
import SideMenu from "../components/SideMenu";
import {SideMenuProvider} from "../context/SideMenuContext";
import BffAuthService from "../service/BffAuthService";

const AppLayout = ({sideMenuItems}) => (
    <SideMenuProvider>
        <div className="container-fluid">
            <Header
                logout={BffAuthService.logout}
                userInfo={BffAuthService.getUserInfo()}
            />
            <SideMenu sideMenuItems={sideMenuItems}/>
            <Outlet/>
        </div>
    </SideMenuProvider>
);

AppLayout.propTypes = {
    sideMenuItems: PropTypes.array.isRequired,
};

export default AppLayout;
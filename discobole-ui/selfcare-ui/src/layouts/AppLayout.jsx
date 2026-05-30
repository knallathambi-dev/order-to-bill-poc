// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useSelector} from "react-redux";
import {Outlet} from "react-router-dom";
import {Footer, Header} from "../components";
import Loading from "../components/Loading/Loading";

const AppLayout = () => {
    const {isLoading} = useSelector(state => state.loading);

    return (
        <div className="app-container">
            <div className="header-sidebar-footer-container">
                <Header/>
                <div className="body-container">
                    <Outlet/>
                    <Loading isLoading={isLoading}></Loading>
                </div>
                <Footer/>
            </div>
        </div>
    );
};

export default AppLayout;
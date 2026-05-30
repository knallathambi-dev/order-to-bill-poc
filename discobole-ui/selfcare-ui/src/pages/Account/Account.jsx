// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";

import SideMenu from "./components/SideMenu";
import Dashboard from "./components/Dashboard";
import Profile from "./components/Profile";
import Orders from "./components/Orders/Orders";
import Plans from "./components/Plans/Plans";
import {useTitlePage} from "../../hooks";
import useTranslations from "../../utlis/i18n/useTranslations";

function Account() {
    useTitlePage("myAccount");

    const [activeTab, setActiveTab] = useState('my-orders');
    const {t} = useTranslations();

    const handleTabClick = (tab) => {
        setActiveTab(tab);

        const url = new URL(window.location.href);
        url.searchParams.set('tab', tab);
        window.history.pushState({}, '', url);
    };

    useEffect(() => {
        const url = new URL(window.location.href);
        const tab = url.searchParams.get('tab');

        if (tab) {
            setActiveTab(tab);
        } else {
            setActiveTab('my-orders');
        }
    }, []);

    const renderTabContent = () => {
        switch (activeTab) {
            case 'dashboard':
                return <Dashboard/>;
            case 'my-profile':
                return <Profile/>;
            case 'my-orders':
                return <Orders/>;
            case 'my-plans':
                return <Plans/>;
            default:
                return <Orders/>;
        }
    };

    return (
        <main className="mb-lg-14 mb-8 mt-8">
            <div className="container my-4">
                <div className="row">
                    <div className="col-12">
                        <div className="py-1">
                            <div className="d-flex mb-3">
                                <div className="me-auto">
                                    <h1 className="fw-bold mb-0">{t('pages.myAccount')}</h1>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="row">
                    <SideMenu activeTab={activeTab} setActiveTab={handleTabClick}/>
                    <div className="col-9">
                        <div className="card profile-card h-100">
                            <div className="card-body">
                                <div className="row">
                                    <div className="col-12">
                                        <div className="tab-content profile-content" id="nav-tabs-content">
                                            <div className="tab-pane fade show active">
                                                {renderTabContent()}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
}

export default Account;
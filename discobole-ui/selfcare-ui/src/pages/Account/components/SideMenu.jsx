// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {Link} from "react-router-dom";
import useTranslations from "../../../utlis/i18n/useTranslations";

function SideMenu({activeTab, setActiveTab}) {
    const {t} = useTranslations();

    const handleTabClick = async (tab) => {
        setActiveTab(tab)
    }

    return (
        <div className="col-3">
            <div className="card profile-card h-100">
                <div className="card-body">
                    <ul role="tablist" aria-owns="nav-tab1 nav-tab2 nav-tab3 nav-tab4"
                        className="nav flex-column nav-tabs profile-nav">
                        <li className="nav-item" role="presentation">
                            <Link
                                className={`nav-link ${activeTab === 'my-orders' ? 'active' : ''}`}
                                id="nav-tab3"
                                to="#tab3-content"
                                data-bs-toggle="tab"
                                data-bs-target="#tab3-content"
                                role="tab"
                                aria-controls="tab3-content"
                                aria-selected={activeTab === 'my-orders'}
                                onClick={() => handleTabClick('my-orders')}>
                                {t('pages.myOrders')}
                            </Link>
                        </li>
                        <li className="nav-item" role="presentation">
                            <Link
                                className={`nav-link ${activeTab === 'my-plans' ? 'active' : ''}`}
                                id="nav-tab4"
                                to="#tab4-content"
                                data-bs-toggle="tab"
                                data-bs-target="#tab4-content"
                                role="tab"
                                aria-controls="tab4-content"
                                aria-selected={activeTab === 'my-plans'}
                                onClick={() => handleTabClick('my-plans')}>
                                {t('pages.myPlans')}
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    )
}

export default SideMenu;
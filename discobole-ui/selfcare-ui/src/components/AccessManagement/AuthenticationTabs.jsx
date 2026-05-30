// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from "react";
import PropTypes from "prop-types";
import {useLocation, useNavigate} from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import useTranslations from "../../utlis/i18n/useTranslations";

function AuthenticationTabs({onLoginSuccess}) {
    const {t} = useTranslations();
    const navigate = useNavigate();
    const location = useLocation();

    const [activeTab, setActiveTab] = useState('login');
    const [prefillEmail, setPrefillEmail] = useState('');
    const [postRegistrationMessage, setPostRegistrationMessage] = useState('');

    const switchToLoginAfterRegistration = (email) => {
        setPrefillEmail(email || '');
        setPostRegistrationMessage(t('auth.registerSuccessPleaseLogin'));
        setActiveTab('login');
    };

    const handleLoginSuccess = useCallback(() => {
        if (typeof onLoginSuccess === 'function') {
            onLoginSuccess();
            return;
        }
        const returnTo = location.state?.from?.pathname || '/home';
        navigate(returnTo, {replace: true});
    }, [onLoginSuccess, location, navigate]);

    const isLoginActive = activeTab === 'login';
    const isRegisterActive = activeTab === 'register';

    return (
        <div className="container">
            <div className="row mt-3">
                <div className="col-md-8 mx-auto">
                    <h1 className="mb-2">{t('auth.greeting')}</h1>
                    <p className="text-muted">{t('auth.pleaseLoginOrRegister')}</p>
                    <div className="select-method">
                        <ul className="nav nav-underline nav-fill mb-3" role="tablist">
                            <li className="nav-item" role="presentation">
                                <button
                                    className={`nav-link d-flex justify-content-center ${isLoginActive ? 'active' : ''}`}
                                    type="button"
                                    role="tab"
                                    aria-selected={isLoginActive}
                                    onClick={() => setActiveTab('login')}
                                >
                                    {t('actions.login')}
                                </button>
                            </li>
                            <li className="nav-item" role="presentation">
                                <button
                                    className={`nav-link d-flex justify-content-center ${isRegisterActive ? 'active' : ''}`}
                                    type="button"
                                    role="tab"
                                    aria-selected={isRegisterActive}
                                    onClick={() => setActiveTab('register')}
                                >
                                    {t('actions.register')}
                                </button>
                            </li>
                        </ul>

                        <div className="tab-content">
                            {isLoginActive && (
                                <div className="tab-pane fade show active" role="tabpanel">
                                    <Login
                                        onSuccess={handleLoginSuccess}
                                        initialEmail={prefillEmail}
                                        successBanner={postRegistrationMessage}
                                    />
                                </div>
                            )}
                            {isRegisterActive && (
                                <div className="tab-pane fade show active" role="tabpanel">
                                    <Register onRegistered={switchToLoginAfterRegistration}/>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

AuthenticationTabs.propTypes = {
    onLoginSuccess: PropTypes.func
};

export default AuthenticationTabs;
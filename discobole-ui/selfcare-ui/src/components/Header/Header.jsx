// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useRef, useState} from "react";
import {Link, useLocation} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {toast} from "react-toastify";
import {Modal} from "../index";
import "./Header.css";
import discoBoleLogo from "../../assests/imgs/Discobole.png";
import profileIconLisa from "../../assests/imgs/profile-img.svg";
import profileIconHomer from "../../assests/imgs/profile-img-2.svg";
import profileIconDefault from "../../assests/imgs/profile-img-3.svg";
import AuthenticationTabs from "../AccessManagement/AuthenticationTabs";
import {resetBreadcrumb} from "../../store/actions/breadcrumbActions";
import {capitalizeFirstLetter} from "../../utlis/helpers";
import useTranslations from "../../utlis/i18n/useTranslations";
import Logout from "../AccessManagement/components/Logout";

const PROFILE_ICONS = {
    lisa: profileIconLisa,
    homer: profileIconHomer,
    default: profileIconDefault,
};

const getProfileIcon = (username) =>
    PROFILE_ICONS[username?.toLowerCase()] ?? PROFILE_ICONS.default;

function useClickOutside(ref, isOpen, onClose) {
    useEffect(() => {
        if (!isOpen) return;

        const handler = (e) => {
            if (ref.current && !ref.current.contains(e.target)) onClose();
        };

        document.addEventListener("mousedown", handler);
        return () => document.removeEventListener("mousedown", handler);
    }, [ref, isOpen, onClose]);
}

function useSessionExpiredToast(location, tNotification) {
    const shown = useRef(false);

    useEffect(() => {
        if (location.state?.sessionExpired && !shown.current) {
            shown.current = true;
            toast.info(tNotification("auth.sessionExpired"));
            window.history.replaceState({}, document.title);
        }
    }, [location.state, tNotification]);

    useEffect(() => {
        if (!location.state?.sessionExpired) shown.current = false;
    }, [location.pathname, location.state?.sessionExpired]);
}

function Brand() {
    return (
        <div className="navbar-brand me-auto d-flex align-items-center">
            <Link className="stretched-link" to="/home" aria-label="Back to Home">
                <img
                    src={discoBoleLogo}
                    width="50"
                    height="50"
                    alt="Online Selfcare Logo"
                    loading="lazy"
                />
            </Link>
            <h1 className="title mt-1">Online Selfcare</h1>
        </div>
    );
}

function ProfileDropdown({
                             displayName,
                             icon,
                             isOpen,
                             onToggle,
                             onNavigateAccount,
                             onLogout,
                             dropdownRef,
                             t,
                         }) {
    return (
        <li
            ref={dropdownRef}
            className={`nav-item dropdown${isOpen ? " show" : ""}`}
        >
            <button
                type="button"
                className="nav-link ms-2 dropdown-toggle btn btn-link"
                aria-expanded={isOpen}
                aria-label="User menu"
                onClick={onToggle}
            >
                <img
                    src={icon}
                    width="30"
                    height="30"
                    alt={`${displayName} profile`}
                    loading="lazy"
                />
                <span className="ms-2">{displayName}</span>
            </button>

            <ul
                className={`dropdown-menu dropdown-menu-dark dropdown-menu-end${isOpen ? " show" : ""}`}
            >
                <li>
                    <Link className="dropdown-item" to="/my-account" onClick={onNavigateAccount}>
                        {t("pages.myAccount")}
                    </Link>
                </li>
                <li>
                    <button type="button" className="dropdown-item" onClick={onLogout}>
                        {t("actions.logout")}
                    </button>
                </li>
            </ul>
        </li>
    );
}

function LoginButton({onClick, t}) {
    return (
        <li className="nav-item">
            <button
                type="button"
                className="nav-link btn btn-link"
                onClick={onClick}
                aria-label="Login"
            >
                <em className="icon-avatar me-1"/>
                <span>{t("actions.login")}</span>
            </button>
        </li>
    );
}

function LanguageDropdown({language, isOpen, onToggle, onChange, dropdownRef}) {
    return (
        <li ref={dropdownRef} className={`nav-item dropdown${isOpen ? " show" : ""}`}>
            <button
                type="button"
                className="nav-link dropdown-toggle d-flex align-items-center btn btn-link"
                aria-expanded={isOpen}
                aria-label="Select language"
                onClick={onToggle}
            >
                {language.codes[language.current]}
                <em className="icon-chevron-down ms-1"/>
            </button>

            <ul
                className={`dropdown-menu dropdown-menu-dark dropdown-menu-end${isOpen ? " show" : ""}`}
            >
                {language.supported.map((code) => (
                    <li key={code}>
                        <button
                            type="button"
                            className="dropdown-item d-flex justify-content-between align-items-center"
                            onClick={() => onChange(code)}
                        >
                            {language.names[code]}
                            {language.current === code && (
                                <em className="icon-tick text-success ms-2" aria-label="Selected"/>
                            )}
                        </button>
                    </li>
                ))}
            </ul>
        </li>
    );
}

function Header() {
    const dispatch = useDispatch();
    const location = useLocation();
    const {t, language, tNotification} = useTranslations();
    const {isAuthenticated, relatedParty} = useSelector((state) => state.auth);

    const [isLogoutModalVisible, setIsLogoutModalVisible] = useState(false);
    const [isLoginModalVisible, setIsLoginModalVisible] = useState(false);

    const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
    const [languageDropdownOpen, setLanguageDropdownOpen] = useState(false);

    const profileDropdownRef = useRef(null);
    const languageDropdownRef = useRef(null);

    const toggleProfileDropdown = useCallback(() => {
        setProfileDropdownOpen((prev) => !prev);
        setLanguageDropdownOpen(false);
    }, []);

    const toggleLanguageDropdown = useCallback(() => {
        setLanguageDropdownOpen((prev) => !prev);
        setProfileDropdownOpen(false);
    }, []);

    const closeProfileDropdown = useCallback(() => setProfileDropdownOpen(false), []);
    const closeLanguageDropdown = useCallback(() => setLanguageDropdownOpen(false), []);

    useClickOutside(profileDropdownRef, profileDropdownOpen, closeProfileDropdown);
    useClickOutside(languageDropdownRef, languageDropdownOpen, closeLanguageDropdown);

    useSessionExpiredToast(location, tNotification);

    useEffect(() => {
        const show = () => setIsLoginModalVisible(true);
        window.addEventListener("auth:login-required", show);
        return () => window.removeEventListener("auth:login-required", show);
    }, []);

    const userName = isAuthenticated ? relatedParty?.name : null;
    const userDisplayName = userName ? capitalizeFirstLetter(userName) : null;
    const userProfileIcon = userName ? getProfileIcon(userName) : null;

    const openLoginModal = useCallback(() => setIsLoginModalVisible(true), []);
    const closeLoginModal = useCallback(() => setIsLoginModalVisible(false), []);
    const openLogoutModal = useCallback(() => setIsLogoutModalVisible(true), []);
    const closeLogoutModal = useCallback(() => setIsLogoutModalVisible(false), []);

    const handleNavigateAccount = useCallback(() => {
        dispatch(resetBreadcrumb());
        setProfileDropdownOpen(false);
    }, [dispatch]);

    const handleLogoutClick = useCallback(() => {
        openLogoutModal();
        setProfileDropdownOpen(false);
    }, [openLogoutModal]);

    const handleLanguageChange = useCallback(
        (code) => {
            language.change(code);
            setLanguageDropdownOpen(false);
        },
        [language],
    );

    return (
        <>
            <header className="sticky-top">
                <nav className="navbar navbar-dark bg-dark navbar-expand-lg">
                    <div className="container-xxl">
                        <Brand/>

                        <button
                            className="navbar-toggler collapsed"
                            type="button"
                            data-bs-toggle="collapse"
                            data-bs-target="#globalHeaderNav"
                            aria-controls="globalHeaderNav"
                            aria-expanded="false"
                            aria-label="Toggle navigation"
                        >
                            <span className="navbar-toggler-icon"/>
                        </button>

                        <div id="globalHeaderNav" className="navbar-collapse collapse d-sm-flex">
                            <ul className="navbar-nav flex-row align-items-center ms-auto">
                                {userDisplayName && userProfileIcon ? (
                                    <ProfileDropdown
                                        displayName={userDisplayName}
                                        icon={userProfileIcon}
                                        isOpen={profileDropdownOpen}
                                        onToggle={toggleProfileDropdown}
                                        onNavigateAccount={handleNavigateAccount}
                                        onLogout={handleLogoutClick}
                                        dropdownRef={profileDropdownRef}
                                        t={t}
                                    />
                                ) : (
                                    <LoginButton onClick={openLoginModal} t={t}/>
                                )}

                                <LanguageDropdown
                                    language={language}
                                    isOpen={languageDropdownOpen}
                                    onToggle={toggleLanguageDropdown}
                                    onChange={handleLanguageChange}
                                    dropdownRef={languageDropdownRef}
                                />
                            </ul>
                        </div>
                    </div>
                </nav>
            </header>

            {isLogoutModalVisible && (
                <Modal
                    show
                    title={t("auth.logout.title")}
                    body={<Logout onCancel={closeLogoutModal}/>}
                    onClose={closeLogoutModal}
                    hideFooter
                    dialogClassName="modal-dialog-centered"
                    className="logout-modal"
                />
            )}

            {isLoginModalVisible && (
                <Modal
                    show
                    body={<AuthenticationTabs onLoginSuccess={closeLoginModal}/>}
                    onClose={closeLoginModal}
                    hideFooter
                    dialogClassName="modal-dialog-centered"
                    className="login-modal"
                />
            )}
        </>
    );
}

export default Header;
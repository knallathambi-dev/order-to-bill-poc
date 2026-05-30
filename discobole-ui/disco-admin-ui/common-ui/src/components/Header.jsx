// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect, useRef, useState} from "react";
import PropTypes from "prop-types";
import logo from "../assets/img/discobole.png";
import NavBar from "./NavBar";
import {APP_LABELS} from "../utils/constants";

const DEFAULT_LINK = "/order-inventory/monitoring/orders";
const COLLAPSE_TARGET = "global-header-collapse";

function Header({logout, userInfo, tabs, link}) {
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);

    const toggleDropdown = () => setDropdownOpen((prev) => !prev);

    useEffect(() => {
        if (!dropdownOpen) return;

        const handleClickOutside = (e) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
                setDropdownOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [dropdownOpen]);

    return (
        <header className="sticky-top">
            <nav
                className="navbar navbar-dark bg-dark navbar-expand-lg"
                aria-label="Global navigation"
            >
                <div className="container-fluid">
                    <div className="navbar-brand me-auto d-flex align-items-center">
                        <a
                            href={link || DEFAULT_LINK}
                            className="d-flex align-items-center"
                        >
                            <img
                                src={logo}
                                width="50"
                                height="50"
                                alt="Boosted - Back to Home"
                                loading="lazy"
                            />
                            <h1 className="title mt-1 color-light">
                                {APP_LABELS.ADMINISTRATION_PORTAL}
                            </h1>
                        </a>
                    </div>

                    <button
                        className="navbar-toggler collapsed"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target={`#${COLLAPSE_TARGET}`}
                        aria-controls={COLLAPSE_TARGET}
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                    >
                        <span className="navbar-toggler-icon"/>
                    </button>

                    <div
                        id={COLLAPSE_TARGET}
                        className="navbar-collapse collapse d-sm-flex"
                    >
                        <ul className="navbar-nav flex-row">
                            <li
                                ref={dropdownRef}
                                className={`nav-item dropdown${dropdownOpen ? " show" : ""}`}
                            >
                                <button
                                    type="button"
                                    className="nav-link dropdown-toggle btn btn-link"
                                    aria-expanded={dropdownOpen}
                                    onClick={toggleDropdown}
                                >
                                    <em className="icon-administrator"/>
                                    {userInfo.name ?? ""}
                                </button>
                                <ul className={`dropdown-menu dropdown-menu-dark dropdown-menu-end${dropdownOpen ? " show" : ""}`}>
                                    <li>
                                        <button
                                            type="button"
                                            className="dropdown-item"
                                            onClick={logout}
                                        >
                                            Logout
                                        </button>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            {tabs?.length > 0 && (
                <nav
                    className="navbar navbar-dark bg-dark navbar-expand-lg py-0"
                    aria-label="Sub navigation"
                >
                    <NavBar tabs={tabs}/>
                </nav>
            )}
        </header>
    );
}

Header.propTypes = {
    logout: PropTypes.func.isRequired,
    userInfo: PropTypes.shape({
        name: PropTypes.string,
    }).isRequired,
    tabs: NavBar.propTypes.tabs,
    link: PropTypes.string,
};

export default Header;
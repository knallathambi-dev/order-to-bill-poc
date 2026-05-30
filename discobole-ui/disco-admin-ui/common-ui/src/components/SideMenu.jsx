// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import {useEffect, useMemo, useState} from "react";
import {Link, useLocation} from "react-router-dom";
import {useSideMenu} from "../context/SideMenuContext";

const filterByPermissions = (items, auth) => {
    if (!auth) return items;

    return items
        .filter((item) => {
            if (!item.requires) return true;
            const keys = Array.isArray(item.requires) ? item.requires : [item.requires];
            return keys.every((key) => auth[key]);
        })
        .map((item) => {
            if (!item.children) return item;
            const filteredChildren = item.children.filter((child) => {
                if (!child.requires) return true;
                const keys = Array.isArray(child.requires) ? child.requires : [child.requires];
                return keys.every((key) => auth[key]);
            });
            return filteredChildren.length > 0 ? {...item, children: filteredChildren} : null;
        })
        .filter(Boolean);
};

export default function SideMenu({sideMenuItems, auth}) {
    const {isActiveNav, toggleMenu} = useSideMenu();
    const location = useLocation();

    const visibleItems = useMemo(
        () => filterByPermissions(sideMenuItems, auth),
        [sideMenuItems, auth]
    );

    const [activeAccordion, setActiveAccordion] = useState(0);

    useEffect(() => {
        visibleItems.forEach((menuItem, index) => {
            if (location.pathname.startsWith(menuItem.path)) {
                setActiveAccordion(index);
            }
        });

        visibleItems.forEach((menuItem, index) => {
            if (
                menuItem?.children.some(
                    (childItem) => location.pathname === `${menuItem.path}${childItem.path}`
                )
            ) {
                setActiveAccordion(index);
            }
        });
    }, [location.pathname, visibleItems]);

    const toggleAccordion = (index) => {
        setActiveAccordion(activeAccordion === index ? null : index);
    };

    const isDetailsPageActive = (menuItem, childItemPath) => {
        const condition = location.pathname.includes("archived")
            ? childItemPath.includes("archived")
            : !childItemPath.includes("archived");

        return (
            location.pathname.includes("details") &&
            condition &&
            location.pathname.startsWith(menuItem.path)
        );
    };

    return (
        <div
            className={`side-navbar d-flex justify-content-between flex-wrap flex-column pt-3 ${isActiveNav ? "active-nav" : ""}`}
            id="sidebar"
        >
            <div className="position-relative">
                <button
                    onClick={toggleMenu}
                    className="position-absolute btn btn-dark border-0"
                    id="menu-btn"
                >
                    <em className="icon-arrow-previous"/>
                </button>
                <div
                    className="accordion accordion-sm side-menu"
                    id="accordionExampleSmall accordionPanelsStayOpenExample"
                >
                    {visibleItems.map((menuItem, index) => (
                        <div key={menuItem.name} className="accordion-item">
                            <h2 className="accordion-header border-top-0">
                                <button
                                    className={`accordion-button ${activeAccordion !== index ? "collapsed" : ""}`}
                                    type="button"
                                    onClick={() => toggleAccordion(index)}
                                    data-bs-target={`#collapseOneSmall-${index}`}
                                    aria-expanded={activeAccordion === index}
                                    aria-controls={`collapseOneSmall-${index}`}
                                >
                                    {menuItem.name}
                                </button>
                            </h2>
                            <div
                                id={`collapseOneSmall-${index}`}
                                className={`${activeAccordion === index ? "show" : ""} accordion-collapse collapse`}
                                data-bs-parent="#accordionExampleSmall"
                            >
                                <div className="accordion-body">
                                    <ul className="nav flex-column">
                                        {menuItem?.children.map((childItem) => (
                                            <li key={childItem.name} className="nav-item">
                                                <Link
                                                    to={`${menuItem.path}${childItem.path}${childItem.defaultQuery || ""}`}
                                                    className={`nav-link ${location.pathname === `${menuItem.path}${childItem.path}` || isDetailsPageActive(menuItem, childItem.path) ? "active" : ""}`}
                                                >
                                                    {childItem.name}
                                                </Link>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

SideMenu.propTypes = {
    sideMenuItems: PropTypes.arrayOf(PropTypes.object).isRequired,
    auth: PropTypes.object,
};
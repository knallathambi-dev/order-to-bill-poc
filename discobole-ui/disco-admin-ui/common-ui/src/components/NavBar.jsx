// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {NavLink} from "react-router-dom";
import {Tooltip} from "react-tooltip";
import PropTypes from "prop-types";

function NavBar({tabs}) {

    return (
        <div className="container-fluid">
            <div id="global-header-3-1.1"
                 className="navbar-collapse collapse m-0 global-header-3-1">
                <ul className="navbar-nav">
                    {tabs.map((tab) => {
                        if (tab.isAuthorized) {
                            return (
                                <li key={tab.label} className="nav-item">
                                    <NavLink
                                        className={({isActive}) => (isActive ? "nav-link active" : "nav-link")}
                                        aria-current={tab.ariaContent}
                                        data-tooltip-id="my-tooltip"
                                        data-tooltip-content={tab.tooltipContent}
                                        to={tab.to}
                                    >
                                        {tab.label}
                                    </NavLink>
                                </li>
                            );
                        }
                    })}
                    <Tooltip id="my-tooltip" place="right"/>
                </ul>
            </div>
        </div>
    );
}

NavBar.propTypes = {
    tabs: PropTypes.arrayOf(
        PropTypes.shape({
            ariaContent: PropTypes.string,
            tooltipContent: PropTypes.string.isRequired,
            to: PropTypes.string.isRequired,
            isAuthorized: PropTypes.bool.isRequired,
            label: PropTypes.string.isRequired,
        })
    ),
};

export default NavBar;
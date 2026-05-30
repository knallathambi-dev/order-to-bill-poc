// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";

export default function ShippingTabContent({shippingMethods, selectedTab}) {

    return (
        <div className="tab-content" id="pills-tabContent">
            {shippingMethods.map((shippingMethod) => {
                return (
                    <div key={shippingMethod.id}
                         className={`tab-pane fade ${selectedTab === shippingMethod.id ? "show active" : ""}`}
                         id={`pills-${shippingMethod.id}`}
                         role="tabpanel"
                         aria-labelledby={`pills-tab-${shippingMethod.id}`}
                         tabIndex={shippingMethod.id}
                    >
                        {shippingMethod.component}
                    </div>
                )
            })}
        </div>
    )
}

ShippingTabContent.propTypes = {
    shippingMethods: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number.isRequired,
            name: PropTypes.string.isRequired,
            component: PropTypes.node.isRequired
        })
    ).isRequired,
    selectedTab: PropTypes.number.isRequired
}
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

export default function ShippingTabsNav({shippingMethods, selectedTab, onSelectTab}) {

    return (
        <ul className="nav nav-pills mb-3" id="pills-tab" role="tablist">
            {shippingMethods.map((shippingMethod) => {
                return (
                    <li key={shippingMethod.id} className="nav-item" role="presentation">
                        <button
                            className="nav-link radio-btn active"
                            id={`pills-tab-${shippingMethod.id}`}
                            data-bs-toggle="pill"
                            data-bs-target={`#pills-${shippingMethod.id}`}
                            type="button"
                            role="tab"
                            aria-controls="pills-address"
                            aria-selected="true"
                        >
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    checked={selectedTab === shippingMethod.id}
                                    onChange={() => {
                                        onSelectTab(shippingMethod.id);
                                    }}
                                    name="flexRadioDefault"
                                    id={`shippingMethod-${shippingMethod.id}`}
                                />
                                <label
                                    className="form-check-label"
                                    htmlFor={`shippingMethod-${shippingMethod.id}`}
                                >
                                    {shippingMethod.name}
                                </label>
                            </div>
                        </button>
                    </li>
                )
            })}
        </ul>
    );
}
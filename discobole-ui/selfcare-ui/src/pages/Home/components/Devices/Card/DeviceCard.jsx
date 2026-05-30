// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import "./DeviceCard.css";
import useTranslations from "../../../../../utlis/i18n/useTranslations";

const DeviceCard = ({device, onOrderNowClick}) => {
    const {t} = useTranslations();
    return (
        <div className="card details-card h-100 device-card">
            <div
                className="product-img device-card-img"
                style={{backgroundImage: `url(${device.imageUrl})`}}
            ></div>
            <div className="card-body d-flex flex-column device-card-body">
                <h5 className="card-title device-card-title">{device.name}</h5>
                <p className="card-text device-card-text">{device.description}</p>

            </div>
            <div className="card-footer device-card-footer">
                <div className="d-grid">
                    <button
                        className="btn btn-outline-secondary"
                        onClick={() => onOrderNowClick(device.id)}
                    >
                        {t("actions.viewDetails")}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DeviceCard;
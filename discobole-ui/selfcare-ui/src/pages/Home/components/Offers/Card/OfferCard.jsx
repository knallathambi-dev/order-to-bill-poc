// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import React from "react";
import "./OfferCard.css";
import useTranslations from "../../../../../utlis/i18n/useTranslations";

function OfferCard({offer, onOrderNowClick}) {
    const isActive = offer.lifecycleStatus === "active";
    const cardClass = isActive ? "card-active" : "";
    const footerClass = isActive ? "footer-active" : "card-footer";
    const {t} = useTranslations();

    return (
        <div className={`card details-card h-100 ${cardClass}`}>
            {isActive && <div className="ribbon">{t("home.active")}</div>}
            <img
                alt={`offer ${offer.name}`}
                src={offer.imageUrl}
                className="bd-placeholder-img card-img-top offer-image"
            />
            <div className="card-body">
                <h5 className="card-title">{offer.name}</h5>
                <p className="card-text">{offer.description}</p>
            </div>
            <div className={footerClass}>
                <div className="d-grid">
                    <button
                        className="btn btn-outline-secondary"
                        onClick={() => onOrderNowClick(offer.id)}
                    >
                        {t("actions.orderNow")}
                    </button>
                </div>
            </div>
        </div>
    );
}

OfferCard.propTypes = {
    offer: PropTypes.shape({
        imageUrl: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        id: PropTypes.string.isRequired,
        lifecycleStatus: PropTypes.oneOf(["launched", "active"]).isRequired,
    }).isRequired,
    onOrderNowClick: PropTypes.func.isRequired,
};

export default OfferCard;
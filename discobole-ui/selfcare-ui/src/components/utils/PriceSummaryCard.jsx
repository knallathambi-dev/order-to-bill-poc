// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import {hasCharges, renderPrices, shouldRenderPricing} from "../../utlis/utils";
import {PriceWithDiscountDetails} from "./PriceWithDiscountDetails";
import useTranslations from "../../utlis/i18n/useTranslations";

const PriceSummaryCard = ({pricing, subtotalPrice, shippingPrice, showShipping}) => {
    const {t} = useTranslations();

    if (!shouldRenderPricing(pricing)) {
        return null;
    }

    return (
        <div className="card mb-2">
            <ul className="list-group list-group-flush">
                {hasCharges(subtotalPrice?.current) && (
                    <li className="list-group-item d-flex justify-content-between align-items-start fw-normal">
                        <div className="me-auto">
                            <div>{t("common.subtotal")}</div>
                        </div>
                        <span>{renderPrices(subtotalPrice.current, subtotalPrice.currency)}</span>
                    </li>
                )}
                {showShipping && (
                    <li className="list-group-item d-flex justify-content-between align-items-start fw-normal">
                        <div className="me-auto">
                            <div>{t("common.shipping")}</div>
                        </div>
                        <span>{renderPrices(shippingPrice, subtotalPrice?.currency || pricing?.currency)}</span>
                    </li>
                )}
                {hasCharges(pricing?.current) && (
                    <li className="list-group-item d-flex justify-content-between align-items-start">
                        <div className="fw-bold">
                            {t("common.total")}
                            <small className="tax-included-label"> ({t("common.taxIncluded")})</small>
                        </div>
                        <div>
                            <span className="fw-bold">{renderPrices(pricing.current, pricing.currency)}</span>
                        </div>
                    </li>
                )}
                <PriceWithDiscountDetails pricing={pricing}/>
            </ul>
        </div>
    );
};

export default PriceSummaryCard;
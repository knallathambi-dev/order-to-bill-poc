// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {hasCharges, renderPrices} from "../../utlis/utils";
import useTranslations from "../../utlis/i18n/useTranslations";

export const PriceWithDiscountDetails = ({pricing}) => {
    const {beforeDiscount, discount, currency} = pricing;
    const {t} = useTranslations();

    return (
        <>
            {hasCharges(discount) && hasCharges(beforeDiscount) && (
                <div className="list-group-item d-flex justify-content-between align-items-start text-muted">
                    <small className="text-muted">
                        {t('common.beforeDiscount')}
                        <small
                            className="tax-excluded-label">{" "}({t('common.taxExcluded')})
                        </small>
                    </small>
                    <span className="fw-bold">{renderPrices(beforeDiscount, currency)}</span>
                </div>
            )}
            {hasCharges(discount) && (
                <div className="list-group-item d-flex justify-content-between align-items-start">
                    <span className="text-danger">{t('common.discount')}
                    </span>
                    <span className="fw-bold text-danger">{renderPrices(discount, currency)}</span>
                </div>
            )}
        </>
    );
};
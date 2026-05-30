// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import useTranslations from '../../../../../utlis/i18n/useTranslations';

const OfferIncompatibilityModal = () => {
    const {t} = useTranslations();

    return (
        <div className="needs-validation my-4">
            <div className="row">
                <div className="col-12 text-center">
                    <div className="warning-circle mb-4">
                        <em className="icon-error_severe"></em>
                    </div>
                    <h5>
                        {t("plan.confirmations.incompatibleOffer")}
                    </h5>
                </div>
            </div>
            <div className="row">
                <h6 className="text-center">{t("common.selectAnotherOffer")}</h6>
            </div>
        </div>
    );
};

export default OfferIncompatibilityModal;

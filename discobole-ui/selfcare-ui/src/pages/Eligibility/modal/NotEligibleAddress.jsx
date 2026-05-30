// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useLocation, useNavigate} from "react-router-dom";
import useTranslations from "../../../utlis/i18n/useTranslations";

const NotEligibleAddress = ({hideModal}) => {
    const navigate = useNavigate();
    const {pathname} = useLocation();
    const isShipping = pathname === '/shipping';
    const {t} = useTranslations();

    function handleEdit() {
        if (pathname === '/shipping') {
            navigate('/set-up-plan');
        } else if (typeof hideModal === 'function') hideModal();
    }

    function handleBackToOffers() {
        navigate('/home');
    }

    return (
        <div className="needs-validation my-4">
            <div className="row">
                <div className="col-12 text-center">
                    <div className="warning-circle mb-4">
                        <em className="icon-error_severe"></em>
                    </div>
                    <h5>
                        {t("eligibility.failure.notEligibleFiber")}
                    </h5>
                    <h6 className="mt-1">
                        {pathname === '/shipping' ? t("common.selectAnotherOffer") : t("eligibility.failure.updateAddress")}
                    </h6>
                </div>
            </div>

            <div className="modal-footer">
                <div className={`row w-100 g-2 m-0 ${isShipping ? 'justify-content-center' : ''}`}>
                    <div className={isShipping ? 'col-12 d-flex justify-content-center' : 'col-md-6'}>
                        <button
                            type="button"
                            className={`btn btn-outline-secondary ${isShipping ? '' : 'w-100'}`}
                            onClick={handleBackToOffers}
                        >
                            {t("actions.goToOffers")}
                        </button>
                    </div>
                    <div className="col-md-6">
                        {!isShipping ? (
                            <button type="button" className="btn btn-primary w-100"
                                    onClick={handleEdit}>{t("actions.edit")}</button>
                        ) : null
                        }
                    </div>
                </div>
            </div>
        </div>

    );
};
export default NotEligibleAddress;
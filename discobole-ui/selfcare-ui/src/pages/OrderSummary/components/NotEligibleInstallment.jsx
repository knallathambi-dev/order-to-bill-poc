// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";
import {useNavigate} from "react-router-dom";
import useTranslations from "../../../utlis/i18n/useTranslations";

const NotEligibleInstallment = ({hideModal}) => {
    const navigate = useNavigate();
    const {t} = useTranslations();

    const handleBackToOffers = () => {
        hideModal();
        navigate("/home");
    };

    return (
        <div className="p-4">
            <div className="d-flex align-items-center gap-3 mb-4">
                <div className="warning-circle flex-shrink-0">
                    <em className="icon-error_severe" aria-hidden="true"/>
                </div>
                <div className="fw-bold flex-grow-1">
                    <p className="mt-2">
                        {t("order.notEligibleInstallment.ineligibleMessage", "You are not eligible to purchase a device with installment option.")}
                    </p>
                    <p className="mt-2 text-center">
                        {t("order.notEligibleInstallment.proceedUpfront", "Please proceed with upfront payment.")}
                    </p>
                </div>
            </div>
            <div className="modal-footer border-0 px-0 pb-0 d-flex justify-content-center gap-2 pt-2">
                <button
                    type="button"
                    className="btn btn-primary"
                    onClick={handleBackToOffers}
                >
                    {t("actions.goToOffers", "Go to Offers")}
                </button>
                <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={hideModal}
                >
                    {t("actions.close", "Close")}
                </button>
            </div>
        </div>
    );
};

NotEligibleInstallment.propTypes = {
    hideModal: PropTypes.func.isRequired
};

export default NotEligibleInstallment;

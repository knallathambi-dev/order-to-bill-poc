// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {setOrderType} from "../../../store/actions/orderActions";

const CheckEligibilityModal = ({hideModal}) => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {orderType} = useSelector(state => state.order);

    const {t} = useTranslations();

    function handleCheck() {
        const isMigration = orderType === 'Migration';

        navigate(isMigration ? '/migrate-plan' : '/set-up-plan');

        if (!isMigration) {
            dispatch(setOrderType('AcquisitionWithEligibility'));
        }
    }

    return (
        <form className="needs-validation my-4" noValidate>
            <div className=" p-2 text-center">
                <div className="success-circle mb-3">
                    <em className="icon-checkbox_tick"></em>
                </div>
                <h4 className="mb-2">{t("eligibility.success.title")}</h4>
                <p>{t("eligibility.success.message")}</p>
            </div>

            <div className="modal-footer">
                <div className="row w-100 g-2 m-0">
                    <div className="col-md-6">
                        <button type="button" className="btn btn-outline-secondary w-100"
                                onClick={hideModal}>{t("actions.cancel")}
                        </button>
                    </div>
                    <div className="col-md-6">
                        <button type="button" className="btn btn-primary w-100"
                                onClick={handleCheck}>{t("actions.proceedToOrder")}
                        </button>
                    </div>
                </div>
            </div>
        </form>

    );
};

export default CheckEligibilityModal;
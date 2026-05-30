// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useDispatch} from "react-redux";
import {useNavigate} from "react-router-dom";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {resetAuthState} from "../../../store/actions/authActions";
import {useAuth} from "../../../context/AuthContext";

const Logout = ({onCancel}) => {
    const {t} = useTranslations();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {logout: performLogout} = useAuth();

    const handleLogout = async () => {
        try {
            await performLogout();
        } finally {
            dispatch(resetAuthState());
            navigate("/home", {replace: true});
            if (typeof onCancel === "function") onCancel();
        }
    };

    return (
        <form className="needs-validation my-4" noValidate>
            <div className="p-2 text-center">
                <h4 className="mb-3">{t("auth.logout.confirmations.title")}</h4>
                <p className="text-muted mb-0">{t("auth.logout.confirmations.message")}</p>
                <p className="text-muted">{t("auth.logout.confirmations.question")}</p>
            </div>

            <div className="modal-footer">
                <div className="row w-100 g-2 m-0">
                    <div className="col-md-6">
                        <button
                            type="button"
                            className="btn btn-outline-secondary w-100"
                            onClick={onCancel}
                        >
                            {t("actions.cancel")}
                        </button>
                    </div>
                    <div className="col-md-6">
                        <button
                            type="button"
                            className="btn btn-primary w-100"
                            onClick={handleLogout}
                        >
                            {t("auth.logout.confirmations.confirm")}
                        </button>
                    </div>
                </div>
            </div>
        </form>
    );
};

export default Logout;
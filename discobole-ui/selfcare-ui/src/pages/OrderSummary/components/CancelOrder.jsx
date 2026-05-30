// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import React from "react";
import {toast} from "react-toastify";
import {toggleLoading} from "../../../store/actions/loadingActions";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {patchTaskFlow} from "../../../services";

export default function CancelOrder({hideModal}) {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {cancelTaskFlow} = useSelector((state) => state.taskManager);
    const {t, tNotification} = useTranslations();

    const createReqBody = () => ({});

    const handleCancel = async () => {
        try {
            dispatch(toggleLoading(true));

            const reqBody = createReqBody();
            await patchTaskFlow(cancelTaskFlow, reqBody);

            hideModal();
            navigate("/orderStatusUpdate", {state: {orderStatus: "cancelled"}});
        } catch {
            toast.error(tNotification("orderSummary.cancelOrderFailed"));
        } finally {
            dispatch(toggleLoading(false));
        }
    };

    return (
        <form className="needs-validation my-4" noValidate>
            <div className="row">
                <div className="col-12">
                    <p>{t("order.confirmations.cancel.body")}</p>
                </div>
            </div>
            <div className="modal-footer">
                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal" onClick={hideModal}>
                    {t("order.confirmations.cancel.decline")}
                </button>
                <button type="button" className="btn btn-primary"
                        onClick={handleCancel}>{t("order.confirmations.cancel.confirm")}</button>
            </div>
        </form>
    );
}
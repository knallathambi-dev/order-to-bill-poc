// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import useTranslations from "../../../../../../utlis/i18n/useTranslations";

const TerminatePlanModal = ({hideModal, onConfirm, hasConfigTerm}) => {
    const {t} = useTranslations();

    return (
        <form className="needs-validation my-4" noValidate>
            <div className="p-2 text-center">
                <h4 className="mb-3">{t("plan.confirmations.general.title")}</h4>
                {hasConfigTerm ? (
                    <div className="alert alert-danger alert-sm p-0" role="alert">
                          <span className="alert-icon">
                            <span className="visually-hidden">{t("plan.warnings.terminationPenalty")}</span>
                          </span>
                        <p className="text-muted">
                            {t("plan.warnings.terminationPenaltyWithCharges")}
                        </p>
                    </div>
                ) : (
                    <>
                        <p className="text-muted mb-0">{t("plan.confirmations.terminate.message")}</p>
                        <p className="text-muted">{t("plan.confirmations.general.question")}</p>
                    </>
                )}
            </div>
            <div className="modal-footer">
                <div className="row w-100 g-2 m-0">
                    <div className="col-md-6">
                        <button
                            type="button"
                            className="btn btn-outline-secondary w-100"
                            onClick={hideModal}
                        >
                            {t("plan.confirmations.general.cancel")}
                        </button>
                    </div>
                    <div className="col-md-6">
                        <button
                            type="button"
                            className="btn btn-primary w-100"
                            onClick={onConfirm}
                        >
                            {t("plan.confirmations.terminate.confirm")}
                        </button>
                    </div>
                </div>
            </div>
        </form>
    );
};

export default TerminatePlanModal;
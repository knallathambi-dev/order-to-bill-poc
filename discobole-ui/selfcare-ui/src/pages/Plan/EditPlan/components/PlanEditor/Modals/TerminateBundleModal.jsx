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

const TerminateBundleModal = ({items, hideModal, onConfirm}) => {
    const {t} = useTranslations();

    return (
        <>
            <div className="mb-3">{t("plan.confirmations.terminate.bundle")}</div>
            <ul className="list-group list-group-flush benefit-items-wrapper m-0">
                {items && items.length > 0 ? (
                    items.map((item, index) => (
                        <li key={index} className="list-group-item benefit-item py-1">
                            <div className="d-flex justify-content-between">
                                <div>
                                    <span>{item?.name}</span>
                                </div>
                                <div className="d-flex align-items-center">
                                    <span className="mb-0 ms-2 tag tag-sm action-type red">
                                        <em className="icon-Modifier_delete action-icon"></em>Terminate
                                    </span>
                                </div>
                            </div>
                        </li>
                    ))
                ) : (
                    <li className="list-group-item benefit-item py-1">
                        <div className="d-flex justify-content-between">
                            <div>
                                <span>{t("plan.modify.noItemsToTerminate")}</span>
                            </div>
                        </div>
                    </li>
                )}
            </ul>
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
        </>
    );
};

export default TerminateBundleModal;
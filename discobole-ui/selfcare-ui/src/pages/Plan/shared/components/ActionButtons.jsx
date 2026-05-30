// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const ActionButtons = ({
                           itemId,
                           canTerminate,
                           hasModifyAction,
                           isTerminating,
                           isModifying,
                           onTerminate,
                           onModify,
                       }) => {
    const {t} = useTranslations();

    return (
        <>
            {canTerminate && (
                <button
                    type="button"
                    className="btn btn-danger btn-sm action-btn terminate"
                    onClick={() => onTerminate(itemId, "terminate")}
                    aria-expanded={!!isTerminating}
                >
                    <em className="icon-delete me-1"></em>
                    {isTerminating ? t("actions.cancelTerminate") : t("actions.terminate")}
                </button>
            )}
            {hasModifyAction && (
                <button
                    type="button"
                    className="btn btn-secondary btn-sm action-btn modify"
                    onClick={() => onModify(itemId, "modify")}
                    aria-expanded={!!isModifying}
                >
                    <em className="icon-Pencil me-1"></em>
                    {isModifying ? t("actions.cancelModify") : t("actions.modify")}
                </button>
            )}
        </>
    );
};

export default ActionButtons;
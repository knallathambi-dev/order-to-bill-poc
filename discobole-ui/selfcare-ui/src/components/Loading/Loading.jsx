// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import useTranslations from "../../utlis/i18n/useTranslations";

export default function Loading({isLoading}) {
    const {t} = useTranslations();

    return (
        isLoading ? (
            <div
                className="position-fixed top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center"
                style={{zIndex: '9999', backgroundColor: 'rgba(255, 255, 255, 0.8)'}}
            >
                <div className="spinner-border text-primary spinner-border-lg">
                    <span className="visually-hidden">{t('loading.loading')}</span>
                </div>
            </div>
        ) : null
    );
}
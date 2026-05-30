// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect} from 'react';
import {useDispatch} from 'react-redux';
import {addBreadcrumb} from "../store/actions/breadcrumbActions";
import useTranslations from "../utlis/i18n/useTranslations";

const useTitlePage = (titleKey, breadcrumbItems = null) => {
    const dispatch = useDispatch();
    const {t} = useTranslations();

    useEffect(() => {
        document.title = t(`pages.${titleKey}`);

        if (breadcrumbItems && Array.isArray(breadcrumbItems)) {
            breadcrumbItems
                .filter(item => item && item !== '')
                .forEach(item => {
                    dispatch(addBreadcrumb(item));
                });
        } else {
            dispatch(addBreadcrumb(titleKey));
        }
    }, [titleKey, JSON.stringify(breadcrumbItems), dispatch, t]);
};

export default useTitlePage;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from 'prop-types';
import useTranslations from "../../../utlis/i18n/useTranslations";
import "./BreadcrumbItem.css";

function BreadcrumbItem({label, isLast = false}) {
    const {t} = useTranslations();

    const isTranslationKey = (text) => {
        return typeof text === 'string' &&
            !text.includes(' ') &&
            t(`pages.${text}`) !== `pages.${text}`;
    };

    const displayText = isTranslationKey(label) ? t(`pages.${label}`) : label;
    const itemClass = `breadcrumb-item ${isLast ? 'active' : ''}`.trim();

    return (
        <li className={itemClass}>
            <span className="breadcrumb-text">{displayText}</span>
        </li>
    );
}

BreadcrumbItem.propTypes = {
    label: PropTypes.string.isRequired,
    isLast: PropTypes.bool
};

export default BreadcrumbItem;
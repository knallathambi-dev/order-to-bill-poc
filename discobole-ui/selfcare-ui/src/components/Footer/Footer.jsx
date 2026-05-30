// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import "./Footer.css";
import useTranslations from "../../utlis/i18n/useTranslations";

const Footer = () => {
    const {t} = useTranslations();
    
    return (
        <footer className="footer">
            <ul className="footer-menu">
                <li>{t('footer.termsAndConditions')}</li>
                <li>{t('footer.privacy')}</li>
                <li>{t('footer.accessibilityStatement')}</li>
                <li>{t('footer.cookiePolicy')}</li>
            </ul>
        </footer>
    );
};

export default Footer;
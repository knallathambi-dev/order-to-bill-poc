// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback, useEffect, useState} from "react";
import useTranslations from "../../../../utlis/i18n/useTranslations";

function EWallet({email, setEmail, password, setPassword, onValidationChange}) {
    const [validationErrors, setValidationErrors] = useState({});
    const {t} = useTranslations();

    const isValidEmail = (email) => /\S+@\S+\.\S+/.test(email);

    const isValidMobileNumber = (mobile) => {
        const mobileRegex = /^\+?[\d\s\-()]{8,}$/;
        return mobileRegex.test(mobile.trim());
    };

    const isValidEmailOrMobile = (input) => {
        return isValidEmail(input) || isValidMobileNumber(input);
    };

    const isValidPassword = (password) => password.length >= 8;

    useEffect(() => {
        setEmail('');
        setPassword('');
    }, [setEmail, setPassword]);

    const validateForm = useCallback(() => {
        const errors = {};

        if (!email.trim()) {
            errors.email = t('validation.required.emailMobileNumber');
        } else if (!isValidEmailOrMobile(email)) {
            errors.email = t('validation.invalid.emailMobileNumber');
        }

        if (!password) {
            errors.password = t('validation.required.password');
        } else if (!isValidPassword(password)) {
            errors.password = t('validation.length.passwordMin');
        }

        setValidationErrors(errors);
        return Object.keys(errors).length === 0;
    }, [email, password, t]);

    useEffect(() => {
        const isValid = validateForm();
        onValidationChange(isValid);
    }, [validateForm, onValidationChange]);

    return (
        <form className="needs-validation mt-4" noValidate>
            <div className="row gy-3">
                <div className="col-md-12">
                    <label htmlFor="email" className="form-label is-required">
                        {t('forms.labels.emailMobileNumber')}<span className="visually-hidden">(required)</span>
                    </label>
                    <input
                        type="text"
                        className={`form-control ${validationErrors.email ? "is-invalid" : ""}`}
                        id="email"
                        placeholder={t('forms.placeholders.emailMobileNumber')}
                        autoComplete="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    {validationErrors.email && (
                        <div className="invalid-feedback">
                            {validationErrors.email}
                        </div>
                    )}
                </div>
                <div className="col-md-12">
                    <label htmlFor="password" className="form-label is-required">
                        {t('forms.labels.password')}<span className="visually-hidden">(required)</span>
                    </label>
                    <input
                        type="password"
                        className={`form-control ${validationErrors.password ? "is-invalid" : ""}`}
                        id="password"
                        placeholder={t('forms.placeholders.password')}
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    {validationErrors.password && (
                        <div className="invalid-feedback">
                            {validationErrors.password}
                        </div>
                    )}
                    {password && !isValidPassword(password) && (
                        <small className="text-muted">{t('validation.length.passwordMin')}</small>
                    )}
                </div>
            </div>
        </form>
    );
}

export default EWallet;
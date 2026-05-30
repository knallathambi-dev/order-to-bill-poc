// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from 'react';
import PropTypes from "prop-types";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {useAuth} from "../../../context/AuthContext";
import {useDispatch} from "react-redux";
import {toggleLoading} from "../../../store/actions/loadingActions";
import {toast} from "react-toastify";

const VALIDATION_RULES = {
    EMAIL: {REGEX: /^[^\s@]+@[^\s@]+\.[^\s@]+$/},
    PASSWORD: {MIN_LENGTH: 8}
};

const validateLoginForm = (data, t) => {
    const errors = {};
    if (!data.email) {
        errors.email = t('validation.required.email');
    } else if (!VALIDATION_RULES.EMAIL.REGEX.test(data.email)) {
        errors.email = t('validation.invalid.email');
    }
    if (!data.password) {
        errors.password = t('validation.required.password');
    } else if (data.password.length < VALIDATION_RULES.PASSWORD.MIN_LENGTH) {
        errors.password = t('validation.length.passwordMin');
    }
    return errors;
};

function Login({onSuccess, initialEmail, successBanner}) {
    const [loginFormData, setLoginFormData] = useState({email: '', password: ''});
    const [validationErrors, setValidationErrors] = useState({});
    const [showSuccessBanner, setShowSuccessBanner] = useState(!!successBanner);

    const {t, tNotification} = useTranslations();
    const dispatch = useDispatch();
    const {login} = useAuth();

    useEffect(() => {
        setShowSuccessBanner(!!successBanner);
    }, [successBanner]);

    useEffect(() => {
        if (initialEmail) {
            const nextData = {...loginFormData, email: initialEmail};
            setLoginFormData(nextData);
            setValidationErrors(validateLoginForm(nextData, t));
        }
    }, [initialEmail]);

    const performValidation = useCallback((formData) => {
        const errors = validateLoginForm(formData, t);
        setValidationErrors(errors);
        return Object.keys(errors).length === 0;
    }, [t]);

    const handleInputChange = (event) => {
        const {name, value} = event.target;
        const nextData = {...loginFormData, [name]: value};
        setLoginFormData(nextData);
        performValidation(nextData);
    };

    const handleLoginSubmit = async () => {
        const isFormValidNow = performValidation(loginFormData);
        if (!isFormValidNow) return;

        dispatch(toggleLoading(true));

        try {
            const result = await login(loginFormData.email, loginFormData.password);

            if (!result.success) {
                setShowSuccessBanner(false);
                toast.error(tNotification("auth.loginFailed"));
                return;
            }

            onSuccess();
        } finally {
            dispatch(toggleLoading(false));
        }
    };

    const isFormValid =
        Object.keys(validationErrors).length === 0 &&
        loginFormData.email &&
        loginFormData.password;

    return (
        <form className="needs-validation my-4" noValidate>
            {showSuccessBanner && successBanner && (
                <div className="alert alert-success" role="alert">
                    {successBanner}
                </div>
            )}
            <div className="row">
                <div className="col-12">
                    <div className="mb-3">
                        <label htmlFor="email" className="form-label is-required" id="emailLabel">
                            {t('forms.labels.email')}
                        </label>
                        <input
                            type="email"
                            className={`form-control ${validationErrors.email ? 'is-invalid' : ''}`}
                            id="email"
                            name="email"
                            placeholder="abdcef@orange.com"
                            value={loginFormData.email}
                            onChange={handleInputChange}
                            required
                            aria-labelledby="emailLabel"
                            aria-describedby={validationErrors.email ? "emailFeedback" : undefined}
                        />
                        {validationErrors.email && (
                            <div className="invalid-feedback" id="emailFeedback">
                                {validationErrors.email}
                            </div>
                        )}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="password" className="form-label is-required" id="passwordLabel">
                            {t('forms.labels.password')}
                        </label>
                        <input
                            type="password"
                            className={`form-control ${validationErrors.password ? 'is-invalid' : ''}`}
                            id="password"
                            name="password"
                            value={loginFormData.password}
                            onChange={handleInputChange}
                            required
                            aria-labelledby="passwordLabel"
                            aria-describedby={validationErrors.password ? "passwordFeedback" : undefined}
                        />
                        {validationErrors.password && (
                            <div className="invalid-feedback" id="passwordFeedback">
                                {validationErrors.password}
                            </div>
                        )}
                    </div>
                </div>

                <div className="d-grid d-md-flex justify-content-md-end">
                    <button
                        className="btn btn-primary"
                        type="button"
                        onClick={handleLoginSubmit}
                        disabled={!isFormValid}
                    >
                        {t('actions.login')}
                    </button>
                </div>
            </div>
        </form>
    );
}

Login.propTypes = {
    onSuccess: PropTypes.func.isRequired,
    initialEmail: PropTypes.string,
    successBanner: PropTypes.string
};

export default Login;
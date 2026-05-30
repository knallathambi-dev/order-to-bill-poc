// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from 'react';
import PropTypes from "prop-types";
import {toast} from "react-toastify";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {useAuth} from "../../../context/AuthContext";
import {useDispatch} from "react-redux";
import {toggleLoading} from "../../../store/actions/loadingActions";

const VALIDATION_RULES = {
    EMAIL: {REGEX: /^[^\s@]+@[^\s@]+\.[^\s@]+$/},
    PASSWORD: {MIN_LENGTH: 8}
};

const DEFAULT_FORM_STATE = {
    title: 'Mr',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    reEnteredPassword: '',
};

const validateRegistrationForm = (formData, termsAccepted, t) => {
    const errors = {};

    if (!formData.firstName.trim()) {
        errors.firstName = t('validation.required.firstName');
    }

    if (formData.email && !VALIDATION_RULES.EMAIL.REGEX.test(formData.email)) {
        errors.email = t('validation.invalid.email');
    }

    if (!formData.password) {
        errors.password = t('validation.required.password');
    } else if (formData.password.length < VALIDATION_RULES.PASSWORD.MIN_LENGTH) {
        errors.password = t('validation.length.passwordMin');
    }

    if (!formData.reEnteredPassword) {
        errors.reEnteredPassword = t('validation.match.reEnterPassword');
    } else if (formData.password !== formData.reEnteredPassword) {
        errors.passwordMatch = t('validation.match.passwordMismatch');
    }

    return errors;
};

function Register({onRegistered}) {
    const [registrationFormData, setRegistrationFormData] = useState(DEFAULT_FORM_STATE);
    const [termsAccepted, setTermsAccepted] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});

    const {t, tNotification} = useTranslations();
    const dispatch = useDispatch();
    const {register} = useAuth();

    const performValidation = useCallback((formData, terms) => {
        const errors = validateRegistrationForm(formData, terms, t);
        setValidationErrors(errors);
        return Object.keys(errors).length === 0 && terms;
    }, [t]);

    const handleInputChange = (event) => {
        const {name, value} = event.target;
        const nextData = {...registrationFormData, [name]: value};
        setRegistrationFormData(nextData);
        performValidation(nextData, termsAccepted);
    };

    const handleTitleChange = (selectedTitle) => {
        const nextData = {...registrationFormData, title: selectedTitle};
        setRegistrationFormData(nextData);
        performValidation(nextData, termsAccepted);
    };

    const handleTermsChange = (event) => {
        const nextTerms = event.target.checked;
        setTermsAccepted(nextTerms);
        performValidation(registrationFormData, nextTerms);
    };

    const handleRegistrationSubmit = async () => {
        const isFormValidNow = performValidation(registrationFormData, termsAccepted);
        if (!isFormValidNow) return;

        dispatch(toggleLoading(true));

        try {
            const result = await register(registrationFormData);

            if (result?.success) {
                onRegistered?.(registrationFormData.email);
                return;
            }

            toast.error(tNotification("auth.registrationFailed"));

        } finally {
            dispatch(toggleLoading(false));
        }
    };

    const isFormValid =
        Object.keys(validationErrors).length === 0 &&
        termsAccepted &&
        registrationFormData.password &&
        registrationFormData.reEnteredPassword;

    return (
        <form className="needs-validation my-4" noValidate>
            <div className="row mb-3">
                <label className="form-label is-required">
                    {t('forms.labels.title')}
                </label>
                <div className="d-flex gap-3">
                    {[t('forms.titles.mr'), t('forms.titles.mrs')].map((titleOption) => (
                        <div className="form-check" key={titleOption}>
                            <input
                                className="form-check-input"
                                type="radio"
                                name="title"
                                id={`title-${titleOption}`}
                                checked={registrationFormData.title === titleOption}
                                onChange={() => handleTitleChange(titleOption)}
                            />
                            <label className="form-check-label" htmlFor={`title-${titleOption}`}>
                                {titleOption}
                            </label>
                        </div>
                    ))}
                </div>
            </div>

            <div className="row mb-3">
                <div className="col-md-6">
                    <label htmlFor="firstName" className="form-label is-required">
                        {t('forms.labels.firstName')}
                    </label>
                    <input
                        type="text"
                        className={`form-control ${validationErrors.firstName ? 'is-invalid' : ''}`}
                        id="firstName"
                        name="firstName"
                        value={registrationFormData.firstName}
                        onChange={handleInputChange}
                        required
                        aria-describedby={validationErrors.firstName ? "firstNameFeedback" : undefined}
                    />
                    {validationErrors.firstName && (
                        <div className="invalid-feedback" id="firstNameFeedback">
                            {validationErrors.firstName}
                        </div>
                    )}
                </div>

                <div className="col-md-6">
                    <label htmlFor="lastName" className="form-label">
                        {t('forms.labels.lastName')}
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        id="lastName"
                        name="lastName"
                        value={registrationFormData.lastName}
                        onChange={handleInputChange}
                    />
                </div>
            </div>

            <div className="mb-3">
                <label htmlFor="email" className="form-label">
                    {t('forms.labels.email')}
                </label>
                <input
                    type="email"
                    className={`form-control ${validationErrors.email ? 'is-invalid' : ''}`}
                    id="email"
                    name="email"
                    placeholder="example@domain.com"
                    value={registrationFormData.email}
                    onChange={handleInputChange}
                    aria-describedby={validationErrors.email ? "emailFeedback" : undefined}
                />
                {validationErrors.email && (
                    <div className="invalid-feedback" id="emailFeedback">
                        {validationErrors.email}
                    </div>
                )}
            </div>

            <div className="mb-3">
                <label htmlFor="phone" className="form-label">
                    {t('forms.labels.contactNumber')}
                </label>
                <input
                    type="tel"
                    className="form-control"
                    id="phone"
                    name="phone"
                    value={registrationFormData.phone}
                    onChange={handleInputChange}
                />
            </div>

            <div className="row mb-3">
                <div className="col-md-6">
                    <label htmlFor="password" className="form-label is-required">
                        {t('forms.labels.password')}
                    </label>
                    <input
                        type="password"
                        className={`form-control ${validationErrors.password ? 'is-invalid' : ''}`}
                        id="password"
                        name="password"
                        value={registrationFormData.password}
                        onChange={handleInputChange}
                        required
                        aria-describedby={validationErrors.password ? "passwordFeedback" : undefined}
                    />
                    {validationErrors.password && (
                        <div className="invalid-feedback" id="passwordFeedback">
                            {validationErrors.password}
                        </div>
                    )}
                </div>

                <div className="col-md-6">
                    <label htmlFor="reEnteredPassword" className="form-label is-required">
                        {t('forms.labels.reEnterPassword')}
                    </label>
                    <input
                        type="password"
                        className={`form-control ${validationErrors.reEnteredPassword || validationErrors.passwordMatch ? 'is-invalid' : ''}`}
                        id="reEnteredPassword"
                        name="reEnteredPassword"
                        value={registrationFormData.reEnteredPassword}
                        onChange={handleInputChange}
                        required
                        aria-describedby={
                            validationErrors.reEnteredPassword || validationErrors.passwordMatch
                                ? "reEnteredPasswordFeedback"
                                : undefined
                        }
                    />
                    {validationErrors.reEnteredPassword && (
                        <div className="invalid-feedback" id="reEnteredPasswordFeedback">
                            {validationErrors.reEnteredPassword}
                        </div>
                    )}
                    {validationErrors.passwordMatch && (
                        <div className="invalid-feedback">
                            {validationErrors.passwordMatch}
                        </div>
                    )}
                </div>
            </div>

            <div className="mb-3 form-check">
                <input
                    className={`form-check-input ${!termsAccepted ? 'is-invalid' : ''}`}
                    type="checkbox"
                    id="termsCheckbox"
                    checked={termsAccepted}
                    onChange={handleTermsChange}
                />
                <label className="form-check-label is-required" htmlFor="termsCheckbox">
                    {t('auth.agreeToTerms')}
                </label>
            </div>

            <div className="d-grid d-md-flex justify-content-md-end">
                <button
                    className="btn btn-primary"
                    type="button"
                    onClick={handleRegistrationSubmit}
                    disabled={!isFormValid}
                >
                    {t('actions.register')}
                </button>
            </div>
        </form>
    );
}

Register.propTypes = {
    onRegistered: PropTypes.func
};

export default Register;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import visaLogo from "../../../../assests/imgs/visa.png";
import mastercardLogo from "../../../../assests/imgs/mastercard.png";
import {useCallback, useEffect, useState} from "react";
import useTranslations from "../../../../utlis/i18n/useTranslations";

function CreditCard({cardNumber, setCardNumber, cvv, setCvv, cardName, setCardName, onValidationChange}) {
    const [cardType, setCardType] = useState('');
    const [expirationDate, setExpirationDate] = useState('');
    const [validationErrors, setValidationErrors] = useState({});
    const {t} = useTranslations();

    const handleCardNumberChange = (event) => {
        let {value} = event.target;
        value = value.replace(/\D/g, '').replace(/(\d{4})(?=\d)/g, '$1 ');
        setCardNumber(value);
        setCardType(getCardType(value));
    };

    const getCardType = (number) => {
        const cleaned = number.replace(/\s+/g, '');
        if (/^4/.test(cleaned)) return 'visa';
        if (/^5[1-5]/.test(cleaned)) return 'mastercard';
        return null;
    };

    const handleCvvChange = (event) => {
        const {value} = event.target;
        setCvv(value.replace(/\D/g, ''));
    };

    const handleCardNameChange = (event) => {
        setCardName(event.target.value.toUpperCase());
    };

    const handleExpirationChange = (event) => {
        setExpirationDate(event.target.value);
    };

    const validateForm = useCallback(() => {
        const errors = {};

        const cleanedCardNumber = cardNumber.replace(/\s/g, '');
        if (!cleanedCardNumber) {
            errors.cardNumber = t('validation.required.creditCardNumber');
        } else if (cleanedCardNumber.length !== 16) {
            errors.cardNumber = t('validation.invalid.creditCardNumber');
        }

        if (!cvv) {
            errors.cvv = t('validation.required.cvv');
        } else if (cvv.length !== 3) {
            errors.cvv = t('validation.invalid.cvv');
        }

        if (!cardName.trim()) {
            errors.cardName = t('validation.required.nameOnCard');
        } else if (cardName.trim().length < 2) {
            errors.cardName = t('validation.invalid.nameOnCard');
        }

        if (!expirationDate) {
            errors.expirationDate = t('validation.required.expirationDate');
        } else {
            const currentDate = new Date();
            const selectedDate = new Date(expirationDate);
            const currentMonth = currentDate.getFullYear() * 12 + currentDate.getMonth();
            const selectedMonth = selectedDate.getFullYear() * 12 + selectedDate.getMonth();

            if (selectedMonth <= currentMonth) {
                errors.expirationDate = t('validation.invalid.expirationDate');
            }
        }

        setValidationErrors(errors);
        return Object.keys(errors).length === 0;
    }, [cardNumber, cvv, cardName, expirationDate, t]);

    useEffect(() => {
        const isValid = validateForm();
        onValidationChange(isValid);
    }, [validateForm, onValidationChange]);

    return (
        <form className="needs-validation mt-4" noValidate>
            <div className="row gy-3">
                <div className="col-md-12">
                    <label htmlFor="cc-number" className="form-label is-required">
                        {t('forms.labels.creditCardNumber')}<span className="visually-hidden">(required)</span>
                    </label>
                    <div className="input-group has-validation">
                        <input
                            type="text"
                            className={`form-control ${validationErrors.cardNumber ? "is-invalid" : ""}`}
                            id="cc-number"
                            placeholder="**** **** **** ****"
                            maxLength="19"
                            value={cardNumber}
                            onChange={handleCardNumberChange}
                            required
                        />
                        <div className="input-group-text card-icons-wrapper">
                            <img
                                src={visaLogo}
                                alt="Visa"
                                className={`card-icons card-icon ${cardType === 'visa' ? 'active' : ''}`}
                                loading="lazy"
                            />
                            <img
                                src={mastercardLogo}
                                alt="MasterCard"
                                className={`card-icons card-icon ${cardType === 'mastercard' ? 'active' : ''}`}
                                loading="lazy"
                            />
                        </div>
                        {validationErrors.cardNumber && (
                            <div className="invalid-feedback">
                                {validationErrors.cardNumber}
                            </div>
                        )}
                    </div>
                </div>

                <div className="col-md-6">
                    <label htmlFor="cc-expiration" className="form-label is-required">
                        {t('forms.labels.expiration')}<span className="visually-hidden">(required)</span>
                    </label>
                    <input
                        type="month"
                        className={`form-control ${validationErrors.expirationDate ? "is-invalid" : ""}`}
                        id="cc-expiration"
                        value={expirationDate}
                        onChange={handleExpirationChange}
                        required
                    />
                    {validationErrors.expirationDate && (
                        <div className="invalid-feedback">
                            {validationErrors.expirationDate}
                        </div>
                    )}
                </div>

                <div className="col-md-6">
                    <label htmlFor="cc-cvv" className="form-label is-required">
                        {t('forms.labels.cvv')}<span className="visually-hidden">(required)</span>
                    </label>
                    <input
                        type="text"
                        className={`form-control ${validationErrors.cvv ? "is-invalid" : ""}`}
                        id="cc-cvv"
                        placeholder="***"
                        maxLength="3"
                        value={cvv}
                        onChange={handleCvvChange}
                        required
                    />
                    {validationErrors.cvv && (
                        <div className="invalid-feedback">
                            {validationErrors.cvv}
                        </div>
                    )}
                </div>

                <div className="col-md-12">
                    <label htmlFor="cc-name" className="form-label is-required">
                        {t('forms.labels.nameOnCard')}<span className="visually-hidden">(required)</span>
                    </label>
                    <input
                        type="text"
                        className={`form-control ${validationErrors.cardName ? "is-invalid" : ""}`}
                        id="cc-name"
                        placeholder={t('forms.placeholders.nameOnCard')}
                        value={cardName}
                        onChange={handleCardNameChange}
                        required
                    />
                    <small className="text-muted">{t('forms.placeholders.fullNameAsDisplayedOnCard')}</small>
                    {validationErrors.cardName && (
                        <div className="invalid-feedback">
                            {validationErrors.cardName}
                        </div>
                    )}
                </div>
            </div>
        </form>
    );
}

export default CreditCard;
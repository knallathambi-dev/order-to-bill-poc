// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import React, {useCallback, useEffect, useState} from "react";
import {useDispatch} from "react-redux";
import {ShippingAddressSummary} from "../ShippingAddressSummary";
import {setShippingInfo} from "../../../../store/actions/shippingActions";
import countriesData from '../../../../utlis/countries.json';
import useTranslations from "../../../../utlis/i18n/useTranslations";

function ShippingForm({onShippingFormValidation, shippingSummary, setShippingSummary}) {
    const dispatch = useDispatch();
    const {t} = useTranslations();
    const [formData, setFormData] = useState({
        address: "",
        country: "",
        city: "",
        postcode: "",
        deliveryDate: ""
    });
    const [minDate, setMinDate] = useState("");
    const [isFormValid, setIsFormValid] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});

    const validateForm = useCallback(() => {
        const errors = {};
        if (!formData.country) errors.country = t("validation.required.country");
        if (!formData.address.trim()) errors.address = t("validation.required.address");
        if (!formData.city) errors.city = t("validation.required.city");
        if (!formData.postcode.trim()) errors.postcode = t("validation.required.postalCode");
        if (!formData.deliveryDate) errors.deliveryDate = t("validation.required.deliveryDate");

        setValidationErrors(errors);
        setIsFormValid(Object.keys(errors).length === 0);
    }, [formData, t]);

    useEffect(() => {
        const today = new Date();
        today.setDate(today.getDate() + 3);
        const minDateString = today.toISOString().split('T')[0];
        setMinDate(minDateString);
    }, []);

    useEffect(() => {
        validateForm();
    }, [validateForm]);

    const countries = [...countriesData]
        .map(({name, cities}) => ({name, cities}))
        .sort((a, b) => a.name.localeCompare(b.name));

    const cities = countries.find(c => c.name === formData.country)?.cities || [];

    const handleInputChange = (e) => {
        const {id, value} = e.target;

        if (id === "country") {
            setFormData({
                ...formData,
                [id]: value,
                city: "",
                postcode: "",
            });
        } else if (id === "city") {
            setFormData({
                ...formData,
                [id]: value,
                postcode: "",
            });
        } else {
            setFormData({
                ...formData,
                [id]: value,
            });
        }
    };

    const handleKeyDown = (e) => {
        if (e.key !== 'Tab' && e.key !== 'Escape') {
            e.preventDefault();
        }
    };

    const saveAndDispatchShippingInfo = () => {
        onShippingFormValidation(isFormValid);
        dispatch(setShippingInfo(`${formData.address}, ${formData.country}, ${formData.city} - ${formData.postcode}`, formData.deliveryDate));
        setShippingSummary(true);
    };

    const editShippingForm = (value) => {
        setShippingSummary(!value);
        onShippingFormValidation(false);
    };

    return (
        <div className="card my-2">
            <div className="card-body">
                <div className="row g-3">
                    {!shippingSummary ? (
                        <>
                            <div className="col-12">
                                <label htmlFor="address" className="form-label">{t("forms.labels.address")}</label>
                                <input
                                    type="text"
                                    className={`form-control ${validationErrors.address ? "is-invalid" : ""}`}
                                    id="address"
                                    placeholder={t("forms.placeholders.enterYourAddress")}
                                    value={formData.address}
                                    onChange={handleInputChange}
                                />
                                {validationErrors.address &&
                                    <div className="invalid-feedback">{validationErrors.address}</div>}
                            </div>
                            <div className="col-md-5">
                                <label htmlFor="country" className="form-label">{t("forms.labels.country")}</label>
                                <select
                                    className={`form-select ${validationErrors.country ? "is-invalid" : ""}`}
                                    id="country"
                                    value={formData.country}
                                    onChange={handleInputChange}
                                >
                                    <option value="">{t("forms.placeholders.selectACountry")}</option>
                                    {countries.map((country) => (
                                        <option key={country.name} value={country.name}>
                                            {country.name}
                                        </option>
                                    ))}
                                </select>
                                {validationErrors.country &&
                                    <div className="invalid-feedback">{validationErrors.country}</div>}
                            </div>
                            <div className="col-md-4">
                                <label htmlFor="city" className="form-label">{t("forms.labels.city")}</label>
                                <select
                                    className={`form-select ${validationErrors.city ? "is-invalid" : ""}`}
                                    id="city"
                                    value={formData.city}
                                    onChange={handleInputChange}
                                >
                                    <option value="">{t("forms.placeholders.selectACity")}</option>
                                    {cities.map((city, index) => (
                                        <option key={index} value={city}>
                                            {city}
                                        </option>
                                    ))}
                                </select>
                                {validationErrors.city &&
                                    <div className="invalid-feedback">{validationErrors.city}</div>}
                            </div>
                            <div className="col-md-3">
                                <label htmlFor="postcode" className="form-label">{t("forms.labels.postalCode")}</label>
                                <input
                                    type="text"
                                    className={`form-control ${validationErrors.postcode ? "is-invalid" : ""}`}
                                    id="postcode"
                                    placeholder={t("forms.placeholders.enterYourPostalCode")}
                                    value={formData.postcode}
                                    onChange={handleInputChange}
                                />
                                {validationErrors.postcode &&
                                    <div className="invalid-feedback">{validationErrors.postcode}</div>}
                            </div>
                            <div className="col-12">
                                <label htmlFor="deliveryDate"
                                       className="form-label">{t("forms.labels.requestedDeliveryDate")}</label>
                                <input
                                    type="date"
                                    className={`form-control ${validationErrors.deliveryDate ? "is-invalid" : ""}`}
                                    id="deliveryDate"
                                    value={formData.deliveryDate}
                                    min={minDate}
                                    onChange={handleInputChange}
                                    onKeyDown={handleKeyDown}
                                />
                                {validationErrors.deliveryDate &&
                                    <div className="invalid-feedback">{validationErrors.deliveryDate}</div>}
                                <p className="mt-1 mb-0 text-muted">
                                    <small className="fw-bold">
                                        {t("validation.custom.deliveryDateMinimum")}
                                    </small>
                                </p>
                            </div>
                            <div className="d-flex justify-content-end">
                                <button
                                    disabled={!isFormValid}
                                    onClick={saveAndDispatchShippingInfo}
                                    className="btn btn-outline-secondary"
                                    type="button"
                                >
                                    {t("actions.save")}
                                </button>
                            </div>
                        </>
                    ) : (
                        <ShippingAddressSummary
                            data={formData}
                            setEditInfo={editShippingForm}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

ShippingForm.propTypes = {
    onShippingFormValidation: PropTypes.func.isRequired,
    shippingSummary: PropTypes.bool.isRequired,
    setShippingSummary: PropTypes.func.isRequired,
};

export default ShippingForm;
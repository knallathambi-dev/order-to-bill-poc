// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from 'react';
import countriesData from '../../../../../utlis/countries.json';
import useTranslations from '../../../../../utlis/i18n/useTranslations';

const AddressModal = ({address, onAddressChange, onValidationChange}) => {
    const [subUnitNumber, setSubUnitNumber] = useState(address.subUnitNumber || '');
    const [streetName, setStreetName] = useState(address.streetName || '');
    const [city, setCity] = useState(address.city || '');
    const [country, setCountry] = useState(address.country || '');
    const [postcode, setPostcode] = useState(address.postcode || '');
    const [validationErrors, setValidationErrors] = useState({});
    const {t} = useTranslations();

    const countries = [...countriesData]
        .map(({name, cities}) => ({name, cities}))
        .sort((a, b) => a.name.localeCompare(b.name));

    const cities = countries.find(c => c.name === country)?.cities || [];

    const validateForm = useCallback(() => {
        const errors = {};

        if (!country) errors.country = t("validation.required.country");
        if (!streetName.trim() && !subUnitNumber.trim()) {
            errors.address = t("validation.required.address");
        }
        if (!city) errors.city = t("validation.required.city");
        if (!postcode.trim()) errors.postcode = t("validation.required.postalCode");

        setValidationErrors(errors);
        const valid = Object.keys(errors).length === 0;

        if (onValidationChange) {
            onValidationChange(valid);
        }
    }, [country, streetName, subUnitNumber, city, postcode, t, onValidationChange]);

    useEffect(() => {
        validateForm();
    }, [validateForm]);

    const handleInputChange = (field, value) => {
        let updatedAddress = {
            addressId: address.addressId,
            subUnitNumber,
            streetName,
            country,
            city,
            postcode,
        };

        if (field === 'country') {
            updatedAddress = {
                ...updatedAddress,
                country: value,
                city: '',
                postcode: '',
                subUnitNumber: '',
                streetName: ''
            };
            setCity('');
            setPostcode('');
            setSubUnitNumber('');
            setStreetName('');
        } else if (field === 'city') {
            updatedAddress = {
                ...updatedAddress,
                city: value,
                postcode: '',
                subUnitNumber: '',
                streetName: ''
            };
            setPostcode('');
            setSubUnitNumber('');
            setStreetName('');
        } else {
            updatedAddress = {
                ...updatedAddress,
                [field]: value,
            };
        }

        onAddressChange(updatedAddress);
    };

    const handleAddressInput = (e) => {
        const addressValue = e.target.value;

        const subUnitMatch = addressValue.match(/^(\d+)/);
        const subUnitNumber = subUnitMatch ? subUnitMatch[1] : '';

        const streetName = addressValue
            .substring(subUnitNumber.length)
            .replace(/^[\s,]+/, '');

        setSubUnitNumber(subUnitNumber);
        setStreetName(streetName);

        onAddressChange({
            addressId: address.addressId,
            subUnitNumber: subUnitNumber,
            streetName: streetName,
            country,
            city,
            postcode,
        });
    };

    useEffect(() => {
        onAddressChange(address);
    }, [address]);

    return (
        <div className="row">
            <div className="col-md-12 mb-3">
                <label htmlFor="address" className="form-label is-required">{t("forms.labels.address")}</label>
                <div className="position-relative">
                    <input
                        type="text"
                        className={`form-control ${validationErrors.address ? "is-invalid" : ""}`}
                        placeholder={t("forms.placeholders.enterYourAddress")}
                        value={subUnitNumber && streetName ? `${subUnitNumber} ${streetName}` : `${subUnitNumber}${streetName}`}
                        onChange={handleAddressInput}
                    />
                    {validationErrors.address &&
                        <div className="invalid-feedback">{validationErrors.address}</div>}
                </div>
            </div>

            <div className="col-md-4">
                <label htmlFor="country" className="form-label is-required">{t("forms.labels.country")}</label>
                <select
                    id="country"
                    className={`form-select ${validationErrors.country ? "is-invalid" : ""}`}
                    value={country}
                    onChange={(e) => {
                        setCountry(e.target.value);
                        handleInputChange('country', e.target.value);
                    }}
                >
                    <option value="">{t("forms.placeholders.selectACountry")}</option>
                    {countries.map((countryOption) => (
                        <option key={countryOption.name} value={countryOption.name}>
                            {countryOption.name}
                        </option>
                    ))}
                </select>
                {validationErrors.country &&
                    <div className="invalid-feedback">{validationErrors.country}</div>}
            </div>

            <div className="col-md-4">
                <label htmlFor="city" className="form-label is-required">{t("forms.labels.city")}</label>
                <select
                    id="city"
                    className={`form-select ${validationErrors.city ? "is-invalid" : ""}`}
                    value={city}
                    onChange={(e) => {
                        setCity(e.target.value);
                        handleInputChange('city', e.target.value);
                    }}
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

            <div className="col-md-4">
                <label htmlFor="postcode" className="form-label is-required">{t("forms.labels.postalCode")}</label>
                <input
                    type="text"
                    className={`form-control ${validationErrors.postcode ? "is-invalid" : ""}`}
                    placeholder={t("forms.placeholders.enterYourPostalCode")}
                    value={postcode}
                    onChange={(e) => {
                        setPostcode(e.target.value);
                        handleInputChange('postcode', e.target.value);
                    }}
                />
                {validationErrors.postcode &&
                    <div className="invalid-feedback">{validationErrors.postcode}</div>}
            </div>
        </div>
    );
};

export default AddressModal;
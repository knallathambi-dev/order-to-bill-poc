// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useRef, useState} from "react";
import countriesData from "../../utlis/countries.json";
import "./Eligibility.css";
import MyMap from "./MyMap";
import {Breadcrumb, Modal} from "../../components";
import CheckEligibilityModal from "./modal/CheckEligibilityModal";
import {useDispatch, useSelector} from "react-redux";
import {toggleLoading} from "../../store/actions/loadingActions";
import {useTitlePage} from "../../hooks";
import {handleAddressConfiguration} from "./services/eligibilityService";
import NotEligibleAddress from "./modal/NotEligibleAddress";
import useTranslations from "../../utlis/i18n/useTranslations";
import {ConfigurationProvider, useConfiguration} from "../Plan/shared/context/ConfigurationContext";
import {
    getDefaultProductConfiguration,
    getProductConfiguration
} from "../Plan/shared/services/productConfigurationService";
import {setConfigurationId} from "../../store/actions/configurationActions";

const EligibilityContent = () => {
    const {orderType} = useSelector((state) => state.order);
    const {selectedOfferName} = useSelector((state) => state.offer);

    useTitlePage(
        'checkEligibility',
        orderType === 'Acquisition'
            ? ['offers', selectedOfferName, 'checkEligibility'].filter(Boolean)
            : [selectedOfferName, 'checkEligibility']
    );

    const dispatch = useDispatch();
    const {configuration, relatedParty, t, tNotification} = useConfiguration();

    const [notEligibleAddressModal, setNotEligibleAddressModal] = useState(false);
    const [formData, setFormData] = useState({
        country: "",
        address: "",
        city: "",
        postcode: "",
    });
    const [isFormValid, setIsFormValid] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});
    const [showCheckModal, setShowCheckModal] = useState(false);

    const validateForm = React.useCallback(() => {
        const errors = {};
        if (!formData.country) errors.country = t("validation.required.country");
        if (!formData.address.trim()) errors.address = t("validation.required.address");
        if (!formData.city) errors.city = t("validation.required.city");
        if (!formData.postcode.trim()) errors.postcode = t("validation.required.postalCode");
        setValidationErrors(errors);
        setIsFormValid(Object.keys(errors).length === 0);
    }, [formData, t]);

    useEffect(() => {
        validateForm();
    }, [validateForm]);

    const handleInputChange = (e) => {
        const {id, value} = e.target;
        if (id === "country") {
            setFormData({...formData, [id]: value, address: "", city: "", postcode: ""});
        } else if (id === "city") {
            setFormData({...formData, [id]: value, postcode: ""});
        } else {
            setFormData({...formData, [id]: value});
        }
    };

    const countries = [...countriesData]
        .map(({name, cities}) => ({name, cities}))
        .sort((a, b) => a.name.localeCompare(b.name));
    const cities = countries.find(c => c.name === formData.country)?.cities || [];

    const getMapLocation = () => {
        const {address, city, country} = formData;
        const parts = [];
        if (address) parts.push(address);
        if (city) parts.push(city);
        if (country) parts.push(country);
        return parts.length > 0 ? parts.join(', ') : 'France';
    };

    const hideCheckModal = () => setShowCheckModal(false);

    const handleCheckEligibility = async () => {
        const addressValue = (formData.address || '').toLowerCase();
        if (addressValue.startsWith('noteligibleaddress')) {
            setNotEligibleAddressModal(true);
            return;
        }
        dispatch(toggleLoading(true));
        const subUnitMatch = formData.address.match(/^(\d+)/);
        const subUnitNumber = subUnitMatch ? subUnitMatch[1].trim() : '';
        const streetName = formData.address.replace(/^(\d+)/, '').replace(/^[,\s]+/, '').trim();
        const addressDetails = {
            subUnitNumber,
            streetName,
            postcode: formData.postcode,
            city: formData.city,
            country: formData.country,
        };
        try {
            await handleAddressConfiguration(
                addressDetails,
                configuration,
                relatedParty,
                dispatch,
                tNotification
            );
            setTimeout(() => {
                dispatch(toggleLoading(false));
                setShowCheckModal(true);
            }, 2000);
        } catch (error) {
            dispatch(toggleLoading(false));
        }
    };

    const handleMapClick = async (latlng) => {
        const {lat, lng} = latlng;
        const response = await fetch(
            `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&accept-language=en`
        );
        const data = await response.json();
        const address = `${data.address.house_number || data.address.unit || data.address.apartment || ''} ${data.address.road || ''}`;
        const city = data.address?.city || data.address?.town || data.address?.village || "";
        const country = data.address?.country || "";
        const postcode = data.address?.postcode || "";
        setFormData({...formData, address, country, city, postcode});
    };

    const hideNotEligibleAddressModal = () => setNotEligibleAddressModal(false);

    return (
        <main>
            <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <Breadcrumb/>
                    </div>
                </div>
            </div>
            <div className="container py-4">
                <div className="row">
                    <div className="col-md-6">
                        <h3 className="mb-2">{t("eligibility.title")}</h3>
                        <p className="mb-4">{t("eligibility.description")}</p>
                        <div className="mb-3">
                            <label htmlFor="country" className="form-label is-required">
                                {t("forms.labels.country")} <span className="visually-hidden">(required)</span>
                            </label>
                            <select
                                id="country"
                                className={`form-select ${validationErrors.country ? "is-invalid" : ""}`}
                                value={formData.country}
                                onChange={handleInputChange}
                            >
                                <option value="">{t("forms.placeholders.selectACountry")}</option>
                                {countries.map((country) => (
                                    <option key={country.name} value={country.name}>{country.name}</option>
                                ))}
                            </select>
                            {validationErrors.country &&
                                <div className="invalid-feedback">{validationErrors.country}</div>}
                        </div>
                        <div className="address-input">
                            <label htmlFor="address"
                                   className="form-label is-required">{t("forms.labels.address")}</label>
                            <div className="position-relative w-100 mb-3">
                                <input
                                    type="text"
                                    id="address"
                                    className={`form-control ${validationErrors.address ? "is-invalid" : ""}`}
                                    placeholder={t("forms.placeholders.enterYourAddress")}
                                    value={formData.address}
                                    autoComplete="off"
                                    onChange={handleInputChange}
                                />
                                {validationErrors.address &&
                                    <div className="invalid-feedback">{validationErrors.address}</div>}
                            </div>
                            <div className="optional-inputs mb-3">
                                <div className="row">
                                    <div className="col-md-4">
                                        <label htmlFor="city" className="form-label is-required">
                                            {t("forms.labels.city")} <span className="visually-hidden">(required)</span>
                                        </label>
                                        <select
                                            id="city"
                                            className={`form-select me-3 ${validationErrors.city ? "is-invalid" : ""}`}
                                            value={formData.city}
                                            onChange={handleInputChange}
                                        >
                                            <option value="">{t("forms.placeholders.selectACity")}</option>
                                            {cities.map((city, index) => (
                                                <option key={index} value={city}>{city}</option>
                                            ))}
                                        </select>
                                        {validationErrors.city &&
                                            <div className="invalid-feedback">{validationErrors.city}</div>}
                                    </div>
                                    <div className="col-md-4">
                                        <label htmlFor="postcode"
                                               className="form-label is-required">{t("forms.labels.postalCode")}</label>
                                        <input
                                            type="text"
                                            id="postcode"
                                            className={`form-control me-3 ${validationErrors.postcode ? "is-invalid" : ""}`}
                                            placeholder={t("forms.placeholders.enterYourPostalCode")}
                                            autoComplete="off"
                                            value={formData.postcode}
                                            onChange={handleInputChange}
                                        />
                                        {validationErrors.postcode &&
                                            <div className="invalid-feedback">{validationErrors.postcode}</div>}
                                    </div>
                                </div>
                            </div>
                            <div className="d-flex justify-content-end">
                                <button
                                    className="btn btn-primary"
                                    type="button"
                                    onClick={handleCheckEligibility}
                                    disabled={!isFormValid}
                                >
                                    {t("actions.checkEligibility")}
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6">
                        <MyMap location={getMapLocation()} onMapClick={handleMapClick}/>
                    </div>
                </div>
            </div>
            {showCheckModal && (
                <Modal
                    show={showCheckModal}
                    body={<CheckEligibilityModal hideModal={hideCheckModal}/>}
                    onClose={hideCheckModal}
                    hideFooter={true}
                    className="EligibilityModal"
                />
            )}
            {notEligibleAddressModal && (
                <Modal
                    show={notEligibleAddressModal}
                    body={<NotEligibleAddress hideModal={hideNotEligibleAddressModal}/>}
                    onClose={hideNotEligibleAddressModal}
                    hideFooter={true}
                />
            )}
        </main>
    );
};

export default function Eligibility() {
    const dispatch = useDispatch();
    const {tNotification} = useTranslations();
    const {relatedParty} = useSelector((state) => state.auth);
    const {selectedOfferId} = useSelector((state) => state.offer);
    const {configurationId} = useSelector((state) => state.configuration);
    const {orderType} = useSelector((state) => state.order);
    const [configuration, setConfiguration] = useState(null);
    const relatedPartyRef = useRef(relatedParty);
    const tNotificationRef = useRef(tNotification);

    useEffect(() => {
        relatedPartyRef.current = relatedParty;
    }, [relatedParty]);

    useEffect(() => {
        tNotificationRef.current = tNotification;
    }, [tNotification]);

    useEffect(() => {
        if (orderType !== 'Migration' || !configurationId) return;

        const loadConfiguration = async () => {
            const data = await getProductConfiguration(
                configurationId,
                dispatch,
                tNotificationRef.current
            );
            if (data) {
                setConfiguration(data);
            }
        };
        loadConfiguration();
    }, [configurationId, orderType, dispatch]);

    useEffect(() => {
        if (orderType === 'Migration' || !selectedOfferId) return;

        const loadConfiguration = async () => {
            const data = await getDefaultProductConfiguration(
                selectedOfferId,
                relatedPartyRef.current,
                "add",
                dispatch,
                tNotificationRef.current
            );

            if (data?.id) {
                dispatch(setConfigurationId(data.id));
            }
            if (data) {
                setConfiguration(data);
            }
        };
        loadConfiguration();
    }, [selectedOfferId, orderType, dispatch]);

    const handleConfigurationChange = useCallback((updatedConfiguration) => {
        setConfiguration(updatedConfiguration);
    }, []);

    if (!configuration) return null;

    return (
        <ConfigurationProvider
            configuration={configuration}
            onConfigurationChange={handleConfigurationChange}
        >
            <EligibilityContent/>
        </ConfigurationProvider>
    );
}
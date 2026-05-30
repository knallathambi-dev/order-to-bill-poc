// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";

import {Breadcrumb} from "../../components";
import {useTitlePage} from "../../hooks";
import useTranslations from "../../utlis/i18n/useTranslations";
import {getProductConfiguration} from "../Plan/shared/services/productConfigurationService";
import {ConfigurationProvider} from "../Plan/shared/context/ConfigurationContext";

import {ShippingItems} from "./components";
import PlanPreview from "./components/PlanPreview";

export function Shipping() {
    useTitlePage("shippingDetails");

    const {t, tNotification} = useTranslations();
    const {configurationId} = useSelector((state) => state.configuration);

    const [isShippingFormValid, setIsShippingFormValid] = useState(false);
    const [configuration, setConfiguration] = useState(null);

    const dispatch = useDispatch();

    useEffect(() => {
        if (!configurationId) return;

        const loadConfiguration = async () => {
            const data = await getProductConfiguration(
                configurationId,
                dispatch,
                tNotification
            );
            if (data) setConfiguration(data);
        };

        loadConfiguration();
    }, [configurationId]);

    const handleConfigurationChange = useCallback((updatedConfiguration) => {
        setConfiguration(updatedConfiguration);
    }, []);

    if (!configuration) return null;

    return (
        <ConfigurationProvider
            configuration={configuration}
            onConfigurationChange={handleConfigurationChange}
        >
            <main>
                <div className="container">
                    <Breadcrumb/>
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="banner-title mb-0">
                                {t("pages.shippingDetails")}
                            </h1>
                        </div>
                    </div>
                </div>
                <div className="container py-4">
                    <div className="row">
                        <div className="col-md-8">
                            <ShippingItems onShippingFormValidation={setIsShippingFormValid}/>
                        </div>
                        <div className="col-md-4">
                            <PlanPreview isShippingFormValid={isShippingFormValid}/>
                        </div>
                    </div>
                </div>
            </main>
        </ConfigurationProvider>
    );
}

export default Shipping;
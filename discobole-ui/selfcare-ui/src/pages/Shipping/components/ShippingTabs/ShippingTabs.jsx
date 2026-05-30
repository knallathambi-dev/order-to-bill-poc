// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import {useSelector} from "react-redux";
import ShippingTabContent from "./ShippingTabContent";
import ShippingTabsNav from "./ShippingTabsNav";
import createRequestBodyShipping from "../../services/createRequestBodyShipping";
import {fetchConfiguration} from "../../../Plan/shared/services/productConfigurationService";
import {setShippingMethod} from "../../../../store/actions/shippingActions";
import {useConfiguration} from "../../../Plan/shared/context/ConfigurationContext";
import PropTypes from "prop-types";
import {toast} from "react-toastify";

export default function ShippingTabs({shippingMethods}) {
    const [selectedTab, setSelectedTab] = useState(2);
    const {shippingConfigItems} = useSelector((state) => state.shipping);

    const {
        configuration,
        onConfigurationChange,
        relatedParty,
        dispatch,
        t,
        tNotification
    } = useConfiguration();

    useEffect(() => {
        dispatch(setShippingMethod(selectedTab));
    }, [selectedTab, dispatch]);

    const handleSetSelectedTab = async (value) => {
        try {
            setSelectedTab(value);
            dispatch(setShippingMethod(value));

            const shippingMode = value === 1 ? "Home delivery" : "Instore";
            const shippingRequestBody = createRequestBodyShipping(
                configuration.id,
                relatedParty,
                shippingConfigItems,
                shippingMode,
                null,
                null
            );

            const updatedConfig = await fetchConfiguration(
                shippingRequestBody,
                shippingConfigItems[0].id,
                dispatch,
                tNotification,
                {silent: true}
            );

            if (updatedConfig && onConfigurationChange) {
                onConfigurationChange(updatedConfig.fullConfig);
            }
        } catch {
            toast.error(tNotification("shipping.updateShippingMethodFailed"));
        }
    };

    return (
        <>
            <h6>{t("shipping.shippingMethod")}</h6>
            <div className="select-method">
                <ShippingTabsNav
                    shippingMethods={shippingMethods}
                    onSelectTab={handleSetSelectedTab}
                    selectedTab={selectedTab}
                />
                <ShippingTabContent
                    selectedTab={selectedTab}
                    shippingMethods={shippingMethods}
                />
            </div>
        </>
    );
}

ShippingTabs.propTypes = {
    shippingMethods: PropTypes.array.isRequired
};
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createChannel, createRelatedPartyArray} from "../../../utlis/utils";
import {toast} from "react-toastify";
import {generateRandomId} from "../../../utlis/helpers";
import {ADDRESS_CHARACTERISTIC_TYPE, TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM} from "../../../utlis/constants";
import apiClient from "../../../services/api/apiClient";
import {setConfigurationId} from "../../../store/actions/configurationActions";

export const handleAddressConfiguration = async (
    addressDetails,
    configuration,
    relatedParty,
    dispatch,
    tNotification
) => {
    const productConfiguratorUrl = process.env.REACT_APP_PRODUCT_CONFIGURATOR_URL;

    if (addressDetails.streetName.toLowerCase().startsWith('noteligibleaddress')) {
        addressDetails.addressId = '123';
    } else if (addressDetails.streetName.toLowerCase().startsWith('noappointmentneeded')) {
        addressDetails.addressId = '456';
    } else {
        addressDetails.addressId = generateRandomId();
    }

    const extractAddressCharacteristic = (configurationItem) => {
        const characteristics = configurationItem.productConfiguration?.configurationCharacteristic || [];
        return characteristics.find((characteristic) =>
            characteristic["@type"] === ADDRESS_CHARACTERISTIC_TYPE
        ) || null;
    };

    const buildConfigurationPayload = () => {
        const requestConfigurationItems = [];
        const relatedPartyArray = createRelatedPartyArray(relatedParty);

        const eligibleConfigurationItems = configuration.computedProductConfigurationItem
            .filter(configurationItem =>
                !configurationItem["@type"] ||
                configurationItem["@type"] === TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM
            );

        eligibleConfigurationItems.forEach((configurationItem) => {
            const addressCharacteristicData = extractAddressCharacteristic(configurationItem);

            if (!addressCharacteristicData) return;

            const addressWithCharacteristicId = {
                ...addressDetails,
                id: addressCharacteristicData.id
            };

            const addressCharacteristicPayload = {
                "@type": ADDRESS_CHARACTERISTIC_TYPE,
                ...addressWithCharacteristicId
            };

            const configurationCharacteristic = {
                id: addressCharacteristicData.id,
                configurationCharacteristicValues: [
                    {
                        isSelected: true,
                        characteristic: addressCharacteristicPayload,
                    },
                ],
            };

            const productConfiguration = {
                isSelected: true,
                configurationCharacteristic: [configurationCharacteristic],
                configurationAction: [
                    {
                        action: "add",
                        isSelected: true,
                        "@type": "ConfigurationAction",
                    },
                ],
                "@type": "ProductConfiguration",
            };

            requestConfigurationItems.push({
                id: configurationItem.id,
                productConfiguration,
                "@type": "QueryProductConfigurationItem",
            });
        });

        return {
            id: configuration.id,
            channel: createChannel(),
            ...(relatedPartyArray.length > 0 && {relatedParty: relatedPartyArray}),
            requestProductConfigurationItem: requestConfigurationItems,
        };
    };

    const requestPayload = buildConfigurationPayload();

    if (requestPayload.requestProductConfigurationItem.length === 0) {
        return null;
    }

    try {
        const res = await apiClient.post(productConfiguratorUrl, requestPayload);

        const {status, data} = res || {};

        if (status !== 200 || data.state.toLowerCase() !== "done") {
            console.error("Configuration fetch failed");
        }

        dispatch(setConfigurationId(data.id));
        return res.data;
    } catch (error) {
        toast.error(tNotification("eligibility.updateAddressFailed"));
        throw error;
    }
};
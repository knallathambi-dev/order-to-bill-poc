// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toast} from "react-toastify";
import createProductConfigItemRequest from "./createProductConfigItemRequest";
import {toggleLoading} from "../../../../store/actions/loadingActions";
import {createChannel, createRelatedPartyArray} from "../../../../utlis/utils";
import apiClient from "../../../../services/api/apiClient";
import {setConfigurationId} from "../../../../store/actions/configurationActions";

const productConfiguratorUrl = process.env.REACT_APP_PRODUCT_CONFIGURATOR_URL;

export const getDefaultProductConfiguration = async (
    productOfferingOrProductId,
    relatedParty,
    actionType,
    dispatch,
    tNotification
) => {
    let orderType;
    let refType;
    switch (actionType) {
        case "add":
            orderType = "productOffering";
            refType = "ProductOfferingRef";
            break;
        case "terminate":
        case "modify":
            orderType = "product";
            refType = "ProductRef";
            break;
        default:
            toast.error(tNotification("common.fetchConfigurationFailed"));
            return null;
    }

    if (!productOfferingOrProductId) {
        toast.error(tNotification("common.fetchConfigurationFailed"));
        return null;
    }

    const relatedPartyArray = createRelatedPartyArray(relatedParty);

    const requestPayload = {
        channel: createChannel(),
        ...(relatedPartyArray.length > 0 && {relatedParty: relatedPartyArray}),
        requestProductConfigurationItem: [
            {
                productConfiguration: {
                    [orderType]: {
                        id: productOfferingOrProductId,
                        "@type": refType,
                    },
                    configurationAction: [
                        {
                            isSelected: true,
                            action: actionType,
                            "@type": "ConfigurationAction",
                        },
                    ],
                    "@type": "ProductConfiguration",
                },
                "@type": "QueryProductConfigurationItem",
            },
        ],
    };

    dispatch(toggleLoading(true));
    const res = await apiClient.post(productConfiguratorUrl, requestPayload).catch(() => null);
    dispatch(toggleLoading(false));

    if (!res || res.status !== 200 || res.data?.state?.toLowerCase() !== "done") {
        toast.error(tNotification("common.fetchConfigurationFailed"));
        return null;
    }

    return res.data;
};

export const performActionAndGetCharacteristics = async ({
                                                             configurationId,
                                                             configurationItemId,
                                                             characteristic,
                                                             isSelected,
                                                             relatedParty,
                                                             actionType,
                                                             dispatch,
                                                             tNotification,
                                                             onConfigurationChange,
                                                         }) => {
    const requestPayload = createProductConfigItemRequest(
        configurationId,
        configurationItemId,
        characteristic,
        relatedParty,
        isSelected,
        actionType
    );

    const result = await fetchConfiguration(
        requestPayload,
        configurationItemId,
        dispatch,
        tNotification,
        {silent: true},
        onConfigurationChange
    );

    if (!result) return null;

    const characteristics = result.item?.productConfiguration?.configurationCharacteristic;
    if (!Array.isArray(characteristics) || characteristics.length === 0) {
        return {updatedCharacteristics: {}};
    }

    const characteristicsMap = characteristics.reduce((acc, char) => {
        if (!char?.isConfigurable) return acc;
        const selected = char?.configurationCharacteristicValues?.find(
            (v) => v?.isSelected
        )?.characteristic;
        if (!selected) return acc;

        const {id, name, value} = selected;
        const type = selected['@type'];
        acc[char.name] = {id, name, value, type};
        return acc;
    }, {});

    return {updatedCharacteristics: characteristicsMap};
};

export const fetchDefaultProductConfigForMigration = async (
    currentPlanId,
    targetProductOfferingId,
    relatedParty,
    dispatch
) => {
    const relatedPartyArray = createRelatedPartyArray(relatedParty);

    const requestPayload = {
        channel: createChannel(),
        ...(relatedPartyArray.length > 0 && {relatedParty: relatedPartyArray}),
        requestProductConfigurationItem: [
            {
                productConfiguration: {
                    product: {
                        id: currentPlanId,
                        "@type": "ProductRef",
                    },
                    configurationAction: [
                        {
                            action: "migrate",
                            isSelected: true,
                            "@type": "ConfigurationAction",
                        },
                    ],
                    "@type": "ProductConfiguration",
                },
                "@type": "SourceQueryProductConfigurationItem",
            },
            {
                productConfiguration: {
                    productOffering: {
                        id: targetProductOfferingId,
                        "@type": "ProductOfferingRef",
                    },
                    "@type": "ProductConfiguration",
                },
                "@type": "TargetQueryProductConfigurationItem",
            },
        ],
    };

    const res = await apiClient.post(productConfiguratorUrl, requestPayload).catch(() => null);
    const {status, data} = res || {};

    if (status !== 200 || data?.state?.toLowerCase() !== "done") {
        return null;
    }

    dispatch(setConfigurationId(data.id));

    return data;
};

export const fetchConfiguration = async (
    requestPayload,
    configurationItemId,
    dispatch,
    tNotification,
    options = {},
    onConfigurationChange
) => {
    const {silent = false, errorMessage = 'common.fetchConfigurationFailed'} = options;

    dispatch(toggleLoading(true));
    const res = await apiClient.post(productConfiguratorUrl, requestPayload).catch(() => null);
    dispatch(toggleLoading(false));

    if (!res || res.status !== 200) {
        if (!silent) toast.error(tNotification(errorMessage));
        return null;
    }

    const {data} = res;

    if (data?.state?.toLowerCase() !== "done") {
        if (!silent) toast.error(tNotification(errorMessage));
        return null;
    }

    const items = data?.computedProductConfigurationItem ?? [];
    const found = items.find((item) => item?.id === configurationItemId);

    if (!found) {
        if (!silent) toast.error(tNotification(errorMessage));
        return null;
    }

    onConfigurationChange?.(data);

    return {fullConfig: data, item: found};
};

export const getProductConfiguration = async (configurationId, dispatch, tNotification) => {
    if (!configurationId) {
        toast.error(tNotification("common.fetchConfigurationFailed"));
        return null;
    }

    const url = `${productConfiguratorUrl}/${configurationId}`;

    dispatch(toggleLoading(true));
    const res = await apiClient.get(url).catch(() => null);
    dispatch(toggleLoading(false));
    const {status, data} = res || {};

    if (status !== 200 || data?.state?.toLowerCase() !== "done") {
        toast.error(tNotification("common.fetchConfigurationFailed"));
        return null;
    }

    return data;
};
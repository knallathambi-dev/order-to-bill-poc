// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toast} from "react-toastify";
import apiClient from "../../../services/api/apiClient";

const productCatalogUrl = process.env.REACT_APP_PRODUCT_CATALOG_URL;

const QUERY_PARAMS = {
    lifecycleStatus: "launched,active",
    "@type": "ATOMICPRODUCTOFFERING",
};

const fetchDevices = async (tNotification) => {
    try {
        const {data} = await apiClient.get(productCatalogUrl, {params: QUERY_PARAMS});
        const list = Array.isArray(data) ? data : [];
        return list.filter((product) => product?.isSellable);
    } catch {
        toast.error(tNotification("home.devices.loadFailed"));
        return [];
    }
};

export default fetchDevices;
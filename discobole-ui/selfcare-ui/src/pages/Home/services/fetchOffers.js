// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import offerImg1 from "../../../assests/imgs/offer-1.png";
import offerImg2 from "../../../assests/imgs/offer-2.png";
import offerImg3 from "../../../assests/imgs/offer-3.png";
import offerImg4 from "../../../assests/imgs/offer-4.png";
import {toggleLoading} from "../../../store/actions/loadingActions";
import {toast} from "react-toastify";
import apiClient from "../../../services/api/apiClient";

const productCatalogUrl = process.env.REACT_APP_PRODUCT_CATALOG_URL;
const offerImages = [offerImg1, offerImg2, offerImg3, offerImg4];

const QUERY_PARAMS = {
    "@type": "CONTRACT",
    lifecycleStatus: "launched,active",
    "allowedAction.Action.name": "Add",
    "allowedAction.ChannelRef.id": "Selfcare",
    "MarketSegmentRef.id": "B2C,B2B",
    sort: "-lastUpdate",
};
const PHASE7_BROADBAND_OFFER_NAMES = [
    "Fiber Broadband 300 Mbps",
    "Static IP Add-on",
];
const PHASE7_BROADBAND_OFFER_NAME_KEYS = new Set(
    PHASE7_BROADBAND_OFFER_NAMES.map((name) => name.toLowerCase())
);

const pickImage = (index) => offerImages[index % offerImages.length];

const fetchOffers = async (dispatch, tNotification) => {
    dispatch(toggleLoading(true));

    try {
        const {data} = await apiClient.get(productCatalogUrl, {params: QUERY_PARAMS});
        let list = Array.isArray(data) ? data : [];

        const hasPhase7Offers = list.some((offer) =>
            PHASE7_BROADBAND_OFFER_NAME_KEYS.has(String(offer?.name || "").toLowerCase())
        );

        if (!hasPhase7Offers) {
            const fallbackResponse = await apiClient.get(productCatalogUrl, {
                params: {
                    lifecycleStatus: "launched,active",
                    sort: "-lastUpdate",
                },
            });
            const fallbackList = Array.isArray(fallbackResponse.data) ? fallbackResponse.data : [];
            const broadbandOffers = fallbackList.filter((offer) =>
                PHASE7_BROADBAND_OFFER_NAME_KEYS.has(String(offer?.name || "").toLowerCase())
            );
            list = [...broadbandOffers, ...list];
        }

        return list.map((offer, index) => ({
            ...offer,
            imageUrl: pickImage(index),
        }));
    } catch {
        toast.error(tNotification("home.offers.loadFailed"));
        return [];
    } finally {
        dispatch(toggleLoading(false));
    }
};

export default fetchOffers;

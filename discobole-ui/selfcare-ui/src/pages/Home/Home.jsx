// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import {useDispatch} from "react-redux";
import {toast} from "react-toastify";
import headerImage from "../../assests/imgs/online-selfcare-illustration.svg";
import {useTitlePage} from "../../hooks";
import "./Home.css";
import fetchOffers from "./services/fetchOffers";
import DevicesList from "./components/Devices/DevicesList";
import fetchDevices from "./services/fetchDevices";
import useTranslations from "../../utlis/i18n/useTranslations";
import OffersList from "./components/Offers/OffersList";
import {resetBreadcrumb} from "../../store/actions/breadcrumbActions";

function Home() {
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();

    const [offers, setOffers] = useState([]);
    const [devices, setDevices] = useState([]);

    useEffect(() => {
        dispatch(resetBreadcrumb());
    }, [dispatch]);

    useTitlePage("home");

    useEffect(() => {
        const getOffers = async () => {
            try {
                const data = await fetchOffers(dispatch, tNotification);
                setOffers(data ?? []);
                const devicesData = await fetchDevices(tNotification);
                setDevices(devicesData ?? []);
            } catch (error) {
                toast.error(
                    error?.serverError || "Unable to load offers. Please try again later."
                );
            }
        };

        getOffers();
    }, []);

    return (
        <div id="home-page">
            <div className="text-bg-dark position-relative">
                <div className="container-xxl position-relative z-1 py-5">
                    <div className="row d-flex align-items-center">
                        <div className="col-12 col-lg-6 py-2 bg-dark">
                            <h2 className="pt-1 pt-md-3 mb-2 mb-md-3 display-2 text-primary">
                                {t("home.greeting")}
                            </h2>
                            <p className="pt-1 mb-2 display-2">
                                {t("home.subtitle")}
                            </p>
                        </div>
                        <div className="col-12 col-lg-6">
                            <img
                                loading="lazy"
                                className="w-100 object-fit-cover"
                                alt=""
                                src={headerImage}
                            />
                        </div>
                    </div>
                </div>
            </div>
            <OffersList offers={offers}/>
            <DevicesList devices={devices}/>
        </div>
    );
}

export default Home;
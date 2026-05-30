// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import Carousel from "react-multi-carousel";
import DeviceCard from "./Card/DeviceCard";
import VR from "../../../../assests/imgs/VR.jpg";
import samsungPhones from "../../../../assests/imgs/samsungPhones.jpg";
import buds from "../../../../assests/imgs/buds.png";
import a55 from "../../../../assests/imgs/a55.png";
import defaultImage from "../../../../assests/imgs/Discobole.png";
import {responsive} from "../../../../utlis/helpers";
import postProcessFlowService from "../../services/postProcessFlowService";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const getDeviceImage = (deviceName) => {
    const name = (deviceName || "").toLowerCase();
    if (name.includes("buds")) return buds;
    if (name.includes("vr")) return VR;
    if (name.includes("samsung")) return samsungPhones;
    if (name.includes("a55")) return a55;
    return defaultImage;
};

const DevicesList = ({devices = []}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const {relatedParty} = useSelector((state) => state.auth);

    const [searchTerm, setSearchTerm] = useState("");

    const {t, tNotification} = useTranslations();

    const normalizedQuery = searchTerm.trim().toLowerCase();
    const hasQuery = normalizedQuery.length > 2;

    const filteredDevices = hasQuery
        ? devices.filter((device) =>
            device?.name?.toLowerCase().includes(normalizedQuery)
        )
        : devices;

    const handleOrderNow = async (deviceId, deviceName, deviceDescription = "") => {
        try {
            const processResponse = await postProcessFlowService(
                deviceId,
                deviceName,
                deviceDescription,
                relatedParty,
                dispatch,
                tNotification
            );

            const data = processResponse?.data;
            if (!data) {
                toast.error(tNotification("home.devices.configureFailed"));
                return;
            }

            navigate("/set-up-device");
        } catch {
            toast.error(tNotification("home.devices.configureFailed"));
        }
    };

    return (
        <div className="container py-4">
            <div className="mt-3">
                <div className="row">
                    <div className="col-md-12">
                        <div className="d-flex justify-content-between align-content-center mb-3">
                            <h2 className="mb-0">{t("pages.devicesAndElectronics")}</h2>
                            <div className="search-container">
                                <input
                                    type="text"
                                    className="form-control search-input"
                                    placeholder={t("forms.placeholders.searchDevices")}
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                {searchTerm && (
                                    <button
                                        type="button"
                                        className="btn btn-icon dismiss-btn"
                                        onClick={() => setSearchTerm("")}
                                    >
                                        ✕
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                {filteredDevices.length === 0 ? (
                    <div className="text-center py-5">
                        <h4>{t("home.noMatchesFound")}</h4>
                    </div>
                ) : (
                    <Carousel responsive={responsive} autoPlay={false} infinite>
                        {filteredDevices.map((device) => (
                            <DeviceCard
                                key={device.id}
                                device={{
                                    ...device,
                                    imageUrl: device.imageUrl || getDeviceImage(device.name),
                                }}
                                onOrderNowClick={() =>
                                    handleOrderNow(device.id, device.name, device.description)
                                }
                            />
                        ))}
                    </Carousel>
                )}
            </div>
        </div>
    );
};

export default DevicesList;
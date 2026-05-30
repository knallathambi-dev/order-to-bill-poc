// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {useSelector} from "react-redux";
import {formatToLocalDateTime} from "../../utlis/helpers";
import {useTitlePage} from "../../hooks";
import useTranslations from "../../utlis/i18n/useTranslations";
import {toast} from "react-toastify";
import {fetchOrder} from "../../services";

function OrderStatusUpdate() {
    useTitlePage("orderStatusUpdate");

    const {t, tNotification} = useTranslations();
    const navigate = useNavigate();
    const location = useLocation();
    const {productOrderId} = useSelector(state => state.order);

    const {orderStatus} = location.state || {};

    const [order, setOrder] = useState({});

    useEffect(() => {
        const loadOrder = async () => {
            if (!productOrderId) return;

            try {
                const fetchedOrder = await fetchOrder(productOrderId, tNotification);
                setOrder(fetchedOrder);
            } catch (error) {
                toast.error(tNotification("orderStatusUpdate.fetchFailed"));
            }
        };

        loadOrder();
    }, [productOrderId]);

    const handleTrackOrder = () => {
        navigate('/track-order');
    };

    const getStatusTitle = () => {
        if (!orderStatus) return "";

        const statusText = t(`order.status.${orderStatus}`);

        return t("order.statusUpdate.title", {
            status: statusText
        });
    };

    const getCompletionDate = () => {
        if (!order.requestedCompletionDate) return null;
        return formatToLocalDateTime(order.requestedCompletionDate);
    };

    return (
        <main className="min-vh-100 d-flex align-items-center py-5">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-12 col-md-11 col-lg-10 col-xl-10">
                        <div className="card">
                            <div className="invoice p-5 text-center">
                                <div className="success-circle mb-4">
                                    <em className="icon-checkbox_tick" aria-hidden="true"></em>
                                </div>

                                <h1 className="h2 mb-3">
                                    {getStatusTitle()}
                                </h1>

                                <p className="mb-3">
                                    <strong>{t("common.orderId")}:</strong> {productOrderId}
                                </p>

                                {getCompletionDate() && (
                                    <p className="mb-4">
                                        {t("order.statusUpdate.completedBy", {
                                            date: getCompletionDate()
                                        })}
                                    </p>
                                )}

                                <div className="d-grid col-8 mx-auto my-5">
                                    <button
                                        className="btn btn-lg btn-primary"
                                        type="button"
                                        onClick={handleTrackOrder}
                                        aria-label={t("actions.trackYourOrder")}
                                    >
                                        {t("actions.trackYourOrder")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
}

export default OrderStatusUpdate;
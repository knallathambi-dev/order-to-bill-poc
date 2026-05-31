// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useDispatch, useSelector} from "react-redux";
import {toast} from "react-toastify";
import React, {useCallback, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {formatActionName, formatToLocalDateTime, getActionColor} from "../../utlis/helpers";
import {toggleLoading} from "../../store/actions/loadingActions";
import {ORDER_STATUSES} from "../../utlis/constants";
import {useTitlePage} from "../../hooks";
import useTranslations from "../../utlis/i18n/useTranslations";
import {resetBreadcrumb} from "../../store/actions/breadcrumbActions";
import {getDisplayableOrderItems} from "../../utlis/utils";
import {fetchOrder} from "../../services";
import {ReloadButton} from "../../components/utils/ReloadButton";

const statusLabel = (state) => ORDER_STATUSES[state] || state || "-";

export default function TrackOrder() {
    useTitlePage("trackOrder");

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {productOrderId} = useSelector(state => state.order);
    const {t, tNotification} = useTranslations();

    const [order, setOrderState] = useState(null);

    const getOrderById = useCallback(async () => {
        if (!productOrderId) return;

        dispatch(toggleLoading(true));
        try {
            const orderData = await fetchOrder(productOrderId);
            if (!orderData) {
                toast.error(tNotification("trackOrder.fetchFailed"));
                return;
            }
            setOrderState(orderData);
        } catch {
            toast.error(tNotification("trackOrder.fetchFailed"));
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [productOrderId, dispatch, tNotification]);

    useEffect(() => {
        getOrderById();
    }, [productOrderId]);

    const creationDate = order?.creationDate ? formatToLocalDateTime(order.creationDate) : '';

    const atomicOrderItems = getDisplayableOrderItems(order);

    const goToMyOrders = () => {
        dispatch(resetBreadcrumb());
        navigate('/my-account');
    };

    if (!order) return null;

    return (
        <div className="container my-4">
            <div className="row">
                <div className="col-12">
                    <div className="d-flex align-items-center justify-content-between mb-2">
                        <h1 className="mb-0">{t("pages.trackOrder")}</h1>
                        <div className="d-flex align-items-center gap-2">
                            <ReloadButton onClick={getOrderById}/>
                            <button
                                type="button"
                                className="btn btn-outline-secondary"
                                onClick={() => goToMyOrders()}
                            >
                                {t("actions.myOrders")}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div className="row row-cols-auto d-flex align-items-center">
                <div className="col">
                    <p>{t("common.orderId")}: <strong>{order.id}</strong></p>
                </div>
                <div className="col">
                    <p>{t("common.creationDate")}: <strong>{creationDate}</strong></p>
                </div>
                <div className="col">
                    <p>
                        {t("common.state")}:
                        <span
                            className={`tag tag-sm status-value ms-2 ${order?.state?.toLowerCase()}`}>{statusLabel(order.state)}</span>
                    </p>
                </div>
            </div>
            <div className="row">
                <div className="col-8">
                    <div className="accordion" id="accordionExample">
                        <div className="accordion-item border-0">
                            {atomicOrderItems
                                .sort((a, b) =>
                                    (a.productOffering?.name || '').localeCompare(b.productOffering?.name || ''))
                                .map((item, index) => (
                                    <div className="accordion-header border-0" key={item.id}>
                                        <button className="accordion-button accordion-item-button collapsed"
                                                type="button"
                                                data-bs-toggle="collapse" data-bs-target={`#collapse${index}`}
                                                aria-expanded="false"
                                                aria-controls={`collapse${index}`}>
                                            {item.productOffering.name}

                                            <p className="mb-0 positioned-label d-flex align-items-center gap-2">
                                                <span
                                                    className={`tag tag-sm action-type ${getActionColor(item.action)} default`}>
                                                    <em className="icon-done_modifier action-icon"></em>
                                                    {formatActionName(item.action)}
                                                </span>
                                                <span
                                                    className={`tag tag-sm status-value m-0 ${item?.state?.toLowerCase()}`}>{statusLabel(item.state)}</span>
                                            </p>
                                        </button>
                                    </div>
                                ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

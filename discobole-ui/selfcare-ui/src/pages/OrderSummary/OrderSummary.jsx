// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";

import {useTitlePage} from "../../hooks";
import {Breadcrumb, Modal} from "../../components";
import ItemList from "./components/ItemList";
import OrderPreview from "./components/OrderPreview";
import CancelOrder from "./components/CancelOrder";

import {toggleLoading} from "../../store/actions/loadingActions";
import {setShippingPrice} from "../../store/actions/pricingActions";

import {fetchOrderDetails} from "./services/orderSummaryService";
import fetchAccessoryOrder from "./services/fetchAccessoryOrder";
import useTranslations from "../../utlis/i18n/useTranslations";

export default function OrderSummary() {
    useTitlePage("orderSummary");

    const [orderData, setOrderData] = useState(null);
    const [accessoryOrderData, setAccessoryOrderData] = useState(null);
    const [isOrderLoading, setIsOrderLoading] = useState(false);
    const [isCancelModalVisible, setIsCancelModalVisible] = useState(false);
    const [hasDataLoaded, setHasDataLoaded] = useState(false);

    const dispatch = useDispatch();
    const {productOrderId, orderType} = useSelector(state => state.order);
    const {planId} = useSelector(state => state.plan);

    const {t, tNotification} = useTranslations();

    const isAccessoryOrder = orderType === 'AcquisitionAccessory';

    const loadStandardOrder = useCallback(async () => {
        const orderDetails = await fetchOrderDetails(productOrderId, planId, orderType, tNotification);
        if (orderDetails) {
            if (orderDetails?.shippingPrice) {
                dispatch(setShippingPrice(orderDetails.shippingPrice));
            }
            setOrderData(orderDetails);
        }
    }, [productOrderId, planId, orderType]);

    const loadAccessoryOrder = useCallback(async () => {
        const accessoryData = await fetchAccessoryOrder(productOrderId, tNotification);
        if (accessoryData) {
            if (accessoryData?.shippingPrice) {
                dispatch(setShippingPrice(accessoryData.shippingPrice));
            }
            setAccessoryOrderData(accessoryData);
        }
    }, [productOrderId]);

    const loadOrderData = useCallback(async () => {
        if (isOrderLoading || hasDataLoaded) return;

        setIsOrderLoading(true);

        try {
            dispatch(toggleLoading(true));

            if (isAccessoryOrder) {
                await loadAccessoryOrder();
            } else {
                await loadStandardOrder();
            }

            setHasDataLoaded(true);
        } catch (error) {
            console.error('Failed to load order data:', error);
        } finally {
            setIsOrderLoading(false);
            dispatch(toggleLoading(false));
        }
    }, [isOrderLoading, hasDataLoaded, isAccessoryOrder, dispatch, loadAccessoryOrder, loadStandardOrder]);

    const toggleCancelModal = useCallback(() => {
        setIsCancelModalVisible(prev => !prev);
    }, []);

    useEffect(() => {
        if (productOrderId && !hasDataLoaded) {
            loadOrderData();
        }
    }, [productOrderId, hasDataLoaded, loadOrderData]);

    const renderItemList = (title, items) => {
        if (!items?.length) return null;

        return (
            <div className="mb-3">
                <ItemList title={title} items={items} t={t}/>
            </div>
        );
    };

    const getCurrentOrderData = () => {
        return isAccessoryOrder ? accessoryOrderData : orderData;
    };

    const getDeviceItems = (currentOrder) => {
        return isAccessoryOrder ? currentOrder?.items : currentOrder?.devicesItems;
    };

    if (!orderData && !accessoryOrderData) {
        return null;
    }

    const currentOrder = getCurrentOrderData();

    return (
        <div className="container">
            <Breadcrumb/>

            <div className="row">
                <div className="col-md-12">
                    <h1 className="banner-title mb-4">
                        {t("pages.orderSummary")}
                    </h1>
                </div>
            </div>

            <div className="row">
                <div className="col-lg-8 col-md-7">
                    {renderItemList(
                        t("order.summary.sections.includedItems"),
                        currentOrder?.mandatoryItems
                    )}

                    {renderItemList(
                        t("order.summary.sections.optionalItems"),
                        currentOrder?.optionalItems
                    )}

                    {renderItemList(
                        t("order.summary.sections.devices"),
                        getDeviceItems(currentOrder)
                    )}

                    {renderItemList(
                        t("order.summary.sections.terminatedItems"),
                        currentOrder?.deletedItems
                    )}
                </div>

                <OrderPreview order={currentOrder}/>
            </div>
            <Link
                to="#"
                className="btn btn-outline-secondary mt-2 mb-5"
                onClick={toggleCancelModal}
            >
                {t("actions.cancelOrder")}
            </Link>
            {isCancelModalVisible && (
                <Modal
                    show={isCancelModalVisible}
                    title={t("order.confirmations.cancel.title")}
                    body={<CancelOrder hideModal={toggleCancelModal}/>}
                    onClose={toggleCancelModal}
                    hideFooter={true}
                    className="CancelOrderModal"
                />
            )}
        </div>
    );
}
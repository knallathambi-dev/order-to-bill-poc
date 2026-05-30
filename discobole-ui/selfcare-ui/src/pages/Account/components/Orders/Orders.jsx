// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from 'react';
import {toast} from 'react-toastify';
import {useDispatch, useSelector} from 'react-redux';
import {Tooltip} from 'react-tooltip';
import './Orders.css';
import OrdersSkeleton from './components/ContentLoader/OrdersSkeleton';
import {fetchAllOrders} from './services/fetchOrders';
import {formatActionName, formatToLocalDateTime, getActionColor} from '../../../../utlis/helpers';
import {toggleLoading} from "../../../../store/actions/loadingActions";
import {ORDER_STATUSES} from "../../../../utlis/constants";
import {getCurrentPrices, getDisplayableOrderItems, renderPrices} from "../../../../utlis/utils";
import {calculatePricingForOrder} from "../../../OrderSummary/services/utils";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {ReloadButton} from "../../../../components/utils/ReloadButton";

function Orders() {
    const [orders, setOrders] = useState([]);
    const [orderDetails, setOrderDetails] = useState({});
    const [openAccordions, setOpenAccordions] = useState({});

    const dispatch = useDispatch();
    const {relatedParty} = useSelector((state) => state.auth);
    const {t, tNotification} = useTranslations();

    const handleOrdersError = useCallback(() => {
        setOrders([]);
        setOrderDetails({});
        toast.error(tNotification("myOrders.fetchOrdersFailed"));
    }, []);

    const getOrders = useCallback(async () => {
        if (!relatedParty.id) return;

        dispatch(toggleLoading(true));
        try {
            const orders = await fetchAllOrders(relatedParty.id, tNotification);

            if (!Array.isArray(orders) || orders.length === 0) {
                handleOrdersError();
                return;
            }

            setOrders(orders);

            const detailsMap = orders.reduce((acc, order) => {
                const atomicItems = getDisplayableOrderItems(order);

                acc[order.id] = {
                    ...order,
                    atomicOrderItems: atomicItems,
                    hasAtomicItems: atomicItems.length > 0
                };
                return acc;
            }, {});

            setOrderDetails(detailsMap);
        } catch {
            handleOrdersError();
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, relatedParty.id, handleOrdersError]);

    useEffect(() => {
        getOrders();
    }, [getOrders]);

    const toggleAccordion = useCallback((orderId, index) => {
        setOpenAccordions((prev) => ({
            ...prev,
            [index]: !prev[index],
        }));
    }, []);

    const groupOrderItems = useCallback((items) => {
        if (!items) return [];

        const groupedMap = new Map();

        items.forEach(item => {
            const productName = item.productOffering?.name;
            const state = item.state;
            const key = `${productName}::${state}`;

            if (!groupedMap.has(key)) {
                groupedMap.set(key, {
                    ...item,
                    quantity: 0,
                    itemPrice: [],
                    groupedItems: []
                });
            }

            const group = groupedMap.get(key);
            group.quantity += item.quantity;
            group.itemPrice = [...group.itemPrice, ...(item.itemPrice || [])];
            group.groupedItems.push(item);
        });

        return Array.from(groupedMap.values())
            .sort((a, b) => {
                const nameA = a.productOffering?.name || '';
                const nameB = b.productOffering?.name || '';
                return nameA.localeCompare(nameB);
            });
    }, []);

    const renderOrderItem = useCallback((item, itemIndex) => {
        const currentPrices = getCurrentPrices(item.itemPrice);

        return (
            <tr key={itemIndex}>
                <td style={{width: "40%"}}>{item.productOffering?.name}</td>
                <td style={{width: "15%"}}>
                    {item.quantity > 1 ? `Qty: ${item.quantity}` : ''}
                </td>
                <td style={{width: "15%"}}>
                    <span>
                        {currentPrices.map((priceObj, index) => (
                            <span key={index}>
                                {priceObj.totalPrice}
                                {priceObj.applicationDuration && (
                                    <small className="fw-bold">
                                        {" "}{priceObj.applicationDuration}
                                    </small>
                                )}
                                {index < currentPrices.length - 1 ? ' + ' : ''}
                            </span>
                        ))}
                    </span>
                </td>
                <td style={{width: "30%"}}>
                    <p className="d-flex align-items-center justify-content-end gap-2 mb-0">
                        <span className={`tag tag-sm action-type ${getActionColor(item.action)} default`}>
                            <em className="icon-done_modifier action-icon"></em>
                            {formatActionName(item.action)}
                        </span>
                        <span className={`tag tag-sm status-value ${item?.state?.toLowerCase()}`}>
                            {ORDER_STATUSES[item.state]}
                        </span>
                    </p>
                </td>
            </tr>
        );
    }, []);

    const renderOrderHeader = useCallback((order) => (
        <div className="row w-100">
            <div className="col-md-4">
                <div className="form-floating border-end border-1 border-light">
                    <input
                        type="text"
                        readOnly
                        className="form-control-plaintext"
                        value={t('common.creationDate')}
                    />
                    <label>
                        {order.creationDate && formatToLocalDateTime(order.creationDate)}
                    </label>
                </div>
            </div>
            <div className="col-md-4">
                <div className="form-floating border-end border-1 border-light">
                    <input
                        type="text"
                        readOnly
                        className="form-control-plaintext"
                        value={t('common.orderId')}
                    />
                    <label>{order.id}</label>
                </div>
            </div>
            <div className="col-md-4">
                <div className="form-floating">
                    <input
                        type="text"
                        readOnly
                        className="form-control-plaintext"
                        value={`${t('common.total')} (${t('common.taxIncluded')})`}
                    />
                    <label>
                        {(() => {
                            const pricing = calculatePricingForOrder(order);
                            return renderPrices(pricing.current, pricing.currency);
                        })()}
                    </label>
                </div>
            </div>
        </div>
    ), [t]);

    const validOrders = orders.filter(order => order.state !== 'draft');
    const isLoading = orders.length === 0;

    return (
        <>
            <Tooltip id="tooltip" className="tooltip" place="top"/>

            <div className="row">
                <div className="col-12">
                    <div className="d-flex justify-content-between align-items-center mt-2 mb-3">
                        <h3 className="mb-0">{t('pages.myOrders')}</h3>
                        <div className="btns-wrapper d-flex gap-2">
                            <ReloadButton onClick={getOrders}/>
                        </div>
                    </div>
                </div>
            </div>

            <div className="row">
                <div className="col-12">
                    {isLoading ? (
                        <OrdersSkeleton/>
                    ) : (
                        <div className="accordion" id="accordionExample">
                            {validOrders.map((order, orderIndex) => (
                                <div className="accordion-item order-item-card" key={order.id}>
                                    <div
                                        className="accordion-header border-0"
                                        id={`heading-${orderIndex}`}
                                        onClick={() => toggleAccordion(order.id, orderIndex)}
                                    >
                                        <div
                                            className={`accordion-button ${openAccordions[orderIndex] ? '' : 'collapsed'} order-item-wrapper`}
                                            type="button"
                                            aria-expanded={openAccordions[orderIndex]}
                                        >
                                            {renderOrderHeader(order)}
                                            <p className="mb-0">
                                                <span
                                                    className={`tag tag-sm status-value positioned-label ${order.state.toLowerCase()}`}>
                                                    {ORDER_STATUSES[order.state]}
                                                </span>
                                            </p>
                                        </div>
                                    </div>
                                    <div
                                        id={`collapse-${orderIndex}`}
                                        className={`accordion-collapse collapse ${openAccordions[orderIndex] ? 'show' : ''}`}
                                        data-bs-parent="#accordionExample"
                                    >
                                        <div className="accordion-body p-0">
                                            <hr className="my-0"/>
                                            <table className="table align-middle mb-0 order-items-table">
                                                <tbody>
                                                {groupOrderItems(orderDetails[order.id]?.atomicOrderItems)
                                                    .map(renderOrderItem)}
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}

export default Orders;
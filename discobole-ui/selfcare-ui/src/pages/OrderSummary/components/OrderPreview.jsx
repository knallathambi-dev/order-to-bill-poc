// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo, useState} from 'react';
import PropTypes from 'prop-types';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import {formatToLocalDateTime} from "../../../utlis/helpers";
import {Modal} from "../../../components";
import {toggleLoading} from "../../../store/actions/loadingActions";
import {
    setAppointmentRefItems,
    setBillingAccountRefItems,
    setPaymentRefItems
} from "../../../store/actions/orderCompletionActions";
import {updateNextTaskAction} from "../../../store/actions/taskManagerActions";
import {createValidateOrderRequest} from "../../../utlis/processFlowRequestBodyUtils";
import {ORDER_CAPTURE_SELECT_OFFER, ORDER_CAPTURE_VALIDATE_ORDER, ORDER_STATUSES} from "../../../utlis/constants";
import {getOrderPreviewPricing} from "../services/utils";
import {setPricing} from "../../../store/actions/pricingActions";
import PriceSummaryCard from "../../../components/utils/PriceSummaryCard";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {patchTaskFlow} from "../../../services";
import NotEligibleInstallment from "./NotEligibleInstallment";
import {hasErrorDescription} from "../../../utlis/utils";

const OrderState = ({state, t}) => (
    <div className="card mb-2">
        <ul className="list-group list-group-flush">
            <li className="list-group-item d-flex justify-content-between align-items-center fw-normal">
                <div className="me-auto">{t("common.state")}</div>
                <p className={`tag tag-sm status-value m-0 ${state.toLowerCase()}`}>
                    {ORDER_STATUSES[state]}
                </p>
            </li>
        </ul>
    </div>
);

OrderState.propTypes = {
    state: PropTypes.string.isRequired
};

const CreationDate = ({creationDate}) => {
    const {t} = useTranslations();

    return (
        <div className="card mb-3">
            <ul className="list-group list-group-flush">
                <li className="list-group-item d-flex justify-content-between align-items-center fw-normal">
                    <div className="me-auto">{t("common.creationDate")}</div>
                    <span className="fw-bold">{formatToLocalDateTime(creationDate)}</span>
                </li>
            </ul>
        </div>
    );
};

CreationDate.propTypes = {
    creationDate: PropTypes.string.isRequired
};

const OrderPriceDetails = ({order, pricing}) => {
    const showShipping = !!order?.shippingItem;

    const subTotalPrice = useMemo(() => {
        const baseNrc = Number(pricing?.current?.nrc || 0);
        const shippingNrcRaw = Number(order?.shippingPrice?.nrc || 0);
        const shippingNrc = shippingNrcRaw !== 0 ? shippingNrcRaw : 0;

        return {
            ...(pricing || {}),
            current: {
                ...(pricing?.current || {}),
                nrc: baseNrc - shippingNrc,
            },
        };
    }, [pricing, order?.shippingPrice?.nrc]);

    return (
        <PriceSummaryCard
            pricing={pricing}
            subtotalPrice={subTotalPrice}
            shippingPrice={order?.shippingPrice}
            showShipping={showShipping}
        />
    );
};

OrderPriceDetails.propTypes = {
    order: PropTypes.shape({
        shippingItem: PropTypes.any
    }),
    pricing: PropTypes.shape({
        current: PropTypes.object.isRequired,
        currency: PropTypes.string.isRequired
    }).isRequired,
    shippingPrice: PropTypes.number
};

const OrderCompletionInfo = ({requestedCompletionDate}) => {
    const {t} = useTranslations();

    return (
        <div className="alert alert-info align-items-center" role="alert">
            <span className="alert-icon">
                <span className="visually-hidden">{t("common.info")}</span>
            </span>
            <p>
                {t("order.statusUpdate.completedBy", {
                    date: formatToLocalDateTime(requestedCompletionDate)
                })}
            </p>
        </div>
    );
};

OrderCompletionInfo.propTypes = {
    requestedCompletionDate: PropTypes.string.isRequired
};

const isNotEligibleInstallmentResponse = (response) => {
    const description = response?.data?.description;
    if (!description || typeof description !== "string") return false;
    const nextTasks = response?.data?._links?.nextTaskstoBePerformed || [];
    const hasSelectOfferTask = nextTasks.some(
        (task) => task?.title === ORDER_CAPTURE_SELECT_OFFER
    );
    const isInstallmentMessage = /installment/i.test(description);
    return hasSelectOfferTask && isInstallmentMessage;
};

const OrderPreview = ({order}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const productOrderId = useSelector(state => state.order.productOrderId);
    const {relatedParty} = useSelector((state) => state.auth);
    const nextTaskToBePerformed = useSelector(state => state.taskManager.nextTaskToBePerformed);
    const {t, tNotification} = useTranslations();
    const pricing = getOrderPreviewPricing(order);

    const [showNotEligibleInstallmentModal, setShowNotEligibleInstallmentModal] = useState(false);
    const closeNotEligibleInstallmentModal = useCallback(() => setShowNotEligibleInstallmentModal(false), []);

    const extractCharacteristics = useCallback((nextTasks) => {
        const paymentRefItems = [];
        const billingAccountRefItems = [];
        const appointmentRefItems = [];
        const characteristics = nextTasks
            .filter(task => task.taskFlowSpecificationCharacteristic)
            .flatMap(task => task.taskFlowSpecificationCharacteristic);

        const characteristicMap = new Map(
            characteristics.map(characteristic => [characteristic.id, characteristic])
        );

        characteristics.forEach(characteristic => {
            if (characteristic.name === 'OrderItem') {
                const orderItemId = characteristic.characteristicValueSpecification?.[0]?.value;
                if (orderItemId) {
                    const relatedPaymentRef = characteristic.characteristicSpecificationRelationship
                        ?.map(rel => characteristicMap.get(rel.id))
                        .find(ref => ref?.name === 'PaymentRef');

                    const relatedBillingAccountRef = characteristic.characteristicSpecificationRelationship
                        ?.map(rel => characteristicMap.get(rel.id))
                        .find(ref => ref?.name === 'BillingAccountRef');

                    const relatedAppointmentRef = characteristic.characteristicSpecificationRelationship
                        ?.map(rel => characteristicMap.get(rel.id))
                        .find(ref => ref?.name === 'AppointmentRef');

                    if (relatedPaymentRef) {
                        paymentRefItems.push(orderItemId);
                    }
                    if (relatedBillingAccountRef) {
                        billingAccountRefItems.push(orderItemId);
                    }
                    if (relatedAppointmentRef) {
                        appointmentRefItems.push(orderItemId);
                    }
                }
            }
        });

        return {paymentRefItems, billingAccountRefItems, appointmentRefItems};
    }, []);

    const handleValidateOrderClick = async () => {
        try {
            dispatch(toggleLoading(true));

            const requestBody = createValidateOrderRequest(productOrderId, relatedParty);
            const response = await patchTaskFlow(nextTaskToBePerformed, requestBody);

            if (isNotEligibleInstallmentResponse(response)) {
                setShowNotEligibleInstallmentModal(true);
                return;
            }

            if (hasErrorDescription(response)) {
                toast.error(tNotification("orderSummary.validateOrderFailed"));
                return;
            }

            if (
                response.data._links.nextTaskstoBePerformed[1]?.title === ORDER_CAPTURE_VALIDATE_ORDER
            ) {
                toast.error(tNotification("orderSummary.validateOrderFailed"));
                return;
            }

            dispatch(setPricing(pricing));

            const nextTasks = response.data._links.nextTaskstoBePerformed || [];
            if (nextTasks.length === 0) {
                navigate("/orderStatusUpdate", {state: {orderStatus: "confirmed"}});
            } else {
                const {
                    paymentRefItems,
                    billingAccountRefItems,
                    appointmentRefItems
                } = extractCharacteristics(nextTasks);
                dispatch(setPaymentRefItems(paymentRefItems));
                dispatch(setBillingAccountRefItems(billingAccountRefItems));
                dispatch(setAppointmentRefItems(appointmentRefItems));
                dispatch(updateNextTaskAction(nextTasks[1].href));
                if (appointmentRefItems && appointmentRefItems.length > 0) {
                    navigate("/book-appointment");
                } else {
                    navigate("/complete-order");
                }
            }
        } catch {
            toast.error(tNotification("orderSummary.validateOrderFailed"));
        } finally {
            dispatch(toggleLoading(false));
        }
    };

    if (!order) return null;

    return (
        <div className="col-12 col-lg-4 col-md-5">
            <h5>{t("common.orderPreview")}</h5>
            <div className="card mb-5">
                <div className="card-body p-6">
                    {order.requestedCompletionDate && (
                        <OrderCompletionInfo requestedCompletionDate={order.requestedCompletionDate}/>
                    )}
                    <OrderState state={order.state} t={t}/>
                    <CreationDate creationDate={order.creationDate}/>
                    <OrderPriceDetails
                        order={order}
                        pricing={pricing}
                    />
                    <div className="d-grid mb-2 mt-4">
                        <button onClick={() => handleValidateOrderClick()} className="btn btn-primary btn-lg"
                                type="button">
                            {t("actions.confirmOrder")}
                        </button>
                    </div>
                    <p className="mb-0">
                        <small>
                            {t("common.confirmPlacement")}
                        </small>
                    </p>
                </div>
            </div>
            {showNotEligibleInstallmentModal && (
                <Modal
                    show={showNotEligibleInstallmentModal}
                    body={<NotEligibleInstallment hideModal={closeNotEligibleInstallmentModal}/>}
                    onClose={closeNotEligibleInstallmentModal}
                    hideFooter={true}
                />
            )}
        </div>
    );
};

OrderPreview.propTypes = {
    order: PropTypes.shape({
        state: PropTypes.string.isRequired,
        creationDate: PropTypes.string.isRequired,
        nrcPrices: PropTypes.number,
        rcPriceArray: PropTypes.arrayOf(PropTypes.shape({
            rcPrice: PropTypes.number,
            recurringChargePeriod: PropTypes.string
        })),
        shippingPrice: PropTypes.object,
        orderTotalPrice: PropTypes.arrayOf(PropTypes.shape({
            priceType: PropTypes.string,
            price: PropTypes.shape({
                taxIncludedAmount: PropTypes.shape({
                    value: PropTypes.number
                })
            }),
            priceAlteration: PropTypes.arrayOf(PropTypes.shape({
                price: PropTypes.shape({
                    taxIncludedAmount: PropTypes.shape({
                        value: PropTypes.number
                    })
                })
            })),
            recurringChargePeriod: PropTypes.object
        }))
    }).isRequired
};

export default OrderPreview;
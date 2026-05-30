// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import React, {useCallback, useMemo} from "react";
import {toggleLoading} from "../../../store/actions/loadingActions";
import PriceSummaryCard from "../../../components/utils/PriceSummaryCard";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {submitOrder} from "../../../services";
import {getBillingAccountId} from "../../../utlis/utils";

const OrderPreview = ({isOrderAppointmentValid}) => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();

    const {pricing, shippingPrice} = useSelector((state) => state.pricing);
    const orderCompletion = useSelector((state) => state.orderCompletion);
    const {relatedParty} = useSelector((state) => state.auth);
    const {nextTaskToBePerformed} = useSelector((state) => state.taskManager);
    const {itemsRequiringShipping = []} = useSelector((state) => state.itemsRequiringShipping);
    const {billingAccountId: storeBillingId,} = useSelector((s) => s.orderCompletion);

    const {
        paymentRefItems = [],
        billingAccountRefItems = [],
        appointmentRefItems = []
    } = orderCompletion;

    const billingAccountId = useMemo(() => {
        return getBillingAccountId(dispatch, relatedParty, storeBillingId);
    }, [dispatch, relatedParty, storeBillingId]);

    const subTotalPrice = useMemo(() => ({
        ...pricing,
        current: {
            ...pricing.current,
            nrc: pricing.current.nrc - (shippingPrice?.nrc || 0),
        },
    }), [pricing, shippingPrice]);

    const hasPaymentSources = useMemo(() =>
            paymentRefItems.length > 0 || billingAccountRefItems.length > 0,
        [paymentRefItems.length, billingAccountRefItems.length]
    );

    const showShipping = useMemo(() =>
            itemsRequiringShipping.length > 0,
        [itemsRequiringShipping.length]
    );

    const buttonText = useMemo(() =>
            hasPaymentSources ? t('actions.continueToPayment') : t('actions.placeOrder'),
        [hasPaymentSources, t]
    );

    const handleCompleteOrder = useCallback(async () => {
        await submitOrder({
            paymentRefItems,
            billingAccountRefItems,
            billingAccountId,
            appointmentRefItems,
            relatedParty,
            nextTaskToBePerformed,
            navigate,
            dispatch,
            toggleLoading,
            tNotification
        });
    }, [
        paymentRefItems,
        billingAccountRefItems,
        billingAccountId,
        appointmentRefItems,
        relatedParty,
        nextTaskToBePerformed,
        navigate,
        dispatch
    ]);

    const handleContinueToPayment = useCallback(() => {
        if (hasPaymentSources) {
            navigate('/complete-order');
        } else {
            handleCompleteOrder();
        }
    }, [hasPaymentSources, navigate, handleCompleteOrder]);

    const renderStatusCard = () => (
        <div className="card mb-2">
            <ul className="list-group list-group-flush">
                <li className="list-group-item d-flex justify-content-between align-items-center fw-normal">
                    <div className="me-auto">
                        <div>{t('common.state')}</div>
                    </div>
                    <p className="tag tag-sm status-value m-0 acknowledged">
                        Acknowledged
                    </p>
                </li>
            </ul>
        </div>
    );

    const renderActionButton = () => (
        <div className="d-grid mb-2 mt-4">
            <button
                className="btn btn-primary btn-lg"
                type="button"
                disabled={!isOrderAppointmentValid}
                onClick={handleContinueToPayment}
            >
                {buttonText}
            </button>
        </div>
    );

    const renderConfirmationText = () => (
        <p className="mb-0">
            <small>{t('common.confirmPlacement')}</small>
        </p>
    );

    return (
        <div className="col-12 col-lg-4 col-md-5">
            <div className="mb-5 card mt-6">
                <div className="card-body p-6">
                    {renderStatusCard()}

                    <PriceSummaryCard
                        pricing={pricing}
                        subtotalPrice={subTotalPrice}
                        shippingPrice={shippingPrice}
                        showShipping={showShipping}
                    />

                    {renderActionButton()}
                    {renderConfirmationText()}
                </div>
            </div>
        </div>
    );
};

export default OrderPreview;
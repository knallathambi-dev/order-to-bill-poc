// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

import PriceSummaryCard from "../../../components/utils/PriceSummaryCard";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {submitOrder} from "../../../services";
import {toggleLoading} from "../../../store/actions/loadingActions";
import {getBillingAccountId} from "../../../utlis/utils";
import {ORDER_STATUSES} from "../../../utlis/constants";

function OrderPreview({isOrderCompletionValid, order}) {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const {pricing, shippingPrice} = useSelector((state) => state.pricing);
    const {relatedParty} = useSelector((state) => state.auth);
    const {
        paymentRefItems,
        billingAccountRefItems,
        appointmentRefItems,
        billingAccountId: storeBillingId,
    } = useSelector((state) => state.orderCompletion);
    const {nextTaskToBePerformed} = useSelector((state) => state.taskManager);
    const {itemsRequiringShipping = []} = useSelector((state) => state.itemsRequiringShipping);

    const {t, tNotification} = useTranslations();

    const billingAccountId = useMemo(() => {
        return getBillingAccountId(dispatch, relatedParty, storeBillingId);
    }, [dispatch, relatedParty, storeBillingId]);

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
            tNotification,
        });
    }, [
        paymentRefItems,
        billingAccountRefItems,
        billingAccountId,
        appointmentRefItems,
        relatedParty,
        nextTaskToBePerformed,
        navigate,
        dispatch,
    ]);

    const subTotalPrice = useMemo(() => {
        const shippingNrc = shippingPrice?.nrc && shippingPrice.nrc !== 0 ? shippingPrice.nrc : 0;
        return {
            ...pricing,
            current: {
                ...pricing.current,
                nrc: pricing.current.nrc - shippingNrc,
            },
        };
    }, [pricing, shippingPrice]);

    return (
        <div className="col-12 col-lg-4 col-md-5">
            <h4 className="mb-3">{t("common.orderPreview")}</h4>
            <div className="mb-5 card mt-6">
                <div className="card-body p-6">
                    <div className="card mb-2">
                        <ul className="list-group list-group-flush">
                            <li className="list-group-item d-flex justify-content-between align-items-center fw-normal">
                                <div className="me-auto">
                                    <div>{t("common.state")}</div>
                                </div>
                                <p className={`tag tag-sm status-value m-0 ${order?.state.toLowerCase()}`}>
                                    {ORDER_STATUSES[order?.state]}
                                </p>
                            </li>
                        </ul>
                    </div>

                    <PriceSummaryCard
                        pricing={pricing}
                        subtotalPrice={subTotalPrice}
                        shippingPrice={shippingPrice}
                        showShipping={itemsRequiringShipping.length > 0}
                    />

                    <div className="d-grid mb-2 mt-4">
                        <button
                            className="btn btn-primary btn-lg"
                            type="button"
                            disabled={!isOrderCompletionValid}
                            onClick={handleCompleteOrder}
                        >
                            {t("actions.placeOrder")}
                        </button>
                    </div>
                    <p className="mb-0">
                        <small>{t("common.confirmPlacement")}</small>
                    </p>
                </div>
            </div>
        </div>
    );
}

export default OrderPreview;
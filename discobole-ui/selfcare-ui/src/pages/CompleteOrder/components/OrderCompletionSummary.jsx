// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useMemo, useState} from "react";
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {toast} from "react-toastify";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCreditCard, faWallet} from "@fortawesome/free-solid-svg-icons";
import {Modal} from "../../../components";
import CancelOrder from "../../OrderSummary/components/CancelOrder";
import CreditCard from "./PaymentMethods/CreditCard";
import EWallet from "./PaymentMethods/EWallet";
import OrderCompletionSummarySkeleton from "./OrderCompletionSummarySkeleton";
import {formatAmount, formatRecurringChargePeriod, getCurrencySymbol, roundAmount} from "../../../utlis/helpers";
import {INSTALLMENT_CHARGE_TYPE, SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE} from "../../../utlis/constants";
import {getBillingAccountId, getCurrentPrices} from "../../../utlis/utils";
import {computeInstallmentPricing} from "../../Plan/shared/services/priceService";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {apiClient, fetchOrder} from "../../../services";
import {setRelatedParty} from "../../../store/actions/authActions";

const INSTALLMENT_DISPLAY = {
    MONTHLY: "monthly",
    UPFRONT: "upfront",
};

const getInstallmentEntry = (itemPrices) => {
    const prices = Array.isArray(itemPrices) ? itemPrices : [];
    const entry = prices.find((ip) => ip?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE);
    return entry ? {entry, offeringPrice: entry.productOfferingPrice} : null;
};

const ItemPriceRow = ({item, installmentDisplay = INSTALLMENT_DISPLAY.MONTHLY, t}) => {
    const installment = getInstallmentEntry(item?.itemPrice);

    if (installment) {
        const {entry, offeringPrice} = installment;

        if (installmentDisplay === INSTALLMENT_DISPLAY.MONTHLY) {
            const values = computeInstallmentPricing({price: entry.price, productOfferingPrice: offeringPrice});
            if (values) {
                const {totalPayment, downPayment, currency, duration, durationUnit} = values;
                const financedAmount = totalPayment - (downPayment || 0);
                const durationLabel = `${duration} ${duration === 1 ? durationUnit.replace(/s$/, '') : durationUnit}`;
                const currencySymbol = getCurrencySymbol(currency);
                return (
                    <li className="list-group-item d-flex justify-content-between align-items-start">
                        <div className="me-auto">{item.productOffering?.name}</div>
                        <span className="fw-bold">
                            <small className="text-muted me-1">
                                ( {t("common.installment.labels.installmentPeriod")} {durationLabel} )
                            </small>
                            {currencySymbol} {formatAmount(financedAmount, currency)}
                        </span>
                    </li>
                );
            }
        }

        if (installmentDisplay === INSTALLMENT_DISPLAY.UPFRONT) {
            const downPayment = typeof offeringPrice?.downPayment === "number" ? offeringPrice.downPayment : null;
            if (downPayment != null) {
                const currency = entry?.price?.taxIncludedAmount?.unit || offeringPrice?.price?.unit;
                const currencySymbol = getCurrencySymbol(currency);
                return (
                    <li className="list-group-item d-flex justify-content-between align-items-start">
                        <div className="me-auto">{item.productOffering?.name}</div>
                        <span className="fw-bold">
                            <small
                                className="text-muted me-1">( {t("common.installment.labels.initialPayment")} )</small>
                            {currencySymbol} {formatAmount(downPayment, currency)}
                        </span>
                    </li>
                );
            }
        }
    }

    const currentPrices = getCurrentPrices(Array.isArray(item?.itemPrice) ? item.itemPrice : []);
    return (
        <li className="list-group-item d-flex justify-content-between align-items-start">
            <div className="me-auto">{item.productOffering?.name}</div>
            <span className="fw-bold">
                {currentPrices.map((priceObj, i) => (
                    <span key={i}>
                        {priceObj.totalPrice}
                        {priceObj.applicationDuration &&
                            <small className="fw-bold"> {priceObj.applicationDuration}</small>}
                        {i < currentPrices.length - 1 ? " + " : ""}
                    </span>
                ))}
            </span>
        </li>
    );
};

const computeInstallmentAmount = (priceEntry, installmentDisplay) => {
    const offeringPrice = priceEntry?.productOfferingPrice;
    const values = computeInstallmentPricing({
        price: priceEntry.price,
        productOfferingPrice: offeringPrice,
    });
    const totalPayment = values?.totalPayment ?? priceEntry?.price?.taxIncludedAmount?.value ?? 0;
    const downPayment = values?.downPayment ?? offeringPrice?.downPayment ?? 0;

    switch (installmentDisplay) {
        case INSTALLMENT_DISPLAY.UPFRONT:
            return downPayment;
        case INSTALLMENT_DISPLAY.MONTHLY:
            return totalPayment - downPayment;
        default:
            return totalPayment;
    }
};

const formatTotalPrices = (items, installmentDisplay) => {
    if (!Array.isArray(items) || items.length === 0) return "";

    const recurringTotals = new Map();
    let oneTimeTotal = 0;
    let currency = "";

    for (const item of items) {
        const itemPrices = Array.isArray(item?.itemPrice) ? item.itemPrice : [];

        for (const priceEntry of itemPrices) {
            const isInstallment = priceEntry?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE;
            const amount = isInstallment
                ? computeInstallmentAmount(priceEntry, installmentDisplay)
                : priceEntry?.price?.taxIncludedAmount?.value;

            if (typeof amount !== "number" || isNaN(amount) || amount < 0) continue;

            if (!currency) {
                currency = priceEntry?.price?.taxIncludedAmount?.unit || priceEntry?.productOfferingPrice?.price?.unit || "";
            }

            const period = priceEntry?.recurringChargePeriod;
            if (period) {
                const periodKey = formatRecurringChargePeriod(period);
                recurringTotals.set(periodKey, roundAmount((recurringTotals.get(periodKey) || 0) + amount, currency));
            } else {
                oneTimeTotal = roundAmount(oneTimeTotal + amount, currency);
            }
        }
    }

    const currencySymbol = getCurrencySymbol(currency);
    const parts = [];
    recurringTotals.forEach((total, period) => parts.push(`${currencySymbol} ${formatAmount(total, currency)}/${period}`));
    if (oneTimeTotal > 0) parts.push(`${currencySymbol} ${formatAmount(oneTimeTotal, currency)}`);

    return parts.join(" + ");
};

const BillingSection = ({recurringItems, billingAccountId, t}) => (
    <div className="accordion-item">
        <h4 className="accordion-header border-top-0">
            <button
                className="accordion-button accordion-button-payment"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#collapseBilling"
                aria-expanded="true"
            >
                {t("payment.billingDetails")}
            </button>
        </h4>
        <div id="collapseBilling" className="accordion-collapse collapse show">
            <div className="accordion-body pt-0">
                <div className="billing-section">
                    <div className="mb-3">
                        <div className="card">
                            <div className="card-body d-flex justify-content-between align-items-center">
                                <div>
                                    {t("payment.billedOn")}{" "}
                                    <strong className="text-primary">{billingAccountId || ""}</strong>{" "}
                                    {t("payment.account")}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="mb-3">
                        <h6 className="mb-2">{t("payment.billableItems")}</h6>
                        <div className="card">
                            <ul className="list-group list-group-flush">
                                {[...recurringItems]
                                    .sort((a, b) => (a.productOffering?.name || "").localeCompare(b.productOffering?.name || ""))
                                    .map((item) => (
                                        <ItemPriceRow
                                            key={item.id}
                                            item={item}
                                            installmentDisplay={INSTALLMENT_DISPLAY.MONTHLY}
                                            t={t}
                                        />
                                    ))
                                }
                                <li className="list-group-item d-flex justify-content-between align-items-start">
                                    <div className="me-auto">{t("common.total")}</div>
                                    <span>{formatTotalPrices(recurringItems, INSTALLMENT_DISPLAY.MONTHLY)}</span>
                                </li>
                            </ul>
                        </div>
                        <div className="alert alert-info alert-sm" role="alert">
                            <span className="alert-icon"><span className="visually-hidden">Info</span></span>
                            <p>
                                {t("payment.billableItemsNote")} {billingAccountId || ""} {t("payment.byEndOfMonth")}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
);

const PaymentSection = ({
                            immediatePurchaseItems,
                            hasRecurringItems,
                            activeTab,
                            onTabClick,
                            cardNumber, setCardNumber,
                            cvv, setCvv,
                            cardName, setCardName,
                            email, setEmail,
                            password, setPassword,
                            setIsCreditCardValid,
                            setIsEWalletValid,
                            isPaymentValid,
                            isSaveDisabled,
                            onSavePaymentMethod,
                            t,
                        }) => (
    <>
        <div className="accordion-item border-bottom-0">
            <h4 className={hasRecurringItems ? "accordion-header" : "accordion-header border-top-0"}>
                <button
                    className={`accordion-button accordion-button-payment ${hasRecurringItems ? "pt-3" : ""}`}
                    data-bs-toggle="collapse"
                    data-bs-target="#collapsePayment"
                    aria-expanded="true"
                >
                    {t("payment.paymentDetails")}
                </button>
            </h4>
            <div id="collapsePayment" className="accordion-collapse collapse show">
                <div className="accordion-body pt-0">
                    <div className="payment-section">
                        <h6 className="mb-2">{t("payment.purchasedItems")}</h6>
                        <div className="card">
                            <ul className="list-group list-group-flush">
                                {[...immediatePurchaseItems]
                                    .sort((a, b) => (a.productOffering?.name || "").localeCompare(b.productOffering?.name || ""))
                                    .map((item) => (
                                        <ItemPriceRow
                                            key={item.id}
                                            item={item}
                                            installmentDisplay={INSTALLMENT_DISPLAY.UPFRONT}
                                            t={t}
                                        />
                                    ))
                                }
                                <li className="list-group-item d-flex justify-content-between align-items-start">
                                    <div className="me-auto">{t("common.total")}</div>
                                    <span>{formatTotalPrices(immediatePurchaseItems, INSTALLMENT_DISPLAY.UPFRONT)}</span>
                                </li>
                            </ul>
                        </div>
                        <div className="alert alert-info alert-sm" role="alert">
                            <span className="alert-icon"><span className="visually-hidden">Info</span></span>
                            <p>{t("payment.purchasedItemsNote")}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div className="select-payment">
            <h6 className="mb-2">{t("payment.selectPaymentMethod")}</h6>
            <ul className="nav nav-pills nav-fill mb-3 gap-2" role="tablist">
                <li className="nav-item" role="presentation">
                    <button
                        className={`nav-link d-flex justify-content-center ${activeTab === "creditCard" ? "active" : ""}`}
                        type="button"
                        role="tab"
                        onClick={() => onTabClick("creditCard")}
                    >
                        <FontAwesomeIcon icon={faCreditCard} beatFade={activeTab === "creditCard"}/>
                        <span className="m-xl-1">{t("payment.methods.creditCard")}</span>
                    </button>
                </li>
                <li className="nav-item" role="presentation">
                    <button
                        className={`nav-link d-flex justify-content-center ${activeTab === "eWallet" ? "active" : ""}`}
                        type="button"
                        role="tab"
                        onClick={() => onTabClick("eWallet")}
                    >
                        <FontAwesomeIcon icon={faWallet} beatFade={activeTab === "eWallet"}/>
                        <span className="m-xl-1">{t("payment.methods.eWallet")}</span>
                    </button>
                </li>
            </ul>

            <div className="tab-content">
                <div className={`tab-pane fade ${activeTab === "creditCard" ? "show active" : ""}`}>
                    <CreditCard
                        cardNumber={cardNumber} setCardNumber={setCardNumber}
                        cvv={cvv} setCvv={setCvv}
                        cardName={cardName} setCardName={setCardName}
                        onValidationChange={setIsCreditCardValid}
                    />
                </div>
                <div className={`tab-pane fade ${activeTab === "eWallet" ? "show active" : ""}`}>
                    <EWallet
                        email={email} setEmail={setEmail}
                        password={password} setPassword={setPassword}
                        onValidationChange={setIsEWalletValid}
                    />
                </div>
            </div>

            <div className="d-grid mt-3">
                <button
                    className="btn btn-secondary"
                    type="button"
                    onClick={onSavePaymentMethod}
                    disabled={!isPaymentValid || isSaveDisabled}
                >
                    {t("payment.savePaymentMethod")}
                </button>
            </div>
        </div>
    </>
);

function OrderCompletionSummary({setOrderCompletionStatus, setOrder}) {
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();

    const [activeTab, setActiveTab] = useState("creditCard");
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [order, setLocalOrder] = useState(null);
    const [isSaveDisabled, setIsSaveDisabled] = useState(false);
    const [isLoadingOrder, setIsLoadingOrder] = useState(true);
    const [cardNumber, setCardNumber] = useState("");
    const [cardName, setCardName] = useState("");
    const [cvv, setCvv] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isCreditCardValid, setIsCreditCardValid] = useState(false);
    const [isEWalletValid, setIsEWalletValid] = useState(false);

    const {productOrderId} = useSelector((s) => s.order);
    const {
        paymentRefItems,
        billingAccountRefItems,
        billingAccountId: storeBillingId
    } = useSelector((s) => s.orderCompletion);
    const {email: userEmail, relatedParty} = useSelector((s) => s.auth);

    const recurringItems = useMemo(
        () => order?.productOrderItem?.filter((i) => billingAccountRefItems.includes(i.id)) || [],
        [order, billingAccountRefItems],
    );

    const immediatePurchaseItems = useMemo(() => {
        const items = order?.productOrderItem || [];
        const isNotShipping = (i) =>
            i.product?.productSpecification?.["@baseType"] !== SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE;

        const byPaymentRef = items.filter((i) => paymentRefItems.includes(i.id) && isNotShipping(i));
        const withInstallment = items.filter(
            (i) => !paymentRefItems.includes(i.id) && isNotShipping(i) &&
                (i.itemPrice || []).some((ip) => ip?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE)
        );
        return [...byPaymentRef, ...withInstallment];
    }, [order, paymentRefItems]);

    const isPaymentValid = activeTab === "creditCard" ? isCreditCardValid : isEWalletValid;

    const billingAccountId = useMemo(
        () => getBillingAccountId(dispatch, relatedParty, storeBillingId),
        [dispatch, relatedParty, storeBillingId],
    );

    useEffect(() => {
        if (!productOrderId) return;
        if (order?.id === productOrderId) {
            setIsLoadingOrder(false);
            return;
        }

        setIsLoadingOrder(true);

        fetchOrder(productOrderId, tNotification)
            .then(async (fetched) => {
                setLocalOrder(fetched);
                setOrder(fetched);

                const orderRole = fetched?.relatedParty[0]?.role;
                if (userEmail && orderRole === "customer" && relatedParty?.role !== orderRole) {
                    try {
                        await apiClient.put("/auth/related-party-role", {
                            email: userEmail,
                            relatedPartyRole: orderRole,
                        });
                        dispatch(setRelatedParty({...relatedParty, role: orderRole}));
                    } catch {
                        toast.error(tNotification("completeOrder.completeFailed"));
                    }
                }
            })
            .catch(() => toast.error(tNotification("common.fetchOrderFailed")))
            .finally(() => setIsLoadingOrder(false));
    }, [productOrderId, order?.id, setOrder, userEmail, relatedParty?.role]);

    useEffect(() => {
        if (recurringItems.length > 0 && immediatePurchaseItems.length === 0) {
            setOrderCompletionStatus(true);
        }
    }, [recurringItems.length, immediatePurchaseItems.length, setOrderCompletionStatus]);

    const handleTabClick = useCallback((tab) => {
        if (tab === activeTab) return;
        if (tab === "creditCard") {
            setEmail("");
            setPassword("");
            setIsEWalletValid(false);
        } else {
            setCardNumber("");
            setCardName("");
            setCvv("");
            setIsCreditCardValid(false);
        }
        setActiveTab(tab);
    }, [activeTab]);

    const handleSavePaymentMethod = useCallback(() => {
        if (!isPaymentValid) return;
        setOrderCompletionStatus(true);
        setIsSaveDisabled(true);
    }, [isPaymentValid, setOrderCompletionStatus]);

    if (isLoadingOrder) {
        return <OrderCompletionSummarySkeleton activeTab={activeTab}/>;
    }

    return (
        <div className="col-12 col-md-7 col-lg-8">
            <div className="accordion accordion-sm included-accordion" id="accordionPayment">
                {recurringItems.length > 0 && (
                    <BillingSection
                        recurringItems={recurringItems}
                        billingAccountId={billingAccountId}
                        t={t}
                    />
                )}

                {immediatePurchaseItems.length > 0 && recurringItems.length > 0 && <hr className="m-0"/>}

                {immediatePurchaseItems.length > 0 && (
                    <PaymentSection
                        immediatePurchaseItems={immediatePurchaseItems}
                        hasRecurringItems={recurringItems.length > 0}
                        activeTab={activeTab}
                        onTabClick={handleTabClick}
                        cardNumber={cardNumber} setCardNumber={setCardNumber}
                        cvv={cvv} setCvv={setCvv}
                        cardName={cardName} setCardName={setCardName}
                        email={email} setEmail={setEmail}
                        password={password} setPassword={setPassword}
                        setIsCreditCardValid={setIsCreditCardValid}
                        setIsEWalletValid={setIsEWalletValid}
                        isPaymentValid={isPaymentValid}
                        isSaveDisabled={isSaveDisabled}
                        onSavePaymentMethod={handleSavePaymentMethod}
                        t={t}
                    />
                )}
            </div>

            <div className="mt-1 mb-5">
                <hr className="mt-4"/>
                <Link to="#" className="btn btn-outline-secondary mt-2 mb-5" onClick={() => setIsModalVisible(true)}>
                    {t("actions.cancelOrder")}
                </Link>
                {isModalVisible && (
                    <Modal
                        show={isModalVisible}
                        title={t("order.confirmations.cancel.title")}
                        body={<CancelOrder hideModal={() => setIsModalVisible(false)}/>}
                        onClose={() => setIsModalVisible(false)}
                        hideFooter
                        className="CancelOrderModal"
                    />
                )}
            </div>
        </div>
    );
}

export default OrderCompletionSummary;
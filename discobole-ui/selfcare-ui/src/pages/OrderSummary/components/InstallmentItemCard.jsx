// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import PropTypes from 'prop-types';
import {
    formatActionName,
    formatAmount,
    formatToLocalDateTime,
    getActionColor,
    getCurrencySymbol
} from "../../../utlis/helpers";
import {INSTALLMENT_CHARGE_TYPE, ORDER_STATUSES} from "../../../utlis/constants";
import {getFormattedValue} from "../../../utlis/utils";

const EXCLUDED_CHARACTERISTICS = ["shipping address", "requested delivery date", "shipping mode"];

export const computeInstallmentPricing = (itemPrices) => {
    if (!Array.isArray(itemPrices)) return null;

    const installmentEntry = itemPrices.find(
        (entry) => entry?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE
    );
    if (!installmentEntry) return null;

    const installmentOffer = installmentEntry.productOfferingPrice;
    const currency = installmentEntry?.price?.taxIncludedAmount?.unit;
    const baseAmount = installmentEntry?.price?.taxIncludedAmount?.value;
    const interestRate = installmentOffer?.interestRate;
    const downPayment = installmentOffer?.downPayment;
    const duration = installmentOffer?.applicationDuration?.amount;
    const durationUnit = installmentOffer?.applicationDuration?.units;

    const totalAmount = baseAmount * (1 + interestRate / 100);
    const financedAmount = totalAmount - downPayment;
    const monthlyAmount = duration > 0 ? financedAmount / duration : 0;
    const periodSuffix = durationUnit ? `/${durationUnit}` : '';

    return {
        currency,
        downPayment,
        monthlyAmount,
        totalAmount,
        periodSuffix,
    };
};

const formatPrice = (amount, currency, suffix = '') => (
    `${getCurrencySymbol(currency)} ${formatAmount(amount, currency)}${suffix}`
);

const formatCharacteristic = (characteristic, t) => {
    if (typeof characteristic.value === 'object') {
        if ('validTo' in characteristic.value) return ` Valid to ${formatToLocalDateTime(characteristic.value.validTo)}`;
        if ('value' in characteristic.value) {
            const unit = characteristic.value.unitOfMeasure ?? '';
            return ` ${characteristic.value.value} ${unit}`;
        }
        return '';
    }
    return ` ${getFormattedValue(characteristic.value, t)}`;
};

const InstallmentItemCard = ({productOrderItem, t, renderProductCharacteristics}) => {
    const pricing = computeInstallmentPricing(productOrderItem?.itemPrice);
    if (!pricing) return null;

    const {currency, downPayment, monthlyAmount, totalAmount, periodSuffix} = pricing;

    const characteristics = (productOrderItem?.product?.productCharacteristic ?? []).filter(
        (c) => c.value && !EXCLUDED_CHARACTERISTICS.includes((c.name || '').toLowerCase())
    );

    return (
        <div className="card mb-2">
            <div className="card-body">
                <div className="row align-items-center">
                    <div className="col-8 col-md-8 col-lg-8">
                        <h6 className="mb-1 d-flex align-items-center text-nowrap">
                            {productOrderItem.productOffering?.name}
                            <span className={`tag tag-sm status-value ms-2 ${productOrderItem.state.toLowerCase()}`}>
                                {ORDER_STATUSES[productOrderItem.state]}
                            </span>
                        </h6>
                        {characteristics.map((characteristic, index) => (
                            <div key={index}>
                                <small className="text-muted">
                                    {characteristic.name}:{formatCharacteristic(characteristic, t)}
                                </small>
                            </div>
                        ))}
                        {renderProductCharacteristics?.(productOrderItem?.product)}
                        <p className={`mb-0 mt-2 tag tag-sm action-type ${getActionColor(productOrderItem?.action)}`}>
                            <em className="icon-modifier_add action-icon"></em>
                            {formatActionName(productOrderItem?.action)}
                        </p>
                    </div>
                    <div className="col-4 text-lg-end text-start text-md-end col-md-4">
                        <ul className="list-group list-group-flush">
                            <li className="list-group-item d-flex justify-content-between align-items-center p-0 border-0 fw-bold">
                                <div className="me-auto">{t("common.installment.labels.initialPayment")}</div>
                                <div>{formatPrice(downPayment, currency)}</div>
                            </li>
                            <li className="list-group-item d-flex justify-content-between align-items-center p-0 border-0 small text-muted">
                                <div className="me-auto">
                                    <small>{t("common.installment.labels.estimatedInstallments")}</small>
                                </div>
                                <div>
                                    <small>
                                        {formatPrice(monthlyAmount, currency, periodSuffix)}
                                    </small>
                                </div>
                            </li>
                            <li className="list-group-item d-flex justify-content-between align-items-center p-0 border-0 small text-muted">
                                <div className="me-auto">
                                    <small>{t("common.installment.labels.estimatedTotalPrice")}</small>
                                </div>
                                <div>
                                    <small>{formatPrice(totalAmount, currency)}</small>
                                </div>
                            </li>
                        </ul>
                        <div className="alert alert-info alert-sm" role="alert">
                            <span className="alert-icon"><span className="visually-hidden">Info</span></span>
                            <p className="text-start mb-0">{t("common.installment.info.paymentNote")}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

InstallmentItemCard.propTypes = {
    productOrderItem: PropTypes.object.isRequired,
    t: PropTypes.func.isRequired,
    renderProductCharacteristics: PropTypes.func
};

export default InstallmentItemCard;
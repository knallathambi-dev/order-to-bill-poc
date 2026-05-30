// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import PropTypes from 'prop-types';
import {formatActionName, formatToLocalDateTime, getActionColor} from "../../../utlis/helpers";
import {ADDRESS_CHARACTERISTIC_TYPE, ORDER_STATUSES, PARTNER_CHARACTERISTIC_NAME} from "../../../utlis/constants";
import {getCurrentPrices, getFormattedValue, hasInstallment} from "../../../utlis/utils";
import useTranslations from "../../../utlis/i18n/useTranslations";
import InstallmentItemCard from "./InstallmentItemCard";

const ProductCharacteristics = ({product}) => {
    const {t} = useTranslations();

    if (!product?.productCharacteristic) return null;
    const characteristics = product.productCharacteristic;
    const shippingAddress = characteristics.find(
        (char) => char.name.toLowerCase() === "shipping address"
    )?.value;

    const requestDeliveryDate = characteristics.find(
        (char) => char.name.toLowerCase() === "requested delivery date"
    )?.value;

    const addressCharacteristic = characteristics.find(
        (char) => char['@type'] === ADDRESS_CHARACTERISTIC_TYPE
    );

    if (addressCharacteristic) {
        const {
            name,
            subUnitNumber,
            streetName,
            city,
            country,
            postcode,
        } = addressCharacteristic;

        const addressLine = `${subUnitNumber && subUnitNumber.trim() !== '' ? `${subUnitNumber} ` : ''}${streetName}, ${city}, ${country} - ${postcode}`;

        return (
            <div className="card mt-1">
                <div className="card-body p-1">
                    {addressCharacteristic && (
                        <p className="mb-0 small">
                            <strong>{name}: </strong>
                            {addressLine}
                        </p>
                    )}
                </div>
            </div>
        );
    }

    if (!shippingAddress && !requestDeliveryDate) return null;

    return (
        <div className="card mt-1">
            <div className="card-body p-1">
                {shippingAddress && (
                    <p className="mb-0 small">
                        <strong>{t("common.shipping")}: </strong>
                        {shippingAddress}
                    </p>
                )}
                {requestDeliveryDate && (
                    <p className="mb-0 small">
                        <strong>{t("forms.labels.requestedDeliveryDate")}: </strong>
                        {formatToLocalDateTime(requestDeliveryDate)}
                    </p>
                )}
            </div>
        </div>
    );
};

ProductCharacteristics.propTypes = {
    product: PropTypes.shape({
        productCharacteristic: PropTypes.arrayOf(PropTypes.shape({
            name: PropTypes.string,
            value: PropTypes.oneOfType([
                PropTypes.string,
                PropTypes.object
            ])
        }))
    })
};

const ItemPrice = ({itemPrice}) => {
    const currentPrices = getCurrentPrices(itemPrice);

    return (
        <span className="fw-bold">
            {currentPrices.map((priceObj, index) => (
                <span key={index}>
                    {priceObj.totalPrice}
                    {priceObj.applicationDuration && (
                        <small className="fw-bold">
                            {" "}
                            {priceObj.applicationDuration}
                        </small>
                    )}
                    {index < currentPrices.length - 1 ? ' + ' : ''}
                </span>
            ))}
        </span>
    );
};

ItemPrice.propTypes = {
    itemPrice: PropTypes.array
};

const ItemList = ({title, items, t}) => {
    const sortedItems = Array.isArray(items) ? [...items].sort((a, b) => {
        const nameA = a.productOffering?.name?.toLowerCase() || '';
        const nameB = b.productOffering?.name?.toLowerCase() || '';
        return nameA.localeCompare(nameB);
    }) : [];

    return (
        <div className="items">
            {title && <h4>{title}</h4>}
            {sortedItems.map((productOrderItem, index) => {

                const excludedNames = [
                    "shipping address",
                    "requested delivery date",
                    "shipping mode"
                ];

                const filteredCharacteristics = productOrderItem.product?.productCharacteristic?.filter(
                    (char) => char.value && !excludedNames.includes((char.name || "").toLowerCase())
                ) || [];

                if (hasInstallment(productOrderItem.itemPrice)) {
                    return (
                        <InstallmentItemCard
                            key={productOrderItem.id || index}
                            productOrderItem={productOrderItem}
                            t={t}
                            renderProductCharacteristics={(product) => <ProductCharacteristics product={product}/>}
                        />
                    );
                }

                return (
                    <div className="card mb-2" key={productOrderItem.id || index}>
                        <div className="card-body">
                            <div className="row align-items-center">
                                <div className="col-6 col-md-6 col-lg-7">
                                    <h6 className="mb-1 d-flex align-items-center text-nowrap">
                                        {productOrderItem.productOffering?.name}
                                        <span
                                            className={`tag tag-sm status-value ms-2 ${productOrderItem.state.toLowerCase()}`}>{ORDER_STATUSES[productOrderItem.state]}
                                        </span>
                                    </h6>
                                    {filteredCharacteristics.map((char, idx) => (
                                        <div key={idx}>
                                            <small className="text-muted">
                                                {char.name}:
                                                {(() => {
                                                    if (typeof char.value === 'object') {
                                                        if ('validTo' in char.value) {
                                                            return ` Valid to ${formatToLocalDateTime(char.value.validTo)}`;
                                                        }
                                                        if ('value' in char.value) {
                                                            const unit = ('unitOfMeasure' in char.value && char.value.unitOfMeasure !== null) ? char.value.unitOfMeasure : '';
                                                            return ` ${char.value.value} ${unit}`;
                                                        }
                                                        return '';
                                                    }
                                                    return ` ${getFormattedValue(char.value, t)}`;
                                                })()}
                                            </small>
                                        </div>
                                    ))}
                                    {productOrderItem?.itemTerm?.map((orderTerm, idx) => (
                                        <div key={idx} className='mt-2'>
                                            <small className="text-muted">
                                                {t("common.commitmentTerm")}: {orderTerm?.duration?.amount} {orderTerm?.duration?.units?.charAt(0)?.toUpperCase()}
                                            </small>
                                            <div>
                                                <small className="text-muted">
                                                    {t("order.notes.terminationPenalty")}
                                                </small>
                                            </div>
                                        </div>
                                    ))}
                                    <ProductCharacteristics product={productOrderItem?.product}/>
                                    <p className={`mb-0 mt-2 tag tag-sm action-type ${getActionColor(productOrderItem?.action)}`}>
                                        <em className="icon-done_modifier action-icon"></em>
                                        {formatActionName(productOrderItem?.action)}
                                    </p>
                                </div>
                                <div className="col-6 text-lg-end text-start text-md-end col-md-5">
                                    <ItemPrice itemPrice={productOrderItem?.itemPrice}/>
                                </div>
                            </div>
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

ItemList.propTypes = {
    items: PropTypes.oneOfType([
        PropTypes.array,
        PropTypes.object
    ]).isRequired
};

export default ItemList;
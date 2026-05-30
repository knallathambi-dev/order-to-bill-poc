// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";
import {formatToLocalDateTime} from "@discobole/common-ui";
import {
    byMigrateToThenName,
    byName,
    extractCurrentPricesAndDiscounts,
    extractInstallmentInfo,
    formatActionName,
    getFormattedSelectedDuration,
    ORDER_STATUSES,
} from "./utils/orderUtils";


const StatusBadge = ({state}) => (
    <span className={`tag tag-sm status-value ${state?.toLowerCase()}`}>
        {ORDER_STATUSES[state] || state}
    </span>
);

const ActionBadge = ({action}) => (
    <span className={`tag tag-sm action-type ${action}`}>
        <em className="icon-done_modifier action-icon"/>
        {formatActionName(action)}
    </span>
);

const CommitmentTerm = ({itemTerm}) => {
    const formatted = getFormattedSelectedDuration(itemTerm);
    return formatted ? <small>({formatted})</small> : null;
};

const PriceDisplay = ({itemPrice}) => {
    const {currentPrices, discounts} = extractCurrentPricesAndDiscounts(itemPrice);

    return (
        <>
            <p className="mb-0">
                {currentPrices.map((priceObj, i) => (
                    <span key={i}>
                        {priceObj.totalPrice}
                        {priceObj.applicationDuration && <small> {priceObj.applicationDuration}</small>}
                        {i < currentPrices.length - 1 ? " + " : ""}
                    </span>
                ))}
            </p>
            {discounts.length > 0 && (
                <p className="text-danger mb-0">
                    {discounts.map((priceObj, i) => (
                        <span key={i} className="d-block">
                            <span className="d-block">{`Discount: ${priceObj.totalPrice}`}</span>
                            {priceObj.applicationOffset && (
                                <small>
                                    {" "}Application starting
                                    from {priceObj.applicationOffset.unit}#{priceObj.applicationOffset.offset}
                                </small>
                            )}
                            {priceObj.applicationDuration && <small> {priceObj.applicationDuration}</small>}
                        </span>

                    ))}
                </p>
            )}
        </>
    );
};

const CharacteristicValue = ({characteristic}) => {
    if (characteristic["@type"] === "AddressCharacteristic") {
        const {subUnitNumber, streetName, city, country, postcode} = characteristic;
        const subUnit = subUnitNumber?.trim() ? `${subUnitNumber} ` : "";
        return ` ${subUnit}${streetName}, ${city}, ${country} - ${postcode}`;
    }

    if (typeof characteristic.value === "object") {
        if ("validTo" in characteristic.value) {
            return ` Valid to ${formatToLocalDateTime(characteristic.value.validTo)}`;
        }
        if ("value" in characteristic.value) {
            const uom = characteristic.value.unitOfMeasure ?? "";
            return ` ${characteristic.value.value} ${uom}`;
        }
        return " Invalid data";
    }

    if (typeof characteristic.value === "string") {
        const trimmed = characteristic.value.trim();
        if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z$/.test(trimmed)) {
            const parsed = new Date(trimmed);
            if (!isNaN(parsed.getTime())) return ` ${formatToLocalDateTime(parsed)}`;
        }
        return ` ${characteristic.value}`;
    }

    return ` ${characteristic.value}`;
};

const InstallmentDisplay = ({item}) => {
    const installment = extractInstallmentInfo(item);
    if (!installment) return null;

    return (
        <>
            {installment.downPaymentFormatted && (
                <p className="small mb-1">
                    <strong>Down payment:</strong> {installment.downPaymentFormatted}
                </p>
            )}
            {installment.totalWithInstallmentsFormatted && (
                <p className="small mb-1">
                    <strong>Installment total:</strong> {installment.totalWithInstallmentsFormatted}
                </p>
            )}
        </>
    );
};

const AtomicItemsTable = ({atomicItems, hasCommitmentTerm}) => {
    const sorted = [...atomicItems].sort(byName);

    return (
        <tr>
            <td colSpan={hasCommitmentTerm ? 8 : 7}>
                <table className="table table-striped align-middle">
                    <thead>
                    <tr className="info">
                        <th className="col-order-item-id">Order Item ID</th>
                        <th className="col-order-item-num">#</th>
                        <th className="col-order-item-name">Item</th>
                        {hasCommitmentTerm && <th className="col-order-item-term">Commitment Term</th>}
                        <th>Characteristics</th>
                        <th className="col-order-item-state">Item State</th>
                        <th className="col-order-item-action">Action</th>
                        <th className="col-order-item-price">Price (tax incl.)</th>
                    </tr>
                    </thead>
                    <tbody>
                    {sorted.map((detail, index) => (
                        <tr key={detail.id || index}>
                            <td>{detail.id || "-"}</td>
                            <td>{index + 1}</td>
                            <td>{detail.name}</td>
                            {hasCommitmentTerm && (
                                <td><CommitmentTerm itemTerm={detail.itemTerm}/></td>
                            )}
                            <td>
                                {detail.characteristics?.map((char, i) => (
                                    <p key={i} className="small mb-1">
                                        <strong>{char.name}:</strong>
                                        <CharacteristicValue characteristic={char}/>
                                    </p>
                                )) || ""}
                            </td>
                            <td><StatusBadge state={detail.state}/></td>
                            <td><ActionBadge action={detail.action}/></td>
                            <td>
                                <PriceDisplay itemPrice={detail.itemPrice}/>
                                <InstallmentDisplay item={detail}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </td>
        </tr>
    );
};

const OrderItemRow = ({item, level = 0, openAccordions, toggleAccordion, hasCommitmentTerm}) => {
    const isBundle = Array.isArray(item.children) && item.children.length > 0;

    const atomicItems = (item.children?.filter((c) => !c.children?.length) || []).sort(byName);
    const nestedBundles = (item.children?.filter((c) => c.children?.length > 0) || []).sort(byMigrateToThenName);

    const paddingStyle = level > 0 ? {paddingLeft: `${level * 20}px`} : undefined;

    return (
        <React.Fragment key={item.id}>
            <tr
                onClick={() => isBundle && toggleAccordion(item.id)}
                className={`accordion-toggle ${isBundle ? "" : "non-expandable"}`}
            >
                <td>{item.id}</td>
                <td style={paddingStyle}>{item.name}</td>
                {hasCommitmentTerm && <td><CommitmentTerm itemTerm={item.itemTerm}/></td>}
                <td><StatusBadge state={item.state}/></td>
                <td><ActionBadge action={item.action}/></td>
                <td><PriceDisplay itemPrice={item.itemPrice}/></td>
                <td>
                    {isBundle && (
                        <button className="btn btn-default btn-sm">
                            <em className={`icon-arrow_${openAccordions[item.id] ? "up" : "down"}`}/>
                        </button>
                    )}
                </td>
            </tr>

            {openAccordions[item.id] && atomicItems.length > 0 && (
                <AtomicItemsTable atomicItems={atomicItems} hasCommitmentTerm={hasCommitmentTerm}/>
            )}

            {openAccordions[item.id] && nestedBundles.map((bundle) => (
                <OrderItemRow
                    key={bundle.id}
                    item={bundle}
                    level={level + 1}
                    openAccordions={openAccordions}
                    toggleAccordion={toggleAccordion}
                    hasCommitmentTerm={hasCommitmentTerm}
                />
            ))}
        </React.Fragment>
    );
};

const OrderItems = ({items, contractItems, openAccordions, toggleAccordion}) => {
    if (!items.length) {
        return (
            <div className="text-center py-3">
                <p>No order items available</p>
            </div>
        );
    }

    const allItems = [...(contractItems || []), ...items];
    const hasCommitmentTerm = allItems.some((item) => item.itemTerm);

    return (
        <div className="col-md-12">
            <div className="card mb-3">
                <div className="card-body p-0">
                    <div className="card-header py-3">
                        <h3 className="mb-0">Order Items</h3>
                    </div>
                    <div className="container-fluid">
                        <div className="col-md-12">
                            <div className="panel panel-default p-2">
                                <div className="panel-body">
                                    <table className="table align-middle">
                                        <thead>
                                        <tr>
                                            <th className="col-bundle-id">Order Item ID</th>
                                            <th className="col-bundle-name">Name</th>
                                            {hasCommitmentTerm && <th className="col-bundle-term">Commitment Term</th>}
                                            <th className="col-bundle-state">State</th>
                                            <th className="col-bundle-action">Action</th>
                                            <th className="col-bundle-price">Price (tax incl.)</th>
                                            <th className="col-bundle-toggle"/>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {(contractItems || []).map((contractItem) => {
                                            if (!contractItem) return null;
                                            return (
                                                <tr key={contractItem.id} className="custom-bg-body-tertiary">
                                                    <td>{contractItem.id}</td>
                                                    <td>{contractItem.name}</td>
                                                    {hasCommitmentTerm && (
                                                        <td><CommitmentTerm itemTerm={contractItem.itemTerm}/></td>
                                                    )}
                                                    <td><StatusBadge state={contractItem.state}/></td>
                                                    <td><ActionBadge action={contractItem.action}/></td>
                                                    <td><PriceDisplay itemPrice={contractItem.itemPrice}/></td>
                                                    <td/>
                                                </tr>
                                            );
                                        })}
                                        {items.map((item) => (
                                            <OrderItemRow
                                                key={item.id}
                                                item={item}
                                                openAccordions={openAccordions}
                                                toggleAccordion={toggleAccordion}
                                                hasCommitmentTerm={hasCommitmentTerm}
                                            />
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

OrderItems.propTypes = {
    items: PropTypes.array.isRequired,
    contractItems: PropTypes.array,
    openAccordions: PropTypes.object.isRequired,
    toggleAccordion: PropTypes.func.isRequired,
};

export default OrderItems;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from "react";
import PropTypes from "prop-types";
import {formatPriceAmount, formatToLocalDateTime} from "@discobole/common-ui";
import {INSTALLMENT_CHARGE_TYPE} from "../../../common/constants.js";

const val = (v) => (v !== undefined && v !== null ? v : "_");
const fmtDate = (d) => (d ? formatToLocalDateTime(d) || "_" : "_");
const amountWithUnit = (amount, unit) => {
    if (amount === undefined || amount === null || amount === "") return "_";
    return `${amount}${unit ? ` ${unit}` : ""}`;
};

const formatApplicationDuration = (duration) => {
    if (!duration || duration.amount == null) return "_";
    return amountWithUnit(duration.amount, duration.units);
};

const isInstallment = (price) =>
    price?.["@type"] === INSTALLMENT_CHARGE_TYPE;

const formatApplicationOffset = (offset, unit) => {
    if (offset === undefined || offset === null || offset === "") return "_";
    if (!unit) return `${offset}`;
    return `${unit}#${offset}`;
};

const LabelValue = ({label, children}) => (
    <li className="list-group-item d-flex justify-content-between align-items-center border-0 p-0 pb-2 bg-transparent">
        <div className="me-4 fw-bold text-nowrap">{label}</div>
        <span className="text-nowrap">{children}</span>
    </li>
);

LabelValue.propTypes = {
    label: PropTypes.string.isRequired,
    children: PropTypes.node.isRequired,
};

const OfferingPriceCell = ({offeringPrice}) => (
    <ul className="list-group">
        <LabelValue label="Id">{val(offeringPrice?.id)}</LabelValue>
        <LabelValue label="Type">{val(offeringPrice?.["@type"])}</LabelValue>
    </ul>
);

OfferingPriceCell.propTypes = {offeringPrice: PropTypes.object};

const PriceAmountCell = ({price}) => (
    <ul className="list-group">
        <LabelValue label="Amount (tax excl.)">
            {formatPriceAmount(price?.dutyFreeAmount?.value, price?.dutyFreeAmount?.unit)}
        </LabelValue>
        <LabelValue label="Amount (tax incl.)">
            {formatPriceAmount(price?.taxIncludedAmount?.value, price?.taxIncludedAmount?.unit)}
        </LabelValue>
    </ul>
);

PriceAmountCell.propTypes = {price: PropTypes.object};

const AlterationPriceCell = ({price}) => {
    const isDefined = (v) => v !== undefined && v !== null;
    const pct = price?.percentage;
    const amount = price?.taxIncludedAmount?.value;
    const unit = price?.taxIncludedAmount?.unit;
    const hasPct = isDefined(pct);
    const hasAmount = isDefined(amount);
    const showFallback = !hasPct && !hasAmount;

    return (
        <ul className="list-group">
            {(hasPct || showFallback) && (
                <LabelValue label="Percentage">
                    {hasPct ? `${val(pct)} %` : <span className="text-muted">—</span>}
                </LabelValue>
            )}
            {(hasAmount || showFallback) && (
                <LabelValue label="Value">
                    {hasAmount ? formatPriceAmount(amount, unit) : <span className="text-muted">—</span>}
                </LabelValue>
            )}
        </ul>
    );
};

AlterationPriceCell.propTypes = {price: PropTypes.object};

const ProductPriceDetails = ({productPrice = []}) => {
    const [expandedAlterations, setExpandedAlterations] = useState({});

    const toggleAlteration = useCallback((index) => {
        setExpandedAlterations((prev) => ({...prev, [index]: !prev[index]}));
    }, []);

    const hasRecurringCharge = productPrice.some(
        (p) => p.recurringChargePeriod?.amount || p.recurringChargePeriod?.units
    );

    const hasInstallment = productPrice.some(isInstallment);

    const hasApplicationOffset = productPrice.some(
        (p) =>
            p.productPriceAlteration?.some(
                (a) => a.applicationOffset !== undefined && a.applicationOffset !== null
            )
    );

    let colSpan = 8;
    if (hasRecurringCharge) colSpan += 1;
    if (hasInstallment) colSpan += 4;

    return (
        <div className="container-fluid">
            <div className="table-responsive">
                <table className="table price-table">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Name</th>
                        <th scope="col">Price Type</th>
                        <th scope="col">Price Amount</th>
                        <th scope="col">Product Offering Price</th>
                        <th scope="col">Start Date Time</th>
                        <th scope="col">End Date Time</th>
                        <th scope="col">Application Duration</th>
                        {hasRecurringCharge && <th scope="col">Recurring Charge Period</th>}
                        {hasInstallment && (
                            <>
                                <th scope="col">Down Payment</th>
                                <th scope="col">Interest Rate</th>
                                <th scope="col">Partner for Installment</th>
                            </>
                        )}
                    </tr>
                    </thead>
                    <tbody>
                    {productPrice.length > 0 ? (
                        productPrice.map((price, index) => {
                            const hasAlter = price.productPriceAlteration?.length > 0;
                            const pop = price.productOfferingPrice;
                            const isInst = isInstallment(price);
                            const currency = price.price?.taxIncludedAmount?.unit;

                            return (
                                <React.Fragment key={`price-${price.name || index}`}>
                                    <tr>
                                        <th scope="row">{index + 1}</th>
                                        <td>{val(price.name)}</td>
                                        <td>{val(price.priceType)}</td>
                                        <td><PriceAmountCell price={price.price}/></td>
                                        <td><OfferingPriceCell offeringPrice={pop}/></td>
                                        <td>{fmtDate(price.validFor?.startDateTime)}</td>
                                        <td>{fmtDate(price.validFor?.endDateTime)}</td>
                                        <td>{formatApplicationDuration(price.applicationDuration)}</td>
                                        {hasRecurringCharge && (
                                            <td>
                                                {amountWithUnit(
                                                    price.recurringChargePeriod?.amount,
                                                    price.recurringChargePeriod?.units
                                                )}
                                            </td>
                                        )}
                                        {hasInstallment && (
                                            <>
                                                <td>
                                                    {isInst && price?.downPayment != null
                                                        ? formatPriceAmount(price.downPayment, currency)
                                                        : "_"}
                                                </td>
                                                <td>
                                                    {isInst && price?.interestRate != null
                                                        ? `${price.interestRate} %`
                                                        : "_"}
                                                </td>
                                                <td>{isInst ? val(price?.partner) : "_"}</td>
                                            </>
                                        )}
                                    </tr>

                                    <tr>
                                        <th colSpan={colSpan} scope="row">
                                            <button
                                                className={`accordion-button btn-sm py-0 price-alteration-button ${
                                                    hasAlter ? "" : "btn-disabled text-muted"
                                                } ${expandedAlterations[index] ? "highlight-orange" : "collapsed"}`}
                                                type="button"
                                                aria-expanded={!!expandedAlterations[index]}
                                                aria-controls={`item${index}`}
                                                disabled={!hasAlter}
                                                onClick={() => toggleAlteration(index)}
                                            >
                                                Product Price Alteration
                                            </button>
                                        </th>
                                    </tr>

                                    {hasAlter && (
                                        <tr>
                                            <td colSpan={colSpan} className="hiddenRow">
                                                <div
                                                    className={`accordion-body ${
                                                        expandedAlterations[index] ? "show" : "collapse"
                                                    }`}
                                                    id={`item${index}`}
                                                >
                                                    <div className="table-responsive">
                                                        <table className="table table-light">
                                                            <thead>
                                                            <tr>
                                                                <th scope="col">#</th>
                                                                <th scope="col">Name</th>
                                                                <th scope="col">Price Type</th>
                                                                <th scope="col">Price Alteration</th>
                                                                <th scope="col">Product Offering Price</th>
                                                                <th scope="col">Priority</th>
                                                                {hasApplicationOffset && (
                                                                    <th scope="col">Application Start</th>
                                                                )}
                                                                <th scope="col">Application Duration</th>
                                                                <th scope="col">Start Date Time</th>
                                                                <th scope="col">End Date Time</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            {price.productPriceAlteration.map((alter, idx) => (
                                                                <tr key={`alter-${alter.name || idx}`}>
                                                                    <td>{idx + 1}</td>
                                                                    <td>{val(alter.name)}</td>
                                                                    <td>{val(alter.priceType)}</td>
                                                                    <td><AlterationPriceCell price={alter.price}/></td>
                                                                    <td>
                                                                        <OfferingPriceCell
                                                                            offeringPrice={alter.productOfferingPrice}
                                                                        />
                                                                    </td>
                                                                    <td>{val(alter.priority)}</td>
                                                                    {hasApplicationOffset && (
                                                                        <td>
                                                                            {formatApplicationOffset(
                                                                                alter.applicationOffset,
                                                                                price.recurringChargePeriod?.units
                                                                            )}
                                                                        </td>
                                                                    )}
                                                                    <td>
                                                                        {formatApplicationDuration(
                                                                            alter.applicationDuration
                                                                        )}
                                                                    </td>
                                                                    <td>{fmtDate(alter.validFor?.startDateTime)}</td>
                                                                    <td>{fmtDate(alter.validFor?.endDateTime)}</td>
                                                                </tr>
                                                            ))}
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    )}
                                </React.Fragment>
                            );
                        })
                    ) : (
                        <tr>
                            <td colSpan={colSpan} className="text-center">
                                No Data Available
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ProductPriceDetails;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useMemo} from "react";
import {formatAmount, getCurrencySymbol} from "../../../../../utlis/helpers";
import useTranslations from "../../../../../utlis/i18n/useTranslations";

const SkeletonRows = () =>
    Array.from({length: 3}).map((_, i) => (
        <tr key={i}>
            <td><span className="placeholder-glow"><span className="placeholder col-6"/></span></td>
            <td>
                <span className="placeholder-glow">
                    <span className="placeholder col-7"/><br/>
                    <span className="placeholder col-5"/>
                </span>
            </td>
            <td><span className="placeholder-glow"><span className="placeholder col-6"/></span></td>
        </tr>
    ));

const getInterestDescription = (plan, t) => {
    if (!plan.interestRate) return `(${t("common.installment.comparison.interestTypes.noInterest")})`;
    const base = `(${t("common.installment.comparison.interestTypes.withInterest")} ${plan.interestRate}%`;
    return plan.downPayment > 0
        ? `${base} + ${t("common.installment.labels.initialPayment")})`
        : `${base})`;
};

const CompareInstallmentModal = ({installmentCharges, selectedPartner}) => {
    const {t} = useTranslations();

    const sortedPlans = useMemo(() => {
        if (!Array.isArray(installmentCharges)) return [];

        return installmentCharges
            .map((charge) => {
                const {productOfferingPrice: op, price} = charge;
                const currency = price?.taxIncludedAmount?.unit ?? price?.dutyFreeAmount?.unit;
                const taxIncludedAmount = price?.taxIncludedAmount?.value;
                const duration = op?.applicationDuration?.amount;
                const totalPayment = taxIncludedAmount * (1 + op?.interestRate / 100);

                return {
                    duration,
                    durationUnit: op?.applicationDuration?.units,
                    monthlyInstallment: duration > 0 ? (totalPayment - op?.downPayment) / duration : 0,
                    totalPayment,
                    downPayment: op?.downPayment,
                    interestRate: op?.interestRate,
                    currency,
                };
            })
            .sort((a, b) => a.duration - b.duration);
    }, [installmentCharges, selectedPartner]);

    return (
        <div>
            <p>{t("common.installment.comparison.description")}</p>
            <table className="table table-striped">
                <thead>
                <tr>
                    <th>{t("common.installment.comparison.tableHeaders.durationMonths")}</th>
                    <th>{t("common.installment.comparison.tableHeaders.monthlyAmount")}</th>
                    <th>{t("common.installment.comparison.tableHeaders.totalAmount")}</th>
                </tr>
                </thead>
                <tbody>
                {!sortedPlans.length ? (
                    <SkeletonRows/>
                ) : (
                    sortedPlans.map((plan, i) => (
                        <tr key={i}>
                            <td>
                                {plan.duration} {plan.durationUnit?.charAt(0).toUpperCase() + plan.durationUnit?.slice(1)}
                                <p className="mb-0"><small>{getInterestDescription(plan, t)}</small></p>
                            </td>
                            <td>
                                {getCurrencySymbol(plan.currency)} {formatAmount(plan.monthlyInstallment, plan.currency)}
                                {plan.downPayment > 0 && (
                                    <p className="mb-0">
                                        <small>
                                            {t("common.installment.labels.initialPayment")} ={" "}
                                            {getCurrencySymbol(plan.currency)} {formatAmount(plan.downPayment, plan.currency)}
                                        </small>
                                    </p>
                                )}
                            </td>
                            <td>{getCurrencySymbol(plan.currency)} {formatAmount(plan.totalPayment, plan.currency)}</td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
        </div>
    );
};

export default CompareInstallmentModal;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {formatAmount, getCurrencySymbol} from "../../../../utlis/helpers";
import {computeInstallmentPricing, findMatchingPartnerCharges} from "../services/priceService";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const InstallmentDetails = ({pricing, t}) => {
    const {currency, monthlyInstallment, downPayment, totalPayment, duration, durationUnit, periodSuffix} = pricing;
    const symbol = getCurrencySymbol(currency);

    return (
        <div className="card mb-2">
            <ul className="list-group list-group-flush">
                <li className="list-group-item d-flex justify-content-between align-items-start">
                    <div className="me-auto">{t("common.installment.labels.estimatedInstallments")}</div>
                    <span>
                        {symbol} {formatAmount(monthlyInstallment, currency)}{periodSuffix}{" "}
                        <small className="text-muted">
                            ( {t("common.installment.labels.duration")} {duration} {durationUnit} )
                        </small>
                    </span>
                </li>
                {downPayment > 0 && (
                    <li className="list-group-item d-flex justify-content-between align-items-start">
                        <div className="me-auto">{t("common.installment.labels.initialPayment")}</div>
                        <span>{symbol} {formatAmount(downPayment, currency)}</span>
                    </li>
                )}
                <li className="list-group-item d-flex justify-content-between align-items-start">
                    <div className="me-auto">
                        {t("common.installment.labels.totalAmount")}{" "}
                        <small className="text-muted">
                            ( {t("common.installment.labels.afterCompletion")} )
                        </small>
                    </div>
                    <span>{symbol} {formatAmount(totalPayment, currency)}</span>
                </li>
            </ul>
        </div>
    );
};

const DurationSelector = ({
                              partner,
                              charges,
                              currentDurationKey,
                              periodOptions,
                              onDurationChange,
                              onOpenCompareModal,
                              t
                          }) => (
    <div className="d-flex gap-3 mb-3">
        <select
            className="form-select"
            value={currentDurationKey || ""}
            onChange={(e) => onDurationChange(partner, e.target.value)}
        >
            {periodOptions.length > 0
                ? periodOptions.map((periodValue) => {
                    const durationKey = `${partner}-${parseInt(periodValue)}`;
                    return <option key={durationKey} value={durationKey}>{periodValue}</option>;
                })
                : charges.map((charge) => {
                    const applicationDuration = charge?.productOfferingPrice?.applicationDuration;
                    const amount = applicationDuration?.amount || 0;
                    const units = applicationDuration?.units || "months";
                    const durationKey = `${partner}-${amount}`;
                    const label = `${amount} ${units.charAt(0).toUpperCase() + units.slice(1)}`;
                    return <option key={durationKey} value={durationKey}>{label}</option>;
                })
            }
        </select>
        <button type="button" className="btn btn-secondary w-100" onClick={onOpenCompareModal}>
            {t("common.installment.comparison.title")}
        </button>
    </div>
);

const PartnerAccordionItem = ({
                                  partner,
                                  partnerIndex,
                                  isExpanded,
                                  charges,
                                  currentDurationKey,
                                  periodOptions,
                                  onPartnerSelect,
                                  onDurationChange,
                                  onOpenCompareModal,
                                  renderProceedButton,
                                  t,
                              }) => {
    const currentCharge = currentDurationKey
        ? charges.find((charge) => `${partner}-${charge?.productOfferingPrice?.applicationDuration?.amount}` === currentDurationKey) || charges[0]
        : charges[0];

    const pricing = computeInstallmentPricing(currentCharge);

    return (
        <div className="accordion-item" key={partner}>
            <h2 className={`accordion-header ${partnerIndex === 0 ? "border-0" : ""}`}>
                <button
                    className={`accordion-button ${isExpanded ? "" : "collapsed"}`}
                    type="button"
                    aria-expanded={isExpanded}
                    aria-controls={`collapse${partnerIndex}`}
                    onClick={() => {
                        if (!isExpanded) onPartnerSelect(partner, charges);
                    }}
                >
                    <div className="form-check" onClick={(e) => e.stopPropagation()}>
                        <input
                            className="form-check-input"
                            type="radio"
                            name="partnerRadio"
                            id={`partnerRadio${partnerIndex}`}
                            checked={isExpanded}
                            onChange={() => onPartnerSelect(partner, charges)}
                        />
                        <label className="form-check-label" htmlFor={`partnerRadio${partnerIndex}`}>
                            {partner}
                        </label>
                    </div>
                </button>
            </h2>
            <div
                id={`collapse${partnerIndex}`}
                className={`accordion-collapse collapse ${isExpanded ? "show" : ""}`}
            >
                <div className="accordion-body">
                    {charges.length === 0 ? (
                        <p className="text-muted">{t("common.installment.comparison.loadingOptions")}</p>
                    ) : (
                        <>
                            <DurationSelector
                                partner={partner}
                                charges={charges}
                                currentDurationKey={currentDurationKey}
                                periodOptions={periodOptions}
                                onDurationChange={onDurationChange}
                                onOpenCompareModal={onOpenCompareModal}
                                t={t}
                            />
                            {pricing && (
                                <div className="row d-flex align-items-center justify-content-between">
                                    <div className="col-md-12">
                                        <InstallmentDetails pricing={pricing} t={t}/>
                                    </div>
                                    {renderProceedButton && (
                                        <div className="col-md-12 d-grid mt-2">{renderProceedButton()}</div>
                                    )}
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

const InstallmentAccordion = ({
                                  chargesByPartner,
                                  availablePartners = [],
                                  selectedPartner,
                                  selectedDuration,
                                  periodOptions = [],
                                  onPartnerSelect,
                                  onDurationChange,
                                  onOpenCompareModal,
                                  renderProceedButton = null,
                                  accordionClassName = "accordion",
                              }) => {
    const {t} = useTranslations();

    const partners = availablePartners.length > 0
        ? availablePartners
        : Object.keys(chargesByPartner);

    return (
        <div className={accordionClassName} id="installmentAccordion">
            {partners.map((partner, index) => (
                <PartnerAccordionItem
                    key={partner}
                    partner={partner}
                    partnerIndex={index}
                    isExpanded={selectedPartner === partner}
                    charges={findMatchingPartnerCharges(partner, chargesByPartner)}
                    currentDurationKey={selectedDuration[partner]}
                    periodOptions={periodOptions}
                    onPartnerSelect={onPartnerSelect}
                    onDurationChange={onDurationChange}
                    onOpenCompareModal={onOpenCompareModal}
                    renderProceedButton={renderProceedButton}
                    t={t}
                />
            ))}
        </div>
    );
};

export default InstallmentAccordion;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {Modal} from "../../../../components";
import CompareInstallmentModal from "../../SetUpPlan/components/Modals/CompareInstallmentModal";
import InstallmentAccordion from "./InstallmentAccordion";
import {hasCharges, renderPrices} from "../../../../utlis/utils";

const PricingDisplay = ({pricing, discounts, t}) => (
    <>
        <div className="align-items-center d-flex">
            <p className="mb-0 fs-1 fw-bold me-2">
                {renderPrices(pricing.current, pricing.currency)}
            </p>
            {discounts.map((priceObj, i) => (
                <p className="mb-0 fs-5 fw-bold text-danger" key={priceObj.id || `discount-${i}`}>
                    (- {priceObj.totalPrice}){i < discounts.length - 1 && " + "}
                </p>
            ))}
        </div>
        <p className="mb-0 text-muted"><small>{t("common.taxIncluded")}</small></p>
        {hasCharges(pricing.discount) && hasCharges(pricing.beforeDiscount) && (
            <p className="mb-0 fs-6">
                {t("common.beforeDiscount")}:
                <del> {renderPrices(pricing.beforeDiscount, pricing.currency)}</del>
            </p>
        )}
    </>
);

const InstallmentPaymentTabs = ({
                                    hasInstallments,
                                    chargesByPartner,
                                    comparisonCharges = [],
                                    periodOptions = [],
                                    availablePartners = [],
                                    pricing,
                                    discounts,
                                    t,
                                    activeTab,
                                    selectedPartner,
                                    selectedDuration,
                                    isCompareModalOpen,
                                    setCompareModalOpen,
                                    onPartnerSelect,
                                    onDurationChange,
                                    onFullPriceTabClick,
                                    onInstallmentTabClick,
                                    onOpenCompareModal,
                                    /* Optional layout props */
                                    showPaymentLabel = false,
                                    renderProceedButton = null,
                                    accordionClassName,
                                    priceColumnClass = "col-md-12",
                                    containerClassName = "payment-options mb-3",
                                }) => {

    /* No installment options → simple price display */
    if (!hasInstallments && availablePartners.length === 0) {
        return (
            <div className="row d-flex align-items-center justify-content-between mb-3">
                <div className={priceColumnClass}>
                    <PricingDisplay pricing={pricing} discounts={discounts} t={t}/>
                </div>
                {renderProceedButton && (
                    <div className="col-md-6 d-grid">
                        {renderProceedButton()}
                    </div>
                )}
            </div>
        );
    }

    return (
        <>
            <div className={containerClassName}>
                {showPaymentLabel && (
                    <label className="form-label me-2 fw-medium mb-3">{t("common.payment")}</label>
                )}
                <nav>
                    <div className="nav nav-tabs" id="nav-tab" role="tablist">
                        <button
                            className={`nav-link ${activeTab === "fullPrice" ? "active" : ""}`}
                            type="button"
                            onClick={onFullPriceTabClick}
                        >
                            {t("common.installment.tabs.fullPrice")}
                        </button>
                        <button
                            className={`nav-link ${activeTab === "installment" ? "active" : ""}`}
                            type="button"
                            onClick={onInstallmentTabClick}
                        >
                            {t("common.installment.tabs.installment")}
                        </button>
                    </div>
                </nav>
                <div className="tab-content" id="nav-tabContent">
                    <div className={`tab-pane fade ${activeTab === "fullPrice" ? "show active" : ""}`}>
                        <div className="row d-flex align-items-center justify-content-between">
                            <div className={priceColumnClass}>
                                <PricingDisplay pricing={pricing} discounts={discounts} t={t}/>
                            </div>
                            {renderProceedButton && (
                                <div className="col-md-12 d-grid mt-3">
                                    {renderProceedButton()}
                                </div>
                            )}
                        </div>
                    </div>
                    <div className={`tab-pane fade ${activeTab === "installment" ? "show active" : ""}`}>
                        <InstallmentAccordion
                            chargesByPartner={chargesByPartner}
                            availablePartners={availablePartners}
                            selectedPartner={selectedPartner}
                            selectedDuration={selectedDuration}
                            periodOptions={periodOptions}
                            onPartnerSelect={onPartnerSelect}
                            onDurationChange={onDurationChange}
                            onOpenCompareModal={onOpenCompareModal}
                            renderProceedButton={renderProceedButton}
                            accordionClassName={accordionClassName}
                        />
                    </div>
                </div>
            </div>

            {isCompareModalOpen && (
                <Modal
                    show={isCompareModalOpen}
                    title="Installment Plans"
                    body={
                        <CompareInstallmentModal
                            installmentCharges={comparisonCharges}
                            selectedPartner={selectedPartner}
                        />
                    }
                    onClose={() => setCompareModalOpen(false)}
                    hideFooter
                />
            )}
        </>
    );
};

export default InstallmentPaymentTabs;
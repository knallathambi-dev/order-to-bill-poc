// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

const times = (n) => Array.from({length: n});

function Line({width = "col-8", height = 16, className = ""}) {
    return (
        <span
            className={`placeholder ${width} ${className}`}
            style={{display: "inline-block", height}}
            aria-hidden="true"
        />
    );
}

function ItemRowSkeleton() {
    return (
        <li className="list-group-item d-flex justify-content-between align-items-start">
            <div className="me-auto placeholder-glow">
                <Line width="col-8 col-md-6"/>
            </div>
            <div className="placeholder-glow">
                <Line width="col-4"/>
            </div>
        </li>
    );
}

function SectionHeaderSkeleton() {
    return (
        <h4 className="accordion-header border-top-0">
            <button
                className="accordion-button accordion-button-payment"
                type="button"
                aria-expanded="true"
            >
        <span className="placeholder-glow w-100">
          <Line width="col-4"/>
        </span>
            </button>
        </h4>
    );
}

export default function OrderCompletionSummarySkeleton({activeTab = "creditCard"}) {
    return (
        <div className="col-12 col-md-7 col-lg-8" aria-busy="true">
            <div className="accordion accordion-sm included-accordion">
                {/* Billing */}
                <div className="accordion-item">
                    <SectionHeaderSkeleton/>
                    <div className="accordion-collapse collapse show">
                        <div className="accordion-body pt-0">
                            <div className="billing-section">
                                <div className="mb-3">
                                    <div className="card">
                                        <div
                                            className="card-body d-flex justify-content-between align-items-center placeholder-glow">
                                            <div className="w-100">
                                                <Line width="col-10"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <div className="placeholder-glow mb-2">
                                        <Line width="col-3"/>
                                    </div>
                                    <div className="card">
                                        <ul className="list-group list-group-flush">
                                            {times(2).map((_, i) => (
                                                <ItemRowSkeleton key={`billable-${i}`}/>
                                            ))}
                                            <li className="list-group-item d-flex justify-content-between align-items-start">
                                                <div className="me-auto placeholder-glow">
                                                    <Line width="col-2"/>
                                                </div>
                                                <div className="placeholder-glow">
                                                    <Line width="col-4"/>
                                                </div>
                                            </li>
                                        </ul>
                                    </div>
                                    <div className="alert alert-info alert-sm mt-3" role="alert">
                                        <div className="placeholder-glow">
                                            <Line width="col-10"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <hr className="m-0"/>

                {/* Payment */}
                <div className="accordion-item border-bottom-0">
                    <SectionHeaderSkeleton/>
                    <div className="accordion-collapse collapse show">
                        <div className="accordion-body pt-0">
                            <div className="payment-section">
                                <div className="placeholder-glow mb-2">
                                    <Line width="col-4"/>
                                </div>
                                <div className="card">
                                    <ul className="list-group list-group-flush">
                                        {times(2).map((_, i) => (
                                            <ItemRowSkeleton key={`purchase-${i}`}/>
                                        ))}
                                        <li className="list-group-item d-flex justify-content-between align-items-start">
                                            <div className="me-auto placeholder-glow">
                                                <Line width="col-2"/>
                                            </div>
                                            <div className="placeholder-glow">
                                                <Line width="col-4"/>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                                <div className="alert alert-info alert-sm mt-3" role="alert">
                                    <div className="placeholder-glow">
                                        <Line width="col-6"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Payment method tabs */}
                <div className="select-payment mt-3">
                    <div className="placeholder-glow mb-2">
                        <Line width="col-5"/>
                    </div>
                    <ul className="nav nav-pills nav-fill mb-3 gap-2" role="tablist"
                        aria-label="Payment methods loading">
                        <li className="nav-item" role="presentation">
                            <button
                                className={`nav-link d-flex justify-content-center ${activeTab === "creditCard" ? "active" : ""}`}
                                type="button"
                                disabled
                                aria-disabled="true"
                                aria-selected={activeTab === "creditCard"}
                            >
                                <span className="placeholder-glow w-100 d-flex justify-content-center">
                                  <Line width="col-6"/>
                                </span>
                            </button>
                        </li>
                        <li className="nav-item" role="presentation">
                            <button
                                className={`nav-link d-flex justify-content-center ${activeTab === "eWallet" ? "active" : ""}`}
                                type="button"
                                disabled
                                aria-disabled="true"
                                aria-selected={activeTab === "eWallet"}
                            >
                                <span className="placeholder-glow w-100 d-flex justify-content-center">
                                  <Line width="col-6"/>
                                </span>
                            </button>
                        </li>
                    </ul>

                    <div className="card p-3">
                        {activeTab === "creditCard" ? (
                            <div className="placeholder-glow">
                                <div className="mb-3">
                                    <Line width="col-3"/>
                                    <div className="mt-2">
                                        <Line width="col-12" height={38} className="rounded"/>
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <Line width="col-4"/>
                                    <div className="mt-2">
                                        <Line width="col-12" height={38} className="rounded"/>
                                    </div>
                                </div>
                                <div className="row g-2">
                                    <div className="col-6">
                                        <Line width="col-5"/>
                                        <div className="mt-2">
                                            <Line width="col-12" height={38} className="rounded"/>
                                        </div>
                                    </div>
                                    <div className="col-6">
                                        <Line width="col-5"/>
                                        <div className="mt-2">
                                            <Line width="col-12" height={38} className="rounded"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="placeholder-glow">
                                <div className="mb-3">
                                    <Line width="col-3"/>
                                    <div className="mt-2">
                                        <Line width="col-12" height={38} className="rounded"/>
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <Line width="col-4"/>
                                    <div className="mt-2">
                                        <Line width="col-12" height={38} className="rounded"/>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    <div className="d-grid mt-3 placeholder-glow">
                        <button className="btn btn-secondary" type="button" disabled aria-disabled="true">
                            <Line width="col-5"/>
                        </button>
                    </div>
                </div>
            </div>

            {/* Footer actions */}
            <div className="mt-1 mb-5">
                <hr className="mt-4"/>
                <div className="placeholder-glow mt-2">
                  <span className="btn btn-outline-secondary disabled" aria-disabled="true">
                    <Line width="col-4"/>
                  </span>
                </div>
            </div>
        </div>
    );
}
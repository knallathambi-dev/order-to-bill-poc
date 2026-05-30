// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

const EligiblePlanSkeletonCard = React.memo(() => (
    <div className="col">
        <div className="card plan-card card-with-label h-100 position-relative">
            <div className="card-body pt-4">
                <div className="placeholder-glow">
                    <h5 className="card-title placeholder col-8 mb-3"></h5>
                    <p className="card-text">
                        <span className="placeholder d-block w-100 mb-2"></span>
                        <span className="placeholder d-block w-75 mb-2"></span>
                        <span className="placeholder d-block w-50 mb-2"></span>
                    </p>
                </div>
            </div>
            <div className="card-footer">
                <div className="placeholder-glow">
                    <p className="fs-3 text-primary fw-bold mb-0">
                        <span className="placeholder col-6"></span>
                    </p>
                </div>
            </div>
        </div>
    </div>
));

export default EligiblePlanSkeletonCard;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

const PriceDisplay = ({currentPrices = [], formattedDuration, className = "me-3 text-muted fw-bold fs-5"}) => {
    if (!currentPrices.length && !formattedDuration) return null;

    return (
        <span className={className}>
            {currentPrices.map((priceObj, index) => (
                <React.Fragment key={index}>
                    {priceObj.totalPrice}
                    {priceObj.applicationDuration && (
                        <small className="fw-bold">
                            {" "}
                            {priceObj.applicationDuration}
                        </small>
                    )}
                    {index < currentPrices.length - 1 ? ' + ' : ''}
                </React.Fragment>
            ))}
            {formattedDuration && (
                <small className="fw-bold"> ({formattedDuration})</small>
            )}
        </span>
    );
};

export default PriceDisplay;
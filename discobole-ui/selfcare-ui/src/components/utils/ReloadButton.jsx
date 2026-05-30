// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {Tooltip} from "boosted";
import BootstrapTooltip from "./BootstrapTooltip";

export const ReloadButton = ({onClick, loading = false, tooltipPlace = "top"}) => {

    const handleClick = (e) => {
        const tooltipInstance = Tooltip.getInstance(e.currentTarget);
        if (tooltipInstance) {
            tooltipInstance.hide();
        }
        onClick?.(e);
    };

    return (
        <BootstrapTooltip placement={tooltipPlace}>
            <button
                type="button"
                className="btn btn-icon btn-outline-secondary p-2"
                aria-label="Reload"
                onClick={!loading ? handleClick : undefined}
                disabled={loading}
            >
                <em className="icon-reload"/>
            </button>
        </BootstrapTooltip>
    );
};
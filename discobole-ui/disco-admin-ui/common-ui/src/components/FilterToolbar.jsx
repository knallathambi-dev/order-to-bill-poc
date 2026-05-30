// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

export const FilterToolbar = ({expanded, onToggle, onClear, disabled}) => (
    <div className="d-flex flex-column align-items-end mt-4">
        <button type="button" className="btn btn-link mb-2" onClick={onToggle}>
            {expanded ? "Show Less Filters" : "Show More Filters"}
            <svg
                className="text-secondary ms-2"
                aria-hidden="true"
                focusable="false"
                width="20"
                height="20"
            >
                <use href={`/svg/chevron-icons.svg#form-chevron-${expanded ? "up" : "down"}`}/>
            </svg>
        </button>
        <div className="d-flex gap-2">
            <button
                type="button"
                className="btn btn-secondary"
                onClick={onClear}
                disabled={disabled}
            >
                Clear Filter
            </button>
            <button type="submit" className="btn btn-primary" disabled={disabled}>
                Filter
            </button>
        </div>
    </div>
);
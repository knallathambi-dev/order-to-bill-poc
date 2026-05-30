// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {components} from "react-select";

export const CustomMultiValueRemove = (props) => (
    <components.MultiValueRemove {...props}>
        <span
            className="btn-close btn-close-sm ms-1"
            role="button"
            aria-label="Remove"
            onClick={props.removeProps.onClick}
        />
    </components.MultiValueRemove>
);

export const CustomOption = (props) => (
    <components.Option {...props}>
        <div className="form-check d-flex align-items-center m-0">
            <input
                className="form-check-input mt-0"
                type="checkbox"
                checked={props.isSelected}
                onChange={(e) => {
                    e.preventDefault();
                    props.selectOption(props.data);
                }}
                id={`checkbox-${props.value}`}
            />
            <label
                className={`form-check-label ms-2 ${props.isSelected ? "fw-bold text-primary" : ""}`}
                htmlFor={`checkbox-${props.value}`}
            >
                {props.label}
            </label>
        </div>
    </components.Option>
);

export const DropdownIndicator = (props) =>
    components.DropdownIndicator && (
        <components.DropdownIndicator {...props}>
            <svg width="20" height="20" viewBox="0 0 20 20" focusable="false" aria-hidden="true">
                <path d="M7 10l5 5 5-5z" fill="currentColor"/>
            </svg>
        </components.DropdownIndicator>
    );

/**
 * Boosted/Bootstrap class mappings for react-select internal slots.
 * Use via: <Select classNames={selectClassNames} ... />
 */
export const selectClassNames = {
    control: ({isFocused}) => `form-control p-0 d-flex align-items-center ${isFocused ? "border-dark" : ""}`,
    valueContainer: () => "d-flex flex-nowrap overflow-auto px-2",
    multiValue: () => "badge bg-light text-dark border me-1",
    multiValueLabel: () => "text-nowrap",
    placeholder: () => "text-secondary",
    menu: () => "shadow border rounded-0 mt-1",
    menuList: () => "list-unstyled m-0 p-0",
    option: ({isFocused, isSelected}) =>
        `px-3 py-2 ${isFocused ? "bg-light" : ""} ${isSelected ? "bg-light fw-bold" : ""}`,
};

/**
 * Minimal style overrides for things Boosted classes cannot reach
 * (react-select controls these via its own internal system).
 */
export const selectStyles = {
    control: (base) => ({...base, minHeight: "40px", borderRadius: 0, boxShadow: "none"}),
    menu: (base) => ({...base, zIndex: 1050}),
};
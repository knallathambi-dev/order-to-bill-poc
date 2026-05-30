// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import Select, {components} from "react-select";

/* ============================================================
   Helpers
   ============================================================ */

const normalizeOptions = (options) =>
    options.map((opt) => (typeof opt === "string" ? {value: opt, label: opt} : opt));

/* ============================================================
   Shared: Clear Button
   ============================================================ */

export const ClearFieldButton = ({onClick}) => (
    <button
        type="button"
        className="btn btn-icon btn-link dismiss-btn"
        onClick={onClick}
    >
        ✕
    </button>
);

ClearFieldButton.propTypes = {
    onClick: PropTypes.func.isRequired,
};

/* ============================================================
   react-select Configuration
   ============================================================ */

export const selectClassNames = {
    control: () => "react-select-control p-0",
};

export const selectStyles = {
    control: (base, state) => ({
        ...base,
        borderWidth: "2px",
        borderColor: state.selectProps.error ? "#dc3545" : "#ccc",
        borderRadius: "0px",
        minHeight: "40px",
        boxShadow: "none",
        padding: "0",
        fontWeight: "bold",
        backgroundColor: "#fff",
        display: "flex !important",
        "&:hover": {borderColor: state.selectProps.error ? "#dc3545" : "#ccc"},
    }),
    menu: (base) => ({
        ...base,
        zIndex: 9999,
    }),
    option: (base, state) => ({
        ...base,
        color: "#000",
        backgroundColor: state.isSelected ? "rgba(128,128,128,0.1)" : base.backgroundColor,
        "&:hover": {backgroundColor: "rgba(128,128,128,0.2)"},
    }),
};

/* ============================================================
   react-select Custom Components
   ============================================================ */

export const DropdownIndicator = (props) => (
    <components.DropdownIndicator {...props}>
        <svg width="20" height="20" viewBox="0 0 20 20" focusable="false" aria-hidden="true">
            <path d="M7 10l5 5 5-5z" fill="#000"/>
        </svg>
    </components.DropdownIndicator>
);

export const CustomClearIndicator = (props) => (
    <components.ClearIndicator {...props}>
        <span className="fw-bold" style={{cursor: "pointer", fontSize: "16px", color: "#000"}}>✕</span>
    </components.ClearIndicator>
);

export const CustomOption = (props) => (
    <components.Option {...props}>
        <div className="form-check d-flex align-items-center">
            <input
                className="form-check-input"
                type="checkbox"
                checked={props.isSelected}
                onChange={(e) => {
                    e.preventDefault();
                    props.selectOption(props.data);
                }}
                id={`checkbox-${props.value}`}
            />
            <label
                className="form-check-label ms-2"
                htmlFor={`checkbox-${props.value}`}
                style={{
                    backgroundColor: props.isSelected ? "rgba(255,165,0,0.1)" : "transparent",
                    color: props.isSelected ? "#ff6600" : "inherit",
                }}
            >
                {props.label}
            </label>
        </div>
    </components.Option>
);

/* ============================================================
   Shared: Validation Error Message
   ============================================================ */

const FieldError = ({error}) =>
    error ? <div className="invalid-feedback d-block">{error}</div> : null;

FieldError.propTypes = {
    error: PropTypes.string,
};

/* ============================================================
   TextInput
   ============================================================ */

export const TextInput = ({id, label, formik, onClear, placeholder}) => {
    const error = formik.touched[id] && formik.errors[id];

    return (
        <div className="col">
            <div className="position-relative w-100 mb-0">
                <label htmlFor={id} className="floating-label">{label}</label>
                <input
                    type="text"
                    className={`form-control ${error ? "is-invalid" : ""}`}
                    id={id}
                    name={id}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    value={formik.values[id]}
                    placeholder={placeholder || label}
                    aria-label={label}
                    autoComplete="off"
                />
                {formik.values[id] && <ClearFieldButton onClick={() => onClear(id)}/>}
                <FieldError error={error}/>
            </div>
        </div>
    );
};

TextInput.propTypes = {
    id: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    formik: PropTypes.object.isRequired,
    onClear: PropTypes.func.isRequired,
    placeholder: PropTypes.string,
};

/* ============================================================
   DateInput
   ============================================================ */

export const DateInput = ({
                              id,
                              label,
                              formik,
                              startId,
                              endId,
                              isStart = true,
                              dateFormat = "dd/MM/yyyy",
                              showTimeSelect = false,
                              onClear,
                          }) => {
    const error = formik.touched[id] && formik.errors[id];

    const rangeProps = isStart
        ? {
            selectsStart: true,
            startDate: formik.values[startId],
            endDate: formik.values[endId],
            ...(formik.values[endId] && {maxDate: formik.values[endId]}),
        }
        : {
            selectsEnd: true,
            minDate: formik.values[startId],
            startDate: formik.values[startId],
            endDate: formik.values[endId],
        };

    return (
        <div className="col">
            <div className="position-relative w-100 mb-0">
                <label htmlFor={id} className="floating-label">{label}</label>
                <DatePicker
                    id={id}
                    name={id}
                    showIcon
                    selected={formik.values[id]}
                    onChange={(date) => {
                        formik.setFieldValue(id, date);
                        formik.setFieldTouched(id, true);
                    }}
                    onBlur={() => formik.setFieldTouched(id, true)}
                    {...rangeProps}
                    placeholderText={label}
                    className={`date-picker form-control ${isStart ? "me-4" : ""} ${error ? "is-invalid" : ""}`}
                    dateFormat={dateFormat}
                    showTimeSelect={showTimeSelect}
                    autoComplete="off"
                    wrapperClassName="w-100"
                />
                {formik.values[id] && <ClearFieldButton onClick={() => onClear(id)}/>}
                <FieldError error={error}/>
            </div>
        </div>
    );
};

DateInput.propTypes = {
    id: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    formik: PropTypes.object.isRequired,
    startId: PropTypes.string,
    endId: PropTypes.string,
    isStart: PropTypes.bool,
    dateFormat: PropTypes.string,
    showTimeSelect: PropTypes.bool,
    onClear: PropTypes.func.isRequired,
};

/* ============================================================
   SelectInput
   ============================================================ */

export const SelectInput = ({id, label, formik, options, placeholder, onClear, onChange}) => {
    const selectOptions = normalizeOptions(options);
    const error = formik.touched[id] && formik.errors[id];

    const currentValue = formik.values[id]
        ? selectOptions.find((o) => o.value === formik.values[id]) ?? null
        : null;

    return (
        <div className="col">
            <div className="position-relative w-100 mb-0">
                <label htmlFor={id} className="floating-label fw-bold">{label}</label>
                <Select
                    classNames={selectClassNames}
                    styles={selectStyles}
                    id={id}
                    name={id}
                    options={selectOptions}
                    isClearable
                    error={error}
                    components={{DropdownIndicator, ClearIndicator: CustomClearIndicator}}
                    onChange={(selected) => {
                        formik.setFieldValue(id, selected?.value || "");
                        formik.setFieldTouched(id, true, true);
                        if (!selected) onClear?.(id);
                        onChange?.(selected);
                    }}
                    onBlur={() => formik.setFieldTouched(id, true)}
                    value={currentValue}
                    placeholder={placeholder}
                />
                <FieldError error={error}/>
            </div>
        </div>
    );
};

SelectInput.propTypes = {
    id: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    formik: PropTypes.object.isRequired,
    options: PropTypes.array.isRequired,
    placeholder: PropTypes.string,
    onClear: PropTypes.func,
    onChange: PropTypes.func,
};

/* ============================================================
   MultiSelectInput
   ============================================================ */

export const MultiSelectInput = ({id, label, formik, options, placeholder, onChange}) => {
    const error = formik.touched[id] && formik.errors[id];

    return (
        <div className="col">
            <div className="position-relative w-100 mb-0">
                <label htmlFor={id} className="floating-label fw-bold">{label}</label>
                <Select
                    classNames={selectClassNames}
                    styles={selectStyles}
                    id={id}
                    name={id}
                    options={options}
                    isMulti
                    error={error}
                    components={{DropdownIndicator, Option: CustomOption}}
                    onChange={(selected) => {
                        formik.setFieldValue(id, selected);
                        formik.setFieldTouched(id, true, true);
                        onChange?.(selected);
                    }}
                    onBlur={() => formik.setFieldTouched(id, true)}
                    value={formik.values[id]}
                    placeholder={placeholder}
                />
                <FieldError error={error}/>
            </div>
        </div>
    );
};

MultiSelectInput.propTypes = {
    id: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    formik: PropTypes.object.isRequired,
    options: PropTypes.array.isRequired,
    placeholder: PropTypes.string,
    onChange: PropTypes.func,
};

/* ============================================================
   SwitchInput
   ============================================================ */

export const SwitchInput = ({id, label, formik, onChange}) => {
    return (
        <div className="switch-input-wrapper">
            <div className="form-check form-switch mb-0">
                <input
                    className="form-check-input"
                    id={id}
                    name={id}
                    type="checkbox"
                    role="switch"
                    checked={formik.values[id] ?? false}
                    onChange={(e) => {
                        formik.handleChange(e);
                        formik.setFieldTouched(id, true, true);
                        onChange?.(e);
                    }}
                />
                <label className="form-check-label text-black" htmlFor={id}>
                    {label}
                </label>
            </div>
        </div>
    );
};
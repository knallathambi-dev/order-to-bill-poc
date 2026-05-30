// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from "react";
import {useFormik} from "formik";
import DatePicker from "react-datepicker";
import {formatISODateTime, useSideMenu} from "@discobole/common-ui";
import ProductInventoryAPI from "../../service/ProductInventoryService";
import InfoSizeConf from "../../components/Administration/JobSpecification/InfoSizeConf";
import {getFieldError, hasFieldError, productsExportingValidationSchema,} from "./productsExportingValidation";
import "react-datepicker/dist/react-datepicker.css";

// ============================================
// CONSTANTS
// ============================================

const STATUS_OPTIONS = ["Created", "Cancelled", "Active", "Terminated", "Sold", "Aborted"];
const CONTENT_TYPE_OPTIONS = ["json", "csv"];

const DATE_RANGES = [
    {label: "Product Start Date", from: "productStartDateFrom", to: "productStartDateTo"},
    {label: "Product Creation Date", from: "productCreationDateFrom", to: "productCreationDateTo"},
];

const FIELD_MAPPING = {
    relatedParty: "relatedParty.partyOrPartyRole.id",
    productStartDateFrom: "startDate.gte",
    productStartDateTo: "startDate.lte",
    productCreationDateFrom: "creationDate.gte",
    productCreationDateTo: "creationDate.lte",
};

const INITIAL_VALUES = {
    contentType: "",
    status: "",
    relatedParty: "",
    productStartDateFrom: null,
    productStartDateTo: null,
    productCreationDateFrom: null,
    productCreationDateTo: null,
};

// ============================================
// HELPER FUNCTIONS
// ============================================

const transformFormValues = (values) => {
    const transformed = {...values};

    Object.keys(values).forEach((key) => {
        if (key.includes("Date") && values[key]) {
            transformed[key] = formatISODateTime(values[key]);
        }
    });

    Object.entries(FIELD_MAPPING).forEach(([formKey, apiKey]) => {
        if (formKey in transformed) {
            transformed[apiKey] = transformed[formKey];
            delete transformed[formKey];
        }
    });

    return transformed;
};

// ============================================
// REUSABLE FIELD COMPONENTS
// ============================================

/**
 * Renders an inline validation error message.
 *
 * The error icon (triangle) and visibility are handled by Boosted CSS via
 * `.is-invalid ~ .invalid-feedback`. This requires FieldError to be a
 * sibling of the element carrying `.is-invalid`.
 */
function FieldError({formik, name}) {
    const error = getFieldError(formik, name);
    if (!error) return null;

    return (
        <div className="invalid-feedback">
            {error}
        </div>
    );
}

function SelectField({id, label, options, formik, required = false}) {
    const isInvalid = hasFieldError(formik, id);
    const hasValue = formik.values[id];

    const selectClassName = `form-select mb-2 ${
        isInvalid ? "is-invalid" : hasValue ? "form-select-selected" : "select-disable"
    }`;

    return (
        <div className="row row-cols-4 mb-4">
            <div className="col-3">
                <label htmlFor={id} className="form-label">
                    {label} {required && <span className="text-danger">*</span>}
                </label>
            </div>
            <div className="col-6">
                <div className="position-relative w-100">
                    <select
                        className={selectClassName}
                        id={id}
                        name={id}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values[id]}
                    >
                        <option disabled value="">
                            Select {label}
                        </option>
                        {options.map((option) => (
                            <option key={option} value={option}>
                                {option}
                            </option>
                        ))}
                    </select>
                    <FieldError formik={formik} name={id}/>
                </div>
            </div>
        </div>
    );
}

function TextField({id, label, placeholder, formik, onClear}) {
    return (
        <div className="row row-cols-4 mb-4">
            <div className="col-3">
                <label htmlFor={id} className="form-label">
                    {label}
                </label>
            </div>
            <div className="col-6">
                <div className="position-relative w-100">
                    <input
                        type="text"
                        className="form-control mb-2"
                        id={id}
                        name={id}
                        onChange={formik.handleChange}
                        value={formik.values[id]}
                        placeholder={placeholder}
                    />
                    {formik.values[id] && (
                        <button
                            type="button"
                            className="btn btn-icon btn-link dismiss-btn custom-clear-btn"
                            onClick={onClear}
                            aria-label={`Clear ${id}`}
                        >
                            ✕
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

function DateRangeField({label, fromId, toId, formik, onClear}) {
    return (
        <div className="row row-cols-4 mb-4">
            <div className="col-3">
                <label className="form-label">{label}</label>
            </div>
            <div className="col">
                <div className="position-relative w-100">
                    <DatePicker
                        id={fromId}
                        name={fromId}
                        selected={formik.values[fromId]}
                        onChange={(date) => formik.setFieldValue(fromId, date)}
                        selectsStart
                        startDate={formik.values[fromId]}
                        endDate={formik.values[toId]}
                        placeholderText="Select start date"
                        className="form-control mb-2"
                        dateFormat="dd/MM/yyyy h:mm aa"
                        showTimeSelect
                    />
                    {formik.values[fromId] && (
                        <button
                            type="button"
                            className="btn btn-icon btn-link dismiss-btn custom-clear-btn"
                            onClick={() => onClear(fromId)}
                            aria-label={`Clear ${fromId}`}
                        >
                            ✕
                        </button>
                    )}
                </div>
            </div>
            <div className="col">
                <div className="position-relative w-100">
                    <DatePicker
                        id={toId}
                        name={toId}
                        selected={formik.values[toId]}
                        onChange={(date) => formik.setFieldValue(toId, date)}
                        selectsEnd
                        startDate={formik.values[fromId]}
                        endDate={formik.values[toId]}
                        minDate={formik.values[fromId]}
                        placeholderText="Select end date"
                        className="form-control mb-2"
                        dateFormat="dd/MM/yyyy h:mm aa"
                        showTimeSelect
                    />
                    {formik.values[toId] && (
                        <button
                            type="button"
                            className="btn btn-icon btn-link dismiss-btn custom-clear-btn"
                            onClick={() => onClear(toId)}
                            aria-label={`Clear ${toId}`}
                        >
                            ✕
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

// ============================================
// MAIN COMPONENT
// ============================================

function ProductsExporting() {
    const {isActiveNav} = useSideMenu();
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (values) => {
        try {
            setIsLoading(true);
            const transformedFields = transformFormValues(values);
            await ProductInventoryAPI.exportProducts(transformedFields, values.contentType);
        } catch (error) {
            console.error("Export failed:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const formik = useFormik({
        initialValues: INITIAL_VALUES,
        validationSchema: productsExportingValidationSchema,
        validateOnMount: true,
        validateOnChange: true,
        validateOnBlur: true,
        onSubmit: handleSubmit,
    });

    const clearField = (fieldId) => formik.setFieldValue(fieldId, "");
    const clearDateField = (fieldId) => formik.setFieldValue(fieldId, null);
    const canSubmit = formik.isValid;

    return (
        <div className={`py-1 content-wrapper ${isActiveNav ? "active-cont" : ""}`}>
            <div className="container-fluid py-3">
                <InfoSizeConf/>

                <div className="row">
                    <div className="col-12">
                        <h1 className="display-3 mb-1">Exporting</h1>
                    </div>
                </div>

                <div className="row mt-0 g-3 py-1">
                    <div className="col-12">
                        <form onSubmit={formik.handleSubmit} noValidate>
                            <div className="row">
                                <SelectField
                                    id="contentType"
                                    label="Content Type"
                                    options={CONTENT_TYPE_OPTIONS}
                                    formik={formik}
                                    required
                                />
                                <SelectField
                                    id="status"
                                    label="Status"
                                    options={STATUS_OPTIONS}
                                    formik={formik}
                                />
                                <TextField
                                    id="relatedParty"
                                    label="Related Party"
                                    placeholder="Enter Party ID"
                                    formik={formik}
                                    onClear={() => clearField("relatedParty")}
                                />
                                {DATE_RANGES.map(({label, from, to}) => (
                                    <DateRangeField
                                        key={from}
                                        label={label}
                                        fromId={from}
                                        toId={to}
                                        formik={formik}
                                        onClear={clearDateField}
                                    />
                                ))}
                            </div>

                            <div className="row mb-4 mt-2">
                                <div className="d-flex justify-content-end">
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        disabled={isLoading || !canSubmit}
                                    >
                                        {isLoading ? (
                                            <>
                                                <span
                                                    className="spinner-border spinner-border-sm me-1"
                                                    aria-hidden="true"
                                                />
                                                <output role="status">Exporting...</output>
                                            </>
                                        ) : (
                                            "Export"
                                        )}
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductsExporting;
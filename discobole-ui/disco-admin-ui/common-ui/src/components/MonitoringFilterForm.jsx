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
// NOTE: Adjust this relative path to match your common-ui source structure
import {FilterToolbar} from "./FilterToolbar";

/**
 * Helper: check if a form value is considered empty.
 */
const isValueEmpty = (value) =>
    value === null || value === undefined || value === "" || (Array.isArray(value) && value.length === 0);

/**
 * Generic filter form component.
 *
 * Handles formik setup, filter param building, clear/reset, expand/collapse,
 * and the FilterToolbar — so each MFE only needs to define its field layout.
 *
 * @param {object}   props
 * @param {object}   props.initialValues     - Formik initial values
 * @param {object}   props.emptyValues       - Values to use when clearing the form (defaults to initialValues with all values emptied)
 * @param {function} props.buildFilterParams  - (values) => params object sent to the API
 * @param {function} props.onFilterSubmit     - Callback receiving the built params
 * @param {function} props.renderMainFilters  - (formik, clearField) => JSX for always-visible filters
 * @param {function} props.renderExtraFilters - (formik, clearField) => JSX for expandable filters (optional)
 * @param {number}   props.columns           - Number of columns per row (default: 4)
 */
const MonitoringFilterForm = ({
                                  initialValues,
                                  emptyValues,
                                  buildFilterParams,
                                  onFilterSubmit,
                                  renderMainFilters,
                                  renderExtraFilters,
                                  columns = 4,
                              }) => {
    const [showMoreFilters, setShowMoreFilters] = useState(false);

    const resolvedEmptyValues = emptyValues ?? Object.fromEntries(
        Object.entries(initialValues).map(([key, value]) => [
            key,
            Array.isArray(value) ? [] : (value === null || value instanceof Date) ? null : "",
        ])
    );

    const formik = useFormik({
        initialValues,
        onSubmit: (values) => {
            onFilterSubmit(buildFilterParams(values));
        },
    });

    const clearField = (fieldId) => {
        const emptyValue = resolvedEmptyValues[fieldId] !== undefined ? resolvedEmptyValues[fieldId] : "";
        const newValues = {...formik.values, [fieldId]: emptyValue};
        formik.setValues(newValues);
        onFilterSubmit(buildFilterParams(newValues));
    };

    const clearForm = () => {
        formik.setValues(resolvedEmptyValues);
        formik.setTouched({});
        onFilterSubmit({});
    };

    const isFormEmpty = Object.values(formik.values).every(isValueEmpty);

    return (
        <form
            onSubmit={(e) => {
                e.preventDefault();
                formik.handleSubmit();
            }}
            className="d-flex flex-column"
        >
            {renderMainFilters(formik, clearField)}

            {showMoreFilters && renderExtraFilters?.(formik, clearField)}

            <FilterToolbar
                expanded={showMoreFilters}
                onToggle={renderExtraFilters ? () => setShowMoreFilters(!showMoreFilters) : undefined}
                onClear={clearForm}
                disabled={isFormEmpty}
            />
        </form>
    );
};

export default MonitoringFilterForm;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect} from "react";
import PropTypes from "prop-types";
import {useFormik} from "formik";
import {DateInput, SwitchInput, TextInput} from "@discobole/common-ui";
import {
    APPROXIMATE_COUNT,
    AVERAGE_LEAD_TIME,
    CUSTOM_PERIOD,
    DAY_IN_MS,
    MIN_MAX_LEAD_TIME,
    TIME_PERIOD_END,
    TIME_PERIOD_START,
} from "../../utils/constants";
import {getFormattedValue} from "../../service/orchestrationUtils.js";

const buildInitialValues = (reportBy, filterParams) => {
    const defaultData = {
        [reportBy]: "",
        [APPROXIMATE_COUNT]: "",
        [TIME_PERIOD_START]: "",
        [TIME_PERIOD_END]: "",
        [AVERAGE_LEAD_TIME]: false,
        [MIN_MAX_LEAD_TIME]: false,
        [CUSTOM_PERIOD]: false,
    };

    const filterValues = Object.fromEntries(
        Array.from(filterParams.entries()).map(([key, value]) => {
            const formatted = getFormattedValue(key, value);
            if ([TIME_PERIOD_START, TIME_PERIOD_END].includes(key)) {
                return [key, formatted ? new Date(formatted) : ""];
            }
            return [key, formatted];
        })
    );

    return {
        ...defaultData,
        ...filterValues,
    };
};

const validate = (values, reportBy) => {
    const errors = {};

    if (!values[reportBy]) {
        errors[reportBy] = "This field is required *";
    }

    if (!values[APPROXIMATE_COUNT] && !values[CUSTOM_PERIOD]) {
        errors[APPROXIMATE_COUNT] = "This field is required *";
    }

    if (values[APPROXIMATE_COUNT] && !values[CUSTOM_PERIOD]) {
        if (isNaN(+values[APPROXIMATE_COUNT])) {
            errors[APPROXIMATE_COUNT] = "This field must be a number";
        } else if (values[APPROXIMATE_COUNT] < 1 || values[APPROXIMATE_COUNT] > 50) {
            errors[APPROXIMATE_COUNT] = "Approximate count must be between 1 and 50";
        }
    }

    if (values[CUSTOM_PERIOD]) {
        if (!values[TIME_PERIOD_START] || !values[TIME_PERIOD_END]) {
            errors.dates = "Please select both start and end date.";
        } else {
            const diffTime =
                new Date(values[TIME_PERIOD_END]).getTime() -
                new Date(values[TIME_PERIOD_START]).getTime();
            const diffDays = Math.ceil(diffTime / DAY_IN_MS);

            if (diffDays <= 0 || diffDays > 2) {
                errors.dates = "Please select a duration up to 2 days";
            }
        }
    }

    if (!values[AVERAGE_LEAD_TIME] && !values[MIN_MAX_LEAD_TIME]) {
        errors.leadTime = "Please check at least one (Average or Min/Max)";
    }

    return errors;
};

const StatisticsFilter = ({onFilterSubmit, resetData, customField, reportBy, filterParams}) => {
    const initialValues = buildInitialValues(reportBy, filterParams);

    const formik = useFormik({
        initialValues,
        validate: (values) => validate(values, reportBy),
        validateOnChange: true,
        validateOnBlur: true,
        onSubmit: (values) => onFilterSubmit(values),
    });

    const isCustomPeriod = formik.values[CUSTOM_PERIOD];

    useEffect(() => {
        formik.validateForm(formik.values);
    }, [formik.values]); // eslint-disable-line react-hooks/exhaustive-deps

    const isFormEmpty = () =>
        !formik.values[reportBy] &&
        !formik.values[CUSTOM_PERIOD] &&
        !formik.values[TIME_PERIOD_START] &&
        !formik.values[TIME_PERIOD_END] &&
        !formik.values[APPROXIMATE_COUNT] &&
        !formik.values[AVERAGE_LEAD_TIME] &&
        !formik.values[MIN_MAX_LEAD_TIME];

    const clearField = (id) => {
        formik.setFieldValue(id, "");
        formik.setFieldTouched(id, true, true);
    };

    const clearForm = () => {
        formik.resetForm();
        resetData();
    };

    const handleCustomPeriodToggle = () => {
        formik.setFieldValue(TIME_PERIOD_START, "");
        formik.setFieldValue(TIME_PERIOD_END, "");
        formik.setFieldValue(APPROXIMATE_COUNT, "");
        formik.setFieldTouched(TIME_PERIOD_START, false);
        formik.setFieldTouched(TIME_PERIOD_END, false);
        formik.setFieldTouched(APPROXIMATE_COUNT, false);
    };

    const handleGenerate = async () => {
        const touchedAll = Object.fromEntries(
            Object.keys(formik.values).map((key) => [key, true])
        );
        await formik.setTouched(touchedAll, true);

        const errors = await formik.validateForm();

        if (Object.keys(errors).length === 0) {
            formik.handleSubmit();
            return;
        }

        const firstErrorKey = Object.keys(errors)[0];
        const firstErrorEl = document.getElementById(firstErrorKey);
        firstErrorEl?.focus();
        firstErrorEl?.scrollIntoView({behavior: "smooth", block: "center"});
    };

    const showDatesError =
        formik.errors.dates &&
        (formik.touched[TIME_PERIOD_START] || formik.touched[TIME_PERIOD_END]);

    const injectedFormikStart = {
        ...formik,
        errors: {
            ...formik.errors,
            [TIME_PERIOD_START]: showDatesError ? formik.errors.dates : formik.errors[TIME_PERIOD_START],
        },
    };

    const injectedFormikEnd = {
        ...formik,
        errors: {
            ...formik.errors,
            [TIME_PERIOD_END]: showDatesError ? formik.errors.dates : formik.errors[TIME_PERIOD_END],
        },
    };

    return (
        <form onSubmit={(e) => e.preventDefault()}>
            <div className="position-relative">
                <div className="row g-2">
                    <TextInput
                        id={reportBy}
                        label={`${customField.label} *`}
                        formik={formik}
                        onClear={clearField}
                        placeholder={customField.placeholder}
                    />

                    {!isCustomPeriod && (
                        <TextInput
                            id={APPROXIMATE_COUNT}
                            label="Approximate Count *"
                            formik={formik}
                            onClear={clearField}
                            placeholder="Enter count (1-50)"
                        />
                    )}

                    {isCustomPeriod && (
                        <>
                            <DateInput
                                id={TIME_PERIOD_START}
                                label="Time Period Start *"
                                formik={injectedFormikStart}
                                startId={TIME_PERIOD_START}
                                endId={TIME_PERIOD_END}
                                isStart
                                dateFormat="dd/MM/yyyy h:mm aa"
                                showTimeSelect
                                onClear={clearField}
                            />
                            <DateInput
                                id={TIME_PERIOD_END}
                                label="Time Period End *"
                                formik={injectedFormikEnd}
                                startId={TIME_PERIOD_START}
                                endId={TIME_PERIOD_END}
                                isStart={false}
                                dateFormat="dd/MM/yyyy h:mm aa"
                                showTimeSelect
                                onClear={clearField}
                            />
                        </>
                    )}

                    <div className="col-auto d-flex align-items-center" style={{ paddingTop: '18px' }}>
                        <SwitchInput
                            id={CUSTOM_PERIOD}
                            label="Customized period"
                            formik={formik}
                            onChange={handleCustomPeriodToggle}
                        />
                    </div>
                </div>

                <div className="row align-items-center mt-3">
                    <div className="col-6 d-flex gap-3 align-items-center">
                        <div className="form-check mb-0">
                            <input
                                className="form-check-input"
                                type="checkbox"
                                id={AVERAGE_LEAD_TIME}
                                name={AVERAGE_LEAD_TIME}
                                onChange={(e) => {
                                    formik.handleChange(e);
                                    formik.setFieldTouched(AVERAGE_LEAD_TIME, true, true);
                                }}
                                checked={formik.values[AVERAGE_LEAD_TIME] ?? false}
                            />
                            <label className="form-check-label text-black" htmlFor={AVERAGE_LEAD_TIME}>
                                Average
                            </label>
                        </div>
                        <div className="form-check mb-0">
                            <input
                                className="form-check-input"
                                type="checkbox"
                                id={MIN_MAX_LEAD_TIME}
                                name={MIN_MAX_LEAD_TIME}
                                onChange={(e) => {
                                    formik.handleChange(e);
                                    formik.setFieldTouched(MIN_MAX_LEAD_TIME, true, true);
                                }}
                                checked={formik.values[MIN_MAX_LEAD_TIME] ?? false}
                            />
                            <label className="form-check-label text-black" htmlFor={MIN_MAX_LEAD_TIME}>
                                Min/Max
                            </label>
                        </div>
                        {formik.errors.leadTime &&
                            (formik.touched[AVERAGE_LEAD_TIME] || formik.touched[MIN_MAX_LEAD_TIME]) && (
                                <span className="text-danger small">
                                    {formik.errors.leadTime}
                                </span>
                            )}
                    </div>
                    <div className="col-6 d-flex justify-content-end">
                        <button
                            type="button"
                            className="btn btn-secondary me-3"
                            onClick={clearForm}
                            disabled={isFormEmpty()}
                        >
                            Reset
                        </button>
                        <button
                            type="button"
                            className="btn btn-primary"
                            onClick={handleGenerate}
                        >
                            Generate report
                        </button>
                    </div>
                </div>
            </div>
        </form>
    );
};

StatisticsFilter.propTypes = {
    reportBy: PropTypes.string.isRequired,
    resetData: PropTypes.func.isRequired,
    onFilterSubmit: PropTypes.func.isRequired,
    customField: PropTypes.shape({
        name: PropTypes.string.isRequired,
        label: PropTypes.string.isRequired,
        placeholder: PropTypes.string,
        options: PropTypes.arrayOf(PropTypes.string),
        optionsValues: PropTypes.arrayOf(PropTypes.string),
    }).isRequired,
    filterParams: PropTypes.object.isRequired,
};

export default StatisticsFilter;
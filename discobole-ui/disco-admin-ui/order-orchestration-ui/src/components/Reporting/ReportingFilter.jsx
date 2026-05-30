// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect} from 'react';
import {useFormik} from 'formik';
import PropTypes from 'prop-types';
import {DateInput, SelectInput, SwitchInput, TextInput} from '@discobole/common-ui';
import {getFilterValues, getInitialValues} from '../../service/orchestrationUtils.js';
import {
    CUSTOM_PERIOD,
    DAY_IN_MS,
    NUMBER_OF_COLUMNS,
    PERIOD,
    PERIOD_DAY,
    PERIOD_MONTH,
    PERIOD_WEEK,
    RECEIVED_DATE_END,
    RECEIVED_DATE_START,
} from '../../utils/constants.js';

const PERIOD_NAMES = {
    [PERIOD_DAY]: 'days',
    [PERIOD_WEEK]: 'weeks',
    [PERIOD_MONTH]: 'months',
};

const PERIOD_LIMITS = {
    [PERIOD_DAY]: 30,
    [PERIOD_WEEK]: 24,
    [PERIOD_MONTH]: 24,
};

const PERIOD_OPTIONS = [
    {value: PERIOD_DAY.toString(), label: 'Daily'},
    {value: PERIOD_WEEK.toString(), label: 'Weekly'},
    {value: PERIOD_MONTH.toString(), label: 'Monthly'},
];

const getPeriodName = (period) => PERIOD_NAMES[+period] ?? 'days';
const getPeriodLimit = (period) => PERIOD_LIMITS[+period] ?? 30;

const ReportingFilter = ({
                             onFilterSubmit,
                             from,
                             changeGraphData,
                             graphDefaultValues,
                             graphDefaultValuesWithoutParams,
                             inputsData,
                             filterParams,
                         }) => {
    const isHistoryReport = from === 'history';

    const validateCustomizedPeriod = (values, errors, periodName, periodLimit) => {
        if (!values[RECEIVED_DATE_START] || !values[RECEIVED_DATE_END]) {
            errors.dates = 'Please select both start and end date.';
        }

        if (values[PERIOD] && values[RECEIVED_DATE_START] && values[RECEIVED_DATE_END]) {
            const diffDays = Math.ceil(
                (new Date(values[RECEIVED_DATE_END]) - new Date(values[RECEIVED_DATE_START])) / DAY_IN_MS
            );

            if (diffDays < 2 * +values[PERIOD]) {
                errors.duration = `Duration must be at least 2 ${periodName}`;
            }
            if (diffDays > periodLimit * +values[PERIOD]) {
                errors.duration = `Duration must not exceed ${periodLimit} ${periodName}`;
            }
        }
    };

    const formik = useFormik({
        initialValues: {
            ...getInitialValues(filterParams, inputsData),
            ...graphDefaultValues,
        },
        validate: (values) => {
            const errors = {};
            const periodName = getPeriodName(values[PERIOD]);
            const periodLimit = getPeriodLimit(values[PERIOD]);

            if (values[CUSTOM_PERIOD] && isHistoryReport) {
                validateCustomizedPeriod(values, errors, periodName, periodLimit);
            }

            if (isHistoryReport) {
                const numCols = +values[NUMBER_OF_COLUMNS];
                if (isNaN(numCols)) {
                    errors[NUMBER_OF_COLUMNS] = 'This field must be a number';
                } else if (numCols < 2) {
                    errors[NUMBER_OF_COLUMNS] = `Number of ${periodName} must be at least 2`;
                } else if (numCols > periodLimit) {
                    errors[NUMBER_OF_COLUMNS] = `Number of ${periodName} must not exceed ${periodLimit}`;
                }
            }

            return errors;
        },
        onSubmit: (values) => {
            if (isHistoryReport) {
                changeGraphData('set', values);
            }

            if (
                isHistoryReport &&
                values[CUSTOM_PERIOD] &&
                (!values[RECEIVED_DATE_START] || !values[RECEIVED_DATE_END])
            ) {
                return;
            }

            onFilterSubmit(getFilterValues(values, inputsData));
        },
    });

    useEffect(() => {
        formik.validateForm(formik.values);
    }, [
        formik.values[RECEIVED_DATE_START],
        formik.values[RECEIVED_DATE_END],
        formik.values[NUMBER_OF_COLUMNS],
        formik.values[PERIOD],
        formik.values[CUSTOM_PERIOD],
    ]);

    const clearField = (fieldId) => {
        if (fieldId === PERIOD) {
            formik.setFieldValue(fieldId, graphDefaultValuesWithoutParams[PERIOD]);
            changeGraphData(PERIOD);
            formik.errors = {};
        } else if (fieldId === NUMBER_OF_COLUMNS) {
            formik.setFieldValue(fieldId, graphDefaultValuesWithoutParams[NUMBER_OF_COLUMNS]);
        } else {
            formik.setFieldValue(fieldId, '');
        }
        formik.submitForm();
    };

    const clearForm = () => {
        const initialValues = {
            ...getInitialValues(new URLSearchParams(), inputsData),
            ...graphDefaultValuesWithoutParams,
        };
        Object.keys(initialValues).forEach((key) => formik.setFieldValue(key, initialValues[key]));

        if (isHistoryReport) changeGraphData('clearForm');
        onFilterSubmit({});
    };

    const handleCustomPeriodToggle = () => {
        changeGraphData(CUSTOM_PERIOD);
        formik.setFieldValue(CUSTOM_PERIOD, !formik.values[CUSTOM_PERIOD]);
        formik.setFieldValue(RECEIVED_DATE_START, null);
        formik.setFieldValue(RECEIVED_DATE_END, null);
        formik.setFieldTouched(RECEIVED_DATE_START, false);
        formik.setFieldTouched(RECEIVED_DATE_END, false);
    };

    const isFormEmpty = () =>
        !formik.values[RECEIVED_DATE_START] &&
        !formik.values[RECEIVED_DATE_END] &&
        (!isHistoryReport || (!formik.values[PERIOD] && !formik.values[NUMBER_OF_COLUMNS]));

    const showDateErrors = isHistoryReport &&
        formik.errors.dates &&
        (formik.touched[RECEIVED_DATE_START] || formik.touched[RECEIVED_DATE_END]);

    const injectedFormikStart = {
        ...formik,
        errors: {
            ...formik.errors,
            [RECEIVED_DATE_START]: showDateErrors ? formik.errors.dates : formik.errors[RECEIVED_DATE_START],
        },
    };

    const injectedFormikEnd = {
        ...formik,
        errors: {
            ...formik.errors,
            [RECEIVED_DATE_END]: showDateErrors ? formik.errors.dates : formik.errors[RECEIVED_DATE_END],
        },
    };

    return (
        <form onSubmit={(e) => {
            e.preventDefault();
            formik.handleSubmit();
        }}>
            <div className="position-relative">
                <div className={`row g-2 ${!isHistoryReport ? 'row-cols-6' : ''} align-items-end`}>

                    {/* ── History: period select + number of columns ────── */}
                    {isHistoryReport && (
                        <>
                            <SelectInput
                                id={PERIOD}
                                label="Report type"
                                formik={formik}
                                options={PERIOD_OPTIONS}
                                placeholder="Select period"
                                onClear={() => clearField(PERIOD)}
                            />

                            {!formik.values[CUSTOM_PERIOD] && (
                                <TextInput
                                    id={NUMBER_OF_COLUMNS}
                                    label={`Number of ${getPeriodName(formik.values[PERIOD])}`}
                                    formik={formik}
                                    onClear={clearField}
                                    placeholder="Number of columns"
                                />
                            )}
                        </>
                    )}

                    {/* ── Date pickers (custom period or non-history) ───── */}
                    {(formik.values[CUSTOM_PERIOD] || !isHistoryReport) && (
                        <>
                            <DateInput
                                id={RECEIVED_DATE_START}
                                label="Report Received Date Start"
                                formik={injectedFormikStart}
                                startId={RECEIVED_DATE_START}
                                endId={RECEIVED_DATE_END}
                                isStart
                                dateFormat="dd/MM/yyyy h:mm aa"
                                showTimeSelect
                                onClear={clearField}
                            />
                            <DateInput
                                id={RECEIVED_DATE_END}
                                label="Report Received Date End"
                                formik={injectedFormikEnd}
                                startId={RECEIVED_DATE_START}
                                endId={RECEIVED_DATE_END}
                                isStart={false}
                                dateFormat="dd/MM/yyyy h:mm aa"
                                showTimeSelect
                                onClear={clearField}
                            />
                        </>
                    )}

                    {/* ── History: custom period toggle ────────────────── */}
                    {isHistoryReport && (
                        <div className="col-auto d-flex align-items-center" style={{paddingTop: '18px'}}>
                            <SwitchInput
                                id={CUSTOM_PERIOD}
                                label="Customized period"
                                formik={formik}
                                onChange={handleCustomPeriodToggle}
                            />
                        </div>
                    )}

                    {/* ── Action buttons ───────────────────────────────── */}
                    <div className="col-4 d-flex align-items-center" style={{paddingTop: '18px'}}>
                        <button
                            type="button"
                            className="btn btn-secondary me-3"
                            onClick={clearForm}
                            disabled={isFormEmpty()}
                        >
                            Reset
                        </button>
                        <button
                            type="submit"
                            className="btn btn-primary"
                            disabled={!formik.isValid || isFormEmpty()}
                        >
                            Generate report
                        </button>
                    </div>

                    {/* ── Duration error ────────────────────────────────── */}
                    {isHistoryReport && formik.errors.duration && (
                        <span className="text-danger">{formik.errors.duration}</span>
                    )}
                </div>
            </div>
        </form>
    );
};

ReportingFilter.propTypes = {
    onFilterSubmit: PropTypes.func.isRequired,
    from: PropTypes.string,
    changeGraphData: PropTypes.func,
    graphDefaultValues: PropTypes.object,
    graphDefaultValuesWithoutParams: PropTypes.object,
    inputsData: PropTypes.object,
    filterParams: PropTypes.object,
};

export default ReportingFilter;
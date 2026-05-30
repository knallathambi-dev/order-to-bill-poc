// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {useFormik} from "formik";
import {Link} from "react-router-dom";
import DatePicker from "react-datepicker";
import Select from "react-select";
import {
    BootstrapTooltip,
    ClearFieldButton,
    CustomOption,
    DateInput,
    DropdownIndicator,
    Modal,
    selectStyles,
} from "@discobole/common-ui";
import ProductInventoryAPI from "../../../../service/ProductInventoryAPI";
import ProductInventoryService from "../../../../service/ProductInventoryService";
import {buildValidationSchema, getFieldError, hasFieldError} from "./jobSpecificationValidation";
import "react-datepicker/dist/react-datepicker.css";

// ============================================
// CONSTANTS
// ============================================

const INITIAL_VALUES = {
    jobSpecificationType: "",
    contentType: "",
    name: "",
    fields: [],
    productStatus: [],
    relatedParty: "",
    productStartDateFrom: null,
    productStartDateTo: null,
    productCreationDateFrom: null,
    productCreationDateTo: null,
    productLastUpdateDateFrom: null,
    productLastUpdateDateTo: null,
    occurrence: "OneTimeJobScheduler",
    plannedDate: null,
    repeatEvery: 1,
    repeatInterval: "Day",
    executionTime: null,
    repeatFrom: null,
    repeatTo: null,
    purgeType: "",
    lifecycleStatus: [],
    productTerminationDateFrom: null,
    productTerminationDateTo: null,
    endDateFrom: null,
    endDateTo: null,
    importType: "",
    selectedFile: null,
};

const JOB_TYPES = [
    {value: "TerminationJobSpecification", label: "Termination Job Specification"},
    {value: "ImportJobSpecification", label: "Import Job Specification"},
    {value: "ExportJobSpecification", label: "Export Job Specification"},
    {value: "PurgeJobSpecification", label: "Purge Job Specification"},
];

const STATUS_LIST = [
    {value: "Created", label: "Created"},
    {value: "Active", label: "Active"},
    {value: "Terminated", label: "Terminated"},
];

const PURGE_STATUS_OPTIONS = [
    {value: "Aborted", label: "Aborted"},
    {value: "Cancelled", label: "Cancelled"},
    {value: "Terminated", label: "Terminated"},
];

const LIFECYCLE_STATUS_OPTIONS = [
    {value: "Terminated", label: "Terminated"},
];

const FIELDS_LIST = [
    {value: "id", label: "Id"},
    {value: "href", label: "Href"},
    {value: "isBundle", label: "Is Bundle"},
    {value: "isCustomerVisible", label: "Is Customer Visible"},
    {value: "name", label: "Name"},
    {value: "orderDate", label: "Order Date"},
    {value: "productSerialNumber", label: "Product Serial Number"},
    {value: "startDate", label: "Start Date"},
    {value: "billingAccount", label: "Billing Account"},
    {value: "agreement", label: "Agreement"},
    {value: "terminationDate", label: "Termination Date"},
    {value: "place", label: "Place"},
    {value: "productCharacteristic", label: "Product Characteristic"},
    {value: "productOffering", label: "Product Offering"},
    {value: "productSpecification", label: "Product Specification"},
    {value: "productOrderItem", label: "Product Order Item"},
    {value: "productPrice", label: "Product Price"},
    {value: "productRelationship", label: "Product Relationship"},
    {value: "productTerm", label: "Commitment Term"},
    {value: "realizingResource", label: "Realizing Resource"},
    {value: "realizingService", label: "Realizing Service"},
    {value: "relatedParty", label: "Related Party"},
    {value: "status", label: "Status"},
    {value: "operationalStatus", label: "Operational Status"},
    {value: "creationDate", label: "Creation Date"},
    {value: "lastUpdateDate", label: "Last Update Date"},
    {value: "@type", label: "@Type"},
    {value: "statusChange", label: "Status Change"},
    {value: "operationalStatusChange", label: "Operational Status Change"},
];

const OCCURRENCE_OPTIONS_FULL = [
    {value: "OneTimeJobScheduler", label: "One time"},
    {value: "RecurringJobScheduler", label: "Recurring"},
    {value: "ImmediateJobScheduler", label: "Immediate"},
];

const OCCURRENCE_OPTIONS_RESTRICTED = [
    {value: "OneTimeJobScheduler", label: "One time"},
    {value: "ImmediateJobScheduler", label: "Immediate"},
];

const RESTRICTED_TYPES = ["TerminationJobSpecification", "ImportJobSpecification"];

const INTERVAL_OPTIONS = ["Day", "Week", "Month", "Year"];

const CONTENT_TYPE_OPTIONS = [
    {value: "json", label: "JSON"},
    {value: "csv", label: "CSV"},
];

const IMPORT_TYPE_OPTIONS = [
    {value: "merge", label: "Merge"},
    {value: "insert", label: "Insert"},
];

const PURGE_TYPE_OPTIONS = [
    {value: "PurgeProduct", label: "Purge Product"},
    {value: "PurgeJob", label: "Purge Job"},
];

const EXPORT_DATE_RANGES = [
    {label: "Product Start Date", from: "productStartDateFrom", to: "productStartDateTo"},
    {label: "Product Creation Date", from: "productCreationDateFrom", to: "productCreationDateTo"},
    {label: "Product Last Update Date", from: "productLastUpdateDateFrom", to: "productLastUpdateDateTo"},
];

// ============================================
// HELPER FUNCTIONS
// ============================================

const formatISODateTime = (date) => {
    if (!date) return null;
    return new Date(date).toISOString();
};

const formatISODate = (date) => {
    if (!date) return null;
    return new Date(date).toISOString().split("T")[0];
};

const formatTimeString = (time) => {
    if (!time) return null;
    return time.split("T")[1]?.split(".")[0] ?? null;
};

const getNow = () => new Date();

const getStartOfDay = () => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
};

const getEndOfDay = () => {
    const d = new Date();
    d.setHours(23, 59, 59, 999);
    return d;
};

const isToday = (date) => date?.toDateString() === new Date().toDateString();

// ============================================
// QUERY / REQUEST BUILDERS
// ============================================

const DATE_FIELD_PARAMS = [
    {key: "productStartDateFrom", param: "startDate.gte"},
    {key: "productStartDateTo", param: "startDate.lte"},
    {key: "productCreationDateFrom", param: "creationDate.gte"},
    {key: "productCreationDateTo", param: "creationDate.lte"},
    {key: "productLastUpdateDateFrom", param: "lastUpdateDate.gte"},
    {key: "productLastUpdateDateTo", param: "lastUpdateDate.lte"},
    {key: "endDateFrom", param: "activePeriod.startDateTime.gte"},
    {key: "endDateTo", param: "activePeriod.endDateTime.lte"},
    {key: "productTerminationDateFrom", param: "terminationDate.gte"},
    {key: "productTerminationDateTo", param: "terminationDate.lte"},
];

const buildQueryParts = (values) => {
    const parts = [];

    if (values.productStatus?.length > 0) {
        values.productStatus.forEach((status) => parts.push(`status=${status}`));
    }

    if (values.lifecycleStatus?.length > 0) {
        parts.push(`lifecycleStatus=${values.lifecycleStatus.map((opt) => opt.value).join(",")}`);
    }

    if (values.relatedParty) {
        parts.push(`relatedParty=${values.relatedParty}`);
    }

    DATE_FIELD_PARAMS.forEach(({key, param}) => {
        if (values[key]) parts.push(`${param}=${formatISODateTime(values[key])}`);
    });

    return parts;
};

const buildSchedule = (values) => {
    const builders = {
        ImmediateJobScheduler: () => ({"@type": "ImmediateJobScheduler"}),
        OneTimeJobScheduler: () => ({
            "@type": "OneTimeJobScheduler",
            plannedDate: formatISODateTime(values.plannedDate),
        }),
        RecurringJobScheduler: () => ({
            "@type": "RecurringJobScheduler",
            frequency: {amount: values.repeatEvery, timePeriod: values.repeatInterval},
            scheduledPeriod: {
                startDate: formatISODate(values.repeatFrom),
                endDate: values.repeatTo ? formatISODate(values.repeatTo) : null,
            },
            executionTime: formatTimeString(values.executionTime),
        }),
    };
    return builders[values.occurrence]?.() || {};
};

const buildRequestBody = (values) => {
    const body = {
        "@type": values.jobSpecificationType,
        schedule: buildSchedule(values),
        name: values.name,
    };

    if (values.jobSpecificationType === "PurgeJobSpecification") {
        body.purgeType = values.purgeType;
        body.query = buildQueryParts(values).join("&");
    } else if (values.jobSpecificationType === "ExportJobSpecification") {
        body.contentType = values.contentType;
        body.fields = values.fields || [];
        body.query = buildQueryParts(values).join("&");
    } else if (values.jobSpecificationType === "ImportJobSpecification") {
        body.contentType = values.contentType;
        body.importType = values.importType;
    }

    return body;
};

// ============================================
// REUSABLE FIELD COMPONENTS
// ============================================

/**
 * Renders a <label> with a coloured asterisk for required fields.
 */
function RequiredLabel({htmlFor, children}) {
    return (
        <label htmlFor={htmlFor} className="form-label">
            {children}
            <span className="text-primary"> *</span>
        </label>
    );
}

/**
 * Renders an inline validation error message.
 *
 * The error icon (triangle) and the visibility of this div are both handled
 * by the Boosted / Orange Design System CSS via the sibling selector
 * `.is-invalid ~ .invalid-feedback`. This requires the FieldError to be
 * a sibling of the element carrying the `.is-invalid` class.
 *
 * Errors are shown immediately (no `touched` gate) because the parent
 * component applies `.is-invalid` based on `formik.errors` directly
 * (validate-on-mount behaviour).
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

/**
 * Native <select> with label, validation icon, and optional required asterisk.
 */
function SelectField({id, label, options, formik, onChange, required = false, disabled = false, placeholder}) {
    const isInvalid = hasFieldError(formik, id);

    return (
        <div className="mb-3">
            {required ? (
                <RequiredLabel htmlFor={id}>{label}</RequiredLabel>
            ) : (
                <label htmlFor={id} className="form-label">{label}</label>
            )}
            <select
                id={id}
                name={id}
                className={`form-select ${isInvalid ? "is-invalid" : ""}`}
                onBlur={formik.handleBlur}
                onChange={onChange || formik.handleChange}
                value={formik.values[id]}
                disabled={disabled}
            >
                <option disabled value="">
                    {placeholder || `Select ${label}`}
                </option>
                {options.map((opt) => {
                    const val = typeof opt === "string" ? opt : opt.value;
                    const lbl = typeof opt === "string" ? opt : opt.label;
                    return (
                        <option key={val} value={val}>
                            {lbl}
                        </option>
                    );
                })}
            </select>
            <FieldError formik={formik} name={id}/>
        </div>
    );
}

/**
 * Text <input> with label, clear button, validation icon, and optional required asterisk.
 */
function TextField({id, label, placeholder, formik, onChange, onClear, required = false}) {
    const isInvalid = hasFieldError(formik, id);

    return (
        <div className="mb-3 text-field-wrapper">
            {required ? (
                <RequiredLabel htmlFor={id}>{label}</RequiredLabel>
            ) : (
                <label htmlFor={id} className="form-label">{label}</label>
            )}
            <input
                type="text"
                id={id}
                name={id}
                className={`form-control ${isInvalid ? "is-invalid" : ""}`}
                placeholder={placeholder}
                onBlur={formik.handleBlur}
                onChange={onChange || formik.handleChange}
                value={formik.values[id]}
            />
            {formik.values[id] && onClear && <ClearFieldButton onClick={onClear}/>}
            <FieldError formik={formik} name={id}/>
        </div>
    );
}

/**
 * react-select multi-select with label, validation icon, and optional required asterisk.
 */
function MultiSelectField({id, label, options, formik, onChange, value, required = false, placeholder}) {
    const isInvalid = hasFieldError(formik, id);

    return (
        <div className="mb-3">
            {required ? (
                <RequiredLabel htmlFor={id}>{label}</RequiredLabel>
            ) : (
                <label htmlFor={id} className="form-label">{label}</label>
            )}
            <Select
                isMulti
                id={id}
                name={id}
                options={options}
                className={`basic-multi-select ${isInvalid ? "is-invalid" : ""}`}
                classNamePrefix="select"
                placeholder={placeholder || `Select ${label}`}
                onChange={onChange}
                value={value}
                styles={selectStyles}
                components={{DropdownIndicator, Option: CustomOption}}
            />
            <FieldError formik={formik} name={id}/>
        </div>
    );
}

/**
 * Paired From / To date range using the shared DateInput component.
 */
function DateRangeRow({label, fromId, toId, formik, dateFormat, showTimeSelect, onClear}) {
    return (
        <div className="mb-3">
            <div className="row">
                <DateInput
                    id={fromId}
                    label={`${label} From`}
                    formik={formik}
                    startId={fromId}
                    endId={toId}
                    isStart
                    dateFormat={dateFormat}
                    showTimeSelect={showTimeSelect}
                    onClear={onClear}
                />
                <DateInput
                    id={toId}
                    label={`${label} To`}
                    formik={formik}
                    startId={fromId}
                    endId={toId}
                    isStart={false}
                    dateFormat={dateFormat}
                    showTimeSelect={showTimeSelect}
                    onClear={onClear}
                />
            </div>
        </div>
    );
}

// ============================================
// MAIN COMPONENT
// ============================================

function CreateJobSpecificationDetails() {
    const [showModal, setShowModal] = useState(false);
    const [showErrorModal, setShowErrorModal] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [jobSpecId, setJobSpecId] = useState(null);
    const [showFieldInfo, setShowFieldInfo] = useState(true);
    const selectedFileRef = useRef(null);

    // Schema key drives reactive schema rebuilds via useMemo
    const [schemaKey, setSchemaKey] = useState({jobType: "", occurrence: "OneTimeJobScheduler"});

    const validationSchema = useMemo(
        () => buildValidationSchema(schemaKey.jobType, schemaKey.occurrence),
        [schemaKey.jobType, schemaKey.occurrence],
    );

    const formik = useFormik({
        initialValues: INITIAL_VALUES,
        validationSchema,
        validateOnMount: true,
        validateOnChange: true,
        validateOnBlur: true,
        onSubmit: handleSubmit,
    });

    // Derived flags
    const jobType = formik.values.jobSpecificationType;
    const occurrence = formik.values.occurrence;

    // Force re-validation whenever the schema changes (e.g. switching occurrence
    // or job type). validateOnMount only fires once; this ensures newly-required
    // fields show errors immediately without needing a click.
    useEffect(() => {
        formik.validateForm();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [validationSchema]);
    const isExport = jobType === "ExportJobSpecification";
    const isPurge = jobType === "PurgeJobSpecification";
    const isImport = jobType === "ImportJobSpecification";
    const isOneTime = occurrence === "OneTimeJobScheduler";
    const isRecurring = occurrence === "RecurringJobScheduler";
    const isRestricted = RESTRICTED_TYPES.includes(jobType);
    const occurrenceOptions = isRestricted ? OCCURRENCE_OPTIONS_RESTRICTED : OCCURRENCE_OPTIONS_FULL;

    // Submit is allowed only when every required field passes validation.
    // validateOnMount ensures isValid is false when the form loads empty,
    // so no separate dirty-check is needed.
    const canSubmit = formik.isValid;

    // ---- Field change handlers ----

    const setFieldValue = useCallback(
        (field, value) => {
            formik.setFieldValue(field, value);
            formik.setFieldTouched(field, true, false);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleFieldChange = useCallback(
        (e) => {
            formik.handleChange(e);
            formik.setFieldTouched(e.target.name, true, false);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleJobTypeChange = useCallback(
        (e) => {
            const newType = e.target.value;
            formik.handleChange(e);
            formik.setFieldTouched("jobSpecificationType", true, false);

            // Reset occurrence to default when switching to a restricted type
            if (RESTRICTED_TYPES.includes(newType) && formik.values.occurrence === "RecurringJobScheduler") {
                formik.setFieldValue("occurrence", "OneTimeJobScheduler");
                setSchemaKey((prev) => ({...prev, jobType: newType, occurrence: "OneTimeJobScheduler"}));
            } else {
                setSchemaKey((prev) => ({...prev, jobType: newType}));
            }

            // Reset type-specific fields
            formik.setFieldValue("contentType", "");
            formik.setFieldValue("importType", "");
            formik.setFieldValue("purgeType", "");
            formik.setFieldValue("productStatus", []);
            formik.setFieldValue("lifecycleStatus", []);
            formik.setFieldValue("fields", []);
            formik.setFieldValue("selectedFile", null);
            selectedFileRef.current = null;
            setShowFieldInfo(true);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleOccurrenceChange = useCallback(
        (e) => {
            const newOccurrence = e.target.value;
            formik.handleChange(e);
            formik.setFieldTouched("occurrence", true, false);

            // Reset schedule-specific fields
            formik.setFieldValue("plannedDate", null);
            formik.setFieldValue("repeatEvery", 1);
            formik.setFieldValue("repeatInterval", "Day");
            formik.setFieldValue("repeatFrom", null);
            formik.setFieldValue("repeatTo", null);
            formik.setFieldValue("executionTime", null);

            setSchemaKey((prev) => ({...prev, occurrence: newOccurrence}));
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handlePurgeTypeChange = useCallback(
        (e) => {
            handleFieldChange(e);
            const type = e.target.value;

            if (type === "PurgeJob") {
                formik.setFieldValue("productStatus", []);
                formik.setFieldValue("productTerminationDateFrom", null);
                formik.setFieldValue("productTerminationDateTo", null);
            } else if (type === "PurgeProduct") {
                formik.setFieldValue("lifecycleStatus", []);
                formik.setFieldValue("endDateFrom", null);
                formik.setFieldValue("endDateTo", null);
            }
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [handleFieldChange],
    );

    const handleMultiSelectChange = useCallback(
        (selectedOptions, fieldName) => {
            const vals = (selectedOptions || []).map((opt) => opt.value);
            formik.setFieldValue(fieldName, vals);
            formik.setFieldTouched(fieldName, true, false);

            if (fieldName === "fields") {
                setShowFieldInfo(vals.length === 0);
            }
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleLifecycleStatusChange = useCallback(
        (selectedOptions) => {
            formik.setFieldValue("lifecycleStatus", selectedOptions || []);
            formik.setFieldTouched("lifecycleStatus", true, false);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleDateClear = useCallback(
        (fieldId) => {
            formik.setFieldValue(fieldId, null);
            formik.setFieldTouched(fieldId, true, false);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleFileChange = useCallback(
        (e) => {
            const file = e.target.files?.[0] || null;
            selectedFileRef.current = file;
            formik.setFieldValue("selectedFile", file);
            formik.setFieldTouched("selectedFile", true, false);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [],
    );

    const handleClearForm = useCallback(() => {
        formik.resetForm({values: INITIAL_VALUES});
        selectedFileRef.current = null;
        setShowFieldInfo(true);
        setSchemaKey({jobType: "", occurrence: "OneTimeJobScheduler"});
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // ---- Submit + file upload ----

    const handleImportFileUpload = useCallback(async (specId) => {
        const jobResponse = await ProductInventoryAPI.getJobSpecById(specId);
        const jobId = Array.isArray(jobResponse) ? jobResponse[0]?.id : jobResponse.id;

        if (!jobId) {
            throw new Error("Job ID not found in response.");
        }

        const selectedFile = selectedFileRef.current;
        if (selectedFile) {
            const uploadUrlData = await ProductInventoryAPI.getUploadFileUrl(jobId);
            if (uploadUrlData?.url) {
                await ProductInventoryService.uploadFile(selectedFile, uploadUrlData.url);
            }
        }
    }, []);

    async function handleSubmit(values) {
        try {
            const requestBody = buildRequestBody(values);
            const response = await ProductInventoryAPI.submitJob(requestBody);

            if (!response?.id) {
                throw new Error("Failed to create Job Specification.");
            }

            const createdJobSpecId = response.id;
            setJobSpecId(createdJobSpecId);

            if (values.jobSpecificationType === "ImportJobSpecification") {
                await handleImportFileUpload(createdJobSpecId);
            }

            setShowModal(true);
            handleClearForm();
        } catch (error) {
            setErrorMessage(
                <>
                    An unexpected error has occurred, please try again later.{" "}
                    <span style={{color: "red"}}>Error: {error.message}</span>
                </>,
            );
            setShowErrorModal(true);
        }
    }

    // ---- Modal handlers ----

    const closeSuccessModal = useCallback(() => {
        setShowModal(false);
        handleClearForm();
    }, [handleClearForm]);

    const closeErrorModal = useCallback(() => {
        setShowErrorModal(false);
        handleClearForm();
    }, [handleClearForm]);

    // ============================================
    // RENDER
    // ============================================

    return (
        <div className="col-md-9">
            <div className="card">
                <div className="card-body">
                    <form onSubmit={formik.handleSubmit} noValidate>
                        <p className="text-end text-muted small mb-3">
                            <span className="text-primary">*</span> Required
                        </p>

                        {/* ===== Job Specification Type ===== */}
                        <SelectField
                            id="jobSpecificationType"
                            label="Job Specification Type"
                            options={JOB_TYPES}
                            formik={formik}
                            onChange={handleJobTypeChange}
                            required
                            placeholder="Select Job Specification Type"
                        />

                        {/* ===== Name ===== */}
                        <TextField
                            id="name"
                            label="Name"
                            placeholder="Enter Job Specification name"
                            formik={formik}
                            onChange={handleFieldChange}
                            onClear={() => setFieldValue("name", "")}
                            required
                        />

                        {/* ===== Schedule Section ===== */}
                        <div className="mb-3">
                            <div className="title_lines mb-3">Schedule</div>
                            <div className="container-fluid">
                                {/* Occurrence row */}
                                <div className="row mt-2">
                                    <div className="col">
                                        <RequiredLabel htmlFor="occurrence">Occurrence</RequiredLabel>
                                        <select
                                            id="occurrence"
                                            name="occurrence"
                                            className={`form-select ${
                                                hasFieldError(formik, "occurrence") ? "is-invalid" : ""
                                            }`}
                                            onBlur={formik.handleBlur}
                                            onChange={handleOccurrenceChange}
                                            value={formik.values.occurrence}
                                        >
                                            {occurrenceOptions.map((opt) => (
                                                <option key={opt.value} value={opt.value}>
                                                    {opt.label}
                                                </option>
                                            ))}
                                        </select>
                                        <FieldError formik={formik} name="occurrence"/>
                                    </div>

                                    {/* One-time: Planned Date */}
                                    {isOneTime && (
                                        <div className="col date-picker-wrapper">
                                            <RequiredLabel htmlFor="plannedDate">Planned Date</RequiredLabel>
                                            <DatePicker
                                                id="plannedDate"
                                                name="plannedDate"
                                                selected={formik.values.plannedDate}
                                                onChange={(date) => setFieldValue("plannedDate", date)}
                                                onBlur={() => formik.setFieldTouched("plannedDate", true)}
                                                placeholderText="Select planned date & time"
                                                wrapperClassName={hasFieldError(formik, "plannedDate") ? "is-invalid" : ""}
                                                className={`date-picker form-control ${
                                                    hasFieldError(formik, "plannedDate") ? "is-invalid" : ""
                                                }`}
                                                dateFormat="dd/MM/yyyy h:mm aa"
                                                showTimeSelect
                                                showMonthYearDropdown
                                                autoComplete="off"
                                                minDate={getNow()}
                                                minTime={
                                                    isToday(formik.values.plannedDate)
                                                        ? getNow()
                                                        : getStartOfDay()
                                                }
                                                maxTime={getEndOfDay()}
                                            />
                                            {formik.values.plannedDate && (
                                                <ClearFieldButton
                                                    onClick={() => setFieldValue("plannedDate", null)}
                                                />
                                            )}
                                            <FieldError formik={formik} name="plannedDate"/>
                                        </div>
                                    )}

                                    {/* Recurring: Repeat Every + Interval */}
                                    {isRecurring && (
                                        <>
                                            <div className="col">
                                                <RequiredLabel htmlFor="repeatEvery">
                                                    Repeat every
                                                </RequiredLabel>
                                                <input
                                                    type="number"
                                                    id="repeatEvery"
                                                    name="repeatEvery"
                                                    className={`form-control ${
                                                        hasFieldError(formik, "repeatEvery") ? "is-invalid" : ""
                                                    }`}
                                                    onBlur={formik.handleBlur}
                                                    onChange={handleFieldChange}
                                                    value={formik.values.repeatEvery}
                                                    min="1"
                                                />
                                                <FieldError formik={formik} name="repeatEvery"/>
                                            </div>

                                            <div className="col">
                                                <RequiredLabel htmlFor="repeatInterval">
                                                    Interval
                                                </RequiredLabel>
                                                <select
                                                    id="repeatInterval"
                                                    name="repeatInterval"
                                                    className={`form-select ${
                                                        hasFieldError(formik, "repeatInterval") ? "is-invalid" : ""
                                                    }`}
                                                    onBlur={formik.handleBlur}
                                                    onChange={handleFieldChange}
                                                    value={formik.values.repeatInterval}
                                                >
                                                    {INTERVAL_OPTIONS.map((opt) => (
                                                        <option key={opt} value={opt}>
                                                            {opt}
                                                        </option>
                                                    ))}
                                                </select>
                                                <FieldError formik={formik} name="repeatInterval"/>
                                            </div>
                                        </>
                                    )}
                                </div>

                                {/* Recurring: Date range + Execution Time */}
                                {isRecurring && (
                                    <div className="row mt-3">
                                        <div className="col date-picker-wrapper">
                                            <RequiredLabel htmlFor="repeatFrom">Repeat From</RequiredLabel>
                                            <DatePicker
                                                id="repeatFrom"
                                                name="repeatFrom"
                                                selected={formik.values.repeatFrom}
                                                onChange={(date) => setFieldValue("repeatFrom", date)}
                                                onBlur={() => formik.setFieldTouched("repeatFrom", true)}
                                                selectsStart
                                                startDate={formik.values.repeatFrom}
                                                endDate={formik.values.repeatTo}
                                                placeholderText="Select start date"
                                                wrapperClassName={hasFieldError(formik, "repeatFrom") ? "is-invalid" : ""}
                                                className={`date-picker form-control ${
                                                    hasFieldError(formik, "repeatFrom") ? "is-invalid" : ""
                                                }`}
                                                dateFormat="yyyy-MM-dd"
                                                autoComplete="off"
                                            />
                                            {formik.values.repeatFrom && (
                                                <ClearFieldButton
                                                    onClick={() => setFieldValue("repeatFrom", null)}
                                                />
                                            )}
                                            <FieldError formik={formik} name="repeatFrom"/>
                                        </div>

                                        <div className="col date-picker-wrapper">
                                            <label htmlFor="repeatTo" className="form-label">
                                                Repeat To
                                            </label>
                                            <DatePicker
                                                id="repeatTo"
                                                name="repeatTo"
                                                selected={formik.values.repeatTo}
                                                onChange={(date) => setFieldValue("repeatTo", date)}
                                                onBlur={() => formik.setFieldTouched("repeatTo", true)}
                                                selectsEnd
                                                startDate={formik.values.repeatFrom}
                                                endDate={formik.values.repeatTo}
                                                minDate={formik.values.repeatFrom}
                                                placeholderText="Select end date (optional)"
                                                wrapperClassName={hasFieldError(formik, "repeatTo") ? "is-invalid" : ""}
                                                className={`date-picker form-control ${
                                                    hasFieldError(formik, "repeatTo") ? "is-invalid" : ""
                                                }`}
                                                dateFormat="yyyy-MM-dd"
                                                autoComplete="off"
                                            />
                                            {formik.values.repeatTo && (
                                                <ClearFieldButton
                                                    onClick={() => setFieldValue("repeatTo", null)}
                                                />
                                            )}
                                            <FieldError formik={formik} name="repeatTo"/>
                                        </div>

                                        <div className="col date-picker-wrapper">
                                            <RequiredLabel htmlFor="executionTime">
                                                Execution Time
                                            </RequiredLabel>
                                            <DatePicker
                                                id="executionTime"
                                                name="executionTime"
                                                selected={
                                                    formik.values.executionTime
                                                        ? new Date(formik.values.executionTime)
                                                        : null
                                                }
                                                onChange={(time) => {
                                                    try {
                                                        setFieldValue(
                                                            "executionTime",
                                                            time ? new Date(time).toISOString() : null,
                                                        );
                                                    } catch {
                                                        setFieldValue("executionTime", null);
                                                    }
                                                }}
                                                onBlur={() => formik.setFieldTouched("executionTime", true)}
                                                showTimeSelect
                                                showIcon
                                                showTimeSelectOnly
                                                timeIntervals={30}
                                                timeCaption="Time"
                                                dateFormat="h:mm aa"
                                                wrapperClassName={hasFieldError(formik, "executionTime") ? "is-invalid" : ""}
                                                className={`form-control ${
                                                    hasFieldError(formik, "executionTime") ? "is-invalid" : ""
                                                }`}
                                                placeholderText="Select execution time"
                                            />
                                            {formik.values.executionTime && (
                                                <ClearFieldButton
                                                    onClick={() => setFieldValue("executionTime", null)}
                                                />
                                            )}
                                            <FieldError formik={formik} name="executionTime"/>
                                        </div>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* ===== Purge Query Section ===== */}
                        {isPurge && (
                            <div className="mb-3">
                                <div className="title_lines mb-3">Purge Query</div>

                                <SelectField
                                    id="purgeType"
                                    label="Purge Type"
                                    options={PURGE_TYPE_OPTIONS}
                                    formik={formik}
                                    onChange={handlePurgeTypeChange}
                                    required
                                    placeholder="Select Purge Type"
                                />

                                {formik.values.purgeType === "PurgeProduct" && (
                                    <>
                                        <MultiSelectField
                                            id="productStatus"
                                            label="Product Status"
                                            options={PURGE_STATUS_OPTIONS}
                                            formik={formik}
                                            onChange={(opts) => handleMultiSelectChange(opts, "productStatus")}
                                            value={PURGE_STATUS_OPTIONS.filter((item) =>
                                                formik.values.productStatus?.includes(item.value),
                                            )}
                                            required
                                            placeholder="Select Product Status"
                                        />

                                        <DateRangeRow
                                            label="Termination Date"
                                            fromId="productTerminationDateFrom"
                                            toId="productTerminationDateTo"
                                            formik={formik}
                                            dateFormat="dd/MM/yyyy"
                                            onClear={handleDateClear}
                                        />
                                    </>
                                )}

                                {formik.values.purgeType === "PurgeJob" && (
                                    <>
                                        <MultiSelectField
                                            id="lifecycleStatus"
                                            label="Job Specification Status"
                                            options={LIFECYCLE_STATUS_OPTIONS}
                                            formik={formik}
                                            onChange={handleLifecycleStatusChange}
                                            value={formik.values.lifecycleStatus}
                                            required
                                            placeholder="Select Job Specification Status"
                                        />

                                        <DateRangeRow
                                            label="End Date"
                                            fromId="endDateFrom"
                                            toId="endDateTo"
                                            formik={formik}
                                            dateFormat="dd/MM/yyyy"
                                            onClear={handleDateClear}
                                        />
                                    </>
                                )}
                            </div>
                        )}

                        {/* ===== Export Query Section ===== */}
                        {isExport && (
                            <>
                                <div className="mb-3 mt-4">
                                    <div className="title_lines mb-3">Export Query</div>
                                </div>

                                <div className="mb-4">
                                    <MultiSelectField
                                        id="productStatus"
                                        label="Product Status"
                                        options={STATUS_LIST}
                                        formik={formik}
                                        onChange={(opts) => handleMultiSelectChange(opts, "productStatus")}
                                        value={STATUS_LIST.filter((item) =>
                                            formik.values.productStatus?.includes(item.value),
                                        )}
                                        placeholder="Select Product Status"
                                    />

                                    <TextField
                                        id="relatedParty"
                                        label="Related Party ID"
                                        placeholder="Enter Related Party ID"
                                        formik={formik}
                                        onChange={(e) => setFieldValue("relatedParty", e.target.value)}
                                        onClear={() => setFieldValue("relatedParty", "")}
                                    />

                                    {/* Date Ranges */}
                                    {EXPORT_DATE_RANGES.map(({label, from, to}) => (
                                        <DateRangeRow
                                            key={from}
                                            label={label}
                                            fromId={from}
                                            toId={to}
                                            formik={formik}
                                            dateFormat="dd/MM/yyyy h:mm aa"
                                            showTimeSelect
                                            onClear={handleDateClear}
                                        />
                                    ))}
                                </div>

                                <hr className="border-light my-4"/>

                                <div className="mb-4">
                                    <SelectField
                                        id="contentType"
                                        label="Content Type"
                                        options={CONTENT_TYPE_OPTIONS}
                                        formik={formik}
                                        onChange={handleFieldChange}
                                        required
                                        placeholder="Select Content Type"
                                    />

                                    <div className="mb-3">
                                        <label htmlFor="fields" className="form-label">
                                            Fields
                                        </label>
                                        <Select
                                            isMulti
                                            id="fields"
                                            name="fields"
                                            options={FIELDS_LIST}
                                            className="basic-multi-select"
                                            classNamePrefix="select"
                                            placeholder="Select fields to export"
                                            onChange={(opts) => handleMultiSelectChange(opts, "fields")}
                                            value={FIELDS_LIST.filter((item) =>
                                                formik.values.fields?.includes(item.value),
                                            )}
                                            styles={selectStyles}
                                            components={{DropdownIndicator, Option: CustomOption}}
                                        />
                                        {showFieldInfo && (
                                            <div className="text-muted mt-2" style={{fontSize: "0.9rem"}}>
                                                <i className="bi bi-info-circle me-1"/>
                                                All fields are exported when none are selected.
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </>
                        )}

                        {/* ===== Import Section ===== */}
                        {isImport && (
                            <>
                                <hr className="border-light my-4"/>

                                <div className="mb-4">
                                    <SelectField
                                        id="contentType"
                                        label="Content Type"
                                        options={CONTENT_TYPE_OPTIONS}
                                        formik={formik}
                                        onChange={handleFieldChange}
                                        required
                                        placeholder="Select Content Type"
                                    />

                                    <SelectField
                                        id="importType"
                                        label="Import Type"
                                        options={IMPORT_TYPE_OPTIONS}
                                        formik={formik}
                                        onChange={handleFieldChange}
                                        required
                                        placeholder="Select Import Type"
                                    />

                                    <div className="mb-3">
                                        <label htmlFor="checkCatalog" className="form-label">
                                            Check Catalog<span className="text-primary"> *</span>
                                        </label>
                                        <select
                                            id="checkCatalog"
                                            className="form-select"
                                            disabled
                                            defaultValue="false"
                                        >
                                            <option value="false">False</option>
                                            <option value="true">True</option>
                                        </select>
                                    </div>

                                    <hr className="border-light my-4"/>

                                    <div className="mb-3">
                                        <RequiredLabel htmlFor="importFile">
                                            Upload Import File
                                        </RequiredLabel>
                                        <input
                                            id="importFile"
                                            type="file"
                                            className={`form-control ${
                                                hasFieldError(formik, "selectedFile") ? "is-invalid" : ""
                                            }`}
                                            onChange={handleFileChange}
                                        />
                                        <FieldError formik={formik} name="selectedFile"/>
                                    </div>
                                </div>
                            </>
                        )}

                        {/* ===== Submit Buttons ===== */}
                        <div className="d-grid gap-2 d-flex justify-content-end">
                            <button
                                className="btn btn-outline-secondary me-2"
                                type="button"
                                onClick={handleClearForm}
                            >
                                Clear
                            </button>
                            <button
                                className="btn btn-primary"
                                type="submit"
                                disabled={!canSubmit}
                            >
                                Create Job Specification
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            {/* Success Modal */}
            <Modal open={showModal} onClose={closeSuccessModal}>
                <div className="modal-header">
                    <h5 className="modal-title">Success</h5>
                    <BootstrapTooltip title="Close" placement="bottom">
                        <button type="button" className="btn-close" onClick={closeSuccessModal}/>
                    </BootstrapTooltip>
                </div>
                <div className="modal-body">
                    <div className="alert alert-success" role="alert">
                        <span className="alert-icon">
                            <span className="visually-hidden">Success</span>
                        </span>
                        <div>
                            <h3 className="alert-heading">Job Specification created successfully</h3>
                        </div>
                    </div>
                    {jobSpecId && (
                        <p>
                            Go to Job Specification Details{" "}
                            <Link to={`/product-inventory/jobSpecification-details-page/${jobSpecId}`}>
                                {jobSpecId}
                            </Link>
                        </p>
                    )}
                </div>
                <div className="modal-footer">
                    <button type="button" className="btn btn-primary" onClick={closeSuccessModal}>
                        Close
                    </button>
                </div>
            </Modal>

            {/* Error Modal */}
            <Modal open={showErrorModal} onClose={closeErrorModal}>
                <div className="modal-header">
                    <h5 className="modal-title">Error</h5>
                    <BootstrapTooltip title="Close" placement="top">
                        <button type="button" className="btn-close" onClick={closeErrorModal}/>
                    </BootstrapTooltip>
                </div>
                <div className="modal-body">
                    <div className="alert alert-danger" role="alert">
                        <span className="alert-icon">
                            <span className="visually-hidden">Error</span>
                        </span>
                        <div>
                            <h3 className="alert-heading">{errorMessage}</h3>
                        </div>
                    </div>
                </div>
                <div className="modal-footer">
                    <button type="button" className="btn btn-danger" onClick={closeErrorModal}>
                        Close
                    </button>
                </div>
            </Modal>
        </div>
    );
}

export default CreateJobSpecificationDetails;
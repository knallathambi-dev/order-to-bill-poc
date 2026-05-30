// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {DateInput, formatISODateTime, MonitoringFilterForm, MultiSelectInput, TextInput,} from "@discobole/common-ui";

const STATUS_OPTIONS = [
    {value: "NotStarted", label: "Not Started"},
    {value: "Running", label: "Running"},
    {value: "Succeeded", label: "Succeeded"},
    {value: "Failed", label: "Failed"},
];

const TYPE_OPTIONS = [
    {value: "TerminationJob", label: "Termination Job"},
    {value: "ExportJob", label: "Export Job"},
    {value: "PurgeJob", label: "Purge Job"},
    {value: "ImportJob", label: "Import Job"},
];

const FIELD_TO_PARAM = {
    jobId: "id",
    jobSpecificationId: "jobSpecification.id",
};

const DATE_FIELD_TO_PARAM = {
    plannedDateFrom: "plannedDate.gte",
    plannedDateTo: "plannedDate.lte",
    startDateFrom: "executionPeriod.startDateTime.gte",
    startDateTo: "executionPeriod.startDateTime.lte",
    endDateFrom: "executionPeriod.endDateTime.gte",
    endDateTo: "executionPeriod.endDateTime.lte",
};

const isValueEmpty = (value) =>
    value === null || value === undefined || value === "" || (Array.isArray(value) && value.length === 0);

const buildFilterParams = (values) =>
    Object.entries(values).reduce((acc, [key, value]) => {
        if (isValueEmpty(value)) return acc;

        if (key === "type") {
            acc["@type"] = value.map((opt) => opt.value).join(",");
        } else if (key === "status") {
            acc.status = value.map((opt) => opt.value).join(",");
        } else if (FIELD_TO_PARAM[key]) {
            acc[FIELD_TO_PARAM[key]] = value;
        } else if (DATE_FIELD_TO_PARAM[key]) {
            acc[DATE_FIELD_TO_PARAM[key]] = formatISODateTime(value);
        }

        return acc;
    }, {});

const JobsFilter = ({onFilterSubmit, initialFilterValues}) => {
    const initialValues = {
        jobId: "",
        jobSpecificationId: initialFilterValues?.["jobSpecification.id"] || "",
        type: [],
        status: [],
        plannedDateFrom: null,
        plannedDateTo: null,
        startDateFrom: null,
        startDateTo: null,
        endDateFrom: null,
        endDateTo: null,
    };

    return (
        <MonitoringFilterForm
            initialValues={initialValues}
            buildFilterParams={buildFilterParams}
            onFilterSubmit={onFilterSubmit}
            renderMainFilters={(formik, clearField) => (
                <div className="row row-cols-4 g-2">
                    <TextInput id="jobId" label="Job Id" formik={formik} onClear={clearField}/>
                    <TextInput id="jobSpecificationId" label="Job Specification Id" formik={formik}
                               onClear={clearField}/>
                    <MultiSelectInput
                        id="type" label="Type" formik={formik}
                        options={TYPE_OPTIONS} placeholder="Select Type"
                    />
                    <MultiSelectInput
                        id="status" label="Status" formik={formik}
                        options={STATUS_OPTIONS} placeholder="Select Status"
                    />
                </div>
            )}
            renderExtraFilters={(formik, clearField) => (
                <>
                    <div className="row row-cols-4 g-2 mt-2">
                        <DateInput id="plannedDateFrom" label="Planned Date From" formik={formik}
                                   startId="plannedDateFrom" endId="plannedDateTo" isStart
                                   dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                        <DateInput id="plannedDateTo" label="Planned Date To" formik={formik}
                                   startId="plannedDateFrom" endId="plannedDateTo" isStart={false}
                                   dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                        <DateInput id="startDateFrom" label="Start Date From" formik={formik}
                                   startId="startDateFrom" endId="startDateTo" isStart
                                   dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                        <DateInput id="startDateTo" label="Start Date To" formik={formik}
                                   startId="startDateFrom" endId="startDateTo" isStart={false}
                                   dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    </div>

                    <div className="row row-cols-4 g-2 mt-2">
                        <DateInput id="endDateFrom" label="End Date From" formik={formik}
                                   startId="endDateFrom" endId="endDateTo" isStart
                                   dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                        <DateInput id="endDateTo" label="End Date To" formik={formik}
                                   startId="endDateFrom" endId="endDateTo" isStart={false}
                                   dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    </div>
                </>
            )}
        />
    );
};

export default JobsFilter;
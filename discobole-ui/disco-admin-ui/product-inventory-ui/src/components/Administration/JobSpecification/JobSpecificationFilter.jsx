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
    {value: "Created", label: "Created"},
    {value: "Active", label: "Active"},
    {value: "Suspended", label: "Suspended"},
    {value: "Terminated", label: "Terminated"},
];

const TYPE_OPTIONS = [
    {value: "TerminationJobSpecification", label: "TerminationJobSpecification"},
    {value: "ImportJobSpecification", label: "ImportJobSpecification"},
    {value: "ExportJobSpecification", label: "ExportJobSpecification"},
    {value: "PurgeJobSpecification", label: "PurgeJobSpecification"},
];

const FIELD_TO_PARAM = {
    id: "id",
    name: "name",
};

const DATE_FIELD_TO_PARAM = {
    creationDateFrom: "creationDate.gte",
    creationDateTo: "creationDate.lte",
    startDateFrom: "activePeriod.startDateTime.gte",
    startDateTo: "activePeriod.startDateTime.lte",
    endDateFrom: "activePeriod.endDateTime.gte",
    endDateTo: "activePeriod.endDateTime.lte",
};

const INITIAL_VALUES = {
    id: "",
    name: "",
    type: [],
    lifecycleStatus: [],
    creationDateFrom: null,
    creationDateTo: null,
    startDateFrom: null,
    startDateTo: null,
    endDateFrom: null,
    endDateTo: null,
};

const isValueEmpty = (value) =>
    value === null || value === undefined || value === "" || (Array.isArray(value) && value.length === 0);

const buildFilterParams = (values) =>
    Object.entries(values).reduce((acc, [key, value]) => {
        if (isValueEmpty(value)) return acc;

        if (key === "type") {
            acc["@type"] = value.map((opt) => opt.value).join(",");
        } else if (key === "lifecycleStatus") {
            acc.lifecycleStatus = value.map((opt) => opt.value).join(",");
        } else if (FIELD_TO_PARAM[key]) {
            acc[FIELD_TO_PARAM[key]] = value;
        } else if (DATE_FIELD_TO_PARAM[key]) {
            acc[DATE_FIELD_TO_PARAM[key]] = formatISODateTime(value);
        }

        return acc;
    }, {});

const JobSpecificationFilter = ({onFilterSubmit}) => (
    <MonitoringFilterForm
        initialValues={INITIAL_VALUES}
        buildFilterParams={buildFilterParams}
        onFilterSubmit={onFilterSubmit}
        renderMainFilters={(formik, clearField) => (
            <div className="row row-cols-4 g-2">
                <TextInput id="id" label="Job Specification Id" formik={formik} onClear={clearField}/>
                <TextInput id="name" label="Name" formik={formik} onClear={clearField}/>
                <MultiSelectInput
                    id="type" label="Type" formik={formik}
                    options={TYPE_OPTIONS} placeholder="Select Type"
                />
                <MultiSelectInput
                    id="lifecycleStatus" label="Status" formik={formik}
                    options={STATUS_OPTIONS} placeholder="Select Status"
                />
            </div>
        )}
        renderExtraFilters={(formik, clearField) => (
            <>
                <div className="row row-cols-4 g-2 mt-2">
                    <DateInput id="creationDateFrom" label="Creation Date From" formik={formik}
                               startId="creationDateFrom" endId="creationDateTo" isStart
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    <DateInput id="creationDateTo" label="Creation Date To" formik={formik}
                               startId="creationDateFrom" endId="creationDateTo" isStart={false}
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

export default JobSpecificationFilter;
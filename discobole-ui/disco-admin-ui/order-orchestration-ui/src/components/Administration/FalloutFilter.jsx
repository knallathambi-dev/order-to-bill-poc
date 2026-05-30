// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {
    DateInput,
    formatDateWithTimeBoundary,
    MonitoringFilterForm,
    SelectInput,
    TextInput,
} from "@discobole/common-ui";
import {
    CREATION_DATE_END,
    CREATION_DATE_START,
    FALLOUT_ID,
    ORDER_ID,
    PARTY_ID,
    PARTY_NAME,
    PARTY_ROLE,
    STATUS,
} from "../../utils/constants.js";

const FALLOUT_STATE_OPTIONS = ["Created", "Analysing", "Held", "Completed"];

const FIELD_TO_PARAM = {
    [FALLOUT_ID]: "id",
    [ORDER_ID]: "relatedEntity.id",
    [STATUS]: "state",
    [PARTY_ID]: "relatedParty.id",
    [PARTY_NAME]: "relatedParty.name",
    [PARTY_ROLE]: "relatedParty.role",
};

const DATE_FIELD_TO_PARAM = {
    [CREATION_DATE_START]: "creationDate.gte",
    [CREATION_DATE_END]: "creationDate.lte",
};

const INITIAL_VALUES = {
    [FALLOUT_ID]: "",
    [STATUS]: "",
    [ORDER_ID]: "",
    [CREATION_DATE_START]: null,
    [CREATION_DATE_END]: null,
    [PARTY_ID]: "",
    [PARTY_NAME]: "",
    [PARTY_ROLE]: "",
};

const buildFilterParams = (values) => {
    const filteredValues = {};

    Object.entries(values).forEach(([key, value]) => {
        if (value === null || value === undefined || value === "") return;

        if (FIELD_TO_PARAM[key]) {
            filteredValues[FIELD_TO_PARAM[key]] = value;
        } else if (DATE_FIELD_TO_PARAM[key]) {
            filteredValues[DATE_FIELD_TO_PARAM[key]] = formatDateWithTimeBoundary(value, DATE_FIELD_TO_PARAM[key]);
        }
    });

    return filteredValues;
};

const FalloutFilter = ({onFilterSubmit}) => (
    <MonitoringFilterForm
        initialValues={INITIAL_VALUES}
        buildFilterParams={buildFilterParams}
        onFilterSubmit={onFilterSubmit}
        renderMainFilters={(formik, clearField) => (
            <div className="row row-cols-5">
                <TextInput id={FALLOUT_ID} label="Fallout Id" formik={formik} onClear={clearField}/>
                <SelectInput
                    id={STATUS} label="Status" formik={formik}
                    options={FALLOUT_STATE_OPTIONS} placeholder="Select Status" onClear={clearField}
                />
                <TextInput id={ORDER_ID} label="Product Order Id" formik={formik} onClear={clearField}/>
                <DateInput
                    id={CREATION_DATE_START} label="Creation Date Start" formik={formik}
                    startId={CREATION_DATE_START} endId={CREATION_DATE_END} isStart onClear={clearField}
                />
                <DateInput
                    id={CREATION_DATE_END} label="Creation Date End" formik={formik}
                    startId={CREATION_DATE_START} endId={CREATION_DATE_END} isStart={false} onClear={clearField}
                />
            </div>
        )}
        renderExtraFilters={(formik, clearField) => (
            <div className="row row-cols-5">
                <TextInput id={PARTY_ID} label="Party Id" formik={formik} onClear={clearField}/>
                <TextInput id={PARTY_NAME} label="Party Name" formik={formik} onClear={clearField}/>
                <TextInput id={PARTY_ROLE} label="Party Role" formik={formik} onClear={clearField}/>
            </div>
        )}
    />
);

export default FalloutFilter;
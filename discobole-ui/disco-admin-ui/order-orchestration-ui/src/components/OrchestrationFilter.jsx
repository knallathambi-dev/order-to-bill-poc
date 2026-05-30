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
    DELIVERY_DATE_END,
    DELIVERY_DATE_START,
    getNodeStatusOptions,
    getStatusOptions,
    NODE_ID,
    NODE_STATUS,
    ORDER_ID,
    PARTY_ID,
    PARTY_NAME,
    PARTY_ROLE,
    PLAN_ID,
    RECEIVED_DATE_END,
    RECEIVED_DATE_START,
    STATUS,
} from "../utils/constants.js";

const FIELD_TO_PARAM = {
    [PLAN_ID]: "id",
    [ORDER_ID]: "relatedProductOrder.id",
    [RECEIVED_DATE_START]: "receivedDate.gte",
    [RECEIVED_DATE_END]: "receivedDate.lte",
    [PARTY_ID]: "relatedParty.id",
    [PARTY_NAME]: "relatedParty.name",
    [PARTY_ROLE]: "relatedParty.role",
    [DELIVERY_DATE_START]: "requestedDeliveryDate.gte",
    [DELIVERY_DATE_END]: "requestedDeliveryDate.lte",
    [STATUS]: "state",
    [NODE_STATUS]: "orchestrationPlanNodes.state",
    [NODE_ID]: "orchestrationPlanNodes.id",
};

const INITIAL_VALUES = {
    [PLAN_ID]: "",
    [ORDER_ID]: "",
    [RECEIVED_DATE_START]: null,
    [RECEIVED_DATE_END]: null,
    [PARTY_ID]: "",
    [PARTY_NAME]: "",
    [PARTY_ROLE]: "",
    [DELIVERY_DATE_START]: null,
    [DELIVERY_DATE_END]: null,
    [STATUS]: "",
    [NODE_STATUS]: "",
    [NODE_ID]: "",
};

const buildFilterParams = (values) => {
    const filteredValues = {};

    Object.entries(values).forEach(([key, value]) => {
        if (value === null || value === undefined || value === "") return;

        const paramKey = FIELD_TO_PARAM[key];
        if (!paramKey) return;

        filteredValues[paramKey] = key.includes("Date")
            ? formatDateWithTimeBoundary(value, paramKey)
            : value;
    });

    return filteredValues;
};

const OrchestrationFilter = ({onFilterSubmit, isArchived}) => {
    const statusOptions = getStatusOptions(isArchived);
    const nodeStatusOptions = getNodeStatusOptions(isArchived);

    return (
        <MonitoringFilterForm
            initialValues={INITIAL_VALUES}
            buildFilterParams={buildFilterParams}
            onFilterSubmit={onFilterSubmit}
            renderMainFilters={(formik, clearField) => (
                <div className="row row-cols-4">
                    <TextInput id={PLAN_ID} label="Plan Id" formik={formik} onClear={clearField}/>
                    <TextInput id={ORDER_ID} label="Product Order Id" formik={formik} onClear={clearField}/>
                    <DateInput
                        id={RECEIVED_DATE_START} label="From Received Date" formik={formik}
                        startId={RECEIVED_DATE_START} endId={RECEIVED_DATE_END} isStart onClear={clearField}
                    />
                    <DateInput
                        id={RECEIVED_DATE_END} label="To Received Date" formik={formik}
                        startId={RECEIVED_DATE_START} endId={RECEIVED_DATE_END} isStart={false} onClear={clearField}
                    />
                </div>
            )}
            renderExtraFilters={(formik, clearField) => (
                <>
                    <div className="row row-cols-4">
                        <TextInput id={PARTY_ID} label="Party Id" formik={formik} onClear={clearField}/>
                        <TextInput id={PARTY_NAME} label="Party Name" formik={formik} onClear={clearField}/>
                        <TextInput id={PARTY_ROLE} label="Party Role" formik={formik} onClear={clearField}/>
                        <DateInput
                            id={DELIVERY_DATE_START} label="From Delivery Date" formik={formik}
                            startId={DELIVERY_DATE_START} endId={DELIVERY_DATE_END} isStart onClear={clearField}
                        />
                    </div>
                    <div className="row row-cols-4">
                        <DateInput
                            id={DELIVERY_DATE_END} label="To Delivery Date" formik={formik}
                            startId={DELIVERY_DATE_START} endId={DELIVERY_DATE_END} isStart={false} onClear={clearField}
                        />
                        <SelectInput
                            id={STATUS} label="Status" formik={formik}
                            options={statusOptions} placeholder="Select Status" onClear={clearField}
                        />
                        <SelectInput
                            id={NODE_STATUS} label="Node Status" formik={formik}
                            options={nodeStatusOptions} placeholder="Select Node Status" onClear={clearField}
                        />
                        <TextInput id={NODE_ID} label="Node Id" formik={formik} onClear={clearField}/>
                    </div>
                </>
            )}
        />
    );
};

export default OrchestrationFilter;
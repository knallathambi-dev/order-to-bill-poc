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
import {ORDER_STATUSES} from "./OrderDetails/utils/orderUtils";

const STATE_OPTIONS = Object.entries(ORDER_STATUSES).map(([value, label]) => ({value, label}));

const FIELD_TO_PARAM = {
    orderId: "id",
    orderState: "state",
    creationStartDate: "creationDate.gte",
    creationEndDate: "creationDate.lte",
    partyId: "relatedParty.id",
    partyName: "relatedParty.name",
    partyRole: "relatedParty.role",
    channelName: "channel.name",
};

const INITIAL_VALUES = {
    orderId: "",
    orderState: "",
    creationStartDate: null,
    creationEndDate: null,
    partyId: "",
    partyName: "",
    partyRole: "",
    channelName: "",
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

const OrdersFilter = ({onFilterSubmit}) => (
    <MonitoringFilterForm
        initialValues={INITIAL_VALUES}
        buildFilterParams={buildFilterParams}
        onFilterSubmit={onFilterSubmit}
        renderMainFilters={(formik, clearField) => (
            <div className="row row-cols-4">
                <TextInput id="orderId" label="Order Id" formik={formik} onClear={clearField}/>
                <SelectInput
                    id="orderState" label="Order State" formik={formik}
                    options={STATE_OPTIONS} placeholder="Select State" onClear={clearField}
                />
                <DateInput id="creationStartDate" label="From Creation Date" formik={formik}
                           startId="creationStartDate" endId="creationEndDate" isStart onClear={clearField}/>
                <DateInput id="creationEndDate" label="To Creation Date" formik={formik}
                           startId="creationStartDate" endId="creationEndDate" isStart={false} onClear={clearField}/>
            </div>
        )}
        renderExtraFilters={(formik, clearField) => (
            <div className="row row-cols-4">
                <TextInput id="partyId" label="Party Id" formik={formik} onClear={clearField}/>
                <TextInput id="partyName" label="Party Name" formik={formik} onClear={clearField}/>
                <TextInput id="partyRole" label="Party Role" formik={formik} onClear={clearField}/>
                <TextInput id="channelName" label="Channel Name" formik={formik} onClear={clearField}/>
            </div>
        )}
    />
);

export default OrdersFilter;
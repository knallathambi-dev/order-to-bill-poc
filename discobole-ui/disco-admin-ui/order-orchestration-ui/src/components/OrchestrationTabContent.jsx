// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo} from "react";
import {Link} from "react-router-dom";
import {formatToLocalDateTime, MonitoringTabContent, MonitoringTableBody} from "@discobole/common-ui";
import OrchestrationFilter from "./OrchestrationFilter.jsx";
import api from '../service/OrchestrationDeliveryAPI.js';
import {
    ARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL,
    ORCHESTRATION_PLANS_ENDPOINT,
    UNARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL,
} from "../utils/constants.js";

const ALL_FIELDS = [
    {name: "Received Date", checked: true, disabled: false, value: "receivedDate", isSortable: true},
    {name: "Plan Id", checked: true, disabled: true, value: "id", isSortable: true},
    {name: "Product Order Id", checked: true, disabled: false, value: "relatedProductOrder.id", isSortable: false},
    {name: "Party Id", checked: true, disabled: false, value: "relatedParty.id", isSortable: false},
    {name: "Status", checked: true, disabled: false, value: "state", isSortable: true},
    {name: "Party Name", checked: false, disabled: false, value: "relatedParty.name", isSortable: false},
    {name: "Party Role", checked: false, disabled: false, value: "relatedParty.role", isSortable: false},
    {name: "Requested Delivery Date", checked: false, disabled: false, value: "requestedDeliveryDate", isSortable: true},
];

const PAGE_SIZE = 10;
const DEFAULT_SORT = `-${ALL_FIELDS[0].value}`;
const UNARCHIVED_BASE_PATH = "/orchestration-delivery/monitoring/orchestration-plans";
const ARCHIVED_BASE_PATH = "/orchestration-delivery/monitoring/archived-orchestration-plans";

const OrchestrationTabContent = ({param, reloadRef, setReloadLoading}) => {
    const isArchived = param?.value;
    const basePath = isArchived ? ARCHIVED_BASE_PATH : UNARCHIVED_BASE_PATH;
    const detailBaseUrl = isArchived
        ? ARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL
        : UNARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL;

    const columns = useMemo(() => ({
        "Received Date": (plan) => formatToLocalDateTime(plan?.receivedDate) || "N/A",
        "Plan Id": (plan) => (
            <Link to={`${detailBaseUrl}/${plan?.id}`}>
                {plan?.id || "N/A"}
            </Link>
        ),
        "Product Order Id": (plan) => plan?.relatedProductOrder?.id || "N/A",
        "Party Id": (plan) => plan?.relatedParty?.[0]?.id || "N/A",
        "Status": (plan) => (
            <p className="mb-0">
                <span className={`tag tag-sm status-value ${plan?.state?.toLowerCase() || ""}`}>
                    {plan?.state || "N/A"}
                </span>
            </p>
        ),
        "Party Name": (plan) => plan?.relatedParty?.[0]?.name || "N/A",
        "Party Role": (plan) => plan?.relatedParty?.[0]?.role || "N/A",
        "Requested Delivery Date": (plan) =>
            plan?.requestedDeliveryDate
                ? formatToLocalDateTime(plan.requestedDeliveryDate)
                : "Immediate",
    }), [detailBaseUrl]);

    const fetchFn = useCallback(async ({filters, page, pageSize, sort}) => {
        const paramFields = ALL_FIELDS.map((f) => f.value.split(".")[0]);
        const uniqueFields = Array.from(new Set(paramFields)).join(",");

        const params = new URLSearchParams({
            fields: uniqueFields,
            limit: pageSize,
            sort,
            offset: page === 0 ? 0 : (page - 1) * pageSize,
        });

        if (filters) {
            Object.entries(filters).forEach(([key, value]) => {
                params.append(key, value);
            });
        }

        if (param?.key) {
            params.append(param.key, isArchived);
        }

        const response = await api.get(ORCHESTRATION_PLANS_ENDPOINT, params);

        if (response.status === 204) {
            return {data: [], totalCount: 0};
        }

        const plans = response.data;
        const totalCount = parseInt(response.headers["x-total-count"], 10);

        // Validate data
        const hasInvalidData = plans.some((item) => !item.state);
        if (hasInvalidData) {
            throw new Error("Missing data");
        }

        return {data: plans, totalCount};
    }, [param?.key, isArchived]);

    return (
        <MonitoringTabContent
            reloadRef={reloadRef}
            setReloadLoading={setReloadLoading}
            allFields={ALL_FIELDS}
            basePath={basePath}
            pageSize={PAGE_SIZE}
            defaultSort={DEFAULT_SORT}
            fetchFn={fetchFn}
            renderFilter={(onFilterSubmit) => (
                <OrchestrationFilter onFilterSubmit={onFilterSubmit} isArchived={isArchived}/>
            )}
            renderTableBody={(data, fields) => (
                <MonitoringTableBody data={data} fields={fields} columns={columns}/>
            )}
        />
    );
};

export default OrchestrationTabContent;
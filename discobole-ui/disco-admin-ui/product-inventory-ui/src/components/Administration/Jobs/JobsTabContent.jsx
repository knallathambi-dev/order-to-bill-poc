// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo} from "react";
import {Link, useLocation} from "react-router-dom";
import {formatToLocalDateTime, MonitoringTabContent, MonitoringTableBody} from "@discobole/common-ui";
import JobsFilter from "./JobsFilter";
import api from "../../../service/ProductInventoryAPI.js";

const BASE_PATH = "/product-inventory/administration/jobs";
const PAGE_SIZE = 10;

const ALL_FIELDS = [
    {name: "Job Id", checked: true, disabled: true, value: "id", isSortable: false, type: "ALL"},
    {
        name: "Job Specification Id",
        checked: true,
        disabled: true,
        value: "jobSpecification.id",
        isSortable: false,
        type: "ALL"
    },
    {name: "Type", checked: true, disabled: false, value: "@type", isSortable: false, type: "ALL"},
    {name: "Status", checked: true, disabled: false, value: "status", isSortable: false, type: "ALL"},
    {name: "Planned Date", checked: true, disabled: false, value: "plannedDate", isSortable: true, type: "ALL"},
    {
        name: "Start Date",
        checked: true,
        disabled: false,
        value: "executionPeriod.startDateTime",
        isSortable: true,
        type: "ALL"
    },
    {
        name: "End Date",
        checked: true,
        disabled: false,
        value: "executionPeriod.endDateTime",
        isSortable: true,
        type: "ALL"
    },
];

const COLUMNS = {
    "Job Id": (item) => (
        <Link to={`/product-inventory/jobs-details-page/${item?.id}`}>
            {item?.id || "N/A"}
        </Link>
    ),
    "Job Specification Id": (item) => (
        <Link to={`/product-inventory/jobSpecification-details-page/${item.jobSpecification?.id}`}>
            {item.jobSpecification?.id || "N/A"}
        </Link>
    ),
    "Type": (item) => item["@type"] || "N/A",
    "Status": (item) => (
        <span className={`tag tag-sm status-value ${item.status?.toLowerCase() || ""}`}>
            {item.status || "N/A"}
        </span>
    ),
    "Planned Date": (item) => formatToLocalDateTime(item.plannedDate) || "N/A",
    "Start Date": (item) => formatToLocalDateTime(item.executionPeriod?.startDateTime) || "N/A",
    "End Date": (item) => formatToLocalDateTime(item.executionPeriod?.endDateTime) || "N/A",
};

function JobsTabContent() {
    const location = useLocation();
    const jobSpecId = location.state?.jobSpecificationId ?? null;

    const defaultFilter = useMemo(() => {
        if (jobSpecId && location.state?.filterApplied) {
            return {"jobSpecification.id": jobSpecId};
        }
        return null;
    }, [jobSpecId, location.state?.filterApplied]);

    const initialFilterValues = useMemo(() => {
        return jobSpecId ? {"jobSpecification.id": jobSpecId} : {};
    }, [jobSpecId]);

    const fetchFn = useCallback(async ({filters, page, pageSize, sort}) => {
        const params = new URLSearchParams({
            limit: pageSize,
            offset: page === 0 ? 0 : (page - 1) * pageSize,
        });

        if (sort) params.set("sort", sort);

        if (filters) {
            Object.entries(filters).forEach(([key, value]) => {
                if (value) params.set(key, value);
            });
        }

        return api.fetchJobs(params);
    }, []);

    return (
        <MonitoringTabContent
            allFields={ALL_FIELDS}
            basePath={BASE_PATH}
            pageSize={PAGE_SIZE}
            defaultSort=""
            defaultFilter={defaultFilter}
            fetchFn={fetchFn}
            renderFilter={(onFilterSubmit) => (
                <JobsFilter onFilterSubmit={onFilterSubmit} initialFilterValues={initialFilterValues}/>
            )}
            renderTableBody={(data, fields) => (
                <MonitoringTableBody data={data} fields={fields} columns={COLUMNS}/>
            )}
        />
    );
}

export default JobsTabContent;
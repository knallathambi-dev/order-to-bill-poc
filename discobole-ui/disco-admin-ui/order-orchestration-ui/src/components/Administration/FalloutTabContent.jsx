// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback} from "react";
import {Link} from "react-router-dom";
import {formatToLocalDateTime, MonitoringTabContent, MonitoringTableBody} from "@discobole/common-ui";
import FalloutFilter from "./FalloutFilter.jsx";
import {
    BASE_ORCHESTRATION_PLANS_UI_URL,
    FALLOUT_INCIDENT_ENDPOINT,
    FALLOUT_INCIDENT_UI_URL,
} from "../../utils/constants.js";

import api from '../../service/OrchestrationDeliveryAPI.js';

const ALL_FIELDS = [
    {name: "Fallout Id", checked: true, disabled: true, value: "id", isSortable: true},
    {name: "Party Id", checked: true, disabled: false, value: "relatedParty.id", isSortable: true},
    {name: "Product Order Id", checked: true, disabled: false, value: "relatedProductOrder.id", isSortable: true},
    {name: "Status", checked: true, disabled: false, value: "state", isSortable: true},
    {name: "Creation Date", checked: true, disabled: false, value: "creationDate", isSortable: true},
    {name: "Modification Date", checked: false, disabled: false, value: "modificationDate", isSortable: true},
    {
        name: "Related Entity Type",
        checked: true,
        disabled: false,
        value: "relatedEntity[@referredtype]",
        isSortable: true
    },
    {name: "Related Entity Id", checked: true, disabled: false, value: "relatedEntity.id", isSortable: true},
    {name: "Party Name", checked: false, disabled: false, value: "relatedParty.name", isSortable: true},
    {name: "Party Role", checked: false, disabled: false, value: "relatedParty.role", isSortable: true},
];

const API_FIELDS = "id,relatedParty,relatedProductOrder,state,creationDate,modificationDate,relatedEntity,parentRelatedEntity";
const PAGE_SIZE = 10;
const DEFAULT_SORT = `-${ALL_FIELDS[4].value}`;
const BASE_PATH = FALLOUT_INCIDENT_UI_URL;
const DETAIL_LINK = `${BASE_ORCHESTRATION_PLANS_UI_URL}/fallout-incidents-details-page/`;

/**
 * Extract entity data by role from relatedEntity array.
 */
const getEntityByRole = (entities, role) => {
    if (!Array.isArray(entities)) return null;
    return entities.find((e) => e.role === role) || null;
};

/** Column renderers */
const COLUMNS = {
    "Fallout Id": (incident) => (
        <Link to={`${DETAIL_LINK}${incident.id}`}>
            {incident.id || "N/A"}
        </Link>
    ),
    "Party Id": (incident) => incident.relatedParty?.id || "N/A",
    "Product Order Id": (incident) =>
        getEntityByRole(incident.relatedEntity, "relatedProductOrder")?.id || "N/A",
    "Status": (incident) => (
        <p className="mb-0">
            <span className={`tag tag-sm status-value ${incident.state?.toLowerCase() || ""}`}>
                {incident.state || "N/A"}
            </span>
        </p>
    ),
    "Creation Date": (incident) => formatToLocalDateTime(incident.creationDate) || "N/A",
    "Modification Date": (incident) => formatToLocalDateTime(incident.modificationDate) || "N/A",
    "Related Entity Type": (incident) =>
        getEntityByRole(incident.relatedEntity, "initiator")?.["@referredType"] || "N/A",
    "Related Entity Id": (incident) =>
        getEntityByRole(incident.relatedEntity, "initiator")?.id || "N/A",
    "Party Name": (incident) => incident.relatedParty?.name || "N/A",
    "Party Role": (incident) => incident.relatedParty?.role || "N/A",
};

function FalloutTabContent() {
    const fetchFn = useCallback(async ({filters, page, pageSize, sort}) => {
        const params = new URLSearchParams({
            fields: API_FIELDS,
            limit: pageSize,
            sort,
            offset: page === 0 ? 0 : (page - 1) * pageSize,
        });

        if (filters) {
            Object.entries(filters).forEach(([key, value]) => {
                params.append(key, value);
            });
        }

        const response = await api.get(FALLOUT_INCIDENT_ENDPOINT, params);

        if (response.status === 204) {
            return {data: [], totalCount: 0};
        }

        const incidents = response.data;
        const totalCount = parseInt(response.headers["x-total-count"], 10);

        const hasInvalidData = incidents.some((item) => !item.state);
        if (hasInvalidData) {
            throw new Error("Missing data");
        }

        return {data: incidents, totalCount};
    }, []);

    return (
        <MonitoringTabContent
            allFields={ALL_FIELDS}
            basePath={BASE_PATH}
            pageSize={PAGE_SIZE}
            defaultSort={DEFAULT_SORT}
            fetchFn={fetchFn}
            renderFilter={(onFilterSubmit) => (
                <FalloutFilter onFilterSubmit={onFilterSubmit}/>
            )}
            renderTableBody={(data, fields) => (
                <MonitoringTableBody data={data} fields={fields} columns={COLUMNS}/>
            )}
        />
    );
}

export default FalloutTabContent;
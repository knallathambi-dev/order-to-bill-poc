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
import ProductsFilter from "./ProductsFilter.jsx";
import {DEFAULT_FILTER, PRODUCT_STATUSES, PRODUCT_OPERATIONAL_STATUSES} from "../../common/constants.js";
import api from "../../service/ProductInventoryAPI.js";

const ALL_FIELDS = [
    {name: "Party Id", checked: true, value: "relatedParty.partyOrPartyRole.id", isSortable: false},
    {name: "Party Name", checked: true, value: "relatedParty.partyOrPartyRole.name", isSortable: false},
    {name: "Product Id", checked: true, disabled: true, value: "id", isSortable: false},
    {name: "Product Name", checked: true, value: "name", isSortable: false},
    {name: "Start Date", checked: true, value: "startDate", isSortable: true},
    {name: "Creation Date", checked: true, value: "creationDate", isSortable: true},
    {name: "Party Role", checked: true, value: "relatedParty.partyOrPartyRole.role", isSortable: false},
    {name: "Main State", checked: true, value: "status", isSortable: false},
    {name: "Operational State", checked: true, value: "operationalStatus", isSortable: false},
    {name: "Type", checked: true, value: "@type", isSortable: false},
];

const API_FIELDS = [
    "productOffering.@type", "productSpecification.@type",
    "relatedParty.partyOrPartyRole.id", "relatedParty.partyOrPartyRole.name",
    "id", "name", "startDate", "creationDate", "relatedParty.role", "status",
    "productOffering.id", "productOffering.name", "operationalStatus",
    "productOrderItem.productOrderId",
].join(",");

const PAGE_SIZE = 10;
const DEFAULT_SORT = `-${ALL_FIELDS[5].value}`;
const BASE_PATH = "/product-inventory/monitoring/products";

const COLUMNS = {
    "Party Id": (p) => p?.relatedParty?.[0]?.partyOrPartyRole?.id || "N/A",
    "Party Name": (p) => p?.relatedParty?.[0]?.partyOrPartyRole?.name || "N/A",
    "Product Id": (p) => (
        <Link to={`/product-inventory/products-details-page/${p?.id}`}>
            {p?.id || "N/A"}
        </Link>
    ),
    "Product Name": (p) => p?.name || "N/A",
    "Start Date": (p) => formatToLocalDateTime(p?.startDate) || "N/A",
    "Creation Date": (p) => formatToLocalDateTime(p?.creationDate) || "N/A",
    "Party Role": (p) => p?.relatedParty?.[0]?.role || "N/A",
    "Main State": (p) => (
        <p className="mb-0">
            <span className={`tag tag-sm status-value ${p?.status?.toLowerCase() || ""}`}>
                {PRODUCT_STATUSES[p?.status] || p?.status || "N/A"}
            </span>
        </p>
    ),
    "Operational State": (p) => (
        <p className="mb-0">
            <span className={`tag tag-sm status-value ${p?.operationalStatus?.toLowerCase() || ""}`}>
                {PRODUCT_OPERATIONAL_STATUSES[p?.operationalStatus] || p?.operationalStatus || "N/A"}
            </span>
        </p>
    ),
    "Type": (p) => (
        <>
            {p?.productOffering?.["@type"] || p?.productSpecification?.["@type"] || ""}
            {p?.productOffering && p?.productSpecification && (
                <>
                    <br/>
                    {p?.productSpecification["@type"] || "N/A"}
                </>
            )}
        </>
    ),
};

const ProductsTabContent = ({reloadRef, setReloadLoading}) => {
    const fetchFn = useCallback(async ({filters, page, pageSize, sort}) => {
        const params = new URLSearchParams({
            fields: API_FIELDS,
            limit: pageSize,
            sort,
            offset: (page - 1) * pageSize,
        });

        if (filters) {
            Object.entries(filters).forEach(([key, value]) => {
                if (Array.isArray(value)) {
                    value.forEach((v) => params.append(key, v));
                } else {
                    params.set(key, value);
                }
            });
        }

        return api.getProducts(params);
    }, []);

    return (
        <MonitoringTabContent
            reloadRef={reloadRef}
            setReloadLoading={setReloadLoading}
            allFields={ALL_FIELDS}
            basePath={BASE_PATH}
            pageSize={PAGE_SIZE}
            defaultSort={DEFAULT_SORT}
            defaultFilter={{...DEFAULT_FILTER}}
            fetchFn={fetchFn}
            renderFilter={(onFilterSubmit) => (
                <ProductsFilter onFilterSubmit={onFilterSubmit}/>
            )}
            renderTableBody={(data, fields) => (
                <MonitoringTableBody data={data} fields={fields} columns={COLUMNS}/>
            )}
        />
    );
};

export default ProductsTabContent;
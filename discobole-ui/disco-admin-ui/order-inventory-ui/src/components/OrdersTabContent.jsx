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
import OrdersFilter from "./OrdersFilter.jsx";
import service from "../service/OrderInventoryService.js";
import {getOperation, ORDER_STATUSES} from "./OrderDetails/utils/orderUtils";

const BASE_PATH = "/order-inventory/monitoring/orders";
const PAGE_SIZE = 10;
const API_FIELDS = "relatedParty.partyOrPartyRole,id,creationDate,relatedParty.role,state,channel.channel.name,productOrderItem";

const ALL_FIELDS = [
    {name: "Party Id", checked: true, disabled: false, value: "relatedParty.id", isSortable: false},
    {name: "Party Name", checked: true, disabled: false, value: "relatedParty.name", isSortable: false},
    {name: "Order Id", checked: true, disabled: true, value: "id", isSortable: false},
    {name: "Creation Date", checked: true, disabled: false, value: "creationDate", isSortable: true},
    {name: "Operation", checked: true, disabled: false, value: "operation", isSortable: false},
    {name: "Party Role", checked: true, disabled: false, value: "relatedParty.role", isSortable: false},
    {name: "Order State", checked: true, disabled: false, value: "state", isSortable: true},
    {name: "Channel", checked: true, disabled: false, value: "channel.name", isSortable: false},
];

const DEFAULT_SORT = `-${ALL_FIELDS[3].value}`;

const COLUMNS = {
    "Party Id": (order) => order.relatedParty?.[0]?.partyOrPartyRole?.id || "",
    "Party Name": (order) => order.relatedParty?.[0]?.partyOrPartyRole?.name || "",
    "Order Id": (order) => (
        <Link to={`/order-inventory/orders-details-page/${order.id}`}>{order.id}</Link>
    ),
    "Creation Date": (order) => formatToLocalDateTime(order.creationDate),
    "Operation": (order) => getOperation(order.productOrderItem),
    "Party Role": (order) => order.relatedParty?.[0]?.role || "",
    "Order State": (order) => (
        <p className="mb-0">
            <span className={`tag tag-sm status-value ${order.state?.toLowerCase()}`}>
                {ORDER_STATUSES[order.state] || order.state}
            </span>
        </p>
    ),
    "Channel": (order) => order.channel?.[0]?.channel?.name || "",
};

const OrdersTabContent = ({reloadRef, setReloadLoading}) => {
    const fetchFn = useCallback(async ({filters, page, pageSize, sort}) => {
        return service.fetchOrders({
            fields: API_FIELDS,
            limit: pageSize,
            sort,
            offset: page === 0 ? 0 : (page - 1) * pageSize,
            filters,
        });
    }, []);

    return (
        <MonitoringTabContent
            reloadRef={reloadRef}
            setReloadLoading={setReloadLoading}
            allFields={ALL_FIELDS}
            basePath={BASE_PATH}
            pageSize={PAGE_SIZE}
            defaultSort={DEFAULT_SORT}
            fetchFn={fetchFn}
            renderFilter={(onFilterSubmit) => (
                <OrdersFilter onFilterSubmit={onFilterSubmit}/>
            )}
            renderTableBody={(data, fields) => (
                <MonitoringTableBody data={data} fields={fields} columns={COLUMNS}/>
            )}
        />
    );
};

export default OrdersTabContent;
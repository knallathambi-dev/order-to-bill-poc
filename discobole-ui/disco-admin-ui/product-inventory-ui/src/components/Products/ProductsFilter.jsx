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
    formatISODateTime,
    MonitoringFilterForm,
    MultiSelectInput,
    SelectInput,
    TextInput,
} from "@discobole/common-ui";
import {PRODUCT_OPERATIONAL_STATUSES, PRODUCT_STATUSES} from "../../common/constants.js";

const adjustEndDate = (date) => {
    if (!date) return null;
    const adjusted = new Date(date);
    adjusted.setSeconds(59, 999);
    return adjusted;
};

const PRODUCT_OFFERING_TYPES = ["Contract", "AtomicProductOffering", "BundleProductOffering"];
const PRODUCT_SPECIFICATION_TYPES = ["ProductSpecificationRef"];

const toOptions = (map) => Object.entries(map).map(([value, label]) => ({value, label}));

const statusOptions = toOptions(PRODUCT_STATUSES);
const operationalStatusOptions = toOptions(PRODUCT_OPERATIONAL_STATUSES);

const FIELD_TO_PARAM = {
    productId: "id",
    orderId: "productOrderItem.productOrderId",
    orderItemId: "productOrderItem.orderItemId",
    partyId: "relatedParty.partyOrPartyRole.id",
    productOfferingId: "productOffering.id",
    productSpecificationId: "productSpecification.id",
    productName: "name",
    partyName: "relatedParty.partyOrPartyRole.name",
    productOfferingName: "productOffering.name",
    partyRole: "relatedParty.partyOrPartyRole.role",
    productOfferingType: "productOffering.@type",
    productSpecificationType: "productSpecification.@type",
    isRoot: "isRoot",
};

const DATE_FIELD_TO_PARAM = {
    startDate: "startDate.gte",
    endDate: "startDate.lte",
    creationStartDate: "creationDate.gte",
    creationEndDate: "creationDate.lte",
    lastUpdateStartDate: "lastUpdateDate.gte",
    lastUpdateEndDate: "lastUpdateDate.lte",
    terminationStartDate: "terminationDate.gte",
    terminationEndDate: "terminationDate.lte",
    orderStartDate: "orderDate.gte",
    orderEndDate: "orderDate.lte",
};

const EMPTY_VALUES = {
    productId: "",
    orderId: "",
    status: [],
    operationalStatus: [],
    orderItemId: "",
    partyId: "",
    startDate: null,
    endDate: null,
    productOfferingId: "",
    productSpecificationId: "",
    creationStartDate: null,
    creationEndDate: null,
    productName: "",
    partyName: "",
    lastUpdateStartDate: null,
    lastUpdateEndDate: null,
    productOfferingName: "",
    partyRole: "",
    terminationStartDate: null,
    terminationEndDate: null,
    productOfferingType: "",
    productSpecificationType: "",
    orderStartDate: null,
    orderEndDate: null,
    isRoot: "",
};

const INITIAL_VALUES = {
    ...EMPTY_VALUES,
    isRoot: "True",
};

const isValueEmpty = (value) =>
    value === null || value === undefined || value === "" || (Array.isArray(value) && value.length === 0);

const buildFilterParams = (values) =>
    Object.entries(values).reduce((acc, [key, value]) => {
        if (isValueEmpty(value)) return acc;

        if (key === "status" || key === "operationalStatus") {
            acc[key] = value.map((opt) => opt.value);
        } else if (FIELD_TO_PARAM[key]) {
            acc[FIELD_TO_PARAM[key]] = value;
        } else if (DATE_FIELD_TO_PARAM[key]) {
            acc[DATE_FIELD_TO_PARAM[key]] = key.includes("End")
                ? formatISODateTime(adjustEndDate(value))
                : formatISODateTime(value);
        }

        return acc;
    }, {});

const ProductsFilter = ({onFilterSubmit}) => (
    <MonitoringFilterForm
        initialValues={INITIAL_VALUES}
        emptyValues={EMPTY_VALUES}
        buildFilterParams={buildFilterParams}
        onFilterSubmit={onFilterSubmit}
        renderMainFilters={(formik, clearField) => (
            <div className="row row-cols-4">
                <TextInput id="productId" label="Product Id" formik={formik} onClear={clearField}/>
                <TextInput id="orderId" label="Product Order Id" formik={formik} onClear={clearField}/>
                <MultiSelectInput
                    id="status" label="Status" formik={formik}
                    options={statusOptions} placeholder="Select Status"
                />
                <MultiSelectInput
                    id="operationalStatus" label="Operational State" formik={formik}
                    options={operationalStatusOptions} placeholder="Select Operational Status"
                />
            </div>
        )}
        renderExtraFilters={(formik, clearField) => (
            <>
                <div className="row row-cols-4">
                    <TextInput id="orderItemId" label="Product Order Item Id" formik={formik} onClear={clearField}/>
                    <TextInput id="partyId" label="Party Id" formik={formik} onClear={clearField}/>
                    <DateInput id="startDate" label="Start Date From" formik={formik}
                               startId="startDate" endId="endDate" isStart
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    <DateInput id="endDate" label="Start Date To" formik={formik}
                               startId="startDate" endId="endDate" isStart={false}
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                </div>

                <div className="row row-cols-4">
                    <TextInput id="productOfferingId" label="Product Offering Id" formik={formik} onClear={clearField}/>
                    <TextInput id="productSpecificationId" label="Product Specification Id" formik={formik}
                               onClear={clearField}/>
                    <DateInput id="creationStartDate" label="Creation Date From" formik={formik}
                               startId="creationStartDate" endId="creationEndDate" isStart
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    <DateInput id="creationEndDate" label="Creation Date To" formik={formik}
                               startId="creationStartDate" endId="creationEndDate" isStart={false}
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                </div>

                <div className="row row-cols-4">
                    <TextInput id="productName" label="Product Name" formik={formik} onClear={clearField}/>
                    <TextInput id="partyName" label="Party Name" formik={formik} onClear={clearField}/>
                    <DateInput id="lastUpdateStartDate" label="Last Update Date From" formik={formik}
                               startId="lastUpdateStartDate" endId="lastUpdateEndDate" isStart
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    <DateInput id="lastUpdateEndDate" label="Last Update Date To" formik={formik}
                               startId="lastUpdateStartDate" endId="lastUpdateEndDate" isStart={false}
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                </div>

                <div className="row row-cols-4">
                    <TextInput id="productOfferingName" label="Product Offering Name" formik={formik}
                               onClear={clearField}/>
                    <TextInput id="partyRole" label="Party Role" formik={formik} onClear={clearField}/>
                    <DateInput id="terminationStartDate" label="Termination Date From" formik={formik}
                               startId="terminationStartDate" endId="terminationEndDate" isStart
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                    <DateInput id="terminationEndDate" label="Termination Date To" formik={formik}
                               startId="terminationStartDate" endId="terminationEndDate" isStart={false}
                               dateFormat="dd/MM/yyyy h:mm aa" showTimeSelect onClear={clearField}/>
                </div>

                <div className="row row-cols-4">
                    <SelectInput id="productOfferingType" label="Product Offering Type" formik={formik}
                                 options={PRODUCT_OFFERING_TYPES} placeholder="Select Product Offering Type"
                                 onClear={clearField}/>
                    <SelectInput id="productSpecificationType" label="Product Specification Type" formik={formik}
                                 options={PRODUCT_SPECIFICATION_TYPES} placeholder="Select Product Specification Type"
                                 onClear={clearField}/>
                    <SelectInput id="isRoot" label="Is Root" formik={formik}
                                 options={["True", "False"]} placeholder="Select Is Root" onClear={clearField}/>
                </div>
            </>
        )}
    />
);

export default ProductsFilter;
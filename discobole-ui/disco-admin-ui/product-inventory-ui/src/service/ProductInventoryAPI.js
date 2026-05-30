// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {httpClient} from "@discobole/common-ui";
import {PRODUCT_INVENTORY_URL} from "../constants.js";

const MGMT = `${PRODUCT_INVENTORY_URL}/productInventoryManagement`;

const API = {
    product: `${MGMT}/v1/product`,
    job: `${MGMT}/v1/job`,
    jobSpec: `${MGMT}/v1/jobSpecification`,
    uploadFile: `${MGMT}/v1/uploadFileUrl`,
    reports: `${MGMT}/v1/reports`,
    reportOfferings: `${MGMT}/v1/reports/productOfferingOptions`,
    configuration: `${PRODUCT_INVENTORY_URL}/configuration`,
};

const toQueryString = (params) =>
    params instanceof URLSearchParams ? params.toString() : params;

const extractFileName = (headers) => {
    const disposition = headers["content-disposition"];
    if (!disposition) return "downloadedFile";
    const match = disposition.match(/filename="?([^";]+)"?/);
    return match?.[1]?.trim() ?? "downloadedFile";
};

class ProductInventoryAPI {

    /* ---- Products ---- */

    fetchProductHierarchy = async (id) =>
        Promise.all([
            httpClient.get(`${API.product}/${id}`),
            httpClient.get(`${API.product}/?productRelationship.product.id=${id}`),
        ]);

    getProductById = async (id) => {
        const {data} = await httpClient.get(`${API.product}/${id}`);
        return data;
    };

    getProducts = async (params) => {
        const {data, headers} = await httpClient.get(
            `${API.product}?${toQueryString(params)}`
        );
        return {data, totalCount: headers["x-total-count"]};
    };

    getProductsByOrderIds = async (productOrderIds) => {
        const query = productOrderIds
            .map((id) => `productOrderItem.productOrderId=${encodeURIComponent(id)}`)
            .join("&");
        const {data} = await httpClient.get(`${API.product}?${query}`);
        return data;
    };

    getProductsByRelationship = async (rootProductId) => {
        const {data} = await httpClient.get(
            `${API.product}?productRelationship.product.id=${rootProductId}`
        );
        return data;
    };

    downloadProductExport = async (params) => {
        const {data, headers} = await httpClient.get(
            `${API.product}/export?${toQueryString(params)}`,
            {responseType: "blob"}
        );
        return {blob: data, fileName: extractFileName(headers)};
    };

    /* ---- Jobs ---- */

    getJobById = async (id) => {
        const {data} = await httpClient.get(`${API.job}/${id}`);
        return data;
    };

    fetchJobs = async (params) => {
        const {data, headers} = await httpClient.get(
            `${API.job}?${toQueryString(params)}`
        );
        return {data, totalCount: headers["x-total-count"]};
    };

    deleteJob = async (id) => {
        const {data} = await httpClient.delete(`${API.job}/${id}`);
        return data;
    };

    getExportFileInfo = async (id) => {
        const {data} = await httpClient.get(`${API.job}/${id}/exportFileInformation`);
        return data;
    };

    /* ---- Job Specifications ---- */

    getJobSpecById = async (id) => {
        const {data} = await httpClient.get(`${API.jobSpec}/${id}`);
        return data;
    };

    fetchJobSpecs = async (params) => {
        const {data, headers} = await httpClient.get(
            `${API.jobSpec}?${toQueryString(params)}`
        );
        return {data, totalCount: headers["x-total-count"]};
    };

    submitJob = async (requestData) => {
        const body = typeof requestData === 'string'
            ? requestData
            : JSON.stringify(requestData);

        const { data } = await httpClient.post(API.jobSpec, body);
        return data;
    };

    /* ---- File Upload ---- */

    getUploadFileUrl = async (jobId) => {
        const {data} = await httpClient.get(`${API.uploadFile}/${jobId}`);
        return data;
    };

    /* ---- Reports ---- */

    getReportsByParams = async (params) => {
        const {data} = await httpClient.get(
            `${API.reports}?${toQueryString(params)}`
        );
        return data;
    };

    getProductOfferingOptions = async () => {
        const {data} = await httpClient.get(API.reportOfferings);
        return data;
    };

    /* ---- Configuration ---- */

    getConfiguration = async () => {
        const {data} = await httpClient.get(API.configuration);
        return data;
    };
}

export default new ProductInventoryAPI();
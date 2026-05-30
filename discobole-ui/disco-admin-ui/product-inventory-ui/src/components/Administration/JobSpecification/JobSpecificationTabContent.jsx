// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useRef, useState} from "react";
import {Link} from "react-router-dom";
import {formatToLocalDateTime, MonitoringTabContent, MonitoringTableBody} from "@discobole/common-ui";
import JobSpecificationFilter from "./JobSpecificationFilter";
import api from "../../../service/ProductInventoryAPI.js";

const BASE_PATH = "/product-inventory/administration/jobSpecification";
const PAGE_SIZE = 10;

const ALL_FIELDS = [
    {name: "Job Specification Id", checked: true, disabled: true, value: "id", isSortable: false, type: "ALL"},
    {name: "Name", checked: true, disabled: false, value: "name", isSortable: false, type: "ALL"},
    {name: "Type", checked: true, disabled: false, value: "@type", isSortable: false, type: "ALL"},
    {name: "Status", checked: true, disabled: false, value: "lifecycleStatus", isSortable: false, type: "ALL"},
    {name: "Creation Date", checked: true, disabled: false, value: "creationDate", isSortable: true, type: "ALL"},
    {
        name: "Start Date",
        checked: true,
        disabled: false,
        value: "activePeriod.startDateTime",
        isSortable: true,
        type: "ALL"
    },
    {
        name: "End Date",
        checked: true,
        disabled: false,
        value: "activePeriod.endDateTime",
        isSortable: true,
        type: "ALL"
    },
    {name: "Actions", checked: true, disabled: true, value: "actions", isSortable: false, type: "ALL"},
];

function JobSpecificationTabContent() {
    const reloadRef = useRef(null);

    const [showModal, setShowModal] = useState(false);
    const [currentItemToDelete, setCurrentItemToDelete] = useState(null);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const handleDelete = (id) => {
        setCurrentItemToDelete(id);
        setShowModal(true);
    };

    const confirmDelete = async () => {
        if (!currentItemToDelete) return;
        setDeleteLoading(true);
        try {
            await api.deleteJob(currentItemToDelete);
            reloadRef.current?.();
        } catch (err) {
            console.error("Delete failed:", err);
        } finally {
            setDeleteLoading(false);
            setShowModal(false);
            setCurrentItemToDelete(null);
        }
    };

    const columns = {
        "Job Specification Id": (item) => (
            <Link to={`/product-inventory/jobSpecification-details-page/${item?.id}`}>
                {item?.id || "N/A"}
            </Link>
        ),
        "Name": (item) => item.name || "N/A",
        "Type": (item) => item["@type"] || "N/A",
        "Status": (item) => (
            <span className={`tag tag-sm status-value ${item.lifecycleStatus?.toLowerCase() || ""}`}>
                {item.lifecycleStatus || "N/A"}
            </span>
        ),
        "Creation Date": (item) => formatToLocalDateTime(item.creationDate) || "N/A",
        "Start Date": (item) => formatToLocalDateTime(item.activePeriod?.startDateTime) || "N/A",
        "End Date": (item) => formatToLocalDateTime(item.activePeriod?.endDateTime) || "N/A",
        "Actions": (item) => (
            <button
                type="button"
                className="btn btn-icon btn-no-outline btn-sm"
                title="Delete"
                onClick={() => handleDelete(item.id)}
            >
                ✕
            </button>
        ),
    };

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

        return api.fetchJobSpecs(params);
    }, []);

    return (
        <>
            <MonitoringTabContent
                reloadRef={reloadRef}
                allFields={ALL_FIELDS}
                basePath={BASE_PATH}
                pageSize={PAGE_SIZE}
                defaultSort=""
                fetchFn={fetchFn}
                renderFilter={(onFilterSubmit) => (
                    <JobSpecificationFilter onFilterSubmit={onFilterSubmit}/>
                )}
                renderTableBody={(data, fields) => (
                    <MonitoringTableBody data={data} fields={fields} columns={columns}/>
                )}
            />

            {showModal && (
                <div className="modal fade show d-block" tabIndex="-1" style={{backgroundColor: "rgba(0,0,0,0.5)"}}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Confirm Deletion</h5>
                                <button
                                    type="button"
                                    className="btn-close"
                                    onClick={() => setShowModal(false)}
                                    disabled={deleteLoading}
                                />
                            </div>
                            <div className="modal-body">
                                <p>Are you sure you want to delete this job specification?</p>
                            </div>
                            <div className="modal-footer">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => setShowModal(false)}
                                    disabled={deleteLoading}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-danger"
                                    onClick={confirmDelete}
                                    disabled={deleteLoading}
                                >
                                    {deleteLoading ? "Deleting..." : "Delete"}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default JobSpecificationTabContent;
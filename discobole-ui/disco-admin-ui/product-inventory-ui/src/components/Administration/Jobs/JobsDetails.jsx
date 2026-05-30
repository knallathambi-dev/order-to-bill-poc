// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import {LoadingIndicator, MonitoringPage, StatusPanel} from "@discobole/common-ui";
import JobsBasicDetails from "./JobsBasicDetails";
import api from "../../../service/ProductInventoryAPI.js";
import service from "../../../service/ProductInventoryService.js";

function JobsDetails() {
    const {id} = useParams();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchJob = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            setData(await api.getJobById(id));
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        fetchJob();
    }, [fetchJob]);

    const handleExport = async () => {
        try {
            await service.downloadJobExport(id);
        } catch (err) {
            console.error("Error exporting file:", err);
        }
    };

    if (loading) return <LoadingIndicator/>;

    if (error) {
        return (
            <StatusPanel
                variant="error"
                title="Something went wrong"
                message={error?.message}
                onAction={fetchJob}
                actionLabel="Retry"
            />
        );
    }

    if (!data) {
        return (
            <StatusPanel
                variant="info"
                title="No job found"
                onAction={fetchJob}
                actionLabel="Refresh"
            />
        );
    }

    return (
        <MonitoringPage
            title={`Job #${data.id}`}
            actions={
                data["@type"] === "ExportJob" ? (
                    <button className="btn btn-primary" type="button" onClick={handleExport}>
                        Export File
                    </button>
                ) : undefined
            }
        >
            <nav aria-label="breadcrumb" className="mt-n3">
                <ol className="breadcrumb mb-3">
                    <li className="breadcrumb-item">
                        <Link to="/product-inventory/administration/jobs">Jobs</Link>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                        Job Details
                    </li>
                </ol>
            </nav>

            <div className="card-body my-3">
                <div className="card-body">
                    <JobsBasicDetails data={data} reloadJobData={fetchJob}/>
                </div>
            </div>
        </MonitoringPage>
    );
}

export default JobsDetails;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {LoadingIndicator, StatusPanel, useSideMenu,} from "@discobole/common-ui";
import {Link, useNavigate, useParams} from "react-router-dom";
import JobSpecificationBasicDetails from "./JobSpecificationBasicDetails";
import api from "../../../service/ProductInventoryAPI.js";

const BASE_PATH = "/product-inventory";

function JobSpecificationDetails() {
    const {id, type} = useParams();
    const {isActiveNav} = useSideMenu();
    const navigate = useNavigate();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchJobSpec = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await api.getJobSpecById(id);
            setData(response);
        } catch (err) {
            setError(err);
            setData(null);
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        fetchJobSpec();
    }, [fetchJobSpec]);

    const navigateToRelatedJobs = () => {
        navigate(`${BASE_PATH}/administration/jobs`, {
            state: {jobSpecificationId: data?.id, filterApplied: true},
        });
    };

    const navigateToCreateJobSpec = () => {
        navigate(`${BASE_PATH}/create-jobSpecification-page`);
    };

    return (
        <>
            {loading || error || !data ? (
                <>
                    {loading && <LoadingIndicator/>}

                    {!loading && error && (
                        <StatusPanel
                            variant="error"
                            title="Something went wrong"
                            message={error?.message}
                            onAction={fetchJobSpec}
                            actionLabel="Retry"
                        />
                    )}

                    {!loading && !error && !data && (
                        <StatusPanel
                            variant="info"
                            title="No job specification found"
                            onAction={fetchJobSpec}
                            actionLabel="Refresh"
                        />
                    )}
                </>
            ) : (
                <div className={`py-1 content-wrapper ${isActiveNav ? "active-cont" : ""}`}>
                    <div className="container-fluid">
                        <div className="row">
                            <div className="col-12">
                                <nav aria-label="breadcrumb">
                                    <ol className="breadcrumb mb-0">
                                        <li className="breadcrumb-item">
                                            <Link to={`${BASE_PATH}/administration/jobSpecification`}>
                                                Job Specifications
                                            </Link>
                                        </li>
                                        <li className="breadcrumb-item active" aria-current="page">
                                            Job Specification Details
                                        </li>
                                    </ol>
                                </nav>
                            </div>
                        </div>
                    </div>
                    <main>
                        <div className="container-fluid py-4">
                            <div className="row">
                                <div className="col-12 mt-3 d-flex justify-content-between align-items-center">
                                    <h1 className="h3 m-0 py-3 d-inline-block">
                                        Job Specification #{data.id}
                                    </h1>
                                    <div className="d-flex gap-2">
                                        <button
                                            type="button"
                                            className="btn btn-primary"
                                            onClick={navigateToCreateJobSpec}
                                        >
                                            Create Job Specification
                                        </button>
                                        <button
                                            type="button"
                                            className="btn btn-secondary"
                                            onClick={navigateToRelatedJobs}
                                        >
                                            Related Jobs
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div className="card-body my-3">
                                <div className="card-body">
                                    <JobSpecificationBasicDetails data={data} type={type}/>
                                </div>
                            </div>
                        </div>
                    </main>
                </div>
            )}
        </>
    );
}

export default JobSpecificationDetails;
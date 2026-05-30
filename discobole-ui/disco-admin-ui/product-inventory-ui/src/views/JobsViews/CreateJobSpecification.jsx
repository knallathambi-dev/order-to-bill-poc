// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {Link} from "react-router-dom";
import {BootstrapTooltip, MonitoringPage} from "@discobole/common-ui";
import CreateJobSpecificationDetails
    from "../../components/Administration/JobSpecification/CreateJobSpecification/CreateJobSpecificationDetails";

function CreateJobSpecification() {
    return (
        <MonitoringPage>
            <nav aria-label="breadcrumb">
                <ol className="breadcrumb mb-0">
                    <li className="breadcrumb-item">
                        <Link to="/product-inventory/administration/jobSpecification">
                            Job Specifications
                        </Link>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                        Create Job Specification
                    </li>
                </ol>
            </nav>

            <div className="py-4">
                <div className="row g-0">
                    <div className="col-md-9">
                        <h3 className="mb-0" style={{fontSize: "32px", fontWeight: "bold"}}>
                            Create Job Specification
                        </h3>
                        <div className="mb-0 mt-3 alert alert-dismissible alert-info" role="alert">
                            <span className="alert-icon">
                                <span className="visually-hidden">Info</span>
                            </span>
                            <p>The date is displayed in your local time but will be saved in UTC.</p>
                            <BootstrapTooltip title="Close" placement="top" disposeOnAlertClose>
                                <button
                                    type="button"
                                    className="btn-close"
                                    data-bs-dismiss="alert"
                                    aria-label="Close"
                                >
                                    <span className="visually-hidden">Close</span>
                                </button>
                            </BootstrapTooltip>
                        </div>
                    </div>
                </div>

                <div className="row mt-3">
                    <div className="col-12">
                        <CreateJobSpecificationDetails/>
                    </div>
                </div>
            </div>
        </MonitoringPage>
    );
}

export default CreateJobSpecification;
// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useNavigate} from "react-router-dom";
import {MonitoringPage} from "@discobole/common-ui";
import JobSpecificationTabContent
    from "../../components/Administration/JobSpecification/JobSpecificationTabContent.jsx";

function JobSpecificationMonitoring() {
    const navigate = useNavigate();

    return (
        <MonitoringPage
            title="Job Specifications"
            actions={
                <button
                    className="btn btn-primary"
                    onClick={() => navigate("/product-inventory/create-jobSpecification-page")}
                >
                    Create Job Specification
                </button>
            }
        >
            <JobSpecificationTabContent/>
        </MonitoringPage>
    );
}

export default JobSpecificationMonitoring;
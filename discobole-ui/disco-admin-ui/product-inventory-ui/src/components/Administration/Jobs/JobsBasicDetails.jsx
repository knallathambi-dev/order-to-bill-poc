// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";
import {Link} from "react-router-dom";
import {ErrorMessageBox, formatToLocalDateTime, ReloadButton} from "@discobole/common-ui";
import JobReport from "./JobReport";

const DETAIL_FIELDS_LEFT = [
    {label: "Id", render: (data) => data?.id},
    {
        label: "Job Spec Id",
        render: (data) => (
            <Link to={`/product-inventory/jobSpecification-details-page/${data?.jobSpecification?.id}`}>
                {data?.jobSpecification?.id || "_"}
            </Link>
        ),
    },
    {label: "Start Date", render: (data) => formatToLocalDateTime(data?.executionPeriod?.startDateTime)},
    {label: "Type", render: (data) => data?.["@type"]},
];

const DETAIL_FIELDS_RIGHT = [
    {
        label: "Status",
        render: (data) => (
            <span className={`tag tag-sm status-value ${data?.status || ""}`}>
                {data?.status || "_"}
            </span>
        ),
    },
    {label: "Planned Date", render: (data) => formatToLocalDateTime(data?.plannedDate)},
    {label: "End Date", render: (data) => formatToLocalDateTime(data?.executionPeriod?.endDateTime)},
    {
        label: "File Name",
        render: (data) => data?.fileName,
        show: (data) => data?.["@type"] === "ExportJob",
    },
];

const DetailColumn = ({fields, data}) => (
    <div className="col-md-6">
        <div className="table-responsive">
            <table className="table align-middle mb-0 fs-6 gy-5 data-table">
                <tbody className="fw-semibold">
                {fields.map((field) => {
                    if (field.show && !field.show(data)) return null;
                    return (
                        <tr key={field.label}>
                            <td className="text-muted td-width">
                                <div className="d-flex align-items-center text-nowrap">
                                    {field.label}
                                </div>
                            </td>
                            <td className="fw-bold">
                                {field.render(data) || "_"}
                            </td>
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    </div>
);

const HAS_REPORT = ["TerminationJob", "ImportJob"];

function JobsBasicDetails({data, reloadJobData}) {
    const errorMessages = data.errorLog
        ? [{
            timeStamp: data.errorTimeStamp || new Date().toISOString(),
            message: data.errorLog,
        }]
        : [];

    return (
        <div>
            {/* Basic Info Card */}
            <div className="card mb-3">
                <div className="card-body">
                    <div className="row">
                        <DetailColumn fields={DETAIL_FIELDS_LEFT} data={data}/>
                        <DetailColumn fields={DETAIL_FIELDS_RIGHT} data={data}/>
                    </div>
                </div>
            </div>

            {/* Job Report Section */}
            {HAS_REPORT.includes(data["@type"]) && (
                <div className="card mb-3">
                    <div className="custom-fieldset">
                        <legend className="w-auto px-2 fw-bold text-dark fs-4">
                            Job Overview
                        </legend>
                        <div className="position-absolute top-0 end-0 mt-3 me-3">
                            <ReloadButton onClick={reloadJobData}/>
                        </div>
                        <div className="mb-5"/>
                        <div className="mt-2">
                            <JobReport data={data}/>
                        </div>
                    </div>
                </div>
            )}

            {/* Error Section */}
            {data.errorLog && (
                <div className="row mt-3">
                    <div className="col-md-12">
                        <ErrorMessageBox errorMessages={errorMessages}/>
                    </div>
                </div>
            )}
        </div>
    );
}

JobsBasicDetails.propTypes = {
    data: PropTypes.shape({
        id: PropTypes.string.isRequired,
        jobSpecification: PropTypes.shape({id: PropTypes.string}),
        executionPeriod: PropTypes.shape({
            startDateTime: PropTypes.string,
            endDateTime: PropTypes.string,
        }),
        plannedDate: PropTypes.string,
        status: PropTypes.string,
        "@type": PropTypes.string,
        fileName: PropTypes.string,
        errorLog: PropTypes.string,
        errorTimeStamp: PropTypes.string,
    }).isRequired,
    reloadJobData: PropTypes.func.isRequired,
};

export default JobsBasicDetails;
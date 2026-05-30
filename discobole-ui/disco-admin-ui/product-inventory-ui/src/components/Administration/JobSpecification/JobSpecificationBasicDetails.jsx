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
import {formatToLocalDateTime} from "@discobole/common-ui";

function JobSpecificationBasicDetails({data}) {
    const schedule = data["schedule"];

    const renderScheduleDetails = () => {
        if (!schedule) return null;

        switch (schedule["@type"]) {
            case "OneTimeJobScheduler":
                return (
                    <div className="schedule-detail">
                        <div className="details-wrapper card border-1 p-2">
                            <div className="row">
                                <div className="col-lg-6 col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Type
                                                </div>
                                            </td>
                                            <td className="fw-bold">One-time Job Scheduler</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div className="col-lg-6 col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Planned
                                                    Date
                                                </div>
                                            </td>
                                            <td className="fw-bold">{formatToLocalDateTime(schedule.plannedDate) || "_"}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                );

            case "RecurringJobScheduler":
                return (
                    <div className="schedule-detail">
                        <div className="details-wrapper mb-3 card border-1 p-2">
                            <div className="row g-0">
                                <div className="col-lg-6 col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Type
                                                </div>
                                            </td>
                                            <td className="fw-bold">Recurring Job Scheduler</td>
                                        </tr>
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Start
                                                    Date
                                                </div>
                                            </td>
                                            <td className="fw-bold"> {schedule?.scheduledPeriod.startDate || "_"}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div className="col-lg-6 col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Execution
                                                    Time
                                                </div>
                                            </td>
                                            <td className="fw-bold">{formatToLocalDateTime(schedule.executionTime) || "_"}</td>
                                        </tr>
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">End
                                                    Date
                                                </div>
                                            </td>
                                            <td className="fw-bold">  {schedule?.scheduledPeriod.endDate || "_"}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        {/* Frequency as legend inside the border */}
                        <div className="details-wrapper card border-1 p-2">
                            <div className="row g-0">
                                <div className="col-md-12">
                                    <h6 className="mt-2 mb-1 px-2 fw-semibold fs-6">Frequency</h6>
                                </div>
                                <div className="col-lg-6 col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Amount
                                                </div>
                                            </td>
                                            <td className="fw-bold">{schedule?.frequency.amount || "_"}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div className="col-lg-6 col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Time
                                                    Period
                                                </div>
                                            </td>
                                            <td className="fw-bold">  {schedule?.frequency.timePeriod || "_"}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                );

            case "ImmediateJobScheduler":
                return (
                    <div className="schedule-detail">
                        <div className="details-wrapper card border-1 p-2">
                            <div className="row">
                                <div className="col-md-12">
                                    <table className="table mb-0">
                                        <tbody className="fw-semibold">
                                        <tr>
                                            <td className="text-muted td-width-2">
                                                <div
                                                    className="d-flex align-items-center text-nowrap">Type
                                                </div>
                                            </td>
                                            <td className="fw-bold">Immediate Job Scheduler</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                );

            default:
                return null;
        }
    };

    const showFileType = (type) => {
        if (data["@type"] === "ExportJobSpecification" || data["@type"] === "ImportJobSpecification") {
            return data?.contentType ? (data.contentType === "json" ? "JSON" : "CSV") : "_";
        } else if (data["@type"] === "PurgeJobSpecification") {
            return data?.purgeType || "_";
        }
        return "_";
    };

    function showQuery(query) {
        if (!query) return "-";

        let parsedQuery;

        try {
            parsedQuery = typeof query === "string" ? JSON.parse(query) : query;
        } catch (e) {
            const params = new URLSearchParams(query);
            parsedQuery = {};

            for (const [key, value] of params.entries()) {
                if (parsedQuery[key]) {
                    if (!Array.isArray(parsedQuery[key])) {
                        parsedQuery[key] = [parsedQuery[key]];
                    }
                    parsedQuery[key].push(value);
                } else {
                    parsedQuery[key] = value;
                }
            }
        }

        const formattedQuery = JSON.stringify(parsedQuery, null, 4);

        return <pre>{formattedQuery}</pre>;
    }

    return (
        <div className="job-specification-details table-container">
            <div className="row g-0">
                <div className="col-lg-6 col-md-12">
                    <div className="table-responsive">
                        <table className="table mb-0">
                            <tbody className="fw-semibold">
                            <tr>
                                <td className="text-muted td-width">Id</td>
                                <td className="fw-bold">{data?.id || "_"}</td>
                            </tr>
                            <tr>
                                <td className="text-muted td-width">Name</td>
                                <td className="fw-bold">{data?.name || "_"}</td>
                            </tr>
                            <tr>
                                <td className="text-muted td-width">Start Date</td>
                                <td className="fw-bold">
                                    {formatToLocalDateTime(data?.activePeriod?.startDateTime) || "_"}
                                </td>
                            </tr>
                            <tr>
                                <td className="text-muted td-width">Type</td>
                                <td className="fw-bold">{data["@type"] || "_"}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="col-lg-6 col-md-12">
                    <div className="table-responsive">
                        <table className="table mb-0">
                            <tbody className="fw-semibold">
                            <tr>
                                <td className="text-muted td-width">Status</td>
                                <td>
                                    <div className="status-container position-relative">
                                            <span
                                                className={`tag tag-sm status-value ${data?.lifecycleStatus || "_"}`}
                                            >
                                                {data?.lifecycleStatus || "_"}
                                            </span>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td className="text-muted td-width">Creation Date</td>
                                <td className="fw-bold">
                                    {formatToLocalDateTime(data?.creationDate) || "_"}
                                </td>
                            </tr>
                            <tr>
                                <td className="text-muted td-width">End Date</td>
                                <td className="fw-bold">
                                    {formatToLocalDateTime(data?.activePeriod?.endDateTime) || "_"}
                                </td>
                            </tr>
                            <tr>
                                <td className="text-muted td-width">
                                    {data["@type"] !== "TerminationJobSpecification" && (
                                        <div className="d-flex align-items-center text-nowrap">
                                            {data["@type"] === "PurgeJobSpecification" ? "Purge Type" : "Content Type"}
                                        </div>
                                    )}
                                </td>

                                <td className="fw-bold">
                                    {data["@type"] !== "TerminationJobSpecification" ? showFileType(data["@type"]) : ""}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="col-md-12">
                    {schedule && (
                        <div className="schedule-section card bg-body-tertiary p-3 border-1 my-2">
                            <h6 className="schedule-header">Schedule Details</h6>
                            <div className="schedule-content">{renderScheduleDetails()}</div>
                        </div>
                    )}
                    {(data["@type"] === "ExportJobSpecification" || data["@type"] === "PurgeJobSpecification") && (
                        <div className="field-container">
                            <table
                                className="table mb-0 align-middle mb-0 fs-6 gy-5 border-top border-light border-1 my-1">
                                <tbody className="fw-semibold">
                                <tr>
                                    <td className="text-muted td-width">
                                        <div
                                            className="d-flex align-items-center text-nowrap">Query
                                        </div>
                                    </td>
                                    <td className="fw-bold">
                                        {showQuery(data?.query)}
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    )}
                    {data["@type"] === "ExportJobSpecification" && (
                        <div className="field-container">
                            <table
                                className="table mb-0 align-middle mb-0 fs-6 gy-5 border-top border-light border-1 my-1">
                                <tbody className="fw-semibold">
                                <tr>
                                    <td className="text-muted td-width">
                                        <div
                                            className="d-flex align-items-center text-nowrap">Fields
                                        </div>
                                    </td>
                                    <td className="fw-bold">
                                        {Array.isArray(data?.fields) && data.fields.length > 0 ? (
                                            <ul>
                                                {data.fields.map((field, index) => (
                                                    <li key={index}>{field}</li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <span>All fields are available</span>
                                        )}
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

JobSpecificationBasicDetails.propTypes = {
    data: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        activePeriod: PropTypes.shape({
            startDateTime: PropTypes.string,
            endDateTime: PropTypes.string,
        }),
        lifecycleStatus: PropTypes.string,
        creationDate: PropTypes.string,
        fields: PropTypes.arrayOf(PropTypes.string),
        query: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
        "@type": PropTypes.string.isRequired,
        contentType: PropTypes.string,
        purgeType: PropTypes.string,
        schedule: PropTypes.shape({
            "@type": PropTypes.string,
            plannedDate: PropTypes.string,
            executionTime: PropTypes.string,
            scheduledPeriod: PropTypes.shape({
                startDateTime: PropTypes.string,
                endDateTime: PropTypes.string,
            }),
            frequency: PropTypes.shape({
                amount: PropTypes.number,
                timePeriod: PropTypes.string,
            }),
        }),
    }).isRequired,
};

export default JobSpecificationBasicDetails;
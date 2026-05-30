// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useNavigate} from "react-router-dom";
import PageNotFound from "./PageNotFound";
import Unauthorized from "./Unauthorized";

const ERROR_CONFIG = {
    400: {
        title: "Bad Request",
        message: "The request was invalid. Please check your input and try again.",
        color: "#ff7900"
    },
    403: {title: "Forbidden", message: "You don't have permission to perform this action.", color: "#cd3c14"},
    408: {
        title: "Request Timeout",
        message: "The server took too long to respond. Please try again.",
        color: "#ff7900"
    },
    500: {title: "Server Error", message: "Something went wrong on our end. Please try again later.", color: "#cd3c14"},
    502: {
        title: "Bad Gateway",
        message: "The server received an invalid response. Please try again later.",
        color: "#cd3c14"
    },
    503: {
        title: "Service Unavailable",
        message: "The service is temporarily unavailable. Please try again later.",
        color: "#cd3c14"
    },
};

const ErrorHandler = ({error}) => {
    const navigate = useNavigate();

    if (!error) return null;

    const status = error.status || error.response?.status;

    // Delegate to dedicated pages
    if (status === 404) return <PageNotFound/>;
    if (status === 401) return <Unauthorized/>;

    const config = ERROR_CONFIG[status] || {
        title: "Unexpected Error",
        message: error.message || "An unexpected error occurred. Please try again.",
        color: "#cd3c14",
    };

    return (
        <div className="d-flex align-items-center justify-content-center" style={{minHeight: "60vh"}}>
            <div className="text-center">
                {status && (
                    <div className="mb-4">
                        <span
                            className="display-1 fw-bold"
                            style={{color: config.color, fontSize: "6rem", lineHeight: 1}}
                        >
                            {status}
                        </span>
                    </div>
                )}
                <h2 className="fw-bold mb-3">{config.title}</h2>
                <p className="text-muted mb-4" style={{maxWidth: "400px", margin: "0 auto"}}>
                    {config.message}
                </p>
                <div className="d-flex gap-2 justify-content-center">
                    <button
                        className="btn btn-outline-secondary"
                        onClick={() => navigate(-1)}
                    >
                        <em className="icon-back me-2" aria-hidden="true"/>
                        Go Back
                    </button>
                    <button
                        className="btn btn-primary"
                        onClick={() => window.location.reload()}
                    >
                        <em className="icon-reload me-2" aria-hidden="true"/>
                        Retry
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ErrorHandler;
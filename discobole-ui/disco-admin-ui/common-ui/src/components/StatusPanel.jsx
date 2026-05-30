// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

const VARIANTS = {
    error: {
        bg: "bg-danger",
        btn: "btn-danger",
        icon: "icon-alert",
        title: "Something went wrong",
        message: "We couldn't load the data. Please try again.",
        actionLabel: "Try again",
    },
    info: {
        bg: "bg-secondary",
        btn: "btn-secondary",
        icon: "icon-search",
        title: "No results found",
        message: "Try adjusting your filters or refreshing the page.",
        actionLabel: "Refresh",
    },
    success: {
        bg: "bg-success",
        btn: "btn-success",
        icon: "icon-checkbox_tick",
        title: "All done",
        message: "Operation completed successfully.",
        actionLabel: "Continue",
    },
    warning: {
        bg: "bg-warning",
        btn: "btn-warning",
        icon: "icon-alert",
        title: "Heads up",
        message: "Please review the details below.",
        actionLabel: "Retry",
    },
};

const StatusPanel = ({variant = "info", title, message, onAction, actionLabel}) => {
    const config = VARIANTS[variant] ?? VARIANTS.info;

    return (
        <div className="d-flex justify-content-center py-5">
            <div className="text-center col-10 col-sm-8 col-md-5 col-lg-4">
                <div
                    className={`${config.bg} rounded-circle d-inline-flex align-items-center justify-content-center mb-4 p-3`}
                >
                    <em className={`${config.icon} text-white fs-1`} aria-hidden="true"/>
                </div>

                <h5 className="fw-bold mb-2">{title ?? config.title}</h5>
                <p className="text-muted mb-4">{message ?? config.message}</p>

                {onAction && (
                    <button type="button" className={`btn ${config.btn} btn-sm`} onClick={onAction}>
                        <em className="icon-Reload me-1" aria-hidden="true"/>
                        {actionLabel ?? config.actionLabel}
                    </button>
                )}
            </div>
        </div>
    );
};

export default StatusPanel;
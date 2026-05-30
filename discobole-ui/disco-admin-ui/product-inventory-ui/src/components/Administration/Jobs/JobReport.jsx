// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useMemo, useState} from "react";
import PropTypes from "prop-types";
import {Link} from "react-router-dom";
import {Pagination} from "@discobole/common-ui";

const REPORT_TABS = [
    {
        name: "Success Case",
        key: "successReport",
        title: "Products Completed",
        icon: (
            <div className="check-circle">
                <span className="icon-checkbox_tick"/>
            </div>
        ),
    },
    {
        name: "Failure Case",
        key: "failureReport",
        title: "Products Failed",
        icon: (
            <div className="warning-circle">
                <span className="icon-error_severe"/>
            </div>
        ),
    },
];

const ITEMS_PER_PAGE = 5;

function JobReport({data}) {
    const [activeTab, setActiveTab] = useState("Success Case");
    const [currentPage, setCurrentPage] = useState(1);

    // Read initial tab from URL if present
    useEffect(() => {
        const tab = new URLSearchParams(window.location.search).get("tab");
        if (tab === "Failure Case") setActiveTab("Failure Case");
    }, []);

    const activeConfig = REPORT_TABS.find((t) => t.name === activeTab);
    const products = useMemo(
        () => data?.[activeConfig.key]?.products || [],
        [data, activeConfig.key]
    );
    const totalPages = Math.ceil(products.length / ITEMS_PER_PAGE);
    const paginatedProducts = products.slice(
        (currentPage - 1) * ITEMS_PER_PAGE,
        currentPage * ITEMS_PER_PAGE
    );

    const isImportJob = data["@type"] === "ImportJob";
    const isFailure = activeTab === "Failure Case";

    // Reset to page 1 when tab or data changes
    useEffect(() => {
        setCurrentPage(1);
    }, [activeTab, data]);

    const handleTabClick = (tabName) => {
        setActiveTab(tabName);
    };

    const handlePagination = (rel) => {
        setCurrentPage((prev) => {
            const next = rel === "next" ? prev + 1 : prev - 1;
            return Math.max(1, Math.min(next, totalPages));
        });
    };

    const colSpan = 3 + (isImportJob ? 1 : 0) + (isImportJob && isFailure ? 1 : 0);

    return (
        <div className="termination-job-report">
            {/* Tabs */}
            <ul className="nav nav-tabs">
                {REPORT_TABS.map((tab) => (
                    <li className="nav-item" key={tab.name}>
                        <a
                            className={`nav-link ${activeTab === tab.name ? "active" : ""}`}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                handleTabClick(tab.name);
                            }}
                        >
                            <span className="me-2">{tab.icon}</span> {tab.name}
                        </a>
                    </li>
                ))}
            </ul>

            {/* Report Content */}
            <div className="report-content mt-3">
                <div className="report-content-container custom-padding">
                    <h6 className="report-title text-start">{activeConfig.title}</h6>
                    <div style={{marginBottom: "30px"}}/>

                    <div className="table-responsive">
                        <table className="table align-middle text-nowrap">
                            <thead>
                            <tr>
                                <th>Product ID</th>
                                <th>Type</th>
                                <th>Name</th>
                                {isImportJob && <th>External Identifier</th>}
                                {isImportJob && isFailure && <th>Fail Message</th>}
                            </tr>
                            </thead>
                            <tbody>
                            {paginatedProducts.length > 0 ? (
                                paginatedProducts.map((product) => (
                                    <tr key={product.id}>
                                        <td>
                                            <Link to={`/product-inventory/products-details-page/${product.id}`}>
                                                {product.id}
                                            </Link>
                                        </td>
                                        <td>{product["@type"] || "N/A"}</td>
                                        <td>{product.name || "N/A"}</td>
                                        {isImportJob && <td>{product.externalIdentifier || "N/A"}</td>}
                                        {isImportJob && isFailure && <td>{product.failReason || "N/A"}</td>}
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan={colSpan} className="text-center">
                                        No products found in this Overview
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>

                    {totalPages > 1 && (
                        <Pagination
                            currentPage={currentPage}
                            totalPages={totalPages}
                            onPageChange={setCurrentPage}
                            handlePagination={handlePagination}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

JobReport.propTypes = {
    data: PropTypes.shape({
        "@type": PropTypes.string.isRequired,
        successReport: PropTypes.shape({
            products: PropTypes.arrayOf(
                PropTypes.shape({
                    id: PropTypes.string.isRequired,
                    "@type": PropTypes.string.isRequired,
                    name: PropTypes.string.isRequired,
                    externalIdentifier: PropTypes.string,
                    failReason: PropTypes.string,
                })
            ),
        }),
        failureReport: PropTypes.shape({
            products: PropTypes.arrayOf(
                PropTypes.shape({
                    id: PropTypes.string.isRequired,
                    "@type": PropTypes.string.isRequired,
                    name: PropTypes.string.isRequired,
                    externalIdentifier: PropTypes.string,
                    failReason: PropTypes.string,
                })
            ),
        }),
    }).isRequired,
};

export default JobReport;
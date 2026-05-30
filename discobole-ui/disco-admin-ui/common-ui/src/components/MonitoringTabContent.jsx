// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import Pagination from "./Pagination";
import TableHead from "./TableHead";
import LoadingIndicator from "./LoadingIndicator";
import StatusPanel from "./StatusPanel";

const MonitoringTabContent = ({
                                  reloadRef,
                                  setReloadLoading,
                                  allFields,
                                  basePath,
                                  pageSize = 10,
                                  defaultSort,
                                  defaultFilter = null,
                                  fetchFn,
                                  renderFilter,
                                  renderTableBody,
                                  noDataMessage = "We couldn’t find any results for your current filters.",
                              }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const pageParam = new URLSearchParams(location.search).get("page");

    const [data, setData] = useState([]);
    const [fields, setFields] = useState(allFields);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filterValues, setFilterValues] = useState(defaultFilter);
    const [currentPage, setCurrentPage] = useState(parseInt(pageParam, 10) || 1);
    const [totalPages, setTotalPages] = useState(0);
    const [sortedColumn, setSortedColumn] = useState(defaultSort);

    const fetchData = useCallback(
        async (filters, page, sort) => {
            setLoading(true);
            setError(null);
            try {
                const {data: responseData, totalCount} = await fetchFn({
                    filters,
                    page,
                    pageSize,
                    sort,
                });

                if (!responseData || responseData.length === 0) {
                    setData([]);
                    setTotalPages(0);
                    return;
                }

                setTotalPages(Math.ceil(totalCount / pageSize));
                setData(responseData);
            } catch (err) {
                setError(err);
                setData([]);
                setTotalPages(0);
            } finally {
                setLoading(false);
            }
        },
        [fetchFn, pageSize],
    );

    useEffect(() => {
        setReloadLoading?.(loading);
    }, [loading, setReloadLoading]);

    // Wire up external reload trigger
    useEffect(() => {
        if (reloadRef) {
            reloadRef.current = () =>
                fetchData(filterValues, currentPage, sortedColumn);
        }
    }, [reloadRef, fetchData, filterValues, currentPage, sortedColumn]);

    useEffect(() => {
        if (!pageParam) {
            navigate(`${basePath}?page=${currentPage}`, {replace: true});
            return;
        }

        const parsedPage = parseInt(pageParam, 10);
        if (!Number.isNaN(parsedPage)) {
            setCurrentPage(parsedPage);
            fetchData(filterValues, parsedPage, sortedColumn);
        }
    }, [location.search, filterValues, sortedColumn, basePath, navigate, pageParam, fetchData]);

    const handleSorting = (fieldName) => {
        const newSort =
            sortedColumn === fieldName
                ? sortedColumn.startsWith("-")
                    ? fieldName
                    : `-${fieldName}`
                : fieldName;
        setSortedColumn(newSort);
        navigate(`${basePath}?page=1`);
    };

    const handlePageChange = (pageNumber) => {
        const safePage = Math.max(1, Math.min(pageNumber, totalPages || 1));
        setCurrentPage(safePage);
        navigate(`${basePath}?page=${safePage}`);
    };

    const handlePagination = (rel) => {
        const newPage = rel === "next" ? currentPage + 1 : currentPage - 1;
        if (newPage >= 1 && newPage <= totalPages) {
            handlePageChange(newPage);
        }
    };

    const onFilterSubmit = (filteredData) => {
        const hasFilters = Object.keys(filteredData).length > 0;
        setFilterValues(hasFilters ? filteredData : null);
        navigate(`${basePath}?page=1`);
    };

    const handleRetry = () => {
        fetchData(filterValues, currentPage, sortedColumn);
    };

    const isEmpty = !loading && !error && data.length === 0;

    return (
        <div>
            {renderFilter(onFilterSubmit)}

            {loading ? (
                <LoadingIndicator/>
            ) : data.length > 0 ? (
                <div className="mt-4">
                    <div className="table-responsive">
                        <table className="table align-middle text-nowrap">
                            <TableHead
                                fields={fields}
                                setFields={setFields}
                                handleSorting={handleSorting}
                                sortedColumn={sortedColumn}
                            />
                            {renderTableBody(data, fields, allFields)}
                        </table>
                        <Pagination
                            currentPage={currentPage}
                            totalPages={totalPages}
                            onPageChange={handlePageChange}
                            handlePagination={handlePagination}
                        />
                    </div>
                </div>
            ) : (
                <StatusPanel
                    variant={error ? "error" : "info"}
                    // Let default titles handle most cases; you can override if needed
                    message={error?.message || (isEmpty ? noDataMessage : undefined)}
                    onAction={handleRetry}
                    // error → "Retry", otherwise default "Refresh" from StatusPanel
                    actionLabel={error ? "Retry" : undefined}
                />
            )}
        </div>
    );
};

export default MonitoringTabContent;
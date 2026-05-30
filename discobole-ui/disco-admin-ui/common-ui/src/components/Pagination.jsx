// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";

export default function Pagination({
                                       currentPage,
                                       totalPages,
                                       handlePagination,
                                       onPageChange,
                                   }) {
    const getPageNumbers = () => {
        const delta = 2;
        const range = [];

        for (
            let i = Math.max(2, currentPage - delta);
            i <= Math.min(totalPages - 1, currentPage + delta);
            i++
        ) {
            range.push(i);
        }
        if (currentPage - delta > 2) {
            range.unshift("...");
        }
        if (currentPage + delta < totalPages - 1) {
            range.push("...");
        }
        range.unshift(1);
        if (totalPages > 1) {
            range.push(totalPages);
        }
        return range;
    };

    const pageNumbers = getPageNumbers();

    return (
        <nav aria-label="Page navigation example">
            <ul className="pagination justify-content-center p-4">
                <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
                    <button
                        className="page-link"
                        aria-disabled={currentPage === 1}
                        title="prev"
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage !== 1) handlePagination("prev");
                        }}
                    ></button>
                </li>
                {pageNumbers.map((number, index) => (
                    <li
                        key={index}
                        className={`page-item ${currentPage === number ? "active" : ""}`}
                    >
                        {number === "..." ? (
                            <span className="page-link">...</span>
                        ) : (
                            <button
                                className="page-link"
                                onClick={() => onPageChange(number)}
                            >
                                {number}
                            </button>
                        )}
                    </li>
                ))}
                <li
                    className={`page-item ${
                        currentPage === totalPages ? "disabled" : ""
                    }`}
                >
                    <button
                        className="page-link"
                        aria-disabled={currentPage === totalPages}
                        title="next"
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage !== totalPages) handlePagination("next");
                        }}
                    ></button>
                </li>
            </ul>
        </nav>
    );
}

Pagination.propTypes = {
    currentPage: PropTypes.number.isRequired,
    totalPages: PropTypes.number.isRequired,
    handlePagination: PropTypes.func.isRequired,
    onPageChange: PropTypes.func.isRequired,
};
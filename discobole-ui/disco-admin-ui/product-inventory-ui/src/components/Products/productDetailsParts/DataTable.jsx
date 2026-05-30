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

const DataTable = ({columns, data, rowKey, renderRow}) => (
    <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
        <thead>
        <tr>
            {columns.map((col) => (
                <th key={col} className="text-muted">{col}</th>
            ))}
        </tr>
        </thead>
        <tbody className="fw-semibold">
        {(data || []).map((item, index) => (
            <tr key={rowKey ? rowKey(item, index) : index}>
                {renderRow(item, index).map((cell, cellIndex) => (
                    <td key={cellIndex} className="fw-bold">{cell ?? "_"}</td>
                ))}
            </tr>
        ))}
        </tbody>
    </table>
);

DataTable.propTypes = {
    columns: PropTypes.arrayOf(PropTypes.string).isRequired,
    data: PropTypes.array,
    rowKey: PropTypes.func,
    renderRow: PropTypes.func.isRequired,
};

export default DataTable;
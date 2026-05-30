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

const KeyValueTable = ({rows}) => (
    <table className="table mb-0">
        <tbody className="fw-semibold">
        {rows.map(({label, value}) => (
            <tr key={label}>
                <td className="text-muted">
                    <div className="d-flex align-items-center text-nowrap">{label}</div>
                </td>
                <td className="fw-bold text-end">{value ?? "_"}</td>
            </tr>
        ))}
        </tbody>
    </table>
);

KeyValueTable.propTypes = {
    rows: PropTypes.arrayOf(
        PropTypes.shape({
            label: PropTypes.string.isRequired,
            value: PropTypes.node,
        })
    ).isRequired,
};

export default KeyValueTable;
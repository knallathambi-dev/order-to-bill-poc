// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";

const PopoverRow = ({ label, children, isError = false }) => (
    <tr>
        <td className="text-muted text-start">
            <div className="d-flex align-items-center">{label}</div>
        </td>
        <td className={`fw-bold text-end ${isError ? "text-danger" : ""}`}>
            {children}
        </td>
    </tr>
);

PopoverRow.propTypes = {
    label: PropTypes.string.isRequired,
    children: PropTypes.node.isRequired,
    isError: PropTypes.bool,
};

export default PopoverRow;
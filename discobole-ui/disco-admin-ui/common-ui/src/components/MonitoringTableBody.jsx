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

/**
 * Generic monitoring table body.
 *
 * Instead of hardcoding column rendering per entity, it accepts a `columns` map
 * that defines how each field should be rendered.
 *
 * @param {object}   props
 * @param {Array}    props.data       - Array of row data objects
 * @param {Array}    props.fields     - Current field definitions (with checked state)
 * @param {object}   props.columns    - Map of field name → (row, field) => ReactNode
 * @param {function} props.getRowKey  - (row) => unique key for each row (defaults to row.id)
 *
 * Usage:
 * ```jsx
 * const columns = {
 *     "Party Id": (product) => product?.relatedParty?.[0]?.partyOrPartyRole?.id || "N/A",
 *     "Product Id": (product) => <Link to={`/details/${product.id}`}>{product.id}</Link>,
 *     "Status": (product) => (
 *         <span className={`tag tag-sm status-value ${product.status?.toLowerCase()}`}>
 *             {product.status || "N/A"}
 *         </span>
 *     ),
 * };
 *
 * <MonitoringTableBody data={data} fields={fields} columns={columns} />
 * ```
 */
function MonitoringTableBody({data, fields, columns, getRowKey}) {
    const resolveRowKey = getRowKey || ((row) => row?.id);

    return (
        <tbody>
        {data.map((row) => {
            const rowKey = resolveRowKey(row);
            return (
                <tr key={rowKey}>
                    {fields.map((field) => {
                        if (!field.checked) return null;

                        const renderer = columns[field.name];
                        if (!renderer) return null;

                        return (
                            <td key={`${field.name}-${rowKey}`}>
                                {renderer(row, field)}
                            </td>
                        );
                    })}
                </tr>
            );
        })}
        </tbody>
    );
}

MonitoringTableBody.propTypes = {
    data: PropTypes.array.isRequired,
    fields: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string.isRequired,
            checked: PropTypes.bool.isRequired,
        })
    ).isRequired,
    columns: PropTypes.objectOf(PropTypes.func).isRequired,
    getRowKey: PropTypes.func,
};

export default MonitoringTableBody;
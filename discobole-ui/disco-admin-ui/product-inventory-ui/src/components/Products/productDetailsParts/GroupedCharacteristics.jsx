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

const PRIORITY_KEYS = ["validFrom", "validTo", "value"];
const DATE_KEYS = new Set(["validFrom", "validTo"]);

const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

const formatCellValue = (key, rawValue) => {
    if (rawValue == null) return "_";
    if (DATE_KEYS.has(key)) {
        return formatToLocalDateTime(rawValue) || rawValue;
    }
    return rawValue;
};

const getDynamicKeys = (characteristics) => {
    const keys = [...new Set(
        characteristics.flatMap((char) =>
            typeof char.value === "object" ? Object.keys(char.value) : ["value"]
        )
    )];

    return keys.sort((a, b) => {
        const idxA = PRIORITY_KEYS.indexOf(a);
        const idxB = PRIORITY_KEYS.indexOf(b);
        if (idxA === -1 && idxB === -1) return a.localeCompare(b);
        if (idxA === -1) return -1;
        if (idxB === -1) return 1;
        return idxA - idxB;
    });
};

const GroupedCharacteristics = ({groupedCharacteristics}) => (
    <div>
        {Object.entries(groupedCharacteristics).map(([type, characteristics]) => {
            const dynamicKeys = getDynamicKeys(characteristics);

            return (
                <div key={type} className="mb-3">
                    <table
                        data-testid="grouped-characteristics-tables"
                        className="table align-middle table-row-bordered mb-0 fs-6 gy-5"
                    >
                        <thead>
                        <tr>
                            <th className="text-muted">Type</th>
                            <th className="text-muted">Id</th>
                            <th className="text-muted">Name</th>
                            {dynamicKeys.map((key) => (
                                <th key={key} className="text-muted">{capitalize(key)}</th>
                            ))}
                        </tr>
                        </thead>
                        <tbody className="card-table-data">
                        {characteristics.map((char, index) => {
                            const value = typeof char.value === "object" ? char.value : {value: char.value};
                            return (
                                <tr key={`${char.id || "no-id"}-${index}`}>
                                    <td className="fw-bold">{char["@type"] ?? "_"}</td>
                                    <td className="fw-bold">{char.id ?? "_"}</td>
                                    <td className="fw-bold">{char.name ?? "_"}</td>
                                    {dynamicKeys.map((key) => (
                                        <td key={key} className="fw-bold">
                                            {formatCellValue(key, value[key])}
                                        </td>
                                    ))}
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                </div>
            );
        })}
    </div>
);

GroupedCharacteristics.propTypes = {
    groupedCharacteristics: PropTypes.object.isRequired,
};

export default GroupedCharacteristics;
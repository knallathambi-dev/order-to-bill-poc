// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useRef, useState} from "react";
import PropTypes from "prop-types";

/**
 * Determines whether a field is visible for the current screen type.
 * If no screenType is provided or the field has no type, the field is always visible.
 */
const isFieldVisible = (field, screenType) => {
    if (!screenType || !field.type) return true;
    if (field.type === "ALL") return true;
    if (Array.isArray(field.type)) return field.type.includes(screenType);
    return field.type === screenType;
};

/**
 * Returns the CSS class for the sort indicator arrow.
 */
const getSortClass = (field, sortedColumn) => {
    if (!field.isSortable) return "";
    if (sortedColumn === field.value) return "btn sort-btn dropdown-toggle up";
    if (sortedColumn === `-${field.value}`) return "btn sort-btn dropdown-toggle down";
    return "btn sort-btn dropdown-toggle";
};

export default function TableHead({
                                      fields,
                                      setFields,
                                      handleSorting,
                                      sortedColumn,
                                      screenType,
                                  }) {
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [draggedField, setDraggedField] = useState(null);
    const dropdownRef = useRef(null);

    // Close dropdown when clicking outside
    useEffect(() => {
        if (!dropdownOpen) return;

        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setDropdownOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [dropdownOpen]);

    const handleCheckboxChange = useCallback(
        (fieldName) => {
            setFields(
                fields.map((field) =>
                    field.name === fieldName ? {...field, checked: !field.checked} : field
                )
            );
        },
        [fields, setFields]
    );

    const handleDragStart = useCallback((event, field) => {
        setDraggedField(field);
        event.dataTransfer.setData("text/plain", field.name);
    }, []);

    const handleDrop = useCallback(
        (targetIndex) => {
            if (!draggedField) return;

            const updated = [...fields];
            const fromIndex = updated.findIndex((f) => f.name === draggedField.name);
            updated.splice(fromIndex, 1);
            updated.splice(targetIndex, 0, draggedField);

            setFields(updated);
            setDraggedField(null);
        },
        [draggedField, fields, setFields]
    );

    const visibleFields = fields.filter((field) => isFieldVisible(field, screenType));

    return (
        <thead>
        <tr>
            {visibleFields.map(
                (field) =>
                    field.checked && (
                        <th
                            key={field.name}
                            className="text-nowrap"
                            scope="col"
                            style={field.isSortable ? {cursor: "pointer"} : undefined}
                            onClick={() => field.isSortable && handleSorting(field.value)}
                        >
                            {field.name}
                            {field.isSortable && (
                                <span
                                    className={getSortClass(field, sortedColumn)}
                                    aria-hidden="true"
                                />
                            )}
                        </th>
                    )
            )}

            <th scope="col" className="manage-columns">
                <div className="btn-group" ref={dropdownRef} style={{position: "relative"}}>
                    <button
                        type="button"
                        className="btn btn-icon btn-no-outline btn-sm"
                        aria-expanded={dropdownOpen}
                        aria-label="Manage columns"
                        onClick={() => setDropdownOpen((prev) => !prev)}
                    >
                        <span>•••</span>
                    </button>

                    {dropdownOpen && (
                        <div
                            className="dropdown-menu dropdown-menu-end show"
                            style={{
                                display: "block",
                                position: "absolute",
                                top: "100%",
                                right: 0,
                                zIndex: 1050,
                            }}
                        >
                            <ul className="Table-group column-selection-wrapper mb-0">
                                {visibleFields.map((field, index) => (
                                    <li
                                        key={field.name}
                                        className={`list-group-item column-selection${field.checked ? " checked" : ""}`}
                                        draggable
                                        onDragStart={(e) => handleDragStart(e, field)}
                                        onDragOver={(e) => e.preventDefault()}
                                        onDrop={() => handleDrop(index)}
                                    >
                                        <input
                                            className="form-check-input me-2"
                                            type="checkbox"
                                            id={`col-toggle-${field.name}`}
                                            disabled={field.disabled}
                                            checked={field.checked}
                                            onChange={() => handleCheckboxChange(field.name)}
                                        />
                                        <label
                                            className="form-check-label"
                                            htmlFor={`col-toggle-${field.name}`}
                                            style={{cursor: "pointer"}}
                                        >
                                            {field.name}
                                        </label>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </th>
        </tr>
        </thead>
    );
}

TableHead.propTypes = {
    fields: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string.isRequired,
            checked: PropTypes.bool.isRequired,
            value: PropTypes.string,
            isSortable: PropTypes.bool,
            disabled: PropTypes.bool,
            type: PropTypes.oneOfType([PropTypes.string, PropTypes.arrayOf(PropTypes.string)]),
        })
    ).isRequired,
    setFields: PropTypes.func.isRequired,
    handleSorting: PropTypes.func.isRequired,
    sortedColumn: PropTypes.string.isRequired,
    screenType: PropTypes.string,
};
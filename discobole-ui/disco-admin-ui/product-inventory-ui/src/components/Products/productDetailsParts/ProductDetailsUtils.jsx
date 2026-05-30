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
import AccordionSection from "./AccordionSection";
import ProductPriceDetails from "./ProductPriceDetails";

export const TableRow = ({label, value, className = "fw-bold"}) => (
    <tr>
        <td className="text-muted td-width">
            <div className="d-flex align-items-center text-nowrap">{label}</div>
        </td>
        <td className={className}>{value}</td>
    </tr>
);

TableRow.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.node.isRequired,
    className: PropTypes.string,
};

export const TableStructure = ({children}) => (
    <div className="table-responsive">
        <table className="table align-middle mb-0 fs-6 gy-5 data-table">
            <tbody className="fw-semibold">{children}</tbody>
        </table>
    </div>
);

TableStructure.propTypes = {
    children: PropTypes.node.isRequired,
};

export const CheckboxField = ({isChecked}) => (
    <input className="form-check-input" type="checkbox" checked={isChecked} disabled readOnly/>
);

CheckboxField.propTypes = {
    isChecked: PropTypes.bool,
};

export const StatusBadge = ({status}) => {
    if (!status) return null;
    return (
        <span className={`tag tag-sm status-value ${status.toLowerCase()}`}>
            {status}
        </span>
    );
};

StatusBadge.propTypes = {
    status: PropTypes.string,
};

export const ProductPriceSection = ({sections, sectionIndex, toggleAccordion, productPrice}) => (
    <AccordionSection
        title="Product Price"
        isExpanded={sections[sectionIndex]?.isExpanded ?? false}
        onToggle={() => toggleAccordion(sectionIndex)}
    >
        {productPrice ? (
            <ProductPriceDetails productPrice={productPrice}/>
        ) : (
            <p className="text-muted text-center">Product Price information is unavailable</p>
        )}
    </AccordionSection>
);

ProductPriceSection.propTypes = {
    sections: PropTypes.array.isRequired,
    sectionIndex: PropTypes.number.isRequired,
    toggleAccordion: PropTypes.func.isRequired,
    productPrice: PropTypes.array,
};
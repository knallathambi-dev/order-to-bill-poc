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
import KeyValueTable from "./KeyValueTable";

const ProductSpecification = ({sections, onClick, productSpecification}) => (
    <AccordionSection title="Product Specification" isExpanded={sections[1].isExpanded} onToggle={onClick}>
        <KeyValueTable rows={[
            {label: "Id", value: productSpecification.id},
            {label: "Type", value: productSpecification["@type"]},
        ]}/>
    </AccordionSection>
);

ProductSpecification.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    productSpecification: PropTypes.shape({
        id: PropTypes.string,
        "@type": PropTypes.string,
    }).isRequired,
};

export default ProductSpecification;
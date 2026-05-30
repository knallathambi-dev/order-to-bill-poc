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

const ProductOfferingDetails = ({sections, onClick, productOffering}) => (
    <AccordionSection title="Product Offering" isExpanded={sections[0].isExpanded} onToggle={onClick}>
        <KeyValueTable rows={[
            {label: "Id", value: productOffering.id},
            {label: "Name", value: productOffering.name},
            {label: "Type", value: productOffering["@type"]},
            {label: "Version", value: productOffering.version},
        ]}/>
    </AccordionSection>
);

ProductOfferingDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    productOffering: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        "@type": PropTypes.string,
        version: PropTypes.string,
    }).isRequired,
};

export default ProductOfferingDetails;
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
import {Link} from "react-router-dom";
import AccordionSection from "./AccordionSection";
import DataTable from "./DataTable";

const RelatedProductItemsDetails = ({sections, onClick, productRelationship}) => (
    <AccordionSection title="Related Product Items" isExpanded={sections[10].isExpanded} onToggle={onClick}>
        <DataTable
            columns={["Product Id", "Relationship Type"]}
            data={productRelationship}
            rowKey={(rel) => rel.product?.id}
            renderRow={(rel) => [
                <Link to={`/product-inventory/products-details-page/${rel.product?.id}`}>
                    {rel.product?.id || "_"}
                </Link>,
                rel.relationshipType,
            ]}
        />
    </AccordionSection>
);

RelatedProductItemsDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    productRelationship: PropTypes.array,
};

export default RelatedProductItemsDetails;
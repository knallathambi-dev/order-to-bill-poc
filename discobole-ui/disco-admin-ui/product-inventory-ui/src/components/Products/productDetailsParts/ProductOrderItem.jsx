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
import DataTable from "./DataTable";

const ProductOrderItem = ({sections, onClick, productOrderItem}) => (
    <AccordionSection title="Product Order Item" isExpanded={sections[5].isExpanded} onToggle={onClick}>
        <DataTable
            columns={["Product Order Id", "Order Item Id", "Order Item Action", "Role"]}
            data={productOrderItem}
            rowKey={(item) => item.orderItemId}
            renderRow={(item) => [
                item.productOrderId,
                item.orderItemId,
                item.orderItemAction,
                item.role,
            ]}
        />
    </AccordionSection>
);

ProductOrderItem.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    productOrderItem: PropTypes.array,
};

export default ProductOrderItem;
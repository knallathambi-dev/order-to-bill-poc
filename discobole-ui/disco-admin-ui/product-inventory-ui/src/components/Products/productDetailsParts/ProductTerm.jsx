// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from "react";
import PropTypes from "prop-types";
import {formatToLocalDateTime} from "@discobole/common-ui";
import AccordionSection from "./AccordionSection";
import DataTable from "./DataTable";

const MAX_DESC_LENGTH = 50;

const formatDuration = (duration) => {
    if (!duration?.amount || !duration?.units) return "_";
    const amount = duration.amount % 1 === 0 ? Math.floor(duration.amount) : duration.amount;
    return `${amount} ${duration.units.toLowerCase()}${amount !== 1 ? "s" : ""}`;
};

const ProductTerm = ({sections, onClick, productTerm}) => {
    const [expandedDescriptions, setExpandedDescriptions] = useState({});

    const toggleDescription = useCallback((index) => {
        setExpandedDescriptions((prev) => ({...prev, [index]: !prev[index]}));
    }, []);

    const renderDescription = (description, index) => {
        if (!description) return "_";
        if (description.length <= MAX_DESC_LENGTH) return description;

        const isExpanded = expandedDescriptions[index];
        return (
            <div>
                <span>{isExpanded ? description : `${description.substring(0, MAX_DESC_LENGTH)}...`}</span>
                <button
                    type="button"
                    className="btn btn-link p-0 ms-1 fs-7 text-decoration-underline term-see-more-btn"
                    onClick={() => toggleDescription(index)}
                >
                    {isExpanded ? "See less" : "See more"}
                </button>
            </div>
        );
    };

    return (
        <AccordionSection title="Commitment Term" isExpanded={sections[12].isExpanded} onToggle={onClick}>
            <DataTable
                columns={["#", "Name", "Duration", "Start Date", "End Date", "Description"]}
                data={productTerm}
                rowKey={(_, index) => index}
                renderRow={(item, index) => [
                    index + 1,
                    item?.name,
                    formatDuration(item?.duration),
                    formatToLocalDateTime(item?.validFor?.startDateTime) || "_",
                    formatToLocalDateTime(item?.validFor?.endDateTime) || "_",
                    <div className="term-description-cell">
                        {renderDescription(item?.description, index)}
                    </div>,
                ]}
            />
        </AccordionSection>
    );
};

ProductTerm.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    productTerm: PropTypes.array,
};

export default ProductTerm;
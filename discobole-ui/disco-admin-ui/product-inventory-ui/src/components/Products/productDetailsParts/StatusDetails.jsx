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
import AccordionSection from "./AccordionSection";
import DataTable from "./DataTable";

const StatusDetails = ({title, section, toggleAccordion, data}) => (
    <AccordionSection title={title} isExpanded={section.isExpanded} onToggle={() => toggleAccordion(section.index)}>
        <DataTable
            columns={["Change Date", "Status"]}
            data={data}
            rowKey={(item, index) => `${item.id || "no-id"}-${index}`}
            renderRow={(statusChange) => [
                formatToLocalDateTime(statusChange.changeDate) || "_",
                statusChange.status ? (
                    <span className={`tag tag-sm status-value ${statusChange.status.toLowerCase()}`}>
                        {statusChange.status}
                    </span>
                ) : "_",
            ]}
        />
    </AccordionSection>
);

StatusDetails.propTypes = {
    title: PropTypes.string.isRequired,
    section: PropTypes.shape({
        isExpanded: PropTypes.bool,
        index: PropTypes.number,
    }).isRequired,
    toggleAccordion: PropTypes.func.isRequired,
    data: PropTypes.array,
};

export default StatusDetails;
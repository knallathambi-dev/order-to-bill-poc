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

const RealizingResourceDetails = ({sections, onClick, realizingResource}) => (
    <AccordionSection title="Realizing Resource" isExpanded={sections[7].isExpanded} onToggle={onClick}>
        <DataTable
            columns={["Realizing Resource Id", "Type"]}
            data={realizingResource}
            rowKey={(resource) => resource.id}
            renderRow={(resource) => [
                resource.id,
                resource["@type"],
            ]}
        />
    </AccordionSection>
);

RealizingResourceDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    realizingResource: PropTypes.array,
};

export default RealizingResourceDetails;
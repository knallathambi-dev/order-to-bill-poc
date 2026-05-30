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

const ExternalIdentifier = ({sections, onClick, externalIdentifier}) => (
    <AccordionSection title="External Identifier" isExpanded={sections[11].isExpanded} onToggle={onClick}>
        <DataTable
            columns={["Product External Identifier Id", "Owner", "Type"]}
            data={externalIdentifier}
            rowKey={(item) => item.id}
            renderRow={(item) => [
                item.id,
                item.owner,
                item.externalIdentifierType,
            ]}
        />
    </AccordionSection>
);

ExternalIdentifier.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    externalIdentifier: PropTypes.array,
};

export default ExternalIdentifier;
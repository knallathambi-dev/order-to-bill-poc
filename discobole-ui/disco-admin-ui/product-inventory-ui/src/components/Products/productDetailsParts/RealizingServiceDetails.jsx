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

const RealizingServiceDetails = ({sections, onClick, realizingService}) => (
    <AccordionSection title="Realizing Service" isExpanded={sections[13].isExpanded} onToggle={onClick}>
        <DataTable
            columns={["Realizing Service Id", "Type"]}
            data={realizingService}
            rowKey={(service) => service.id}
            renderRow={(service) => [
                service.id,
                service["@type"],
            ]}
        />
    </AccordionSection>
);

RealizingServiceDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    realizingService: PropTypes.array,
};

export default RealizingServiceDetails;
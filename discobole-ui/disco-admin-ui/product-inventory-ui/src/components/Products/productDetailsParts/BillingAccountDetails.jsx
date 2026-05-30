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

const BillingAccountDetails = ({sections, onClick, billingAccount}) => (
    <AccordionSection title="Billing Account" isExpanded={sections[4].isExpanded} onToggle={onClick}>
        <KeyValueTable rows={[
            {label: "Id", value: billingAccount?.id},
            {label: "Name", value: billingAccount?.name},
            {label: "@Referred Type", value: billingAccount?.["@referredType"]},
        ]}/>
    </AccordionSection>
);

BillingAccountDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    billingAccount: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        "@referredType": PropTypes.string,
    }).isRequired,
};

export default BillingAccountDetails;
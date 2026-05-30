// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useMemo} from "react";
import PropTypes from "prop-types";
import AccordionSection from "./AccordionSection";
import DataTable from "./DataTable";

const PARTY_TYPES = ["PartyRef", "PartyRoleRef"];

const RelatedPartyDetails = ({sections, onClick, relatedParty}) => {
    const filteredParties = useMemo(
        () => relatedParty?.filter((p) => PARTY_TYPES.includes(p.partyOrPartyRole?.["@type"])) || [],
        [relatedParty]
    );

    return (
        <AccordionSection title="Related Party" isExpanded={sections[3].isExpanded} onToggle={onClick}>
            <DataTable
                columns={["Id", "Name", "Role", "Referred Type", "Type", "Party Name", "Party Id"]}
                data={filteredParties}
                rowKey={(_, i) => `party-${i}`}
                renderRow={(party) => [
                    party.partyOrPartyRole?.id,
                    party.partyOrPartyRole?.name,
                    party.role,
                    party.partyOrPartyRole?.["@referredType"],
                    party.partyOrPartyRole?.["@type"],
                    party.partyOrPartyRole?.partyName,
                    party.partyOrPartyRole?.partyId,
                ]}
            />
        </AccordionSection>
    );
};

RelatedPartyDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    relatedParty: PropTypes.array,
};

export default RelatedPartyDetails;
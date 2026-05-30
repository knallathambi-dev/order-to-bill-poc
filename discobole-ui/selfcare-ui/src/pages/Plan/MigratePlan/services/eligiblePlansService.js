// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {v4 as uuidv4} from "uuid";
import apiClient from "../../../../services/api/apiClient";

const POQ_API_URL = process.env.REACT_APP_POQ_URL;
const CATALOG_API_URL = process.env.REACT_APP_PRODUCT_CATALOG_URL;

const isValidRelatedParty = (party) =>
    Boolean(party?.id && party?.name && party?.role);

const isValidPlanId = (planId) =>
    Boolean(planId && String(planId).trim());

export const formatRelatedPartyForPoq = (party) => {
    if (!isValidRelatedParty(party)) {
        return [];
    }

    return [{
        id: party.id,
        name: party.name,
        role: party.role,
        "@type": "RelatedParty",
        "@referredType": "individual",
    }];
};

export const buildMigrationQualificationRequest = (planId, relatedParty) => {
    const formattedParty = formatRelatedPartyForPoq(relatedParty);

    const requestBody = {
        description: `Migration qualification request - ${uuidv4()}`,
        provideAlternative: true,
        productOfferingQualificationItem: [
            {
                id: uuidv4(),
                action: "migrate",
                product: {
                    id: planId,
                    "@type": "ProductRef",
                },
            }
        ],
    };

    if (formattedParty.length > 0) {
        requestBody.relatedParty = formattedParty;
    }

    return requestBody;
};

export const extractAllEligibleOfferings = (response) => {
    const qualificationItems = response?.productOfferingQualificationItem;

    if (!Array.isArray(qualificationItems) || qualificationItems.length === 0) {
        return [];
    }

    return qualificationItems.flatMap((item) => {
        const alternateProposals = item?.alternateProductOfferingProposal;

        if (!Array.isArray(alternateProposals)) {
            return [];
        }

        return alternateProposals
            .map((proposal) => proposal?.alternateProductOffering?.id)
            .filter(Boolean);
    });
};

export const fetchOfferingsFromCatalog = async (offeringIds) => {
    const validIds = (offeringIds || []).filter(Boolean);
    if (validIds.length === 0) {
        return [];
    }

    const idQueryParam = validIds.join(",");

    const {data} = await apiClient.get(CATALOG_API_URL, {
        params: {id: idQueryParam},
    });

    if (Array.isArray(data)) return data;
    if (data?.id) return [data];
    return [];
};

export const fetchMigrationEligibility = async (planId, relatedParty) => {
    if (!POQ_API_URL) {
        throw new Error("POQ API URL is not configured (REACT_APP_POQ_URL)");
    }

    if (!isValidPlanId(planId)) {
        throw new Error("A valid plan ID is required for eligibility check");
    }

    if (!isValidRelatedParty(relatedParty)) {
        throw new Error("Related party with id, name, and role is required");
    }

    const requestBody = buildMigrationQualificationRequest(planId, relatedParty);
    const {data} = await apiClient.post(POQ_API_URL, requestBody);

    return data;
};
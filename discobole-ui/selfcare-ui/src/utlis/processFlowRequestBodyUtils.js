// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createChannel} from "./utils";

export const createRelatedParty = (relatedParty) => {
    if (!relatedParty || !relatedParty.id || !relatedParty.name || !relatedParty.role) return null;

    return [
        {
            id: relatedParty.id,
            name: relatedParty.name,
            role: relatedParty.role,
            "@referredType": "individual"
        }
    ];
};

export const createPostProcessFlowRequest = (id, isOffer, relatedParty) => {
    const requestBody = {
        processFlowSpecification: "OrderCapture",
        channel: [createChannel()],
        relatedEntity: [
            {
                id: id,
                "@referredType": isOffer ? "productOffering" : "product"
            }
        ]
    };

    const relatedPartyData = createRelatedParty(relatedParty);
    if (relatedPartyData) {
        requestBody.relatedParty = relatedPartyData;
    }

    return requestBody;
};

export const createConfirmConfigurationRequest = (id, relatedParty) => {
    const requestBody = {
        channel: [createChannel()],
        characteristic: [
            {
                name: "ConfirmConfigurationIsProcessed",
                valueType: "Object",
                value: {
                    "configuration.id": id,
                    "configuration.state": "confValidated"
                },
                "@type": "ObjectCharacteristic"
            }
        ]
    };

    const relatedPartyData = createRelatedParty(relatedParty);
    if (relatedPartyData) {
        requestBody.relatedParty = relatedPartyData;
    }

    return requestBody;
};

export const createPartyIdentifierRequest = (relatedParty) => {
    return {
        channel: [createChannel()],
        characteristic: [
            {
                name: "PartyIdentifier",
                valueType: "Object",
                value: {
                    "partyId": relatedParty.id,
                    "partyName": relatedParty.name,
                    "referredType": "individual"
                },
                "@type": "ObjectCharacteristic"
            }
        ]
    };
};

export const createValidateOrderRequest = (productOrderId, relatedParty) => {
    const requestBody = {
        channel: [createChannel()],
        characteristic: [
            {
                name: "ValidateOrderByCustomer",
                valueType: "Object",
                value: {
                    productOrderId: productOrderId,
                    orderValidationStatus: "orderValidatedByCustomer"
                },
                "@type": "ObjectCharacteristic"
            }
        ]
    };

    const relatedPartyData = createRelatedParty(relatedParty);
    if (relatedPartyData) {
        requestBody.relatedParty = relatedPartyData;
    }

    return requestBody;
}

export const createCompleteOrderRequest = (characteristic, relatedParty) => {
    const requestBody = {
        channel: [createChannel()],
        characteristic: characteristic
    };

    const relatedPartyData = createRelatedParty(relatedParty);
    if (relatedPartyData) {
        requestBody.relatedParty = relatedPartyData;
    }

    return requestBody;
}
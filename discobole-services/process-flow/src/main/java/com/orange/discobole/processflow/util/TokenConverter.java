// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class TokenConverter {
    public TokenConverter() {
    }

    public static List<String> convert(String token) {
        List<String> clientRole = new ArrayList<>();
        // Parse the token without verifying the signature
        Claims claims = getClaimsFromToken(token);
        Object rolesObject = claims.get("resource_access");
        //Object email = claims.get("email");
        Map<String, Object> resourceAccess = (Map<String, Object>) claims.get("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Object clientRoles = entry.getValue();

                if (clientRoles instanceof Map) {
                    // Assuming roles is a List<String> within the clientRoles Map
                    List<String> roles = ((Map<?, ?>) clientRoles).containsKey("roles")
                            ? (List<String>) ((Map<?, ?>) clientRoles).get("roles")
                            : null;
                    clientRole.addAll(roles);
                }
            }
        }
        return clientRole;
    }

    private static Claims  getClaimsFromToken(String jwtToken) {
        // JWT format: header.payload.signature
        String[] parts = jwtToken.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        String payload = parts[1];
        byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);
        return Jwts.parserBuilder().build().parseClaimsJwt("."+payload+".").getBody();
    }


    public static String getRelatedPartyIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if(claims.containsKey("relatedPartyId")){
            return claims.get("relatedPartyId").toString();
        }
        return null;
    }
}

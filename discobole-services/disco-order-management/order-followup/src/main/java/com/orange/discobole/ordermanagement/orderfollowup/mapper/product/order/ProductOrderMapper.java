// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.mapper.product.order;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRoleRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelatedPartyRefOrPartyRoleRef;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Slf4j
public class ProductOrderMapper {

    /**
     * Converts a URI to its string representation.
     */
    private String convertUriToString(URI uri) {
        String result = uri != null ? uri.toString() : null;
        log.debug("Converted URI to string: [{}]", result);
        return result;
    }

    /**
     * Maps a PartyRef to a RelatedParty using setters.
     */
    public RelatedParty toRelatedPartyFromPartyRef(PartyRef partyRef) {
        log.debug("Mapping PartyRef to RelatedParty");
        RelatedParty relatedParty = new RelatedParty();
        if (partyRef != null) {
            relatedParty.setReferredType(partyRef.getAtReferredType());
            relatedParty.setBaseType(partyRef.getAtBaseType());
            relatedParty.setSchemaLocation(convertUriToString(partyRef.getAtSchemaLocation()));
            relatedParty.setId(partyRef.getId());
            relatedParty.setName(partyRef.getName());
            log.debug("Mapped PartyRef - id: [{}], name: [{}], referredType: [{}]",
                    partyRef.getId(), partyRef.getName(), partyRef.getAtReferredType());
        } else {
            log.warn("PartyRef is null, returning empty RelatedParty");
        }
        return relatedParty;
    }

    /**
     * Maps a PartyRoleRef to a RelatedParty using setters.
     */
    public RelatedParty toRelatedPartyFromPartyRoleRef(PartyRoleRef partyRoleRef) {
        log.debug("Mapping PartyRoleRef to RelatedParty");
        RelatedParty relatedParty = new RelatedParty();
        if (partyRoleRef != null) {
            relatedParty.setReferredType(partyRoleRef.getAtReferredType());
            relatedParty.setBaseType(partyRoleRef.getAtBaseType());
            relatedParty.setSchemaLocation(convertUriToString(partyRoleRef.getAtSchemaLocation()));
            relatedParty.setId(partyRoleRef.getId());
            relatedParty.setName(partyRoleRef.getName());
            log.debug("Mapped PartyRoleRef - id: [{}], name: [{}], referredType: [{}]",
                    partyRoleRef.getId(), partyRoleRef.getName(), partyRoleRef.getAtReferredType());
        } else {
            log.warn("PartyRoleRef is null, returning empty RelatedParty");
        }
        return relatedParty;
    }

    /**
     * Maps a RelatedPartyRefOrPartyRoleRef to a RelatedParty.
     */
    public RelatedParty toRelatedParty(RelatedPartyRefOrPartyRoleRef relatedPartyRefOrPartyRoleRef) {
        if (relatedPartyRefOrPartyRoleRef == null) {
            log.warn("RelatedPartyRefOrPartyRoleRef is null, returning empty RelatedParty");
            return new RelatedParty();
        }

        Object partyOrPartyRole = relatedPartyRefOrPartyRoleRef.getPartyOrPartyRole();
        log.debug("Resolving party type: [{}]",
                partyOrPartyRole != null ? partyOrPartyRole.getClass().getSimpleName() : "null");

        if (partyOrPartyRole instanceof PartyRef partyRef) {
            return toRelatedPartyFromPartyRef(partyRef);
        } else if (partyOrPartyRole instanceof PartyRoleRef partyRoleRef) {
            return toRelatedPartyFromPartyRoleRef(partyRoleRef);
        }

        log.warn("Unknown party type encountered: [{}], returning empty RelatedParty",
                partyOrPartyRole != null ? partyOrPartyRole.getClass().getName() : "null");
        return new RelatedParty();
    }

    /**
     * Maps a list of RelatedPartyRefOrPartyRoleRef objects to a list of RelatedParty objects.
     */
    public List<RelatedParty> toRelatedPartyList(List<RelatedPartyRefOrPartyRoleRef> relatedPartyRefOrPartyRoleRefs) {
        if (relatedPartyRefOrPartyRoleRefs == null || relatedPartyRefOrPartyRoleRefs.isEmpty()) {
            log.warn("Related party references list is null or empty, returning empty list");
            return List.of();
        }
        log.debug("Mapping [{}] related party references to RelatedParty list", relatedPartyRefOrPartyRoleRefs.size());
        List<RelatedParty> result = relatedPartyRefOrPartyRoleRefs.stream()
                .map(this::toRelatedParty)
                .toList();
        log.debug("Successfully mapped [{}] related party references", result.size());
        return result;
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.v1.PartyRef;
import com.orange.discobole.productinventory.dto.v1.PartyRoleRef;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.RelatedPartyOrPartyRole;
import com.orange.discobole.productinventory.repository.ProductRepository;
import com.orange.discobole.productinventory.service.SecurityService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;

import static com.orange.discobole.productinventory.exception.model.BusinessErrors.CANNOT_ACCESS_THIS_RESOURCE;
import static com.orange.discobole.productinventory.util.ApiUtil.addListToMap;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class AuthorizationService {

    private final SecurityService securityService;
    private final ProductRepository productRepository;

    /**
     * Validates query parameters when fetching a list of products.
     * For non-admin users, replaces the related party filter with the one from the security token.
     */
    public void validateFetchRequest(final MultiValueMap<String, Object> multiValueMap, final String relatedPartyParamName) {
        if (securityService.isAdmin()) {
            return;
        }

        final String relatedPartyId = securityService.getRelatedPartyId()
                .orElseThrow(() -> new AccessDeniedException(CANNOT_ACCESS_THIS_RESOURCE));
        List<Object> requestedIds = multiValueMap.get(relatedPartyParamName);
        if (requestedIds != null && requestedIds.stream().anyMatch(o -> !o.equals(relatedPartyId))) {
            throw new AccessDeniedException(CANNOT_ACCESS_THIS_RESOURCE);
        }
        // Override related party parameter
        addListToMap(relatedPartyParamName, multiValueMap, List.of(relatedPartyId));
    }

    /**
     * Validates that the given product belongs to the related party of the current user.
     */
    public void validateByIdRequest(final String productId) {
        if (securityService.isAdmin()) {
            return;
        }

        final String relatedPartyId = securityService.getRelatedPartyId()
                .orElseThrow(() -> new AccessDeniedException(CANNOT_ACCESS_THIS_RESOURCE));

        final boolean hasAccess = productRepository.existsByIdAndRelatedPartyId(productId, relatedPartyId);
        if (!hasAccess) {
            throw new AccessDeniedException(CANNOT_ACCESS_THIS_RESOURCE);
        }
    }

    /**
     * Validates product creation to ensure users can only create products for themselves.
     * This prevents IDOR and privilege escalation where a user could create resources for other users.
     *
     * @param product The product to be created
     * @throws AccessDeniedException if user tries to create a product for another user
     */
    public void validateProductCreation(final Product product) {
        if (securityService.isAdmin()) {
            return;
        }

        final String currentUserRelatedPartyId = securityService.getRelatedPartyId()
                .orElseThrow(() -> new AccessDeniedException(CANNOT_ACCESS_THIS_RESOURCE));

        List<RelatedPartyOrPartyRole> relatedParties = product.getRelatedParty();

        // If no relatedParty is provided, that's acceptable - the service layer may assign one
        if (relatedParties == null || relatedParties.isEmpty()) {
            return;
        }

        // Check if user is trying to create a product for another user
        boolean createsForOtherUser = relatedParties.stream()
                .map(this::extractIdFromRelatedParty)
                .filter(Objects::nonNull)
                .anyMatch(id -> !Objects.equals(id, currentUserRelatedPartyId));

        if (createsForOtherUser) {
            log.warn("User {} attempted to create a product for another user", currentUserRelatedPartyId);
            throw new AccessDeniedException(CANNOT_ACCESS_THIS_RESOURCE);
        }
    }

    /**
     * Extracts the ID from a RelatedPartyOrPartyRole object.
     * Handles both PartyRef and PartyRoleRef types.
     */
    private String extractIdFromRelatedParty(RelatedPartyOrPartyRole relatedParty) {
        if (relatedParty == null || relatedParty.getPartyOrPartyRole() == null) {
            return null;
        }

        Object partyOrRole = relatedParty.getPartyOrPartyRole();
        if (partyOrRole instanceof PartyRef partyRef) {
            return partyRef.getId();
        }
        if (partyOrRole instanceof PartyRoleRef partyRoleRef) {
            return partyRoleRef.getId();
        }

        return null;
    }

    /**
     * Validates product update to ensure users can only update their own products.
     * This prevents IDOR and privilege escalation during product updates.
     *
     * @param productId The ID of the product to be updated
     * @throws AccessDeniedException if user tries to update a product belonging to another user
     */
    public void validateProductUpdate(final String productId) {
        // Same validation as read - user must own the product to update it
        validateByIdRequest(productId);
    }
}

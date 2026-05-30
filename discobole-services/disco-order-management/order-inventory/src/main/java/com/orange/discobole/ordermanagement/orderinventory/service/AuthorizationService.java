// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service;

import com.orange.discobole.ordermanagement.orderinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.repository.ProductOrderRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorMessages.CANNOT_ACCESS_THIS_RESOURCE;

@Service
@Slf4j
public class AuthorizationService {

    private final SecurityConfigProperties securityConfigProperties;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderInventoryException forbiddenException = new ProductOrderInventoryException(HttpStatus.FORBIDDEN,
            ErrorCodeEnum.INVALID_URL_PARAMETER_VALUE.getCode(),
            ErrorCodeEnum.INVALID_URL_PARAMETER_VALUE.getStatus(),
            CANNOT_ACCESS_THIS_RESOURCE);

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "ProductOrderRepository and SecurityConfigProperties are effectively immutable Spring beans managed by the container"
    )
    public AuthorizationService(SecurityConfigProperties securityConfigProperties, ProductOrderRepository productOrderRepository) {
        this.securityConfigProperties = securityConfigProperties;
        this.productOrderRepository = productOrderRepository;
    }

    /**
     * Validates query parameters when fetching a list of products.
     * For non-admin users, replace the related party filter with the one from the security token.
     */
    public void validateFetchRequest(final MultiValueMap<String, Object> parameters, final String relatedPartyParamName) {
        if (isAdmin()) {
            return;
        }

        final String relatedPartyId = getRelatedPartyId()
                .orElseThrow(() -> forbiddenException);
        List<Object> requestedIds = parameters.get(relatedPartyParamName);
        if (requestedIds != null && requestedIds.stream().anyMatch(o -> !o.equals(relatedPartyId))) {
            throw forbiddenException;
        }
        parameters.remove(relatedPartyParamName);
        // Override related party parameter
        parameters.add(relatedPartyParamName, relatedPartyId);

    }

    /**
     * Validates that the given product belongs to the related party of the current user.
     */
    public void validateByIdRequest(final String productOrderId) {
        if (isAdmin()) {
            return;
        }

        final String relatedPartyId = getRelatedPartyId()
                .orElseThrow(() -> forbiddenException);

        final boolean hasAccess = productOrderRepository.existsByIdAndRelatedPartyId(productOrderId, relatedPartyId);
        if (!hasAccess) {
            throw forbiddenException;
        }
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            // No authentication (cron/system call) → treat as non-admin
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> this.securityConfigProperties.getDiscoAdminRole().equals(a.getAuthority()));
    }

    public Optional<String> getRelatedPartyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            // No authentication (cron/system call)
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return Optional.ofNullable(jwt.getClaimAsString("relatedPartyId"));
        }

        return Optional.empty();
    }
}
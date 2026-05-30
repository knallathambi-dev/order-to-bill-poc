// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.productinventory.service.SecurityService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

    private final Map<String, String> defaultRoles;
    private final SecurityConfigProperties securityConfigProperties;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public SecurityServiceImpl(SecurityConfigProperties securityConfigProperties) {
        defaultRoles = securityConfigProperties.getDefaultRoles();
        this.securityConfigProperties = securityConfigProperties;
    }

    @Override
    public boolean hasRoleEntitlement(String role) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String roleEntitlement = defaultRoles.getOrDefault(role, null);
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleEntitlement));
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            // No authentication (cron/system call) → treat as non-admin
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> this.securityConfigProperties.getDiscoAdminRole().equals(a.getAuthority()));
    }

    @Override
    public Optional<String> getRelatedPartyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            // No authentication (cron/system call)
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            // Use Optional.ofNullable in case claim is missing
            return Optional.ofNullable(jwt.getClaimAsString("relatedPartyId"));
        }

        return Optional.empty();
    }

}

// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import com.orange.discobole.ordermanagement.commons.dto.user.role.Entitlement;
import com.orange.discobole.ordermanagement.commons.dto.user.role.UserRole;
import com.orange.discobole.ordermanagement.orderinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.ordermanagement.orderinventory.service.impl.UserRolesRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String RESOURCE_ACCESS = "resource_access";

    public static final String ROLES = "roles";
    private final UserRolesRequestService userRolesRequestService;
    private final SecurityConfigProperties securityConfigProperties;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public KeycloakGrantedAuthoritiesConverter(UserRolesRequestService userRolesRequestService, SecurityConfigProperties securityConfigProperties) {
        this.userRolesRequestService = userRolesRequestService;
        this.securityConfigProperties = securityConfigProperties;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Set<String> entitlements = getEntitlements(source.getTokenValue());
        if (hasDiscoAdminRole(source)) {
            entitlements.add(securityConfigProperties.getDiscoAdminRole());
        }
        return entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private boolean hasDiscoAdminRole(Jwt source) {
        if (source.hasClaim(RESOURCE_ACCESS)) {
            Map<String, Map<String, List<String>>> resourceAccess = source.getClaim(RESOURCE_ACCESS);
            Map<String, List<String>> resourceAccessClientRoles = resourceAccess.get(securityConfigProperties.getResourceAccessRolesClient());
            if (Objects.nonNull(resourceAccessClientRoles) && resourceAccessClientRoles.containsKey(ROLES)) {
                return resourceAccessClientRoles.get(ROLES).stream().anyMatch(s -> s.equals(securityConfigProperties.getDiscoAdminRole()));
            }
        }
        return false;
    }
    private Set<String> getEntitlements(String token) {
        List<UserRole> userRoles = userRolesRequestService.fetchUserRoles(token);
        return getRolesEntitlementIds(userRoles);
    }

    private Set<String> getRolesEntitlementIds(List<UserRole> userRoles) {
        Set<String> entitlementIds = new HashSet<>();
        for (UserRole role : userRoles) {
            for (Entitlement entitlement : role.getEntitlement()) {
                entitlementIds.add(entitlement.getId());
            }
        }
        return entitlementIds;
    }
}
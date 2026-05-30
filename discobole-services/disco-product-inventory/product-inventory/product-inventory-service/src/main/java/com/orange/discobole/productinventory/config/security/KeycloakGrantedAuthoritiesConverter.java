// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config.security;

import com.orange.discobole.productinventory.dto.Entitlement;
import com.orange.discobole.productinventory.dto.UserRole;
import com.orange.discobole.productinventory.service.impl.UserRolesRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Slf4j
public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    public static final String RESOURCE_ACCESS = "resource_access";

    public static final String ROLES = "roles";

    private final UserRolesRequestService userRolesRequestService;

    private final SecurityConfigProperties securityConfigProperties;

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
            Map<String, List<String>> gatewayRoles = resourceAccess.get(securityConfigProperties.getRolesAuthClientId());
            if (Objects.nonNull(gatewayRoles) && gatewayRoles.containsKey(ROLES)) {
                return gatewayRoles.get(ROLES).stream().anyMatch(s -> s.equals(securityConfigProperties.getDiscoAdminRole()));
            }
        }
        return false;
    }

    private Set<String> getEntitlements(String token) {
        Set<String> entitlements = new HashSet<>();
        if (securityConfigProperties.isEnableTmf627Validation()) {
            List<UserRole> userRoles = userRolesRequestService.fetchUserRoles(token);
            entitlements.addAll(getRolesEntitlementIds(userRoles));
        }
        return entitlements;
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

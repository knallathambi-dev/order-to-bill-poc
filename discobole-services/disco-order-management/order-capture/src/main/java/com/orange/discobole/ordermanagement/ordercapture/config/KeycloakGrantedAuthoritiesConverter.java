// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;

import com.orange.discobole.ordermanagement.commons.dto.user.role.Entitlement;
import com.orange.discobole.ordermanagement.commons.dto.user.role.UserRole;
import com.orange.discobole.ordermanagement.ordercapture.service.impl.UserRolesRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserRolesRequestService userRolesRequestService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public KeycloakGrantedAuthoritiesConverter(UserRolesRequestService userRolesRequestService) {
        this.userRolesRequestService = userRolesRequestService;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Set<String> entitlements = getEntitlements(source.getTokenValue());
        return entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
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
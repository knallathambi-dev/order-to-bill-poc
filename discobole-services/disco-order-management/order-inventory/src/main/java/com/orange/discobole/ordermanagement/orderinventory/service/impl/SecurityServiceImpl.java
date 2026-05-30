// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.ordermanagement.orderinventory.service.SecurityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

    private final Map<String, String> defaultRoles;

    public SecurityServiceImpl(SecurityConfigProperties securityConfigProperties) {
        defaultRoles = securityConfigProperties.getDefaultRoles();
    }

    public boolean hasRoleEntitlement(String role) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String roleEntitlement = defaultRoles.getOrDefault(role, null);
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleEntitlement));
    }
}

// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config.security;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
@Validated
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class SecurityConfigProperties {

    private String userRoleRetrievalUrl;
    private String resourceAccessRolesClient;
    private String discoAdminRole;
    private Map<String, String> defaultRoles;

    public void setDefaultRoles(Map<String, String> roles) {
        this.defaultRoles = Collections.unmodifiableMap(roles);
    }

    public Map<String, String> getDefaultRoles() {
        return Collections.unmodifiableMap(defaultRoles);
    }
}
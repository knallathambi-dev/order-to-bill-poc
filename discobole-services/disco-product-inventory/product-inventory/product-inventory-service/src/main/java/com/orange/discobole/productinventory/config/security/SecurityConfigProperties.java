// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "config.security")
@Getter
@Setter
@Validated
public class SecurityConfigProperties {

    private String userRoleRetrievalUrl;

    private String rolesAuthClientId;
    private String discoAdminRole;

    private Map<String, String> defaultRoles;

    private boolean enableTmf627Validation;

    public void setDefaultRoles(Map<String, String> roles) {
        this.defaultRoles = Collections.unmodifiableMap(roles);
    }

    public Map<String, String> getDefaultRoles() {
        return Collections.unmodifiableMap(defaultRoles);
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.annotation.IntegrationTest;
import com.orange.discobole.productinventory.config.security.SecurityConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@IntegrationTest
@Slf4j
public abstract class JwtAuthHelper extends AbstractTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private SecurityConfigProperties securityConfigProperties;
    @AfterEach
    @Override
    void cleanDatabase() {
        mongoTemplate.remove(new Query(), "products");
    }

    /**
     * Creates a JWT token with proper Keycloak structure for testing authorization.
     * This triggers the KeycloakGrantedAuthoritiesConverter properly.
     *
     * @param authority The authority/entitlement (e.g., "x501" for read permission)
     * @param relatedPartyId The related party ID to add to the token (can be null)
     * @return RequestPostProcessor for MockMvc
     */
    protected RequestPostProcessor jwtWithAuthority(String authority, String relatedPartyId) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    jwt.claim("resource_access", createResourceAccessClaim(authority));
                    if (relatedPartyId != null) {
                        jwt.claim("relatedPartyId", relatedPartyId);
                    }
                    // Add standard JWT claims
                    jwt.claim("preferred_username", "test.user@orange.com");
                    jwt.claim("email", "test.user@orange.com");
                    jwt.claim("name", "Test User");
                });
    }

    /**
     * Creates a JWT token for an admin user with disco-admin role.
     * Admin users can access all resources without relatedPartyId filtering.
     *
     * @return RequestPostProcessor for MockMvc
     */
    protected RequestPostProcessor jwtAsAdmin() {
        List<String> entitlements = List.of("x501", securityConfigProperties.getDiscoAdminRole());
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    Map<String, List<String>> gatewayRoles = new HashMap<>();
                    gatewayRoles.put("roles", entitlements);

                    Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();
                    resourceAccess.put("gateway", gatewayRoles);

                    jwt.claim("resource_access", resourceAccess);
                    jwt.claim("preferred_username", "admin.user@orange.com");
                    jwt.claim("email", "admin.user@orange.com");
                    jwt.claim("name", "Admin User");
                })
                .authorities(jwt -> entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    /**
     * Creates a JWT token for a regular (non-admin) user with a specific relatedPartyId.
     * This user will only be able to access resources belonging to their relatedPartyId.
     *
     * @param relatedPartyId The related party ID for the user
     * @return RequestPostProcessor for MockMvc
     */
    protected RequestPostProcessor jwtAsUser(String relatedPartyId) {
        List<String> entitlements = List.of("x501");

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    Map<String, List<String>> gatewayRoles = new HashMap<>();
                    gatewayRoles.put("roles", List.of("x501")); // Read permission only

                    Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();
                    resourceAccess.put("gateway", gatewayRoles);

                    jwt.claim("resource_access", resourceAccess);
                    jwt.claim("relatedPartyId", relatedPartyId);
                    jwt.claim("preferred_username", "user.rp@orange.com");
                    jwt.claim("email", "user.rp@orange.com");
                    jwt.claim("name", "Regular User");
                })
                .authorities(jwt -> entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    /**
     * Creates a JWT token for a user without a relatedPartyId claim.
     * This simulates a user that should be denied access to resources.
     *
     * @return RequestPostProcessor for MockMvc
     */
    protected RequestPostProcessor jwtAsUserWithoutRelatedPartyId() {
        List<String> entitlements = List.of("x501");
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    Map<String, List<String>> gatewayRoles = new HashMap<>();
                    gatewayRoles.put("roles", List.of("x501")); // Read permission

                    Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();
                    resourceAccess.put("gateway", gatewayRoles);

                    jwt.claim("resource_access", resourceAccess);
                    // No relatedPartyId claim
                    jwt.claim("preferred_username", "incomplete.user@orange.com");
                    jwt.claim("email", "incomplete.user@orange.com");
                })
                .authorities(jwt -> entitlements.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    /**
     * Helper method to create resource_access claim structure matching Keycloak format.
     * This structure is expected by KeycloakGrantedAuthoritiesConverter.
     *
     * @param authority The authority to add to the roles
     * @return Map representing the resource_access claim
     */
    private Map<String, Map<String, List<String>>> createResourceAccessClaim(String authority) {
        // Note: "gateway" should match securityConfigProperties.getRolesAuthClientId()
        // Adjust if your configuration uses a different client ID
        Map<String, List<String>> roles = new HashMap<>();
        roles.put("roles", List.of(authority));

        Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();
        resourceAccess.put("gateway", roles);

        return resourceAccess;
    }
}



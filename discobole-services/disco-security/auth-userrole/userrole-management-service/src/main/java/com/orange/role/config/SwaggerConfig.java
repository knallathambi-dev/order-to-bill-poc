// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;

/**
 * The Class SwaggerConfig.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */

@Configuration
public class SwaggerConfig {

    @Resource
    private AppProperties app;

    @Value("${spring.keycloak}")
    String authServerUrl;

    private static final String OAUTH_SCHEME_NAME = "spring_oauth";
    
    /**
     * Method for swagger configuration.
     *
     * @return the docket
     */

    Info apiInfo() {
        return new Info().title("UserRolePermissionManagement").description(getDescription());
    }

    @Bean
    public OpenAPI api() {

        return new OpenAPI().components(new Components())
                .info(apiInfo());
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components().addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .info(new Info().title(app.getName()).description(getDescription()).version(app.getVersion()));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows();
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = createAuthorizationCodeFlow();
        return new OAuthFlows().implicit(flow);
    }

    private OAuthFlow createAuthorizationCodeFlow() {
        return new OAuthFlow().authorizationUrl(authServerUrl)
                .scopes(new Scopes().addString("read_access", "read data").addString("write_access", "modify data"));
    }

    private String getDescription() {
        return """
                ### Release 2.0.0 Dec 2024

                #### Goal

                The user role and permissions management API allows the management of the user roles, permissions, and privileges.
                
                #### Operations

                The DISCO implementation of Authorization API to manage users

                

                #### TMF API Reference

                TMF672 User Roles & Permissions Management

                | **User Roles & Permissions Management API** | **TMF API version** |
                |-----------------------------|---------------------|
                | 1.0.0                       | 4.0.1              |
                | 2.0.0                       | 4.0.1   	Added component in user role resource.             |
                | 2.1.0                       | 4.0.1       Aligned API Design              |
                """;
    }



}


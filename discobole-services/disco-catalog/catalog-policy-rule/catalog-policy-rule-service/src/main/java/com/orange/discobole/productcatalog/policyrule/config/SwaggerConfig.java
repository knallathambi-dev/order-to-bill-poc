// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.config;



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
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Configuration
public class SwaggerConfig {

    @Resource
    private AppProperties app;

    @Value("${spring.keycloakAuthUrl}")
    String authServerUrl;

    private static final String OAUTH_SCHEME_NAME = "spring_oauth";

    /**
     * Method for swagger configuration.
     *
     * @return the docket
     */
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
                ### Release 1.0.0 December 2023

                #### Goal

                Policy API goal is to provide the ability to manage Policy rule and its components:  
                **policyEvent**, **policyCondition**, **policyAction** and **policyDomain**.

                #### Operations

                Policy rule API performs the following operations on the resources:

                create<br>
                modify<br>
                search<br>
                delete<br>

                #### TMF API Reference

                TMF723 Policy Rule 19.0.1 (pre-production)

                | **Policy Rule API version** | **TMF API version** |
                |-----------------------------|---------------------|
                | 1.0.0                       | 4.0.0               |
                | 2.2.0                       | 4.0.0               |
                """;
    }


}


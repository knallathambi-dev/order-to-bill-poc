// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.config;


import com.orange.discobole.processflow.config.AppProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * The Class SwaggerConfig.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Profile({"dev", "!test"})
@Configuration
public class SwaggerOverrideConfig {

    private static final String OAUTH_SCHEME_NAME = "spring_oauth";
    @Value("${spring.keycloakAuthUrl}")
    private String authServerUrl;
    @Resource
    private AppProperties app;

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
        return "<h2>Fallout Management</h2>\n" +
                "Fallout management uses TMF 701 process flow library to create fallout,\\n" +
                "    close fallout.\\n \\nFallout management contains fallout apis such as get fallout\\n" +
                "     and filter it.";
    }

}

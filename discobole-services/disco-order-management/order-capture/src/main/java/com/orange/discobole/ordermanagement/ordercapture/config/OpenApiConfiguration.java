// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;

import com.orange.discobole.processflow.config.AppProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class OpenApiConfiguration {
    private static final String OAUTH_SCHEME_NAME = "Keycloak";
    @Resource
    private AppProperties app;

    @Value("${spring.security.oauth2.resourceServer.jwt.issuer-uri}")
    private String issuerUri;
    private final Logger log = LoggerFactory.getLogger(OpenApiConfiguration.class);

    Info apiInfo() {
        return new Info()
                .title(app.getName())
                .description(getDescription())
                .version(app.getVersion());
    }

    @Primary
    @Bean
    public OpenAPI api() {
        log.info("Configuring Swagger using OpenAPI.");
        return new OpenAPI().components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .info(apiInfo());
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows();
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = createAuthorizationCodeFlow();
        return new OAuthFlows().implicit(flow);
    }

    private OAuthFlow createAuthorizationCodeFlow() {
        return new OAuthFlow()
                .authorizationUrl(issuerUri + "/protocol/openid-connect/auth");
    }

    private String getDescription() {
        return "Implements TM Forum Open API TMF701 – Product Ordering Management for capturing, validating, \n" +
                "  and tracking product orders before fulfillment, ensuring compliance with TMF701 standards.";
    }
}
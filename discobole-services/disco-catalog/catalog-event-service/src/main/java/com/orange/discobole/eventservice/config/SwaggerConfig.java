package com.orange.discobole.eventservice.config;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

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
                .info(new Info().title(getName()).description(getDescription()).version(getVersion()));
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
        return  "<h3>Release 1.0.0 June 2023</h3>\r\n<h4>Goal</h4>\r\n<p>Event service is a mock event service to generate events for mock CFS and stockItems.</p>\r\n<h4>Operations</h4>\r\n<p>Event service performs the following operations on the resources :</p>\r\n<p>service specification create event</p>\r\n<p>service specification state change event</p>\r\n<p>stockitem create event</p>\r\n<p>stockItem state change event</p>\r\n<h3>TMF API Reference</h3>\r\n<p>TMF688 Event Management API Release 19.0.1</p>\r\n<table>\r\n<thead>\r\n<tr><th>Service Catalog API version</th><th>TMF API version</th></tr>\r\n</thead>\r\n<tbody>\r\n<tr><td>1.0.0</td><td>4.0.1</td></tr>\r\n</tbody>\r\n</table>";


    }

    private String getName(){
        return "event-service";
    }

    private String getVersion(){
        return "1.1.0-Release";
    }


}

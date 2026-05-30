// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.orange.discobole.ordermanagement.orderinventory.config.Constants.SPECIFICATION_FILE_PATH;
import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.OAUTH_SCHEME_NAME;

@Configuration
public class OpenApiConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfiguration.class);

    @Value("${spring.security.oauth2.resourceServer.jwt.issuer-uri}")
    private String keycloakIssuerUri;

    @Bean
    public OpenAPI configureOpenApi() throws IOException {
        OpenAPI yamlSpecification = loadSpecificationFromYaml();
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, buildOAuth2SecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .info(yamlSpecification.getInfo());
    }

    @Bean
    public OpenApiCustomizer enrichWithAllSchemas() {
        return springdocGeneratedApi -> {
            try {
                OpenAPI yamlSpecification = loadSpecificationFromYaml();
                addAllSchemasToApi(springdocGeneratedApi, yamlSpecification);
            } catch (IOException e) {
                log.error("Failed to load YAML specification for schema enrichment", e);
            }
        };
    }

    private OpenAPI loadSpecificationFromYaml() throws IOException {
        ClassPathResource yamlResource = new ClassPathResource(SPECIFICATION_FILE_PATH);
        String yamlContent;
        try (InputStream inputStream = yamlResource.getInputStream()) {
            yamlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        SwaggerParseResult parseResult = new OpenAPIV3Parser().readContents(yamlContent, null, null);
        OpenAPI parsedApi = parseResult.getOpenAPI();
        if (parsedApi == null) {
            throw new IllegalStateException("Failed to parse OpenAPI specification from YAML");
        }
        return parsedApi;
    }

    private void addAllSchemasToApi(OpenAPI destinationApi, OpenAPI sourceSpecification) {
        Components destinationComponents = destinationApi.getComponents();
        if (destinationComponents == null) {
            destinationComponents = new Components();
            destinationApi.setComponents(destinationComponents);
        }

        Components sourceComponents = sourceSpecification.getComponents();
        if (sourceComponents != null && sourceComponents.getSchemas() != null) {
            sourceComponents.getSchemas().forEach(destinationComponents::addSchemas);
        }
    }

    private SecurityScheme buildOAuth2SecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .implicit(new OAuthFlow()
                                .authorizationUrl(keycloakIssuerUri + "/protocol/openid-connect/auth")));
    }
}
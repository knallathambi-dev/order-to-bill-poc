// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogOpenApiCustomizer {

   @Value("${spring.keycloakAuthUrl}")
   private String authServerUrl;

 @Bean
 public OpenApiCustomizer customOpenApi() {
    return openApi ->
            openApi.info(new Info()
                    .title("ProductCatalogManagement")
                    .version("2.0.0")
                    .description("""
                            ### Release 2.0.0 - March 2026
                             
                            #### Goal
                            Product Catalog API is one of Catalog Management API Family.  
                            It provides a catalog of products.
                             
                            #### Operations
                            Product Catalog API performs the following operations on the resources :

                            - Retrieve an entity or a collection of entities depending on filter criteria
                            - For the implementation of DISCO, product catalog API supports retrieve operations only.
                             

                            ### TMF API Reference
                               TMF620 Product Catalog Management Release 2.0.0 March 2026
                             
                            | Product Catalog API version | TMF API version | Description |
                            |---------------------------|----------------|------------|
                            | 1.1.0 | 4.0.1 | Basic implementation |
                            | 1.2.0 | 4.0.1 | Added Export job |
                            | 1.3.0 | 4.0.1 | Rating & billing (rollovers, proration) |
                            | 1.4.0 | 4.0.1 | Tax implementation |
                            | 1.5.0 | 4.0.1 | Commitment Term |
                            | 2.0.0 | 4.0.1 | Adapted allowedAction from v5 |
                            """));
    }

}
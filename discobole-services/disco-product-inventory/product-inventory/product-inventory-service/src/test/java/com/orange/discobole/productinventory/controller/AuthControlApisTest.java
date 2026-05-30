// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.RelatedPartyEntity;
import com.orange.discobole.productinventory.util.JwtAuthHelper;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT;
import static com.orange.discobole.productinventory.controller.GetProductsApiTest.getDefaultProductEntityBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControlApisTest extends JwtAuthHelper {


    private static String getIdFromRelatedPartyOrPartyRole(@Valid final RelatedPartyOrPartyRole relatedPartyEntity) {
        if (relatedPartyEntity == null || relatedPartyEntity.getPartyOrPartyRole() == null) {
            return null;
        }

        final Object partyOrRole = relatedPartyEntity.getPartyOrPartyRole();
        if (partyOrRole instanceof PartyRef partyRef) {
            return partyRef.getId();
        }
        if (partyOrRole instanceof PartyRoleRef partyRoleRef) {
            return partyRoleRef.getId();
        }

        return null;
    }

    @Test
    void givenAdminUser_whenGetProducts_thenAllProductsRetrieved() throws Exception {
        // Given: Multiple products with different relatedPartyIds
        List<ProductEntity> allProducts = new ArrayList<>();

        ProductEntity product1 = mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("106", "Party 1")))
                        .build()
        );
        allProducts.add(product1);

        ProductEntity product2 = mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("107", "Party 2")))
                        .build()
        );
        allProducts.add(product2);

        // When: Admin calls the endpoint using JWT auth
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .with(jwtAsAdmin())
        );

        // Then: All products are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSameSizeAs(allProducts);
    }

    @Test
    void givenAdminUser_whenGetProductsWithRelatedPartyIdFilter_thenFilteredProductsRetrieved() throws Exception {
        // Given: Multiple products with different relatedPartyIds
        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("106", "Party 1")))
                        .build()
        );

         mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("107", "Party 2")))
                        .build()
        );

        // When: Admin filters by specific relatedPartyId
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.RELATED_PARTY_PARTY_OR_PARTY_ROLE + QueryFields.ID_SUFFIX, "106")
                        .with(jwtAsAdmin())
        );

        // Then: Only filtered products are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSize(1);
        assertThat(getIdFromRelatedPartyOrPartyRole(actualProducts.get(0).getRelatedParty().get(0))).isEqualTo("106");
    }

    @Test
    void givenNonAdminUser_whenGetProducts_thenOnlyUserProductsRetrieved() throws Exception {
        // Given: Products for different parties
        String userRelatedPartyId = "106";

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .build()
        );

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("107", "Other Party")))
                        .build()
        );

        // When: Non-admin user calls endpoint with JWT
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Only user's products are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSize(1);
        assertThat(getIdFromRelatedPartyOrPartyRole(actualProducts.get(0).getRelatedParty().get(0))).isEqualTo(userRelatedPartyId);
    }

    @Test
    void givenNonAdminUser_whenGetProductsWithMatchingRelatedPartyIdFilter_thenProductsRetrieved() throws Exception {
        // Given: User's product
        String userRelatedPartyId = "106";

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .build()
        );

        // When: Non-admin user filters by their own relatedPartyId
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.RELATED_PARTY_PARTY_OR_PARTY_ROLE + QueryFields.ID_SUFFIX, userRelatedPartyId)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: User's products are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSize(1);
        assertThat(getIdFromRelatedPartyOrPartyRole(actualProducts.get(0).getRelatedParty().get(0))).isEqualTo(userRelatedPartyId);
    }

    @Test
    void givenNonAdminUser_whenGetProductsWithDifferentRelatedPartyIdFilter_thenAccessDenied() throws Exception {
        // Given: Products for different parties
        String userRelatedPartyId = "106";
        String otherPartyId = "107";

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(otherPartyId, "Other Party")))
                        .build()
        );

        // When: Non-admin user tries to filter by another party's ID
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.RELATED_PARTY_PARTY_OR_PARTY_ROLE + QueryFields.ID_SUFFIX, otherPartyId)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Access denied
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenNonAdminUser_whenGetProductsWithMultipleRelatedPartyIdFilters_thenAccessDeniedIfNotAllMatch() throws Exception {
        // Given: User's related party ID
        String userRelatedPartyId = "106";
        String otherPartyId = "107";

        // When: Non-admin user tries to filter by multiple IDs including others
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.RELATED_PARTY_PARTY_OR_PARTY_ROLE + QueryFields.ID_SUFFIX,
                                userRelatedPartyId, otherPartyId)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Access denied
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenNonAdminUserWithoutRelatedPartyId_whenGetProducts_thenAccessDenied() throws Exception {
        // Given: Products exist
        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("106", "Party 1")))
                        .build()
        );

        // When: User without relatedPartyId in token calls endpoint
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .with(jwtAsUserWithoutRelatedPartyId())
        );

        // Then: Access denied
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenNonAdminUser_whenGetProductsWithNoFilter_thenOnlyUserProductsRetrieved() throws Exception {
        // Given: Multiple products
        String userRelatedPartyId = "106";
        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .name("User Product 1")
                        .build()
        );

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .name("User Product 2")
                        .build()
        );

        // Other user's products
        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("107", "Other Party")))
                        .name("Other Product 1")
                        .build()
        );

        // When: Non-admin calls without filter
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Only user's products returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts)
                .hasSize(2)
                .allMatch(p ->
                getIdFromRelatedPartyOrPartyRole(p.getRelatedParty().get(0)).equals(userRelatedPartyId));
    }

    @Test
    void givenNonAdminUser_whenGetProductsWithOtherFilters_thenUserProductsFilteredCorrectly() throws Exception {
        // Given: User's products with different statuses
        String userRelatedPartyId = "106";

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .status(ProductStatusType.ACTIVE)
                        .build()
        );

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .status(ProductStatusType.TERMINATED)
                        .build()
        );

        // When: Non-admin filters by status
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.STATUS, ProductStatusType.ACTIVE.getValue())
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Only active products for user are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSize(1);
        assertThat(actualProducts.get(0).getStatus()).isEqualTo(ProductStatusType.ACTIVE);
        assertThat(getIdFromRelatedPartyOrPartyRole(actualProducts.get(0).getRelatedParty().get(0))).isEqualTo(userRelatedPartyId);
    }

    @Test
    void givenNonAdminUser_whenGetEmptyDatabase_thenEmptyListRetrieved() throws Exception {
        // Given: Empty database
        String userRelatedPartyId = "106";

        // When: Non-admin user calls endpoint
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Empty list returned with OK status
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).isEmpty();
    }

    @Test
    void givenAdminUser_whenGetEmptyDatabase_thenEmptyListRetrieved() throws Exception {
        // Given: Empty database

        // When: Admin calls endpoint
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .with(jwtAsAdmin())
        );

        // Then: Empty list returned with OK status
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).isEmpty();
    }

    @Test
    void givenAdminUser_whenGetProductsWithNameFilter_thenFilteredProductsRetrieved() throws Exception {
        // Given: Products with different names
        String productName = "Special Product";

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("106", "Party 1")))
                        .name(productName)
                        .build()
        );

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("107", "Party 2")))
                        .name("Regular Product")
                        .build()
        );

        // When: Admin filters by name
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.NAME, productName)
                        .with(jwtAsAdmin())
        );

        // Then: Only products with matching name are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSize(1);
        assertThat(actualProducts.get(0).getName()).isEqualTo(productName);
    }

    @Test
    void givenNonAdminUser_whenGetProductsWithNameAndRelatedPartyFilters_thenBothFiltersApplied() throws Exception {
        // Given: Products for the user with different names
        String userRelatedPartyId = "106";
        String productName = "Special Product";

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .name(productName)
                        .build()
        );

        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty(userRelatedPartyId, "User Party")))
                        .name("Regular Product")
                        .build()
        );

        // Other user's product with same name
        mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .relatedParty(List.of(createRelatedParty("107", "Other Party")))
                        .name(productName)
                        .build()
        );
        // When: Non-admin filters by name
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT)
                        .param(QueryFields.NAME, productName)
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Only user's products with matching name are returned
        List<Product> actualProducts = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProducts).hasSize(1);
        assertThat(actualProducts.get(0).getName()).isEqualTo(productName);
        assertThat(getIdFromRelatedPartyOrPartyRole(actualProducts.get(0).getRelatedParty().get(0))).isEqualTo(userRelatedPartyId);
    }
    @Test
    void givenAdminUser_whenRetrieveAnyProduct_thenProductReturned() throws Exception {
        // Given: A product belonging to another related party
        ProductEntity savedProduct = mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .id("product-001")
                        .relatedParty(List.of(createRelatedParty("107", "Other Party")))
                        .build()
        );

        // When: Admin retrieves it
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT + "/{id}", savedProduct.getId())
                        .with(jwtAsAdmin())
        );

        // Then: Success and correct product returned
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(actualProduct.getId()).isEqualTo(savedProduct.getId());
    }
    @Test
    void givenNonAdminUser_whenRetrieveProductOfAnotherParty_thenAccessDenied() throws Exception {
        // Given: A product belonging to a different related party
        ProductEntity savedProduct = mongoTemplate.save(
                getDefaultProductEntityBuilder()
                        .id("product-002")
                        .relatedParty(List.of(createRelatedParty("107", "Other Party")))
                        .build()
        );

        // When: Non-admin user tries to access it
        String userRelatedPartyId = "106";
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT + "/{id}", savedProduct.getId())
                        .with(jwtAsUser(userRelatedPartyId))
        );

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());
    }

    // Helper method to create related party
    private RelatedPartyEntity createRelatedParty(String id, String name) {
        return RelatedPartyEntity.builder()
                .id(id)
                .atType("PartyRef")
                .name(name)
                .build();
    }
}
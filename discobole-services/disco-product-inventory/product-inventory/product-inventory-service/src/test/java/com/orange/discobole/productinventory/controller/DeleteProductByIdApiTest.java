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
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.RelatedPartyEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.RESOURCE_NOT_FOUND;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForDeleteProductEvent;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductBuilderWithRelation;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductSpecificationEntityBuilder;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteProductByIdApiTest extends AbstractTest {


    @Test
    void givenValidProductId_whenDeleteProduct_thenProductDeleted() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isNoContent());
        ProductEntity deletedProductEntity = mongoTemplate.findById(expectedProductId, ProductEntity.class);
        assertNull(deletedProductEntity, "The product should be deleted and not found in the database.");
    }

    @Test
    void givenInvalidProductId_whenDeleteProduct_thenProductNotExist() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, "exampleProductId123"));
        resultActions.andExpect(status().isNotFound());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotFoundErrorExists(error, RESOURCE_NOT_FOUND.getCode(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, "exampleProductId123"), RESOURCE_NOT_FOUND.getStatus());

    }

    @Test
    void givenValidProductIdWithRelationShip_whenDeleteProduct_thenProductDeleted() throws Exception {
        ObjectId childProductId = ObjectId.get();
        final ProductEntity childProductEntity = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(childProductId.toString()).build());
        ObjectId expectedProductId = ObjectId.get();
        ProductEntity expectedProductEntity = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "AtomicOffering", childProductId.toString(), false, randomAlphabetic(10), PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId.toString()).build();
        mongoTemplate.save(expectedProductEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, childProductId));
        resultActions.andExpect(status().isNoContent());
        ProductEntity updatedProductEntity = mongoTemplate.findById(expectedProductId, ProductEntity.class);
        Assertions.assertNotNull(updatedProductEntity);
        Assertions.assertNotNull(updatedProductEntity.getLastUpdateDate());
        Assertions.assertTrue(updatedProductEntity.getProductRelationship().isEmpty() ||
                updatedProductEntity.getProductRelationship().stream().noneMatch(rel -> childProductId.equals(rel.getProduct().getId())));
       Product productPublishEvent = Product.builder().id(String.valueOf(childProductId)).status(childProductEntity.getStatus()).atType(childProductEntity.getAtType()).build();
        consumeAndAssertEqualityForDeleteProductEvent(productPublishEvent);

    }

    @Test
    void givenNonAdminUser_whenDeleteOwnProduct_thenSuccess() throws Exception {
        // Given: User owns a product
        String userRelatedPartyId = "106";
        String productId = ObjectId.get().toString();
        ProductEntity userProduct = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id(userRelatedPartyId)
                        .name("User Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(userProduct);

        // When: User deletes their own product (with DELETE permission x503, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, DELETE,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                "x503", userRelatedPartyId);

        // Then: Delete is successful
        resultActions.andExpect(status().isNoContent());

        // Verify product is deleted
        ProductEntity deletedProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertNull(deletedProduct);
    }

    @Test
    void givenNonAdminUser_whenDeleteProductOfAnotherUser_thenAccessDenied() throws Exception {
        // Given: Product belongs to another user
        String userRelatedPartyId = "106";
        String otherRelatedPartyId = "107";
        String productId = ObjectId.get().toString();

        ProductEntity otherUserProduct = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id(otherRelatedPartyId)
                        .name("Other Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(otherUserProduct);

        // When: User tries to delete another user's product (with DELETE permission x503, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, DELETE,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                "x503", userRelatedPartyId);

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());

        // Verify product still exists
        ProductEntity stillExistingProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertNotNull(stillExistingProduct);
    }

    @Test
    void givenAdminUser_whenDeleteAnyProduct_thenSuccess() throws Exception {
        // Given: Product belongs to any user
        String anyRelatedPartyId = "999";
        String productId = ObjectId.get().toString();

        ProductEntity anyProduct = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id(anyRelatedPartyId)
                        .name("Any Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(anyProduct);

        // When: Admin deletes the product (using default admin auth which includes admin role)
        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId));

        // Then: Delete is successful
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void givenUserWithoutRelatedPartyIdInToken_whenDeleteProduct_thenAccessDenied() throws Exception {
        // Given: Product exists
        String productId = ObjectId.get().toString();
        ProductEntity product = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id("106")
                        .name("Some Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(product);

        // When: User without relatedPartyId tries to delete (with DELETE permission x503, without admin role, without relatedPartyId)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, DELETE,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                "x503", null);

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());

        // Verify product still exists
        ProductEntity stillExistingProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertNotNull(stillExistingProduct);
    }

}
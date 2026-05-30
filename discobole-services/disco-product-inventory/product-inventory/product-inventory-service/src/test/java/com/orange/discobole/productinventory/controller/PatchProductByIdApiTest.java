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
import com.orange.discobole.productinventory.constant.TestConstant;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Value;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.enumerate.UnitEnum;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.ValidationUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.APPLICATION_MERGE_PATCH_JSON;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.METHOD_NOT_ALLOWED;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_OPERATIONAL_STATUS_CANNOT_BE_MODIFIED;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_STATUS_CANNOT_BE_MODIFIED;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForStateChangeEvent;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductEntityWithCharacteristics;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductSpecificationEntityBuilder;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatchProductByIdApiTest extends AbstractTest {
    @Autowired
    private ProductMapper productMapper;

    @ParameterizedTest
    @ValueSource(strings = {TestConstant.APPLICATION_JSON, APPLICATION_MERGE_PATCH_JSON})
    void givenValidProductIdOfProductSpec_whenPatchWithInvalidPatchFiled_thenSucceed(String contentType) throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        Product productPatch = Product.builder().operationalStatus(ProductOperationalStatusType.CONFIRMED).startDate(OffsetDateTime.now()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, contentType));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), "PATCH method not supported by that resource <startDate>", METHOD_NOT_ALLOWED.getStatus());
    }


    @ParameterizedTest
    @ValueSource(strings = {TestConstant.APPLICATION_JSON, APPLICATION_MERGE_PATCH_JSON})
    void givenValidProductIdOfProductSpec_whenPatchWithInvalidStatus_thenBadRequest(String contentType) throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        Product productPatch = Product.builder().operationalStatus(ProductOperationalStatusType.ACTIVE).status(ProductStatusType.ACTIVE).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, contentType));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_OPERATIONAL_STATUS_CANNOT_BE_MODIFIED, ProductStatusType.CREATED, ProductOperationalStatusType.ACTIVE), INVALID_INPUT.getStatus());
    }

    @Test
    void givenValidProductIdOfProductSpec_whenPatchWithInvalidMappingStatus_thenBadRequest() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        mongoTemplate.save(expectedProductEntity);
        Product productPatch = Product.builder().status(ProductStatusType.TERMINATED).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_STATUS_CANNOT_BE_MODIFIED, ProductStatusType.CREATED, ProductOperationalStatusType.TERMINATED), INVALID_INPUT.getStatus());
    }


    @Test
    void givenValidProductIdOfProductSpec_whenPatchWithValidStatus_thenSucceed() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        mongoTemplate.save(expectedProductEntity);
        Product productPatch = Product.builder().status(ProductStatusType.ACTIVE).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        ProductEntity actualProductEntity = mongoTemplate.findById(expectedProductId, ProductEntity.class);
        assert actualProductEntity != null;
        assertEquals(ProductStatusType.ACTIVE, actualProductEntity.getStatus());
        Product dtoWithFullMapping = productMapper.toDtoWithFullMapping(actualProductEntity);
        consumeAndAssertEqualityForStateChangeEvent(StateChangeProduct.fromProduct(dtoWithFullMapping, ProductStatusType.CREATED));
        assertThat(actualProductEntity.getStartDate().toLocalDate()).isEqualTo(OffsetDateTime.now(ZoneOffset.UTC).toLocalDate());
    }
    @Test
    void givenValidProductWithCharacteristics_whenPatch_thenSucceed() throws Exception {
        CharacteristicEntity characteristicEntity = CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").build();
        ProductEntity productEntity = createProductEntityWithCharacteristics(List.of(characteristicEntity), PRODUCT.getValue()).build();
        mongoTemplate.save(productEntity);
        StringCharacteristic stringCharacteristic = StringCharacteristic.builder().atType("StringCharacteristic").id("id").value("stringValue").valueType("string").build();
        Product productPatch = Product.builder().productCharacteristic(List.of(stringCharacteristic)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productEntity.getId()), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        ProductEntity savedProductAfterPatch = mongoTemplate.findById(productEntity.getId(), ProductEntity.class);
        assertEquals("stringValue", savedProductAfterPatch.getProductCharacteristic().get(0).getValue());
    }

    @Test
    void givenValidProductId_whenPatchDifferentId_thenMethodNotAllowed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(productId).build();
        mongoTemplate.save(productEntity);
        Product productPatch = Product.builder().id("ANY_ID").build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isMethodNotAllowed());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), ValidationUtil.getUnpatchableErrorMessageForField("id"), METHOD_NOT_ALLOWED.getStatus());

    }

    @Test
    void givenValidProductId_whenPatchDifferentCreationDate_thenMethodNotAllowed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(productId).creationDate(LocalDateTime.now().atOffset(ZoneOffset.UTC)).build();
        mongoTemplate.save(productEntity);
        Product productPatch = Product.builder().id(productEntity.getId()).creationDate(LocalDateTime.now().minusDays(1).atOffset(ZoneOffset.UTC)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isMethodNotAllowed());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), ValidationUtil.getUnpatchableErrorMessageForField("creationDate"), METHOD_NOT_ALLOWED.getStatus());


    }

    @Test
    void givenValidProductId_whenPatchDifferentAtType_thenMethodNotAllowed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(productId).atType(ProductTypeEnum.PRODUCT.getValue()).build();
        mongoTemplate.save(productEntity);
        PhysicalProduct productPatch = PhysicalProduct.builder().id(productEntity.getId()).atType(ProductTypeEnum.PHYSICAL_PRODUCT.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isMethodNotAllowed());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), ValidationUtil.getUnpatchableErrorMessageForField("@type"), METHOD_NOT_ALLOWED.getStatus());


    }

    @Test
    void givenValidProductId_whenPatchDifferentHref_thenMethodNotAllowed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(productId).href("VALUE").build();
        mongoTemplate.save(productEntity);
        Product productPatch = Product.builder().id(productEntity.getId()).href("DIFFERENT_VALUE").build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isMethodNotAllowed());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), ValidationUtil.getUnpatchableErrorMessageForField("href"), METHOD_NOT_ALLOWED.getStatus());
    }

    @Test
    void givenProductInRelationship_whenPatchProduct_thenParentTypeIsTheSame() throws Exception {
        String parentId = ObjectId.get().toString();
        ProductEntity parentProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(parentId).href("VALUE").build();
        mongoTemplate.save(parentProductEntity);
        String childId = "parent";
        ProductEntity childProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(childId).href("VALUE")
                .productRelationship(List.of(ProductRelationshipEntity.builder().product(new ProductRefEntity(parentProductEntity.getId())).build())
                ).build();
        mongoTemplate.save(childProductEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, childId));
        Product childProductFromApi = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, childId), contentBody(childProductFromApi),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        ProductEntity databaseParentEntity = mongoTemplate.findById(parentId, ProductEntity.class);
        assertEquals(parentProductEntity.getAtType(), databaseParentEntity.getAtType());
    }

    @Test
    void givenTwoProducts_whenPatchToAddRelation_thenRelationAdded() throws Exception {
        String product1Id = ObjectId.get().toString();
        String product2Id = ObjectId.get().toString();
        ProductEntity product1 = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(product1Id).href("VALUE").build();
        mongoTemplate.save(product1);
        ProductEntity product2 = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10))
                .id(product2Id).href("VALUE").build();
        mongoTemplate.save(product2);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, product1Id));
        Product product1FromApi = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        product1FromApi.setHref(null);
        product1FromApi.getProductRelationship().add(
                ProductRelationship.builder()
                        .relationshipType(ProductRelationshipType.RELIESON.getValue())
                        .product(ProductRef.builder().id(product2Id).build())
                        .build()
        );
        callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, product1Id), contentBody(product1FromApi),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        ProductEntity product1FromDatabase = mongoTemplate.findById(product1Id, ProductEntity.class);
        assert product1FromDatabase != null;
        assertEquals(1, product1FromDatabase.getProductRelationship().size());
    }

    private static void assertRelatedPartyEquality(Product productResponse, ProductEntity productEntity) {
        assertNotNull(productResponse.getRelatedParty());
        assertEquals(1, productResponse.getRelatedParty().size());
        RelatedPartyOrPartyRole relatedPartyOrPartyRole = productResponse.getRelatedParty().get(0);
        RelatedPartyEntity relatedPartyOrPartyRoleEntity = productEntity.getRelatedParty().get(0);
        assertEquals("RelatedPartyRefOrPartyRoleRef", relatedPartyOrPartyRole.getAtType());
        assertEquals(relatedPartyOrPartyRole.getRole(), relatedPartyOrPartyRoleEntity.getRole());
        assertEquals(relatedPartyOrPartyRole.getPartyOrPartyRole().getAtType(), relatedPartyOrPartyRoleEntity.getAtType());
        if (relatedPartyOrPartyRole.getPartyOrPartyRole() instanceof PartyRef partyRef) {
            assertEquals(partyRef.getId(), relatedPartyOrPartyRoleEntity.getId());
            assertEquals(partyRef.getName(), relatedPartyOrPartyRoleEntity.getName());
            assertEquals(partyRef.getAtReferredType(), relatedPartyOrPartyRoleEntity.getAtReferredType());
        } else if (relatedPartyOrPartyRole.getPartyOrPartyRole() instanceof PartyRoleRef partyRoleRef) {
            assertEquals(partyRoleRef.getId(), relatedPartyOrPartyRoleEntity.getId());
            assertEquals(partyRoleRef.getName(), relatedPartyOrPartyRoleEntity.getName());
            assertEquals(partyRoleRef.getAtReferredType(), relatedPartyOrPartyRoleEntity.getAtReferredType());
            assertEquals(partyRoleRef.getPartyId(), relatedPartyOrPartyRoleEntity.getPartyId());
            assertEquals(partyRoleRef.getPartyName(), relatedPartyOrPartyRoleEntity.getPartyName());
        }
    }

    @Test
    void givenValidProductWithRelatedParty_whenPatch_thenRelatedPartyNotRemoved() throws Exception {
        CharacteristicEntity characteristicEntity = CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").build();
        ProductEntity productEntity = createProductEntityWithCharacteristics(List.of(characteristicEntity), PRODUCT.getValue()).build();
        productEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        mongoTemplate.save(productEntity);
        Product productPatch = Product.builder().status(ProductStatusType.ACTIVE).operationalStatus(ProductOperationalStatusType.ACTIVE).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productEntity.getId()), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        Product productResponse = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertRelatedPartyEquality(productResponse, productEntity);
        ProductEntity savedProductAfterPatch = mongoTemplate.findById(productEntity.getId(), ProductEntity.class);
        assertNotNull(savedProductAfterPatch);
        assertRelatedPartyEquality(productResponse, savedProductAfterPatch);
    }

    @Test
    void givenValidProductWithRelatedParty_whenPatchRelatedParty_thenRelatedPartyNotRemoved() throws Exception {
        CharacteristicEntity characteristicEntity = CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").build();
        ProductEntity productEntity = createProductEntityWithCharacteristics(List.of(characteristicEntity), PRODUCT.getValue()).build();
        String newName = "NEW_NAME";
        String newRole = "NEW_ROLE";
        RelatedPartyEntity relatedPartyEntity = productEntity.getRelatedParty().get(0);

        mongoTemplate.save(productEntity);
        Product productPatch = Product.builder().relatedParty(List.of(createAndUpdateRelatedPartyFromEntity(relatedPartyEntity, newName, newRole))).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productEntity.getId()), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        Product productResponse = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        productEntity.getRelatedParty().get(0).setName(newName);
        productEntity.getRelatedParty().get(0).setRole(newRole);
        assertRelatedPartyEquality(productResponse, productEntity);
        ProductEntity savedProductAfterPatch = mongoTemplate.findById(productEntity.getId(), ProductEntity.class);
        assertNotNull(savedProductAfterPatch);
        assertRelatedPartyEquality(productResponse, savedProductAfterPatch);
    }

    private RelatedPartyOrPartyRole createAndUpdateRelatedPartyFromEntity(
            RelatedPartyEntity relatedPartyEntity,
            String newName,
            String newRole) {
        RelatedPartyOrPartyRole.RelatedPartyOrPartyRoleBuilder<?, ?> relatedPartyOrPartyRole = RelatedPartyOrPartyRole.builder();

        relatedPartyOrPartyRole.atType("RelatedPartyRefOrPartyRoleRef");
        relatedPartyOrPartyRole.role(newRole);
        if (relatedPartyEntity.getAtType() != null) {
            if (relatedPartyEntity.getAtType().equals("PartyRef")) {
                relatedPartyOrPartyRole.partyOrPartyRole(PartyRef
                        .builder()
                        .id(relatedPartyEntity.getId())
                        .atType("PartyRef")
                        .name(newName)
                        .atReferredType(relatedPartyEntity.getAtReferredType())
                        .build());

            } else if (relatedPartyEntity.getAtType().equals("PartyRoleRef")) {
                relatedPartyOrPartyRole.partyOrPartyRole(PartyRoleRef
                        .builder()
                        .id(relatedPartyEntity.getId())
                        .name(relatedPartyEntity.getName())
                        .atType("PartyRoleRef")
                        .partyId(relatedPartyEntity.getPartyId())
                        .partyName(relatedPartyEntity.getPartyName())
                        .atReferredType(relatedPartyEntity.getAtReferredType())
                        .build());
            }
        }
        return relatedPartyOrPartyRole.build();
    }
    @Test
    void givenProductWithValidityCharacteristics_whenPatchStatusToActive_thenSucceedAndTerminationDateIsSetByValidUntil() throws Exception {
        OffsetDateTime validTo = OffsetDateTime.parse("2024-11-20T09:43:16Z");
        Value value = Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).validTo(validTo).build();
        CharacteristicEntity characteristicEntity = CharacteristicEntity.builder().id("id").valueType("string").atType("ValidityCharacteristic").value(value).build();
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).productCharacteristic(List.of(characteristicEntity)).build();
        expectedProductEntity = mongoTemplate.save(expectedProductEntity);
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        mongoTemplate.save(expectedProductEntity);
        Product productPatch = Product.builder().status(ProductStatusType.ACTIVE).operationalStatus(ProductOperationalStatusType.ACTIVE).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        mongoTemplate.findById(productId, ProductEntity.class);
        resultActions.andExpect(status().isOk());

        Product product = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(product.getTerminationDate()).isEqualTo(validTo);

    }


    @Test
    void givenProductWithValidityCharacteristics_whenPatchStatusToActive_thenSucceedAndTerminationDateIsSet() throws Exception {
        Value value = Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).build();
        CharacteristicEntity characteristicEntity = CharacteristicEntity.builder().id("id").valueType("string").atType("ValidityCharacteristic").value(value).build();
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).productCharacteristic(List.of(characteristicEntity)).build();
        mongoTemplate.save(expectedProductEntity);
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        mongoTemplate.save(expectedProductEntity);

        Product productPatch = Product.builder().status(ProductStatusType.ACTIVE).operationalStatus(ProductOperationalStatusType.ACTIVE).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        mongoTemplate.findById(productId, ProductEntity.class);
        Product product = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(product.getTerminationDate()).isEqualTo(product.getStartDate().plusDays(value.getValue()));
    }

    @Test
    void givenNonAdminUser_whenPatchOwnProduct_thenSuccess() throws Exception {
        // Given: User owns a product
        String userRelatedPartyId = "106";
        String productId = ObjectId.get().toString();
        ProductEntity userProduct = createProductSpecificationEntityBuilder(
                ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id(userRelatedPartyId)
                        .name("User Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(userProduct);

        Product patchProduct = Product.builder()
                .description("Updated description")
                .build();

        // When: User patches their own product (with PATCH permission x502, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, PATCH,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                "x502", userRelatedPartyId,
                contentBody(patchProduct), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Patch is successful
        resultActions.andExpect(status().isOk());
        Product patchedProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(patchedProduct.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void givenNonAdminUser_whenPatchProductOfAnotherUser_thenAccessDenied() throws Exception {
        // Given: Product belongs to another user
        String userRelatedPartyId = "106";
        String otherRelatedPartyId = "107";
        String productId = ObjectId.get().toString();

        ProductEntity otherUserProduct = createProductSpecificationEntityBuilder(
                ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id(otherRelatedPartyId)
                        .name("Other Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(otherUserProduct);

        Product patchProduct = Product.builder()
                .description("Malicious update")
                .build();

        // When: User tries to patch another user's product (with PATCH permission x502, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, PATCH,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                "x502", userRelatedPartyId,
                contentBody(patchProduct), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());

        // Verify product wasn't modified
        ProductEntity unchangedProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertThat(unchangedProduct.getDescription()).isNotEqualTo("Malicious update");
    }

    @Test
    void givenAdminUser_whenPatchAnyProduct_thenSuccess() throws Exception {
        // Given: Product belongs to any user
        String anyRelatedPartyId = "999";
        String productId = ObjectId.get().toString();

        ProductEntity anyProduct = createProductSpecificationEntityBuilder(
                ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id(anyRelatedPartyId)
                        .name("Any Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(anyProduct);

        Product patchProduct = Product.builder()
                .description("Admin update")
                .build();

        // When: Admin patches the product (using default admin auth which includes admin role)
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                contentBody(patchProduct), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Patch is successful
        resultActions.andExpect(status().isOk());
        Product patchedProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(patchedProduct.getDescription()).isEqualTo("Admin update");

        // Verify product was modified in database
        ProductEntity modifiedProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertThat(modifiedProduct.getDescription()).isEqualTo("Admin update");
    }

    @Test
    void givenUserWithoutRelatedPartyIdInToken_whenPatchProduct_thenAccessDenied() throws Exception {
        // Given: Product exists
        String productId = ObjectId.get().toString();
        String originalDescription = randomAlphabetic(10);
        ProductEntity product = createProductSpecificationEntityBuilder(
                ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue())
                .description(originalDescription)
                .id(productId)
                .relatedParty(List.of(RelatedPartyEntity.builder()
                        .id("106")
                        .name("Some Party")
                        .atType("PartyRef")
                        .build()))
                .build();
        mongoTemplate.save(product);

        Product patchProduct = Product.builder()
                .description("Unauthorized update")
                .build();

        // When: User without relatedPartyId tries to patch (with PATCH permission x502, without admin role, without relatedPartyId)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, PATCH,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId),
                "x502", null,
                contentBody(patchProduct), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());

        // Verify product wasn't modified
        ProductEntity unchangedProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertThat(unchangedProduct.getDescription()).isEqualTo(originalDescription);
        assertThat(unchangedProduct.getDescription()).isNotEqualTo("Unauthorized update");
    }
}


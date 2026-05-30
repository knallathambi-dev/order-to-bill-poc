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
import com.fasterxml.jackson.databind.JsonNode;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Value;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.enumerate.UnitEnum;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.creator.ProductEntityCreator;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.orange.discobole.productinventory.constant.TestConstant.APPLICATION_JSON_PATCH_PATCH;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.SELLS;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.METHOD_NOT_ALLOWED;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForAttributeChangeEvent;
import static com.orange.discobole.productinventory.util.ValidationUtil.getNotEmptyMessage;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.*;
import static com.orange.discobole.productinventory.util.creator.ProductPatchCreator.createProductPatchBuilder;
import static com.orange.discobole.productinventory.util.creator.ProductPatchCreator.createProductPatchBuilderForRemove;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatchProductsApiTest extends AbstractTest {

    @Test
    void givenValidProductOperationalStatus_whenPatch_thenSucceed() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(expectedProductEntity);
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        String operationalStatus = "\"Confirmed\"";
        JsonNode valueOperationalStatus = objectMapper.readTree(operationalStatus);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueOperationalStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        products.stream().filter(product -> productId.equals(product.getId()))
                .findFirst()
                .ifPresent(product -> assertNotNull(product.getLastUpdateDate()));
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, List.of(expectedProductEntity), false);
    }


    @Test
    void givenInvalidProductOperationalStatus_whenPatch_thenBadRequest() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(expectedProductEntity);
        String operationalStatus = "\"PendingActive\"";
        JsonNode valueOperationalStatus = objectMapper.readTree(operationalStatus);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueOperationalStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), "The operational status " + expectedProductEntity.getOperationalStatus() + " cannot be modified into PendingActive", INVALID_INPUT.getStatus());
    }

    @Test
    void givenInvalidStatus_whenPatch_thenBadRequest() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(expectedProductEntity);
        String status = "\"Terminated\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), "The status " + expectedProductEntity.getStatus() + " cannot be modified into Terminated", INVALID_INPUT.getStatus());
    }

    @Test
    void givenValidStatusActive_whenPatch_thenSucceed() throws Exception {
        String productId = ObjectId.get().toString();
        String productWithReliesFromId = "spReliesFromId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        ProductEntity productEntityReliesFrom = createProductEntityBuilderWithProductSpecificationReliesFrom(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, productId, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productWithReliesFromId).build();
        productEntityReliesFrom.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        mongoTemplate.save(expectedProductEntity);
        mongoTemplate.save(productEntityReliesFrom);
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productWithReliesFromId + "/operationalStatus", valueStatus);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productWithReliesFromId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchOperationStatus, productPatch1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void givenInvalidProductMappingStatus_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + expectedProductId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_MAPPING_BETWEEN_THE_STATUS_AND_THE_OPERATIONAL_STATUS_IS_INVALID, ACTIVE, ProductStatusType.CREATED.getValue()), INVALID_INPUT.getStatus());
    }

    @Test
    void givenEmptyProductOrderID_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, "1111", "1111", randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String productOrderItem = "{\"productOrderId\":\"\",\"orderItemId\":\"1111\",\"orderItemAction\":\"change\",\"@referredType\":\"ProductOrder\",\"role\":\"change management order\"}";
        JsonNode valueProductOrderItem = objectMapper.readTree(productOrderItem);
        ProductPatch productPatch = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + expectedProductId + "/productOrderItem/-",
                valueProductOrderItem);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EMPTY_PRODUCT_ORDER_ID_DETECTED, INVALID_INPUT.getStatus());
    }

    @Test
    void givenEmptyOrderItemID_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, "1111", "1111", randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String productOrderItem = "{\"productOrderId\":\"1111\",\"orderItemId\":\"\",\"orderItemAction\":\"change\",\"@referredType\":\"ProductOrder\",\"role\":\"change management order\"}";
        JsonNode valueProductOrderItem = objectMapper.readTree(productOrderItem);
        ProductPatch productPatch = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + expectedProductId + "/productOrderItem/-",
                valueProductOrderItem);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EMPTY_ORDER_ITEM_ID_DETECTED, INVALID_INPUT.getStatus());
    }

    @Test
    void givenValidProductOrderItemNotExist_whenPatch_thenSucceed() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, "1111", "1111", randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        expectedProductEntity.setProductOrderItem(List.of(getRelatedProductOrderItemBuilder("1111", "1111").build(),
                getRelatedProductOrderItemBuilder("2222",
                        "2222").build(), getRelatedProductOrderItemBuilder("3333", "3333").build()));
        List<String> productOrderItems = List.of(
                "{\"productOrderId\":\"2222\",\"orderItemId\":\"2222\",\"orderItemAction\":\"add\",\"@referredType\":\"ProductOrder\",\"role\":\"change " + "management order\"}",
                "{\"productOrderId\":\"3333\",\"orderItemId\":\"3333\",\"orderItemAction\":\"add\",\"@referredType\":\"ProductOrder\",\"role\":\"change management order\"}");
        List<ProductPatch> productPatches = new ArrayList<>();
        for (String item : productOrderItems) {
            JsonNode productOrderItem = objectMapper.readTree(item);
            productPatches.add(createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + expectedProductId + "/productOrderItem/-", productOrderItem));
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productPatches), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, List.of(expectedProductEntity), false);
    }


    @Test
    void givenSameProductWithDifferentRelationship_whenPatch_thenBadRequest() throws Exception {
        String childProductId = ObjectId.get().toString();
        ProductEntity childProduct = mongoTemplate.save(createProductEntityBuilderWithProductSpecification(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(childProductId).build());
        String newProductId = ObjectId.get().toString();
        mongoTemplate.save(createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(newProductId).build());
        String exampleProductId = ObjectId.get().toString();
        mongoTemplate.save(createProductBuilderWithRelation(CREATED, randomAlphabetic(10), randomAlphabetic(10), "AtomicOffering", childProduct.getId(), false, randomAlphabetic(10), PRODUCT.getValue()).description(randomAlphabetic(10)).id(exampleProductId).build());
        List<String> productRelationShips = List.of("{\"relationshipType\":\"sells\",\"product\":{\"id\":\"" + newProductId + "\",\"@type\":\"ProductRef\"}}", "{\"relationshipType\":\"reliesOn\",\"product\":{\"id\":\"" + newProductId + "\",\"@type\":\"ProductRef\"}}");
        List<ProductPatch> productPatches = new ArrayList<>();
        for (String productRelationship : productRelationShips) {
            JsonNode productRelationshipList = objectMapper.readTree(productRelationship);
            productPatches.add(createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + exampleProductId + "/productRelationship/-", productRelationshipList));
        }

        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((productPatches)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), SAME_PRODUCT_WITH_DIFFERENT_RELATIONSHIP_TYPES_IS_NOT_ALLOWED, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductOnRelationWithSameProduct_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String productRelationship = "{\"relationshipType\":\"sells\",\"product\":{\"id\":\"" + expectedProductId + "\",\"@type\":\"ProductRef\"}}";
        JsonNode productRelationshipList = objectMapper.readTree(productRelationship);
        ProductPatch productPatch = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + expectedProductId + "/productRelationship/-", productRelationshipList);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRODUCT_CANNOT_BE_ON_RELATIONSHIP_WITH_THE_SAME_PRODUCT, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductOnRelationNotExists_whenPatch_thenBadRequest() throws Exception {
        String childProductId = ObjectId.get().toString();
        ProductEntity childProduct = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(childProductId).build());
        String expectedProductId = ObjectId.get().toString();
        mongoTemplate.save(createProductBuilderWithRelation(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), "AtomicOffering", childProduct.getId(), false, randomAlphabetic(10), PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build());
        String productRelationship = "{\"relationshipType\":\"reliesOn\",\"product\":{\"id\":\"" + childProductId + "\",\"@type\":\"ProductRef\",\"@type\":\"ProductRef\"}}";
        JsonNode productRelationshipList = objectMapper.readTree(productRelationship);
        ProductPatch productPatch = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + expectedProductId + "/productRelationship/-", productRelationshipList);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THIS_PRODUCT_IS_ALREADY_ON_RELATIONSHIP_WITH_THE_PRODUCT, childProductId), INVALID_INPUT.getStatus());
    }


    @Test
    void givenProductInnerProductRelationShipStatusTerminated_whenPatch_thenBadRequest() throws Exception {
        String childProductId = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        OffsetDateTime startDate = LocalDateTime.parse("2023-11-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);

        ProductEntity.ProductEntityBuilder productEntityBuilder = createProductEntityBuilderWithAtTypeProductSpecification(ACTIVE, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue());

        ProductEntity childProduct = productEntityBuilder.operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                .startDate(startDate)
                .id(childProductId)
                .build();

        childProduct = mongoTemplate.save(childProduct);

        mongoTemplate.save(
                commonProductBuilder(ACTIVE, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                        .startDate(startDate)
                        .id(exampleProductId)
                        .operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                        .productRelationship(
                                List.of(
                                        ProductRelationshipEntity.builder()
                                                .relationshipType(SELLS.getValue())
                                                .product(new ProductRefEntity(childProduct.getId())).build()
                                )
                        )
                        .build()
        );

        String status = "\"Terminated\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatus, productPatchOperationStatus))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_STATUS_CANNOT_BE_TERMINATED, exampleProductId, childProductId), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductInnerProductRelationShipStatusAborted_whenPatch_thenBadRequest() throws Exception {
        String childProductId = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        OffsetDateTime startDate = LocalDateTime.parse("2023-11-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);

        ProductEntity.ProductEntityBuilder productEntityBuilder = createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue());

        ProductEntity childProduct = productEntityBuilder.operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                .startDate(startDate)
                .id(childProductId)
                .build();

        childProduct = mongoTemplate.save(childProduct);

        ProductEntity save = mongoTemplate.save(
                commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                        .startDate(startDate)
                        .id(exampleProductId)
                        .operationalStatus(ProductOperationalStatusType.PENDINGACTIVE)
                        .productRelationship(
                                List.of(
                                        ProductRelationshipEntity.builder()
                                                .relationshipType(SELLS.getValue())
                                                .product(new ProductRefEntity(childProduct.getId())).build()
                                )
                        )
                        .build()
        );

        String status = "\"Aborted\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + save.getId() + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + save.getId() + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatus, productPatchOperationStatus))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_STATUS_CANNOT_BE_TERMINATED, save.getId(), childProductId), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductInnerProductRelationShipStatusCanceled_whenPatch_thenBadRequest() throws Exception {
        String childProductId = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        OffsetDateTime startDate = LocalDateTime.parse("2023-11-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);

        ProductEntity.ProductEntityBuilder productEntityBuilder = createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10),
                randomAlphabetic(10), randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue());

        ProductEntity childProduct = productEntityBuilder.operationalStatus(ProductOperationalStatusType.PENDINGCANCEL)
                .startDate(startDate)
                .id(childProductId)
                .build();

        childProduct = mongoTemplate.save(childProduct);

        ProductEntity save = mongoTemplate.save(
                commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                        .startDate(startDate)
                        .id(exampleProductId)
                        .operationalStatus(ProductOperationalStatusType.PENDINGCANCEL)
                        .productRelationship(
                                List.of(
                                        ProductRelationshipEntity.builder()
                                                .relationshipType(SELLS.getValue())
                                                .product(new ProductRefEntity(childProduct.getId())).build()
                                )
                        )
                        .build()
        );

        String status = "\"Cancelled\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + save.getId() + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + save.getId() + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatus, productPatchOperationStatus))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_STATUS_CANNOT_BE_TERMINATED, save.getId(), childProductId), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductInnerProductRelationShipStatusTerminatedSold_whenPatch_thenSucceed() throws Exception {
        String childProductId = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        OffsetDateTime startDate = LocalDateTime.parse("2023-11-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);

        ProductEntity childPhysicalProduct1 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId).build());

        mongoTemplate.save(commonProductBuilder(ACTIVE, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).startDate(startDate).id(exampleProductId).operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue())
                        .product(new ProductRefEntity(childPhysicalProduct1.getId())).build())).build());

        String statusTerminated = "\"Terminated\"";
        JsonNode valueStatusTerminated = objectMapper.readTree(statusTerminated);
        String statusSold = "\"Sold\"";
        JsonNode valueStatusSold = objectMapper.readTree(statusSold);
        ProductPatch productPatchStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatusTerminated);
        ProductPatch productPatchOperationStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatusTerminated);
        ProductPatch productPatchStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId + "/status", valueStatusSold);
        ProductPatch productPatchOperationStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId + "/operationalStatus", valueStatusSold);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatusParent, productPatchOperationStatusChild, productPatchOperationStatusParent, productPatchStatusChild))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductInnerProductRelationShipStatusTerminated_whenPatch_thenSucceed() throws Exception {
        String childProductId = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        OffsetDateTime startDate = LocalDateTime.parse("2023-11-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);
        ProductEntity childProduct = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE).startDate(startDate).id(childProductId).build());
        mongoTemplate.save(commonProductBuilder(ACTIVE, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).startDate(startDate).id(exampleProductId).operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue())
                        .product(new ProductRefEntity(childProduct.getId())).build())).build());
        String status = "\"Terminated\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatus);
        ProductPatch productPatchStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatusParent, productPatchOperationStatusChild, productPatchOperationStatusParent, productPatchStatusChild))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductCheckStartDate_whenPatch_thenSucceed() throws Exception {
        String productId = "productId";
        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(productId).build());
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus))));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        OffsetDateTime startDate = products.get(0).getStartDate();
        assertThat(startDate)
                .isNotNull()
                .isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    void givenProductStartDateChanged_whenPatchRequest_thenStartDateIsUpdatedOnly() throws Exception {
        String productId = "productId";
        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(productId).build());
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus))));
        resultActions.andExpect(status().isOk());
        List<Product> productsAfterStatusChange = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        OffsetDateTime startDateAfterStatusChange = productsAfterStatusChange.get(0).getStartDate();
        assertThat(startDateAfterStatusChange)
                .isNotNull()
                .isBeforeOrEqualTo(OffsetDateTime.now());

        String newDescription = "\"Updated Product Description\"";
        JsonNode valueName = objectMapper.readTree(newDescription);
        ProductPatch productPatchName = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/description", valueName);
        ResultActions resultActionsName = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody(List.of(productPatchName)));
        resultActionsName.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActionsName)).isEqualTo(HttpStatus.OK.value());
        List<Product> productsAfterNameChange = readJsonFromAPIResponse(resultActionsName, new TypeReference<>() {});
        OffsetDateTime startDateAfterNameChange = productsAfterNameChange.get(0).getStartDate();
        assertThat(startDateAfterNameChange).isEqualTo(startDateAfterStatusChange);
        assertThat(productsAfterNameChange.get(0).getDescription()).isEqualTo("Updated Product Description");
    }

    @Test
    void givenProductCheckValidStartDate_whenPatch_thenSucceed() throws Exception {
        String productId = "productId";
        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(productId).build());
        String status = "\"Active\"";
        String startDateValue = "\"2023-11-20T12:43:16.681925400Z\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        JsonNode newStartDateValue = objectMapper.readTree(startDateValue);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/operationalStatus", valueStatus);
        ProductPatch productPatchStartDate = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/productId/startDate", newStartDateValue);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE,
                APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus, productPatchStartDate))));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductCheckStartDateAlreadyExists_whenPatch_thenBadRequest() throws Exception {
        String productId = "productId";
        OffsetDateTime startDate = LocalDateTime.parse("2023-10-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);
        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).startDate(startDate).id(productId).build());
        String status = "\"Active\"";
        String startDateValue = "\"2023-11-20T12:43:16.681925400Z\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        JsonNode newStartDateValue = objectMapper.readTree(startDateValue);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/operationalStatus", valueStatus);
        ProductPatch productPatchStartDate = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/startDate", newStartDateValue);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE,
                APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus, productPatchStartDate))));
        resultActions.andExpect(status().isMethodNotAllowed());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), "PATCH method not supported by that resource <startDate>", METHOD_NOT_ALLOWED.getStatus());
    }

    @Test
    void givenProductCheckTerminationDate_whenPatch_thenSucceed() throws Exception {
        String productId = "productId";
        OffsetDateTime startDate = LocalDateTime.parse("2023-11-20T09:43:16.681925400").atOffset(ZoneOffset.UTC);
        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.ACTIVE).startDate(startDate).id(productId).build());
        String status = "\"Terminated\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus))));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductCheckInvalidTerminationDate_whenPatch_thenBadRequest() throws Exception {
        String productId = "productId";
        OffsetDateTime terminationDate = OffsetDateTime.now();
        OffsetDateTime startDate = terminationDate.plusDays(5);
        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.ACTIVE).startDate(startDate).id(productId).build());
        String status = "\"Terminated\"";
        String terminationDateValue = "\"" + terminationDate + "\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        JsonNode valueTerminationDate = objectMapper.readTree(terminationDateValue);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/productId/operationalStatus", valueStatus);
        ProductPatch productPatchTerminationDate = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/productId/terminationDate", valueTerminationDate);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE,
                APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus, productPatchTerminationDate))));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), INVALID_TERMINATION_DATE, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductCheckInvalidId_whenPatch_thenBadRequest() throws Exception {
        String invalidProductId = "invalidProductId";
        OffsetDateTime terminationDate = OffsetDateTime.now();
        String status = "\"Terminated\"";
        String terminationDateValue = "\"" + terminationDate + "\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        JsonNode valueTerminationDate = objectMapper.readTree(terminationDateValue);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + invalidProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + invalidProductId + "/operationalStatus", valueStatus);
        ProductPatch productPatchTerminationDate = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + invalidProductId + "/terminationDate", valueTerminationDate);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE,
                APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatchStatus, productPatchOperationStatus, productPatchTerminationDate))));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_PRODUCTS_WITH_IDS_S_DOES_NOT_EXIST, List.of(invalidProductId)), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductInnerProductRelationShipStatusActive_whenPatch_thenBadRequest() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId2).build());

        mongoTemplate.save(
                commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                        .id(exampleProductId)
                        .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                        .productRelationship(
                                List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct1.getId())).build(),
                                        ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct2.getId())).build())
                        )
                        .build()
        );
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatus, productPatchOperationStatus))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_STATUS_CANNOT_BE_ACTIVE, exampleProductId), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductInnerProductRelationShipStatusActiveSold_whenPatch_thenSucceed() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();

        ProductEntity childPhysicalProduct1 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId2).build());

        mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).id(exampleProductId).operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue())
                        .product(new ProductRefEntity(childPhysicalProduct1.getId())).build(), ProductRelationshipEntity.builder().relationshipType(SELLS.getValue())
                        .product(new ProductRefEntity(childProduct2.getId())).build())).build());

        String activeStatus = "\"Active\"";
        JsonNode valueActiveStatus = objectMapper.readTree(activeStatus);
        String soldStatus = "\"Sold\"";
        JsonNode valueSoldStatus = objectMapper.readTree(soldStatus);
        ProductPatch productPatchStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueActiveStatus);
        ProductPatch productPatchOperationStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueActiveStatus);
        ProductPatch productPatchStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueSoldStatus);
        ProductPatch productPatchOperationStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus", valueSoldStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatusParent, productPatchOperationStatusChild, productPatchOperationStatusParent, productPatchStatusChild))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductInnerProductRelationShipStatusActive_whenPatch_thenSucceed() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId2).build());

        mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).id(exampleProductId).operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue())
                        .product(new ProductRefEntity(childProduct1.getId())).build(), ProductRelationshipEntity.builder().relationshipType(SELLS.getValue())
                        .product(new ProductRefEntity(childProduct2.getId())).build())).build());

        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatusParent = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatus);
        ProductPatch productPatchStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatusParent, productPatchOperationStatusChild, productPatchOperationStatusParent, productPatchStatusChild))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenValidProductWithCharacteristics_whenPatch_thenSucceed() throws Exception {
        CharacteristicEntity characteristicEntity = CharacteristicEntity
                .builder().id("id").valueType("string").atType("StringCharacteristic").build();
        String exampleProductId = ObjectId.get().toString();
        ProductEntity productEntity = createProductEntityWithCharacteristics(List.of(characteristicEntity), PRODUCT.getValue()).id(exampleProductId).build();
        mongoTemplate.save(productEntity);
        String basePath = "/productInventoryManagement/v1/product/" + exampleProductId + "/productCharacteristic/0";
        List<ProductPatch> patches = List.of(
                createProductPatchBuilder("replace", basePath + "/valueType", objectMapper.readTree("\"boolean\"")),
                createProductPatchBuilder("add", basePath + "/value", objectMapper.readTree("false")),
                createProductPatchBuilder("replace", basePath + "/@type", objectMapper.readTree("\"BooleanCharacteristic\""))
        );

        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(patches), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));

        resultActions.andExpect(status().isOk());
        ProductEntity savedProduct = mongoTemplate.findById(productEntity.getId(), ProductEntity.class);
        if (Objects.nonNull(savedProduct) && Objects.nonNull(savedProduct.getProductCharacteristic()) && !savedProduct.getProductCharacteristic().isEmpty()) {
            assertEquals(false, savedProduct.getProductCharacteristic().get(0).getValue());
        } else {
            fail("Invalid test data: Either ProductEntity is null, or ProductCharacteristics is null or empty");
        }
    }

    @Test
    void givenInvalidFieldProductFormat_whenPatch_thenBadRequest() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(expectedProductEntity);
        String operationalStatus = "\"PendingActive\"";
        JsonNode valueOperationalStatus = objectMapper.readTree(operationalStatus);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationaltatus", valueOperationalStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), ONE_OR_MULTIPLE_ISSUES_ARE_EXIST_IN_THE_PATH_OR_THE_VALUE_FIELDS, INVALID_INPUT.getStatus());
    }

    @Test
    void givenInvalidOperationProduct_whenPatch_thenBadRequest() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(expectedProductEntity);
        JsonNode patchWithInvalidOperation = objectMapper.readTree(String.format("""
                [{
                  "op": "replacee",
                  "path": "/productInventoryManagement/v1/product/%s/operationalStatus",
                  "value": "PendingActive"
                }]
                """, productId));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody(patchWithInvalidOperation));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), "Invalid '[0].op' Field", INVALID_INPUT.getStatus());
    }

    @Test
    void givenInvalidProductStatusSold_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String status = "\"Sold\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + expectedProductId + "/operationalStatus", valueStatus);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + expectedProductId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchOperationStatus, productPatch1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_STATUS_CANNOT_BE_MODIFIED, CREATED, SOLD), INVALID_INPUT.getStatus());
    }

    @Test
    void givenPhysicalProductInnerProductRelationShipStatusSold_whenPatch_thenBadRequest() throws Exception {
        String childProductId1 = ObjectId.get().toHexString();
        String childProductId2 = ObjectId.get().toHexString();
        String exampleProductId = ObjectId.get().toHexString();
        ProductEntity childProduct1 = mongoTemplate.save(createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId1).build());
        ProductEntity childProduct2 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId2).build());
        ProductEntity product = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PHYSICAL_PRODUCT.getValue())
                .id(exampleProductId)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct1.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct2.getId())).build())).build();

        mongoTemplate.save(product);
        String status = "\"Sold\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatus, productPatchOperationStatus))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_STATUS_CANNOT_BE_SOLD, exampleProductId), INVALID_INPUT.getStatus());
    }

    @Test
    void givenPhysicalProductInnerProductRelationShipStatusSold_whenPatch_thenSucceed() throws Exception {
        String childPhysicalProductId1 = ObjectId.get().toString();
        String childPhysicalProductId2 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        ProductEntity childPhysicalProduct1 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childPhysicalProductId1).build());
        ProductEntity childPhysicalProduct2 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childPhysicalProductId2).build());
        ProductEntity product = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, ProductTypeEnum.PHYSICAL_PRODUCT.getValue())
                .id(exampleProductId)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childPhysicalProduct1.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childPhysicalProduct2.getId())).build())).build();

        mongoTemplate.save(product);
        String status = "\"Sold\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch physicalProductPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch physicalProductPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus", valueStatus);
        ProductPatch physicalProductPatchStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childPhysicalProductId1 + "/status", valueStatus);
        ProductPatch physicalProductPatchOperationStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childPhysicalProductId1 + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(physicalProductPatchStatus, physicalProductPatchStatusChild, physicalProductPatchOperationStatus, physicalProductPatchOperationStatusChild))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        for (Product p : products) {
            Assertions.assertThat(p.getAtType()).isEqualTo(PHYSICAL_PRODUCT.getValue());
        }
    }

    @Test
    void givenProductInnerProductReliesOnRelationShipStatusActive_whenPatch_thenSucceed() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String childProductId3 = "childProductId3";

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId2).build());

        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId3)
                .productRelationship(List.of(
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct1.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct2.getId())).build()))
                .build());

        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId3 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId3 + "/operationalStatus", valueStatus);
        ProductPatch productPatchStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus", valueStatus);
        ProductPatch productPatchStatusChild2 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId2 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild2 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId2 + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatusChild3, productPatchOperationStatusChild3, productPatchStatusChild2, productPatchOperationStatusChild2, productPatchStatusChild1, productPatchOperationStatusChild1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductInnerProductReliesOnPartialRelationShipStatusActive_whenPatch_thenOK() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String childProductId3 = "childProductId3";

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId2).build());

        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId3)
                .productRelationship(List.of(
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct1.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct2.getId())).build()))
                .build());

        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/childProductId3/status", valueStatus);
        ProductPatch productPatchOperationStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/childProductId3/operationalStatus", valueStatus);
        ProductPatch productPatchStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchStatusChild3, productPatchOperationStatusChild3, productPatchStatusChild1, productPatchOperationStatusChild1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void givenValidProductId_whenPatchDifferentId_thenMethodNotAllowed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(productEntity);
        String anyId = "\"ANY_ID\"";
        JsonNode anyIdNode = objectMapper.readTree(anyId);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/id", anyIdNode);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isMethodNotAllowed());
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), String.format(PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE, "id"), METHOD_NOT_ALLOWED.getStatus());

    }

    @Test
    void givenValidProductId_whenPatchDifferentCreationDate_thenMethodNotAllowed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).creationDate(LocalDateTime.now().atOffset(ZoneOffset.UTC)).build();
        mongoTemplate.save(productEntity);
        String anyDate = "\"" + LocalDateTime.now().minusDays(1).atOffset(ZoneOffset.UTC) + "\"";
        JsonNode anyDateNode = objectMapper.readTree(anyDate);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/creationDate", anyDateNode);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isMethodNotAllowed());
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), String.format(PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE, "creationDate"), METHOD_NOT_ALLOWED.getStatus());
    }

    @Test
    void givenValidProductId_whenPatchDifferentAtType_thenMethodNotAllowed() throws Exception {
        String productId = "productId";

        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).atType(ProductTypeEnum.PRODUCT.getValue()).build();
        mongoTemplate.save(productEntity);
        String differentType = "\"PhysicalProduct\"";
        JsonNode differentTypeNode = objectMapper.readTree(differentType);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/@type", differentTypeNode);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isMethodNotAllowed());
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), String.format(PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE, "@type"), METHOD_NOT_ALLOWED.getStatus());

    }

    @Test
    void givenValidProductId_whenPatchDifferentHref_thenMethodNotAllowed() throws Exception {
        String productId = "productId";

        ProductEntity productEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).href("VALUE").build();
        mongoTemplate.save(productEntity);
        String anyValue = "\"ANY_VALUE\"";
        JsonNode anyValueNode = objectMapper.readTree(anyValue);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/href", anyValueNode);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isMethodNotAllowed());
        assertMethodNotAllowedErrorExists(error, METHOD_NOT_ALLOWED.getCode(), String.format(PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE, "href"), METHOD_NOT_ALLOWED.getStatus());
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
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueStatus);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchOperationStatus, productPatch1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(products.get(0).getTerminationDate()).isEqualTo(products.get(0).getStartDate().plusDays(value.getValue()));
        consumeAndAssertEqualityForAttributeChangeEvent(new HashSet<>(products));

    }

    @Test
    void givenProductWithValidityCharacteristics_whenPatchStatusToActive_thenSucceedAndTerminationDateIsSetByValidUntil() throws Exception {
        OffsetDateTime validTo = OffsetDateTime.parse("2024-11-20T09:43:16Z");
        Value value = Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).validTo(validTo).build();
        CharacteristicEntity characteristicEntity = CharacteristicEntity.builder().id("id").valueType("string").atType("ValidityCharacteristic").value(value).build();
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).productCharacteristic(List.of(characteristicEntity)).build();
        mongoTemplate.save(expectedProductEntity);
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);
        mongoTemplate.save(expectedProductEntity);
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueStatus);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatchOperationStatus, productPatch1))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(products.get(0).getTerminationDate()).isEqualTo(validTo);
        consumeAndAssertEqualityForAttributeChangeEvent(new HashSet<>(products));

    }

    ProductEntity createPhysicalProduct(String childPhysicalProductId1, String physicalProductAtomicId) {
        ProductEntity childPhysicalProduct1 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childPhysicalProductId1).build());
        ProductEntity physicalAtomicProduct = mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PHYSICAL_PRODUCT.getValue())
                .id(physicalProductAtomicId)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(
                        List.of(
                                ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childPhysicalProduct1.getId())).build()
                        )).build());
        ProductEntity physicalProductBundle = mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                .id(ObjectId.get().toString())
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(
                        List.of(
                                ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(physicalAtomicProduct.getId())).build()
                        )).build());
        ProductEntity parentContract = mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                .id(ObjectId.get().toString())
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(
                        List.of(
                                ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(physicalProductBundle.getId())).build()
                        )).build());
        mongoTemplate.save(parentContract);

        physicalProductBundle.setProductRelationship(new ArrayList<>(physicalProductBundle.getProductRelationship()));
        childPhysicalProduct1.setProductRelationship(new ArrayList<>());
        physicalAtomicProduct.setProductRelationship(new ArrayList<>(physicalAtomicProduct.getProductRelationship()));

        physicalProductBundle.getProductRelationship().add(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(new ProductRefEntity(parentContract.getId())).build());
        childPhysicalProduct1.getProductRelationship().add(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(new ProductRefEntity(parentContract.getId())).build());
        physicalAtomicProduct.getProductRelationship().add(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(new ProductRefEntity(parentContract.getId())).build());
        mongoTemplate.save(physicalProductBundle);
        mongoTemplate.save(childPhysicalProduct1);
        mongoTemplate.save(physicalAtomicProduct);
        return physicalAtomicProduct;
    }

    void createShipmentProduct(ProductEntity physicalAtomicProduct, ObjectId physicalSpecId, String childShipmentProductId1, String shipmentProductAtomicId) {
        ProductEntity childShipmentProduct1 = mongoTemplate.save(
                ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), "ProductSpecificationRef", false, SHIPMENT_PRODUCT.getValue())
                        .operationalStatus(ProductOperationalStatusType.CREATED)
                        .id(childShipmentProductId1)
                        .build());
        ProductEntity shipmentAtomicProduct = mongoTemplate.save(
                commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, SHIPMENT_PRODUCT.getValue())
                        .id(shipmentProductAtomicId)
                        .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                        .productRelationship(
                                List.of(
                                        ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childShipmentProduct1.getId())).build(),
                                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(physicalAtomicProduct.getId())).build()
                                )).build());
        ProductEntity shipmentBundle = mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                .id(ObjectId.get().toString())
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(
                        List.of(
                                ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(shipmentAtomicProduct.getId())).build()
                        )).build());
        ProductEntity shipmentParentContract = mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                .id(ObjectId.get().toString())
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(
                        List.of(
                                ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(shipmentBundle.getId())).build()
                        )).build());
        shipmentParentContract = mongoTemplate.save(shipmentParentContract);
        childShipmentProduct1.setProductRelationship(new ArrayList<>());
        shipmentAtomicProduct.setProductRelationship(new ArrayList<>(shipmentAtomicProduct.getProductRelationship()));
        childShipmentProduct1.getProductRelationship().add(
                ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(new ProductRefEntity(shipmentParentContract.getId())).build()
        );
        childShipmentProduct1.getProductRelationship().add(
                ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(physicalSpecId)).build()
        );

        shipmentAtomicProduct.getProductRelationship().add(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(new ProductRefEntity(shipmentParentContract.getId())).build());
        shipmentBundle.setProductRelationship(new ArrayList<>(shipmentBundle.getProductRelationship()));
        shipmentBundle.getProductRelationship().add(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(new ProductRefEntity(shipmentParentContract.getId())).build());
        mongoTemplate.save(childShipmentProduct1);
        mongoTemplate.save(shipmentAtomicProduct);
        mongoTemplate.save(shipmentBundle);
    }

    @Test
    void givenProductsWithDifferentParents_whenPatchStatusToSold_thenPatched() throws Exception {
        String childPhysicalProductId1 = ObjectId.get().toString();
        String physicalProductAtomicId = ObjectId.get().toString();
        String childShipmentProductId1 = ObjectId.get().toString();
        String shipmentProductAtomicId = ObjectId.get().toString();
        ProductEntity physicalProduct = createPhysicalProduct(childPhysicalProductId1, physicalProductAtomicId);
        ObjectId physicalSpecId = physicalProduct
                .getProductRelationship()
                .stream()
                .filter(productRelationshipEntity -> SELLS.getValue().equals(productRelationshipEntity.getRelationshipType()))
                .map(productRelationshipEntity -> productRelationshipEntity.getProduct().getId())
                .findAny().orElseThrow();

        createShipmentProduct(physicalProduct, physicalSpecId, childShipmentProductId1, shipmentProductAtomicId);
        String statusSold = "\"Sold\"";
        JsonNode valueStatusSold = objectMapper.readTree(statusSold);
        List<ProductPatch> productPatchBuilder = List.of(
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childPhysicalProductId1 + "/status", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childShipmentProductId1 + "/status", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + physicalProductAtomicId + "/status", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + shipmentProductAtomicId + "/status", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childPhysicalProductId1 + "/operationalStatus", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childShipmentProductId1 + "/operationalStatus", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + physicalProductAtomicId + "/operationalStatus", valueStatusSold),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + shipmentProductAtomicId + "/operationalStatus", valueStatusSold));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productPatchBuilder), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void givenEmptyBillingAccountID_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, "1111", "1111", randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String billingAccount = "{\"id\":\"\"}";
        JsonNode valueProductOrderItem = objectMapper.readTree(billingAccount);
        ProductPatch productPatch = createProductPatchBuilder("add", "/productInventoryManagement/v1/product/" + expectedProductId + "/billingAccount",
                valueProductOrderItem);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(productPatch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), getNotEmptyMessage("billingAccount.id"), INVALID_INPUT.getStatus());
    }

    @Test
    void givenValidProductStatusActiveToPendingMigrate_whenPatch_thenSucceed() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        expectedProductEntity.setStatus(ProductStatusType.ACTIVE);
        mongoTemplate.save(expectedProductEntity);
        String operationalStatus = "\"PendingMigrate\"";
        JsonNode valueOperationalStatus = objectMapper.readTree(operationalStatus);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueOperationalStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenValidProductStatusActiveToLockedActive_whenPatch_thenSucceed() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.LOCKEDACTIVE);
        expectedProductEntity.setStatus(ProductStatusType.ACTIVE);
        mongoTemplate.save(expectedProductEntity);
        String operationalStatus = "\"LockedActive\"";
        JsonNode valueOperationalStatus = objectMapper.readTree(operationalStatus);
        ProductPatch productPatch = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueOperationalStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    void givenProductRemoveProductRelationShip_whenPatch_thenSucceed() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(childProductId2).build());

        mongoTemplate.save(
                commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                        .id(exampleProductId)
                        .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                        .productRelationship(
                                List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct1.getId())).build(),
                                        ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct2.getId())).build())
                        )
                        .build()
        );

        ProductPatch childProduct1Patch = createProductPatchBuilderForRemove("remove", "/productInventoryManagement/v1/product/" + exampleProductId + "/productRelationship/1");
        ProductPatch childProduct2Patch = createProductPatchBuilderForRemove("remove", "/productInventoryManagement/v1/product/" + exampleProductId + "/productRelationship/0");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody((List.of(childProduct1Patch, childProduct2Patch))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    void givenInValidProductOperationalRequiredValue_whenPatch_thenBadRequest() throws Exception {
        String productId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        mongoTemplate.save(expectedProductEntity);
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.CONFIRMED);

        ProductPatch productPatch = createProductPatchBuilderForRemove("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody((List.of(productPatch))));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(VALUE_REQUIRED_FOR_NON_REMOVE_OPERATION, "replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus"), INVALID_INPUT.getStatus());
    }


    @Test
    void givenMultipleRelationshipUpdatesAndRemovals_whenPatch_thenShouldUpdateAndRemoveCorrectly() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String childProductId3 = ObjectId.get().toString();
        String childProductId4 = ObjectId.get().toString();
        String childProductId5 = ObjectId.get().toString();
        String parentProductId = "ParentProduct";
        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId2).build());
        ProductEntity childProduct3 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId3).build());

        ProductEntity childProduct4 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId4).build());
        ProductEntity childProduct5 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId5).build());

        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(parentProductId)
                .productRelationship(List.of(
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(childProduct1.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(childProduct2.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct3.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue()).product(new ProductRefEntity(childProduct4.getId())).build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct5.getId())).build()))
                .build());

        String nameRelationship = "\"ReliesFrom\"";
        JsonNode valueNameRelationship = objectMapper.readTree(nameRelationship);
        List<ProductPatch> productPatchBuilder = List.of(
                createProductPatchBuilderForRemove("remove", "/productInventoryManagement/v1/product/" + parentProductId + "/productRelationship/3"),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + parentProductId + "/productRelationship/2/relationshipType", valueNameRelationship),
                createProductPatchBuilderForRemove("remove", "/productInventoryManagement/v1/product/" + parentProductId + "/productRelationship/1"),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + parentProductId + "/productRelationship/4/relationshipType", valueNameRelationship));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productPatchBuilder), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        ProductEntity patchedProduct = mongoTemplate.findById(parentProductId, ProductEntity.class);
        assertThat(patchedProduct).isNotNull();
        assertThat(patchedProduct.getProductRelationship()).hasSize(3);
        List<String> updatedRelationshipTypes = patchedProduct.getProductRelationship().stream()
                .map(ProductRelationshipEntity::getRelationshipType)
                .toList();
        assertThat(updatedRelationshipTypes).containsExactlyInAnyOrder(
                ProductRelationshipType.BUNDLES.getValue(),
                "ReliesFrom",
                "ReliesFrom"
        );
    }

    @Test
    void givenValidStatusLocked_whenPatchToActive_thenSucceed() throws Exception {
        String productId = "productId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(productId).build();
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.LOCKED);
        mongoTemplate.save(expectedProductEntity);
        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueStatus);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(productPatchOperationStatus, productPatchStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void givenProductWithRecurringPrice_whenTerminated_thenRecurringPriceEndDateIsSet() throws Exception {

        OffsetDateTime startDate = OffsetDateTime.now().minusDays(10);
        String productId = ObjectId.get().toString();
        ProductEntity productEntity = createProductEntityBuilderWithAtTypeProductSpecification(ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).id(productId).startDate(startDate)
                        .productPrice(List.of(ProductPriceEntity.builder().atType("ProductPrice").price(PriceEntity.builder().taxRate(0.0F).build())
                                        .priceType("recurringCharge").recurringChargePeriod(MeasuredValue.builder().amount(10F).units("day").build())
                                        .validFor(TimePeriodEntity.builder().startDateTime(startDate).endDateTime(null).build())
                                        .build())).build();

        productEntity.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        mongoTemplate.save(productEntity);
        JsonNode statusValue = objectMapper.readTree("\"Terminated\"");
        ProductPatch patchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", statusValue);
        ProductPatch patchOpStatus = createProductPatchBuilder("replace",  "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", statusValue);
        ResultActions result = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT,  contentBody(List.of(patchStatus, patchOpStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        result.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(result)).isEqualTo(HttpStatus.OK.value());
        ProductEntity updated = mongoTemplate.findById(productId, ProductEntity.class);
        assertThat(updated).isNotNull();
        assertThat(updated.getTerminationDate()).isNotNull();
        ProductPriceEntity price = updated.getProductPrice().get(0);
        assertThat(price.getValidFor().getEndDateTime()).isNotNull().isEqualTo(updated.getTerminationDate());
    }

    @Test
    void givenParentAndChildProductsWithMultiplePrices_whenTerminated_thenOnlyRecurringPricesAreEnded() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.now().minusDays(5);
        String childProductId = ObjectId.get().toString();
        ProductEntity childProduct = createProductEntityBuilderWithAtTypeProductSpecification(ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue())
                        .id(childProductId).startDate(startDate).operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                        .productPrice(List.of(ProductPriceEntity.builder().atType("ProductPrice").priceType("recurringCharge").price(PriceEntity.builder().taxRate(0.0F).build())
                                        .recurringChargePeriod(MeasuredValue.builder().amount(5F).units("day").build())
                                        .validFor(TimePeriodEntity.builder().startDateTime(startDate).build()).build(),
                                ProductPriceEntity.builder().atType("ProductPrice").priceType("oneTime").price(PriceEntity.builder().taxRate(0.0F).build())
                                        .validFor(TimePeriodEntity.builder().startDateTime(startDate).build())
                                        .build()
                        ))
                        .build();

        mongoTemplate.save(childProduct);
        String parentProductId = ObjectId.get().toString();
        ProductEntity parentProduct = commonProductBuilder(ACTIVE, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).id(parentProductId).startDate(startDate).operationalStatus(ProductOperationalStatusType.PENDINGTERMINATE)
                        .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProductId)).build()))
                        .productPrice(List.of(ProductPriceEntity.builder().atType("ProductPrice").priceType("recurringCharge").price(PriceEntity.builder().taxRate(0.0F).build())
                                        .recurringChargePeriod(MeasuredValue.builder().amount(10F).units("month").build())
                                        .validFor(TimePeriodEntity.builder().startDateTime(startDate).build()).build(),
                                ProductPriceEntity.builder().atType("ProductPrice").priceType("usage").price(PriceEntity.builder().taxRate(0.0F).build())
                                        .validFor(TimePeriodEntity.builder().startDateTime(startDate).build()).build()))
                        .build();

        mongoTemplate.save(parentProduct);
       JsonNode terminated = objectMapper.readTree("\"Terminated\"");
        List<ProductPatch> patches = List.of(
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + parentProductId + "/status", terminated),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId + "/status", terminated),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + parentProductId + "/operationalStatus", terminated),
                createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId + "/operationalStatus", terminated)
        );

        ResultActions result = callRestfulEndpoint(mockMvc,  PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(patches), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        result.andExpect(status().isOk());
        ProductEntity parentUpdated = mongoTemplate.findById(parentProductId, ProductEntity.class);
        assertThat(parentUpdated.getTerminationDate()).isNotNull();
        OffsetDateTime terminationDateParent = parentUpdated.getTerminationDate();
        ProductPriceEntity parentRecurring = parentUpdated.getProductPrice().stream().filter(p -> "recurringCharge".equalsIgnoreCase(p.getPriceType())).findFirst().orElseThrow();
        assertThat(parentRecurring.getValidFor().getEndDateTime()).isEqualTo(terminationDateParent);
        ProductPriceEntity parentUsage = parentUpdated.getProductPrice().stream().filter(p -> "usage".equalsIgnoreCase(p.getPriceType())).findFirst().orElseThrow();
        assertThat(parentUsage.getValidFor().getEndDateTime()).isNull();
        ProductEntity childUpdated = mongoTemplate.findById(childProductId, ProductEntity.class);
        assertThat(childUpdated.getTerminationDate()).isNotNull();
        OffsetDateTime terminationDateChild = childUpdated.getTerminationDate();
        ProductPriceEntity childRecurring = childUpdated.getProductPrice().stream().filter(p -> "recurringCharge".equalsIgnoreCase(p.getPriceType())).findFirst().orElseThrow();
        assertThat(childRecurring.getValidFor().getEndDateTime()).isEqualTo(terminationDateChild);
        ProductPriceEntity childOneTime = childUpdated.getProductPrice().stream().filter(p -> "oneTime".equalsIgnoreCase(p.getPriceType())).findFirst().orElseThrow();
        assertThat(childOneTime.getValidFor().getEndDateTime()).isNull();
    }

    @Test
    void givenProductWithRecurringPriceAndNullValidFor_whenTerminated_thenValidForIsCreatedAndEndDateIsSet() throws Exception {

        OffsetDateTime startDate = OffsetDateTime.now().minusDays(10);
        String productId = ObjectId.get().toString();
        ProductEntity productEntity =
                createProductEntityBuilderWithAtTypeProductSpecification(
                        ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10),
                        "ProductSpecificationRef", false, PRODUCT.getValue())
                        .id(productId)
                        .startDate(startDate)
                        .productPrice(List.of(
                                ProductPriceEntity.builder().atType("ProductPrice")
                                        .priceType("recurringCharge")
                                        .price(PriceEntity.builder().taxRate(0.0F).build())
                                        .recurringChargePeriod(MeasuredValue.builder()
                                                .amount(10F)
                                                .units("day")
                                                .build())
                                        .validFor(null)
                                        .build()
                        ))
                        .build();

        productEntity.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        mongoTemplate.save(productEntity);
        JsonNode statusValue = objectMapper.readTree("\"Terminated\"");
        ProductPatch patchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", statusValue);
        ProductPatch patchOpStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", statusValue);
        ResultActions result = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(patchStatus, patchOpStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        result.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(result)).isEqualTo(HttpStatus.OK.value());
        ProductEntity updated = mongoTemplate.findById(productId, ProductEntity.class);
        assertThat(updated).isNotNull();
        assertThat(updated.getTerminationDate()).isNotNull();
        ProductPriceEntity updatedPrice = updated.getProductPrice().get(0);
        assertThat(updatedPrice.getValidFor()).isNotNull();
        assertThat(updatedPrice.getValidFor().getEndDateTime()).isNotNull().isEqualTo(updated.getTerminationDate());
    }

    @Test
    void givenValidProduct_whenAddProductPriceByPatch_thenSucceed() throws Exception {
        String productId = "productId";
        ProductEntity productEntity = createProductEntityWithCharacteristics(List.of(), PRODUCT.getValue()).id(productId).productPrice(null).build();
        mongoTemplate.save(productEntity);
        List<ProductPatch> patches = List.of(createProductPatchBuilder(
                        "add", "/productInventoryManagement/v1/product/productId/productPrice/-",
                        objectMapper.readTree("""
                    {
                      "@type": "ProductPrice",
                      "name": "15 Euro RC",
                      "priceType": "recurring",
                      "recurringChargePeriod": {
                        "amount": 30,
                        "units": "day"
                      },
                      "productOfferingPrice": {
                        "id": "9a43a57c-1cd6-476e-9584-660c764a8439",
                        "name": "15 Euro RC",
                        "@type": "ProductOfferingPriceRef"
                      },
                      "price": {
                        "dutyFreeAmount": {
                          "unit": "EUR",
                          "value": 15.0
                        },
                        "@type": "Price"
                      }
                    }
                    """)
                )
        );

        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(patches), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        ProductEntity savedProduct = mongoTemplate.findById(productId, ProductEntity.class);
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getProductPrice());
        assertEquals(1, savedProduct.getProductPrice().size());
        ProductPriceEntity price = savedProduct.getProductPrice().get(0);
        assertEquals("ProductPrice", price.getAtType());
        assertEquals("15 Euro RC", price.getName());
        assertEquals("recurring", price.getPriceType());
        assertEquals("EUR", price.getPrice().getDutyFreeAmount().getUnit());
    }

}
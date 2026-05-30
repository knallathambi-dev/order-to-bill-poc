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
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductPatch;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.creator.ProductEntityCreator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.APPLICATION_JSON_PATCH_PATCH;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT;
import static com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType.PENDINGDELIVERY;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.SELLS;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PHYSICAL_PRODUCT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.PRODUCT_STATUS_CANNOT_BE_SOLD;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_STATUS_CANNOT_BE_MODIFIED;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.*;
import static com.orange.discobole.productinventory.util.creator.ProductPatchCreator.createProductPatchBuilder;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatchTangibleProductApiTest extends AbstractTest {

    @Test
    void givenInvalidProductStatusSold_whenPatch_thenBadRequest() throws Exception {
        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);
        String status = "\"Sold\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + expectedProductId + "/operationalStatus", valueStatus);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + expectedProductId + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(productPatchOperationStatus, productPatch1)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_STATUS_CANNOT_BE_MODIFIED, CREATED, SOLD), INVALID_INPUT.getStatus());
    }

    @Test
    void givenPhysicalProductInnerProductRelationShipStatusSold_whenPatch_thenBadRequest() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        ProductEntity childProduct1 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
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
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(productPatchStatus, productPatchOperationStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
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
                .productRelationship(
                        List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childPhysicalProduct1.getId())).build(),
                                ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childPhysicalProduct2.getId())).build()))
                .build();

        mongoTemplate.save(product);
        String status = "\"Sold\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch physicalProductPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/status", valueStatus);
        ProductPatch physicalProductPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + exampleProductId + "/operationalStatus",
                valueStatus);
        ProductPatch physicalProductPatchStatusChild = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childPhysicalProductId1 + "/status", valueStatus);
        ProductPatch physicalProductPatchOperationStatusChild = createProductPatchBuilder("replace",
                "/productInventoryManagement/v1/product/" + childPhysicalProductId1 + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(physicalProductPatchStatus, physicalProductPatchStatusChild, physicalProductPatchOperationStatus, physicalProductPatchOperationStatusChild)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        OffsetDateTime startDate = products.get(0).getStartDate();
        assertThat(startDate).isNotNull();
        assertThat(startDate.toLocalDate()).isEqualTo(OffsetDateTime.now(ZoneOffset.UTC).toLocalDate());
    }

    @Test
    void givenPhysicalProductStatusChangedToSold_whenPatchRequest_thenStartDateIsUpdatedOnly() throws Exception {

        String productId = ObjectId.get().toString();
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(productId).build());
        String status = "\"Sold\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/status", valueStatus);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/operationalStatus", valueStatus);
        ResultActions resultActionsStatus = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody(List.of(productPatchStatus, productPatchOperationStatus)));
        resultActionsStatus.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActionsStatus)).isEqualTo(HttpStatus.OK.value());
        List<Product> productsAfterStatusChange = readJsonFromAPIResponse(resultActionsStatus, new TypeReference<>() {});
        OffsetDateTime startDateAfterStatusChange = productsAfterStatusChange.get(0).getStartDate();
        assertThat(startDateAfterStatusChange)
                .isNotNull()
                .isBeforeOrEqualTo(OffsetDateTime.now());
        String newDescription = "\"Updated Product Description\"";
        JsonNode valueDescription = objectMapper.readTree(newDescription);
        ProductPatch productPatchDescription = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + productId + "/description", valueDescription);
        ResultActions resultActionsDescription = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH), contentBody(List.of(productPatchDescription)));
        resultActionsDescription.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActionsDescription)).isEqualTo(HttpStatus.OK.value());

        List<Product> productsAfterDescriptionChange = readJsonFromAPIResponse(resultActionsDescription, new TypeReference<>() {});
        OffsetDateTime startDateAfterDescriptionChange = productsAfterDescriptionChange.get(0).getStartDate();
        assertThat(startDateAfterDescriptionChange).isEqualTo(startDateAfterStatusChange);
        assertThat(productsAfterDescriptionChange.get(0).getDescription()).isEqualTo("Updated Product Description");
    }

    @Test
    void givenProductInnerProductReliesOnRelationShipStatusActive_whenPatch_thenSucceed() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String childProductId3 = ObjectId.get().toString();

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId2).build());

        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId3)
                .productRelationship(List.of(
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct1.getId()))
                                .build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct2.getId()))
                                .build()))
                .build());

        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId3 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId3 + "/operationalStatus",
                valueStatus);
        ProductPatch productPatchStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus",
                valueStatus);
        ProductPatch productPatchStatusChild2 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId2 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild2 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId2 + "/operationalStatus",
                valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(productPatchStatusChild3, productPatchOperationStatusChild3, productPatchStatusChild2, productPatchOperationStatusChild2, productPatchStatusChild1, productPatchOperationStatusChild1)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenProductInnerProductReliesOnPartialRelationShipStatusActive_whenPatch_thenOK() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String childProductId2 = ObjectId.get().toString();
        String childProductId3 = ObjectId.get().toString();

        ProductEntity childProduct1 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());

        ProductEntity childProduct2 = mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId2).build());

        mongoTemplate.save(createProductEntityBuilderWithAtTypeProductSpecification(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId3)
                .productRelationship(List.of(
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct1.getId()))
                                .build(),
                        ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.RELIESON.getValue()).product(new ProductRefEntity(childProduct2.getId()))
                                .build()))
                .build());

        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId3 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild3 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId3 + "/operationalStatus",
                valueStatus);
        ProductPatch productPatchStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueStatus);
        ProductPatch productPatchOperationStatusChild1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus",
                valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(productPatchStatusChild3, productPatchOperationStatusChild3, productPatchStatusChild1, productPatchOperationStatusChild1)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void givenInvalidProductStatus_whenPatch_thenBadRequest() throws Exception {
        String childProductId1 = ObjectId.get().toString();
        String exampleProductId = ObjectId.get().toString();
        ProductEntity childProduct1 = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(childProductId1).build());
        ProductEntity product = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PHYSICAL_PRODUCT.getValue())
                .id(exampleProductId)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(SELLS.getValue()).product(new ProductRefEntity(childProduct1.getId())).build()))
                .build();
        mongoTemplate.save(product);

        String status = "\"Active\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch productPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/operationalStatus", valueStatus);
        ProductPatch productPatch1 = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + childProductId1 + "/status", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(productPatchOperationStatus, productPatch1)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(THE_STATUS_CANNOT_BE_MODIFIED, CREATED, ACTIVE), INVALID_INPUT.getStatus());
    }
    @Test
    void givenPhysicalProductToLockedStatus_whenPatch_thenSucceed() throws Exception {
        String physicalProductId = ObjectId.get().toString();
        ProductEntity physicalProduct = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(physicalProductId).build());

        mongoTemplate.save(physicalProduct);
        String status = "\"Locked\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch physicalProductPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + physicalProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(physicalProductPatchOperationStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenPhysicalProductToPendingDeliveryStatus_whenPatch_thenSucceed() throws Exception {
        String physicalProductId = ObjectId.get().toString();
        ProductEntity physicalProduct = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(physicalProductId).build());

        mongoTemplate.save(physicalProduct);
        String status = "\"PendingDelivery\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch physicalProductPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + physicalProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(physicalProductPatchOperationStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenPhysicalProductToCancelledStatus_whenPatch_thenSucceed() throws Exception {
        String physicalProductId = ObjectId.get().toString();
        ProductEntity physicalProduct = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(physicalProductId).build());

        mongoTemplate.save(physicalProduct);
        String status = "\"Cancelled\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch physicalProductPatchOperationStatus = createProductPatchBuilder("replace", "/productManagement/v1/product/" + physicalProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(physicalProductPatchOperationStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenPhysicalProductPendingDeliveryToLockedStatus_whenPatch_thenSucceed() throws Exception {
        String physicalProductId = ObjectId.get().toString();
        ProductEntity physicalProduct = mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CONFIRMED).id(physicalProductId).build());
        physicalProduct.setOperationalStatus(PENDINGDELIVERY);
        mongoTemplate.save(physicalProduct);
        String status = "\"Locked\"";
        JsonNode valueStatus = objectMapper.readTree(status);
        ProductPatch physicalProductPatchOperationStatus = createProductPatchBuilder("replace", "/productInventoryManagement/v1/product/" + physicalProductId + "/operationalStatus", valueStatus);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(List.of(physicalProductPatchOperationStatus)), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH));
        resultActions.andExpect(status().isOk());
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }
}

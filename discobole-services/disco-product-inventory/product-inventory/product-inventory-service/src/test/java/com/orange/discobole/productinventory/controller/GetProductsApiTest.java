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
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.creator.ProductEntityCreator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.assertions.Assertions.assertNull;
import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.RESOURCE_NOT_FOUND;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.*;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.PageableHeader.X_TOTAL_COUNT;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetProductsApiTest extends AbstractTest {
    @Value("${config.pagination.limit}")
    private int paginationLimit;
    public static ProductEntity.ProductEntityBuilder getDefaultProductEntityBuilder() {
        return createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue());
    }

    private static ProductEntity.ProductEntityBuilder getProductWithStatusEntityBuilder(ProductStatusType status) {
        return createProductSpecificationEntityBuilder(status, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue());
    }

    private static List<ProductEntity> getExpectedListOfProductEntities(int numberOfProducts) {
        List<ProductEntity> listOfProductEntities = new ArrayList<>();
        for (int i = 0; i < numberOfProducts; i++) {
            listOfProductEntities.add(getDefaultProductEntityBuilder().build());
        }
        return listOfProductEntities;
    }

    @Test
    void givenEmptyDatabase_whenGetProductsWithNoFieldsDefined_thenEmptyListRetrieved() throws Exception {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT);
        List<ProductEntity> expectedProducts = new ArrayList<>();
        List<Product> actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProduct).hasSameSizeAs(expectedProducts);
        assertListProductDtoEqualsToListProductEntity(actualProduct, expectedProducts, false);
    }

    @Test
    void givenNonEmptyDatabase_whenGetProductsWithNoFieldsDefined_thenAllFieldsListRetrieved() throws Exception {
        List<ProductEntity> productEntities = new ArrayList<>();
        String childProductId = ObjectId.get().toString();
        ProductEntity childProduct = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(childProductId).build());
        productEntities.add(childProduct);
        String expectedProductId = ObjectId.get().toString();
        ProductEntity exampleProduct = mongoTemplate.save(createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                "AtomicOffering", childProductId, false, randomAlphabetic(10), PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build());
        productEntities.add(exampleProduct);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT);
        List<Product> actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertThat(actualProduct).hasSameSizeAs(productEntities);
        assertListProductDtoEqualsToListProductEntity(actualProduct, productEntities, false);
    }

    @Test
    void givenNonEmptyDatabase_whenGetAllProducts_thenAllProductsWithDiffAtTypeRetrieved() throws Exception {
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, OFFER.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(randomAlphabetic(10)).build());
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, SERVICE.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(randomAlphabetic(10)).build());
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, SIM_CARD.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(randomAlphabetic(10)).build());
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, MOBILE_LINE.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(randomAlphabetic(10)).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT);
        List<Product> actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isOk());
        assertNotNull(actualProduct);
        assertEquals(4, actualProduct.size());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithLimitOnlyIsNegative_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(LIMIT, "-5")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), LIMIT_SHOULD_NOT_BE_NEGATIVE, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithOffsetNegativeAndLimitNull_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(OFFSET, "-5")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), OFFSET_SHOULD_NOT_BE_NEGATIVE, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithOffsetNegativeAndLimitPositive_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(OFFSET, "-5", LIMIT, "5")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), OFFSET_SHOULD_NOT_BE_NEGATIVE, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithOffsetAndLimitAreNegative_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(OFFSET, "-5", LIMIT, "-5")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), OFFSET_AND_LIMIT_SHOULD_NOT_BE_NEGATIVE, HttpStatus.BAD_REQUEST).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"limit"})
    void givenNotEmptyDatabase_whenGetProductWithLimitNotValidType_thenBadRequest(String limit) {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(LIMIT, limit)));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), String.format(VALUE_IS_NOT_A_VALID_TYPE, LIMIT), HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithOffsetGreaterThenTotalCount_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(OFFSET, "14")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), ERROR_IN_OFFSET, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithOffsetEqualToTotalCount_thenEmptyListRetrieved() {
        for (ProductEntity product : getExpectedListOfProductEntities(2)) {
            mongoTemplate.save(product);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(OFFSET, "2")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<ProductEntity> expectedProducts = new ArrayList<>();
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithLimitGreaterThenTotalCount_thenAllProductListRetrieved() {
        List<ProductEntity> expectedProducts = getExpectedListOfProductEntities(2);
        for (ProductEntity product : expectedProducts) {
            mongoTemplate.save(product);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(LIMIT, "7")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithValidOffsetAndLimit_thenProductListRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        for (ProductEntity product : getExpectedListOfProductEntities(4)) {
            mongoTemplate.save(product);
            expectedProducts = List.of(product);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(OFFSET, "3", LIMIT, "3")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.PARTIAL_CONTENT.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFieldsContainSpaces_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(FIELDS, "id, status ")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), SUPPLEMENT_SPACES_CANNOT_BE_INCLUDED_ON_FIELDS, HttpStatus.BAD_REQUEST).toString());
    }


    @ParameterizedTest
    @ValueSource(strings = {"None", "id", "href"})
    void givenNotEmptyDatabase_whenGetProductWithSpecificFields_thenFieldsIdAndHrefRetrieved(String field) {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        for (ProductEntity product : getExpectedListOfProductEntities(2)) {
            mongoTemplate.save(product);
            expectedProducts.add(ProductEntity.builder().id(product.getId()).href(product.getHref()).build());
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(FIELDS, field)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Created"})
    void givenNotEmptyDatabase_whenGetProductWithDifferentStatusParamCaseAndNoPaging_thenProductListRetrieved(String status) {
        List<ProductEntity> expectedProducts = getExpectedListOfProductEntities(2);
        mongoTemplate.save(getProductWithStatusEntityBuilder(ProductStatusType.ACTIVE).build());
        mongoTemplate.save(getProductWithStatusEntityBuilder(ProductStatusType.TERMINATED).build());
        for (ProductEntity product : expectedProducts) {
            mongoTemplate.save(product);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(Product.Fields.status, status, Product.Fields.operationalStatus, status)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithNotValidField_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(FIELDS, "statuss,productOffering")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "statuss" + NOT_INCLUDED_IN_PRODUCT_FIELDS, HttpStatus.BAD_REQUEST).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Createdd"})
    void givenNotEmptyDatabase_whenGetProductWithNotValidFilterFieldOpStatusEnum_thenBadRequest(String status) {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(Product.Fields.operationalStatus, status)));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), String.format(VALUE_IS_NOT_A_VALID_TYPE, Product.Fields.operationalStatus), HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithNotValidFilterValueTypeLocalDateTime_thenBadRequest() {
        mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-12T12:30:55").atOffset(ZoneOffset.UTC)).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(Product.Fields.startDate, "2022-12-12X12:30:55")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), String.format(VALUE_IS_NOT_A_VALID_TYPE, Product.Fields.startDate), HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithNotValidFilterValueTypeLocalDateTime_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(Product.Fields.startDate, "2022-12-12T12:30:55Z")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByListOfProductId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        String[] productIdList = new String[3];
        int productsCount = 5;
        for (int i = 0; i < productsCount; i++) {
            ProductEntity productEntity = getDefaultProductEntityBuilder().id(String.format("Product_%s", i)).build();
            mongoTemplate.save(productEntity);
            if (i % 2 == 0) {
                productIdList[i / 2] = productEntity.getId();
                expectedProducts.add(productEntity);
            }
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(Product.Fields.id, productIdList)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByIdAndStatusInFieldsParamAndLimit_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        int productsCount = 4;
        for (int i = 0; i < productsCount; i++) {
            ProductEntity productEntity = getDefaultProductEntityBuilder().id(String.format("Product_%s", i)).build();
            mongoTemplate.save(productEntity);
            expectedProducts =
                    List.of(ProductEntity.builder().id(productEntity.getId()).status(productEntity.getStatus()).href(productEntity.getHref()).build());
        }
        String forthProductId = "Product_3";
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(LIMIT, String.valueOf(productsCount),
                FIELDS, Product.Fields.status, Product.Fields.id, forthProductId)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithPartyOrPartyRoleIdAndNameFieldsInFieldsQueryParam_thenPartyOrPartyRoleFieldsAreReturnedAsPartOfResult() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        int productsCount = 1;
        for (int i = 0; i < productsCount; i++) {
            ProductEntity productEntity = getDefaultProductEntityBuilder().id(String.format("Product_%s", i)).build();
            mongoTemplate.save(productEntity);
            expectedProducts =
                    List.of(
                            ProductEntity.builder()
                                    .id(productEntity.getId())
                                    .relatedParty(
                                            productEntity.getRelatedParty().stream()
                                                    .map(rp -> RelatedPartyEntity.builder()
                                                            .atType(rp.getAtType())
                                                            .id(rp.getId())
                                                            .name(rp.getName())
                                                            .build()
                                                    ).toList()
                                    )
                                    .href(productEntity.getHref()).build()
                    );
        }

        final String partyOrPartyRoleIdJsonPath = Product.Fields.relatedParty + "." + RelatedPartyOrPartyRole.Fields.partyOrPartyRole + "." + PartyRef.Fields.id;
        final String partyOrPartyRoleNameJsonPath = Product.Fields.relatedParty + "." + RelatedPartyOrPartyRole.Fields.partyOrPartyRole + "." + PartyRef.Fields.name;
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(LIMIT, String.valueOf(productsCount),
                FIELDS, partyOrPartyRoleIdJsonPath + "," + partyOrPartyRoleNameJsonPath)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithPartyOrPartyRoleAllFieldsInFieldsQueryParam_thenPartyOrPartyRoleFieldsAreReturnedAsPartOfResult() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        int productsCount = 1;
        for (int i = 0; i < productsCount; i++) {
            ProductEntity productEntity = getDefaultProductEntityBuilder().id(String.format("Product_%s", i)).build();
            mongoTemplate.save(productEntity);
            expectedProducts =
                    List.of(
                            ProductEntity.builder()
                                    .id(productEntity.getId())
                                    .relatedParty(
                                            productEntity.getRelatedParty().stream()
                                                    .map(rp -> RelatedPartyEntity.builder()
                                                            .atType(rp.getAtType())
                                                            .id(rp.getId())
                                                            .name(rp.getName())
                                                            .href(rp.getHref())
                                                            .role(rp.getRole())
                                                            .atReferredType(rp.getAtReferredType())
                                                            .partyId(rp.getPartyId())
                                                            .partyName(rp.getPartyName())
                                                            .build()
                                                    ).toList()
                                    )
                                    .href(productEntity.getHref()).build()
                    );
        }

        final String partyOrPartyRoleJsonPath = Product.Fields.relatedParty + "." + RelatedPartyOrPartyRole.Fields.partyOrPartyRole;
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(LIMIT, String.valueOf(productsCount),
                FIELDS, partyOrPartyRoleJsonPath)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterContainsAtAndFields_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        ProductEntity productEntity = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        mongoTemplate.save(productEntity);
        expectedProducts.add(ProductEntity.builder().id(productEntity.getId()).href(productEntity.getHref()).productOffering(productEntity.getProductOffering()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of("productOffering.id", "productOffering123", FIELDS, Product.Fields.productOffering)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterProductSpecification_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity productEntity = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "productSpecification123", false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(randomAlphabetic(10)).build());
        mongoTemplate.save(productEntity);
        expectedProducts.add(ProductEntity.builder().id(productEntity.getId()).href(productEntity.getHref()).productSpecification(productEntity.getProductSpecification()).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of("productSpecification.id", "productSpecification123", FIELDS, Product.Fields.productSpecification)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFieldContainsAt_thenFieldsIdAndHrefRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        for (ProductEntity product : getExpectedListOfProductEntities(2)) {
            mongoTemplate.save(product);
            expectedProducts.add(ProductEntity.builder().id(product.getId()).href(product.getHref()).productSpecification(ProductSpecificationRefEntity.builder().atType(product.getProductSpecification().getAtType()).build()).build());
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(FIELDS, "productSpecification.@type")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithSortStartDate_thenProductListRetrieved() {
        mongoTemplate.save(getProductWithStatusEntityBuilder(ProductStatusType.ACTIVE).startDate(LocalDateTime.of(2023, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC)).build());
        mongoTemplate.save(getProductWithStatusEntityBuilder(ProductStatusType.ACTIVE).startDate(LocalDateTime.of(2024, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC)).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(SORT, QueryFields.START_DATE)));
        readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithSortStartDateDesc_thenProductListRetrieved() {
        mongoTemplate.save(getProductWithStatusEntityBuilder(ProductStatusType.ACTIVE).startDate(LocalDateTime.of(2023, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC)).build());
        mongoTemplate.save(getProductWithStatusEntityBuilder(ProductStatusType.ACTIVE).startDate(LocalDateTime.of(2024, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC)).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(SORT, "-startDate")));
        readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByStartDateBetween_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-11T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-15T12:30:55").atOffset(ZoneOffset.UTC)).build());
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("startDate.gte", ("2022-12-11T12:30:55Z"));
        dateFilter.put("startDate.lte", ("2022-12-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByStartDateLessThanEq_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-11T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-15T12:30:55").atOffset(ZoneOffset.UTC)).build());
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("startDate.lte", ("2022-12-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByStartDateGreaterThanEq_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-11T12:30:55").atOffset(ZoneOffset.UTC)).build());
        mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-12T12:30:55").atOffset(ZoneOffset.UTC)).build());
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-15T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("startDate.gte", ("2022-12-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }


    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByStartDateGreaterThanTwoDateWithOrFilter_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-11T12:30:55").atOffset(ZoneOffset.UTC)).build());
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().startDate(LocalDateTime.parse("2022-12-15T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("startDate.gte", ("2022-12-12T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedPartyId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity build = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(build));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put(QueryFields.RELATED_PARTY_PARTY_OR_PARTY_ROLE + QueryFields.ID_SUFFIX, (build.getRelatedParty().get(0).getId()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedPartyName_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("relatedParty.partyOrPartyRole.name", (expected.getRelatedParty().get(0).getName()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }
    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedPartyPartyName_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity build = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(build));
        Map<String, String> filter = new HashMap<>();
        filter.put("relatedParty.partyOrPartyRole.partyName", (build.getRelatedParty().get(0).getPartyName()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedPartyPartyId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(expected));
        Map<String, String> filter = new HashMap<>();
        filter.put("relatedParty.partyOrPartyRole.partyId", (expected.getRelatedParty().get(0).getPartyId()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }
    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedPartyAtReferredType_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(expected));
        Map<String, String> filter = new HashMap<>();
        filter.put("relatedParty.partyOrPartyRole.@referredType", (expected.getRelatedParty().get(0).getAtReferredType()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }
    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByNameRegex_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).name("test").build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("name", "t*t");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedPartyNameRegex_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).relatedParty(List.of(getRelatedPartyBuilder("test Related Party name").build())).build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("relatedParty.partyOrPartyRole.name", "*name");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByRelatedInvalidPartyNameRegex_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).relatedParty(List.of(getRelatedPartyBuilder("test Related Party name").build())).build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("relatedParty.partyOrPartyRole.name", "^name+");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductOrderItemProductOrderId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("productOrderItem.productOrderId", (expected.getProductOrderItem().get(0).getProductOrderId()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductOrderItemOrderItemId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("productOrderItem.orderItemId", (expected.getProductOrderItem().get(0).getOrderItemId()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductRelationshipRelationshipType_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        ProductEntity expected = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity productEntity2 = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering1234", PRODUCT.getValue()).build();
        productEntity2.getProductRelationship().get(0).setRelationshipType(ProductRelationshipType.BUNDLES.getValue());
        mongoTemplate.save(productEntity2);
        Map<String, String> filter = new HashMap<>();
        filter.put("productRelationship.relationshipType", expected.getProductRelationship().get(0).getRelationshipType());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterIsRootFalse_thenProductWithFilterRetrieved() {
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).productRelationship(
                        List.of(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue())
                                .product(ProductRefEntity.builder().id(ObjectId.get()).build()).build())).build());
        ProductEntity product1 = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        mongoTemplate.save(product1);
        ProductEntity productEntity2 = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering1234", PRODUCT.getValue()).build();
        productEntity2.getProductRelationship().get(0).setRelationshipType(ProductRelationshipType.BUNDLES.getValue());
        mongoTemplate.save(productEntity2);
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(childProduct1);
        Map<String, String> filter = new HashMap<>();
        filter.put("isRoot", "false");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterIsRootTrue_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString())
                        .productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(
                                ProductRefEntity.builder().id(ObjectId.get()).build()).build())).build());
        ProductEntity product1 = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).isRootProduct(true).build();
        expectedProducts.add(mongoTemplate.save(product1));
        ProductEntity productEntity2 = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering1234", PRODUCT.getValue()).isRootProduct(true).build();
        productEntity2.getProductRelationship().get(0).setRelationshipType(ProductRelationshipType.BUNDLES.getValue());
        expectedProducts.add(mongoTemplate.save(productEntity2));
        Map<String, String> filter = new HashMap<>();
        filter.put("isRoot", "true");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductOfferingAtType_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        ProductEntity expected = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity productEntity2 = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering1234", PRODUCT.getValue()).build();
        productEntity2.getProductOffering().setAtType("DIFFERENT_TYPE");
        mongoTemplate.save(productEntity2);
        Map<String, String> filter = new HashMap<>();
        filter.put("productOffering.@type", (expected.getProductOffering().getAtType()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductSpecificationAtType_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity notExpected = getDefaultProductEntityBuilder().build();
        notExpected.getProductSpecification().setAtType("DIFFERENT_TYPE");
        mongoTemplate.save(notExpected);
        Map<String, String> filter = new HashMap<>();
        filter.put("productSpecification.@type", (expected.getProductSpecification().getAtType()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductStatusType_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity activeProduct = getDefaultProductEntityBuilder().build();
        activeProduct.setStatus(ProductStatusType.ACTIVE);
        expectedProducts.add(mongoTemplate.save(activeProduct));
        ProductEntity createdProduct = getDefaultProductEntityBuilder().build();
        createdProduct.setStatus(ProductStatusType.CREATED);
        mongoTemplate.save(createdProduct);
        Map<String, String> filter = new HashMap<>();
        filter.put("status", ProductStatusType.ACTIVE.getValue());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductOperationalStatusType_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity activeProduct = getDefaultProductEntityBuilder().build();
        activeProduct.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        ProductEntity abortedProduct = getDefaultProductEntityBuilder().build();
        abortedProduct.setOperationalStatus(ProductOperationalStatusType.ABORTED);
        expectedProducts.add(mongoTemplate.save(abortedProduct));
        mongoTemplate.save(activeProduct);
        Map<String, String> filter = new HashMap<>();
        filter.put("operationalStatus", ProductOperationalStatusType.ABORTED.getValue());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductStatusType_thenProductWithFilterNotEmpty() {
        ProductEntity activeProduct = getDefaultProductEntityBuilder().build();
        activeProduct.setStatus(ProductStatusType.ACTIVE);
        mongoTemplate.save(activeProduct);
        Map<String, String> filter = new HashMap<>();
        filter.put("status", "");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "status" + NOT_BE_EMPTY, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void whenGetProductWithFilterByProductCharacteristicNameWithoutValue_thenProductWithFilterNotEmpty() {
        List<ProductEntity> expectedProducts = new ArrayList<>();

        expectedProducts.add(mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_NAME").value("ANY_VALUE").build(),
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_OTHER_NAME").value("ANY_OTHER_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build()));
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_NAME").value("ANY_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of("productCharacteristic.name", "ANY_NAME")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void whenGetProductWithFilterByProductCharacteristicAtType_thenProductWithFilterNotEmpty() {
        List<ProductEntity> expectedProducts = new ArrayList<>();

        expectedProducts.add(mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_NAME").value("ANY_VALUE").build(),
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_OTHER_NAME").value("ANY_OTHER_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build()));
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("NumberCharacteristic").name("ANY_OTHER_NAME").value(1).build()),
                        PRODUCT.getValue()).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of("productCharacteristic.@type", "StringCharacteristic")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByAtType_thenProductWithFilterNotEmpty() {
        getDefaultProductEntityBuilder().build();
        Map<String, String> filter = new HashMap<>();
        filter.put("@type", "");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "@type" + NOT_BE_EMPTY, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void whenGetProductWithFilterByProductCharacteristicValueWithoutName_thenProductWithFilterNotEmpty() {
        List<ProductEntity> expectedProducts = new ArrayList<>();

        expectedProducts.add(mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_NAME").value("ANY_VALUE").build(),
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_OTHER_NAME").value("ANY_OTHER_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build()));
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_NAME").value("ANY_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of("productCharacteristic.value", "ANY_VALUE")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductCharacteristicNameAndValue_thenProductWithFilterNotEmpty() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_NAME").value("ANY_VALUE").build(),
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_OTHER_NAME").value("ANY_OTHER_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build()));
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_NAME").value("ANY_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build());
        Map<String, String> filter = new HashMap<>();
        filter.put("productCharacteristic.value", "ANY_VALUE");
        filter.put("productCharacteristic.name", "ANY_NAME");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenProduct_whenGetProductWithFilterByProductCharacteristicNameMatchAndValueMatchInDifferentIndex_thenNoMatchReturned() {
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_NAME").value("ANY_VALUE").build(),
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_OTHER_NAME").value("ANY_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build());
        Map<String, String> filter = new HashMap<>();
        filter.put("productCharacteristic.value", "ANY_OTHER_VALUE");
        filter.put("productCharacteristic.name", "ANY_NAME");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertTrue(products.isEmpty());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithSortByCreationDate_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity product1 = getDefaultProductEntityBuilder().build();
        product1.setCreationDate(OffsetDateTime.parse("2024-02-07T13:57:08.936+00:00"));
        expectedProducts.add(mongoTemplate.save(product1));
        ProductEntity product2 = getDefaultProductEntityBuilder().build();
        product2.setCreationDate(OffsetDateTime.parse("2024-01-07T13:57:08.936+00:00"));
        expectedProducts.add(mongoTemplate.save(product2));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(SORT, "-creationDate")));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductRelationshipProductId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id("65c38c3b036c53780b13e469").build());
        ProductEntity expected = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        expectedProducts.add(mongoTemplate.save(expected));
        Map<String, String> filter = new HashMap<>();
        filter.put("productRelationship.product.id", (expected.getProductRelationship().get(0).getProduct().getId().toString()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFields_thenProductWithFieldsInvalid() {
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        ProductEntity productEntity = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        mongoTemplate.save(productEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of(FIELDS, "productOffering.")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "productOffering." + NOT_INCLUDED_IN_PRODUCT_FIELDS, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByCreationDateBetween_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-11T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-12-15T12:30:55").atOffset(ZoneOffset.UTC)).build());
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("creationDate.gte", ("2023-02-11T12:30:55Z"));
        dateFilter.put("creationDate.lte", ("2023-02-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByCreationDateGreaterThanTwoDateWithOrFilter_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-11T12:30:55").atOffset(ZoneOffset.UTC)).build());
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-15T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("creationDate.gte", ("2023-02-12T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByCreationDateLessThanEq_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-11T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        mongoTemplate.save(getDefaultProductEntityBuilder().creationDate(LocalDateTime.parse("2023-02-15T12:30:55").atOffset(ZoneOffset.UTC)).build());
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("creationDate.lte", ("2023-02-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByTerminationDateBetween_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-11T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-12-15T12:30:55").atOffset(ZoneOffset.UTC)).build());
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("terminationDate.gte", ("2023-02-11T12:30:55Z"));
        dateFilter.put("terminationDate.lte", ("2023-02-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByTerminationDateGreaterThanTwoDateWithOrFilter_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-11T12:30:55").atOffset(ZoneOffset.UTC)).build());
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-15T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("terminationDate.gte", ("2023-02-12T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByTerminationDateDateLessThanEq_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-11T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-12T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        expectedProducts.add(mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-13T12:30:55").atOffset(ZoneOffset.UTC)).build()));
        mongoTemplate.save(getDefaultProductEntityBuilder().terminationDate(LocalDateTime.parse("2023-02-15T12:30:55").atOffset(ZoneOffset.UTC)).build());
        Map<String, String> dateFilter = new HashMap<>();
        dateFilter.put("terminationDate.lte", ("2023-02-13T12:30:55Z"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(dateFilter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithValidFilterByProductOfferingName_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        ProductEntity productEntity = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering3322", PRODUCT.getValue()).build();
        productEntity.getProductOffering().setName("productOffering123");
        mongoTemplate.save(productEntity);
        expectedProducts.add(ProductEntity.builder().id(productEntity.getId()).href(productEntity.getHref()).productOffering(productEntity.getProductOffering()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(Map.of("productOffering.name", "productOffering123", FIELDS, Product.Fields.productOffering)));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(products);
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByProductName_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        ProductEntity expected = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).name("Product1").build();
        expectedProducts.add(mongoTemplate.save(expected));

        Map<String, String> filter = new HashMap<>();
        filter.put("name", "Product1");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenPhysicalProductId_whenListProducts_thenAtTypeReturned() throws Exception {
        String exampleProductId = "exampleProductId";
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(exampleProductId).build());

        Map<String, String> filter = new HashMap<>();
        filter.put("id", exampleProductId);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        resultActions.andExpect(status().isOk());

        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        assertThat(products.get(0).getId()).isEqualTo(exampleProductId);
        assertThat(products.get(0).getAtType()).isEqualTo(PHYSICAL_PRODUCT.getValue());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterAndFieldsByProductCharacteristicNameAndValue_thenProductWithFilterNotEmpty() {
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_NAME").value("ANY_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build());
        Map<String, String> filter = new HashMap<>();
        filter.put(FIELDS, "productCharacteristic.@Type,productCharacteristic.valueType,productCharacteristic.id");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertNotNull(products.get(0).getProductCharacteristic());
        assertNull(products.get(0).getProductCharacteristic().get(0).getName());
        assertNotNull(products.get(0).getProductCharacteristic().get(0).getValueType());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterAndFieldsByProductCharacteristicNameAndValueWithoutAtTpe_thenProductWithFilterNotEmpty() {
        mongoTemplate.save(
                createProductEntityWithCharacteristics(List.of(
                                CharacteristicEntity.builder().id("id").valueType("string").atType("StringCharacteristic").name("ANY_OTHER_NAME").value("ANY_OTHER_VALUE").build()),
                        PRODUCT.getValue()).build());
        Map<String, String> filter = new HashMap<>();
        filter.put(FIELDS, "productCharacteristic.valueType,productCharacteristic.id");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertNotNull(products.get(0).getProductCharacteristic());
        assertNull(products.get(0).getProductCharacteristic().get(0).getName());
        assertNotNull(products.get(0).getProductCharacteristic().get(0).getValueType());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByInvalidProductRelationshipProductId_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id("65c38c3b036c53780b13e469").build());
        ProductEntity expected = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        expectedProducts.add(mongoTemplate.save(expected));
        Map<String, String> filter = new HashMap<>();
        filter.put("productRelationship.product.id", ("PPPPPPPPPPPPPPPPPP"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), INVALID_FORMAT_FOR_PRODUCT_RELATIONSHIP_ID, HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithUnsupportedFilter_thenProductWithFilterNotRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity childProduct1 = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id("65c38c3b036c53780b13e469").build());
        ProductEntity expected = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        expectedProducts.add(mongoTemplate.save(expected));
        Map<String, String> filter = new HashMap<>();
        filter.put("productPrice", ("1000"));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), UNSUPPORTED_FILTER + "productPrice", HttpStatus.BAD_REQUEST).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithFilterByCharacteristicValueRegex_thenProductWithFilterRetrieved() {
        List<ProductEntity> expectedProducts = new ArrayList<>();
        ProductEntity expected = commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                .productCharacteristic(List.of(CharacteristicEntity.builder().atType("StringCharacteristic").id("11").name("test").valueType("string").value("stringValue").build())).name("test").build();
        expectedProducts.add(mongoTemplate.save(expected));
        ProductEntity build1 = getDefaultProductEntityBuilder().build();
        mongoTemplate.save(build1);
        Map<String, String> filter = new HashMap<>();
        filter.put("productCharacteristic.value", "s*");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertListProductDtoEqualsToListProductEntity(products, expectedProducts, false);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductWithInvalidFilterByCharacteristicValueRegex_thenProductWithFilterNotRetrieved() {
        mongoTemplate.save(commonProductBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue())
                .productCharacteristic(List.of(CharacteristicEntity.builder().atType("StringCharacteristic").id("11").name("test").valueType("string").value("stringValue").build())).name("test").build());
        Map<String, String> filter = new HashMap<>();
        filter.put("productCharacteristic.value", "sop*");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }
    @Test
    void givenNotEmptyDatabase_whenGetProductWithBadUrl_thenNotFound() {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        String badUrl = "/productInventoryManagement/v1/product//";
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, badUrl);
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), BAD_URL_OR_RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND).toString());
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductsThatExceedsLimit_thenPaginationIsAtLimit() {
        List<ProductEntity> builds = new ArrayList<>();
        for (int i = 0; i < paginationLimit + 10; i++) {
            ProductEntity build = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toHexString()).build();
            builds.add(build);
        }
        mongoTemplate.insertAll(builds);

        Map<String, String> filter = Map.of(LIMIT, "200", OFFSET, "0");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        List<Product> products = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(Long.valueOf(getHeaderValueFromAPIResponse(resultActions, X_TOTAL_COUNT, new TypeReference<>() {
        }))).isEqualTo(paginationLimit);

        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
        assertNotNull(products);
        assertThat(products).hasSize(paginationLimit);
    }

    @Test
    void givenNotEmptyDatabase_whenGetProductsWithoffesetLargerThanPaginationLimit_thenBadRequest() {
        List<ProductEntity> builds = new ArrayList<>();
        for (int i = 0; i < paginationLimit + 10; i++) {
            ProductEntity build = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toHexString()).build();
            builds.add(build);
        }
        mongoTemplate.insertAll(builds);

        Map<String, String> filter = Map.of(LIMIT, "100", OFFSET, "100");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(filter));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), String.format(DOCUMENT_COUNT_EXCEEDED_PAGINATION_LIMIT, paginationLimit), HttpStatus.BAD_REQUEST).toString());

    }

    @Test
    void givenMultipleProducts_whenFilterByNameWithSpecialChars_thenCorrectProductsRetrieved() {
        List<ProductEntity> savedProducts = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ProductEntity product = commonProductBuilder(CREATED, randomAlphabetic(5), randomAlphabetic(5), false, PRODUCT.getValue()).name("Product ツ").build();
            mongoTemplate.save(product);
            savedProducts.add(product);
        }
        Map<String, String> unicodeFilter = new HashMap<>();
        unicodeFilter.put("name", "Product ツ");
        unicodeFilter.put("limit", "5");
        unicodeFilter.put("offset", "0");
        ResultActions unicodeResult = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, param(unicodeFilter));

        List<Product> unicodeProducts = readJsonFromAPIResponse(unicodeResult, new TypeReference<>() {});
        assertThat(readStatusFromAPIResponse(unicodeResult)).isEqualTo(HttpStatus.PARTIAL_CONTENT.value());
        assertNotNull(unicodeProducts);
        assertListProductDtoEqualsToListProductEntity(unicodeProducts, savedProducts.subList(0, 5), false);

        String totalCountHeader = unicodeResult.andReturn().getResponse().getHeader("X-Total-Count");
        assertNotNull(totalCountHeader, "X-Total-Count header should not be null");
        assertEquals("20", totalCountHeader, "Total count should be 20");
        String linkHeader = unicodeResult.andReturn().getResponse().getHeader("Link");
        assertNotNull(linkHeader, "Link header should not be null");

        assertTrue(linkHeader.contains("offset=5") && linkHeader.contains("rel=\"next\""), "Link header should contain a 'next' link with offset=5");
        assertTrue(linkHeader.contains("offset=15") && linkHeader.contains("rel=\"last\""), "Link header should contain a 'last' link with offset=15");

        assertTrue(linkHeader.contains("Product%20%E3%83%84"), "Link header should contain encoded product name");

    }

}

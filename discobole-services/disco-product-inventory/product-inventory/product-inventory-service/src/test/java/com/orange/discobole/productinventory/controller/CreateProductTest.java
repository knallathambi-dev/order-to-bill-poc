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
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.orange.discobole.productinventory.config.ApplicationConfigProperties;
import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.enumerate.UnitEnum;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.*;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.ATOMIC_PRODUCT_OFFERING;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.CONTRACT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForCreateProductEvent;
import static com.orange.discobole.productinventory.util.ValidationUtil.getNotEmptyMessage;
import static com.orange.discobole.productinventory.util.ValidationUtil.getNotNullMessage;
import static com.orange.discobole.productinventory.util.creator.CommonCreator.*;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.*;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductWithoutRelationShip;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class CreateProductTest extends AbstractTest {
    @Autowired
    private ApplicationConfigProperties applicationConfigProperties;

    @Test
    void givenNullBodyRequest_whenCreate_thenAdded() {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(null), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), "Invalid Input", INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithValidProductSpecificationUri_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertNull(productEntity.getProductCharacteristic());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
        consumeAndAssertEqualityForCreateProductEvent(productResponseContent);

    }

    @Test
    void givenProductWithInvalidProductSpecificationUri_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, NOT_VALID_PRODUCT_SPECIFICATION_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, NOT_VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_OFFERING + WITH_ID + VALID_ATOMIC_PRODUCT_OFFERING_ID + INVALID_RELATIONSHIP, NOT_VALID_PRODUCT_SPECIFICATION_ID), INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithValidProductSpecificationUriAndValidProductOfferingPriceUri_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithValidProductSpecificationUriAndEmptyProductOfferingPriceID_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(EMPTY_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EMPTY_PRODUCT_OFFERING_PRICE_ID_DETECTED, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithInvalidProductSpecificationUriAndValidProductPriceUri_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, NOT_VALID_PRODUCT_SPECIFICATION_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, NOT_VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(EMPTY_ID).build())).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_OFFERING + WITH_ID + VALID_ATOMIC_PRODUCT_OFFERING_ID + INVALID_RELATIONSHIP, NOT_VALID_PRODUCT_SPECIFICATION_ID), INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();

    }

    @Test
    void givenProductWithInvalidProductSpecificationUriAndInvalidProductPriceUri_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, NOT_VALID_PRODUCT_SPECIFICATION_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, NOT_VALID_PRODUCT_OFFERING_PRICE_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, NOT_VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(NOT_VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_OFFERING + WITH_ID + VALID_ATOMIC_PRODUCT_OFFERING_ID + INVALID_RELATIONSHIP, NOT_VALID_PRODUCT_SPECIFICATION_ID), INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithValidProductPriceUriAndValidProductOfferingUri_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenValidProduct_AcquisitionCase_whenCreate_thenAddedWithContractParentRelationship() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
        assertNotNull(productEntity);
        ObjectId contractId = new ObjectId(productEntity.getId());
        ProductEntity product1 = mongoTemplate.findById(productEntity.getProductRelationship().get(0).getProduct().getId(), ProductEntity.class);

        assert product1 != null;
        assertTrue(product1.getProductRelationship().stream().anyMatch(
                productRelationship -> productRelationship.getRelationshipType().equals(ROOTPRODUCT.getValue()) && productRelationship.getProduct().getId().equals(contractId)));
    }


    @Test
    void givenProductWithValidProductPriceUriAndInvalidProductOfferingUri_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(NOT_VALID_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRODUCT_OFFERING + WITH_IDS + NOT_VALID_PRODUCT_OFFERING_ID + DOESN_T_EXIST_IN_CATALOG, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithInvalidProductPriceUriAndValidProductOfferingUri_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, NOT_VALID_PRODUCT_OFFERING_PRICE_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(NOT_VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRODUCT_OFFERING_PRICE + WITH_IDS + NOT_VALID_PRODUCT_OFFERING_PRICE_ID + DOESN_T_EXIST_IN_CATALOG, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithInvalidProductPriceUriAndInvalidProductOfferingUri_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, NOT_VALID_PRODUCT_OFFERING_PRICE_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(NOT_VALID_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(NOT_VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRODUCT_OFFERING + WITH_IDS + NOT_VALID_PRODUCT_OFFERING_ID + DOESN_T_EXIST_IN_CATALOG, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithStartDate_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        Product product = createProductBuilderInnerRelation(VALID_ATOMIC_PRODUCT_OFFERING_ID,
                BUNDLES, CONTRACT.getValue(),
                createProductBuilderWithProductSpecification(CREATED,
                        randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).startDate(OffsetDateTime.now())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_START_DATE_NOT_ADDED_IN_POST_REQUEST, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithoutStatusMandatoryField_whenCreate_thenBadRequest() {
        Product product = createProductBuilderWithProductSpecification(ABORTED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).status(null).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), getNotNullMessage("status"), MISSING_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithoutOrderItemMandatoryField_whenCreate_thenBadRequest() {
        Product product = Product.builder().status(ProductStatusType.CREATED).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), getNotEmptyMessage("productOrderItem"), INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithStatusNotCreated_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        Product product = createProductBuilderInnerRelation(VALID_ATOMIC_PRODUCT_OFFERING_ID, BUNDLES, CONTRACT.getValue(), createProductBuilderWithProductSpecification(ABORTED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_STATUS_SHOULD_BE_CREATED, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithoutProductOfferingId_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED).productOffering(ProductOfferingRef.builder().atType(CONTRACT.getValue()).build()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        //todo check if message and reason is reverted
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), getNotNullMessage("productOffering.id"), MISSING_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithoutProductOfferingType_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED).productOffering(ProductOfferingRef.builder().id(VALID_ATOMIC_PRODUCT_OFFERING_ID).build()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        //todo check if message and reason is reverted
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), getNotNullMessage("productOffering.atType"), MISSING_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenValidProductsWithRelationshipHasParent_ModificationCase_whenCreateBothInOrder_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();

        ResultActions resultAcquisitionAction = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultAcquisitionAction.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultAcquisitionAction, new TypeReference<>() {
        });
        String parentId = productResponseContent.getProductRelationship().get(0).getProduct() instanceof Product product1 ? product1.getId() : null;

        Product child = createProductBuilderWithRelationChildHasParent(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, parentId).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(child), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productModificationResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productModificationResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productModificationResponseContent, productEntity, true);
        assertNotNull(productEntity);
        Product productAcquisitionResponseContent = readJsonFromAPIResponse(resultAcquisitionAction, new TypeReference<>() {
        });
        ObjectId contractId = new ObjectId(productAcquisitionResponseContent.getId());
        assertTrue(productEntity.getProductRelationship().stream().anyMatch(
                productRelationship -> productRelationship.getRelationshipType().equals(ROOTPRODUCT.getValue()) && productRelationship.getProduct().getId().equals(contractId)));
    }


    @Test
    void givenProductWithInvalidProductSpecificationIdInnerProductRelationship_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, NOT_VALID_PRODUCT_SPECIFICATION_ID, null, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, NOT_VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_OFFERING + WITH_ID + VALID_ATOMIC_PRODUCT_OFFERING_ID + INVALID_RELATIONSHIP, NOT_VALID_PRODUCT_SPECIFICATION_ID), INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithInvalidStatusProductInnerProductRelationship_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createProductBuilderInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, ACTIVE, CONTRACT.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_STATUS_SHOULD_BE_CREATED, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithInvalidOperationalStatusProductInnerProductRelationship_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.ACTIVE).productOffering(ProductOfferingRef.builder().id(VALID_ATOMIC_PRODUCT_OFFERING_ID).atType(CONTRACT.getValue()).build()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_OPERATIONAL_STATUS_SHOULD_BE_CREATED, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithValidProductInnerProductRelationship_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenTwoProductsWithRelationshipHasParent_whenCreateBothInOrder_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();

        ResultActions resultAcquisitionAction = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultAcquisitionAction.andExpect(status().isCreated());
        Product productResponseContent1 = readJsonFromAPIResponse(resultAcquisitionAction, new TypeReference<>() {
        });
        String parentId = productResponseContent1.getProductRelationship().get(0).getProduct() instanceof Product product1 ? product1.getId() : null;
        Product child = createProductBuilderWithRelationChildHasParent(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, parentId).build();

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(child), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithInvalidRelatedProductId_modificationCase_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        String invalidParentId = INVALID_PRODUCT_ID;
        Product child = createProductBuilderWithRelationChildHasParent(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, invalidParentId).build();

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(child), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotFoundErrorExists(error, RESOURCE_NOT_FOUND.getCode(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, invalidParentId), RESOURCE_NOT_FOUND.getStatus());
    }

    @Test
    void givenProductWithEmptyProductSpecificationIdInnerProductRelationship_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, "", PRODUCT_SPECIFICATION_JSON, HttpStatus.NO_CONTENT.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createProductBuilderInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, null, BUNDLES, ACTIVE, CONTRACT.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), getNotNullMessage("productRelationship[0].product.productRelationship[0].product.productSpecification.id"), MISSING_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithOperationStateActive_whenCreate_thenBadRequest() {
        Product product = createProductBuilderWithProductOffering(CREATED, ProductOperationalStatusType.ACTIVE, QueryFields.PRODUCT_ORDER_ITEM_PRODUCT_ORDER_ID, QueryFields.PRODUCT_ORDER_ITEM_ORDER_ITEM_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID, CONTRACT.getValue()).build();
        product.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_OPERATIONAL_STATUS_SHOULD_BE_CREATED, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithEmptyStringStartDate_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        Product product = createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).build();
        product.setStartDate(LocalDateTime.of(2023, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC));
        final String productJson = toJsonString(List.of(product)).replace("2023-12-05T05:05:00", "");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(INVALID_INPUT.getCode(), error.getCode());
    }

    @Test
    void givenProductWithCharacteristics_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        StringCharacteristic stringCharacteristic = StringCharacteristic.builder().id("1").value("string").atType("StringCharacteristic").build();
        StringArrayCharacteristic stringArrayCharacteristic = StringArrayCharacteristic.builder().id("1").value(List.of("string 1")).atType("StringArrayCharacteristic").build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of(stringCharacteristic, stringArrayCharacteristic))).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<Characteristic> characteristics = ((Product) ((Product) ((Product) productResponseContent.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductCharacteristic();
        assertEquals(2, characteristics.size());
        assertEquals("string 1", ((StringArrayCharacteristic) characteristics.get(1)).getValue().get(0));
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithEmptyProductOfferingID_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(EMPTY_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRODUCT_OFFERING_ID_CANNOT_BE_EMPTY, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithEmptyProductSpecificationID_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_ATOMIC_PRODUCT_OFFERING_ID, EMPTY_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).productPrice(List.of(createBuilderProductPrice(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EMPTY_PRODUCT_SPECIFICATION_ID_DETECTED, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithEmptyProductOrderID_whenCreate_thenBadRequest() throws Exception {
        Product parentAndGetId = createParentAndGetProduct(VALID_PRODUCT_OFFERING_CONTRACT_ID);
        String parentId = parentAndGetId.getProductRelationship().get(0).getProduct() instanceof Product product1 ? product1.getId() : null;
        Product product = createProductBuilderWithRelationChildHasParent(CREATED, EMPTY_ID, randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), BUNDLES, parentId).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EMPTY_PRODUCT_ORDER_ID_DETECTED, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithInvalidProductRelationShip_thatDoesntExitsInCatalog_whenCreateWithModificationCase_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product parentAndGetId = createParentAndGetProduct(VALID_PRODUCT_OFFERING_CONTRACT_ID);
        Product child = createProductBuilderWithRelationChildHasParent(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, parentAndGetId.getId()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(child), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRODUCT_OFFERING + WITH_ID + VALID_PRODUCT_OFFERING_CONTRACT_ID + String.format(INVALID_RELATIONSHIP, VALID_ATOMIC_PRODUCT_OFFERING_ID), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithValidProductRelationShip_thatExitsInCatalog_whenCreateWithModificationCase_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product parentAndGetId = createParentAndGetProduct(VALID_PRODUCT_OFFERING_CONTRACT_ID_WITH_CORRECT_RELATIONSHIP_WITH_ATOMIC_PRODUCT_OFFERING);
        Product child = createProductBuilderWithRelationChildHasParent(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID_WITH_CORRECT_RELATIONSHIP_WITH_CONTRACT, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, parentAndGetId.getId()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(child), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());

    }

    @Test
    void givenProductWithEmptyOrderItemID_whenCreate_thenBadRequest() throws Exception {

        Product parentAndGetId = createParentAndGetProduct(VALID_PRODUCT_OFFERING_CONTRACT_ID);
        String parentId = parentAndGetId.getProductRelationship().get(0).getProduct() instanceof Product product1 ? product1.getId() : null;
        Product product = createProductBuilderWithRelationChildHasParent(CREATED, randomAlphabetic(STRING_SIZE), EMPTY_ID, VALID_PRODUCT_SPECIFICATION_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), BUNDLES, parentId).build();

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EMPTY_ORDER_ITEM_ID_DETECTED, INVALID_INPUT.getStatus());
    }


    private Product createParentAndGetProduct(String contractOfferingId) throws IOException {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product parent = createProductBuilderInnerRelationShipValidRelationShip(contractOfferingId, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();

        ResultActions resultActions1 = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(parent), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        return readJsonFromAPIResponse(resultActions1, new TypeReference<>() {});
    }


    @Test
    void givenProductWithNotNullCreationDate_whenCreate_thenCreationDateIsGivenDate() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        OffsetDateTime creationDate = LocalDateTime.of(2024, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC);
        product.setCreationDate(creationDate);
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertTrue(productResponseContent.getCreationDate().isEqual(creationDate));
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        assertTrue(productEntity.getCreationDate().isEqual(creationDate));
    }

    @Test
    void givenProductWithNullCreationDate_whenCreate_thenCreationDateIsSysdate() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        product.setCreationDate(null);
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(productResponseContent.getCreationDate());
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        assertNotNull(productEntity.getCreationDate());
    }

    @Test
    void givenProductWithProductAtTypeValue_whenCreate_thenAtTypeIsProduct() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();

        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        assertEquals(ProductTypeEnum.PRODUCT.getValue(), productEntity.getAtType());
    }

    @Test
    void givenProductWithValidityCharacteristics_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        ValidityCharacteristic validityCharacteristic = ValidityCharacteristic.builder().id("123").value(Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).build()).atType("ValidityCharacteristic").build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of(validityCharacteristic))).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<Characteristic> characteristics = ((Product) ((Product) ((Product) productResponseContent.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductCharacteristic();
        assertEquals(1, characteristics.size());
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithOnlyOneOccurrenceValidityCharacteristics_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        ValidityCharacteristic validityCharacteristic = ValidityCharacteristic.builder().id("123").value(Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).build()).atType("ValidityCharacteristic").build();
        ValidityCharacteristic validityCharacteristic1 = ValidityCharacteristic.builder().id("133").value(Value.builder().value(22).unitOfMeasure(UnitEnum.HOUR.getValue()).build()).atType("ValidityCharacteristic").build();

        Product product = createProductBuilderInnerRelation(VALID_ATOMIC_PRODUCT_OFFERING_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of(validityCharacteristic, validityCharacteristic1))).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), ONLY_ONE_OCCURRENCE_IS_ACCEPTED_FOR_VALIDITY_CHARACTERISTIC, INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithProductWithLocation_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        assertEquals(ProductTypeEnum.PRODUCT.getValue(), productEntity.getAtType());
        assertNotNull(resultActions.andReturn().getResponse().getHeader(HttpHeaders.LOCATION));
    }

    @Test
    void givenProductWithValidProductName_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        product.setName("Mobile Package 1");
        product.getProductOffering().setName("Mobile Package 1");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertEquals("Mobile Package 1", productEntity.getName());
        assertEquals(productEntity.getName(), productEntity.getProductOffering().getName());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithValidProductNameFromProductSpecification_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        Product productSpec = ((Product) (
                (Product) productResponseContent.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct());
        assertNotNull(productResponseContent);
        assertEquals("Mobile Handset", productSpec.getName());
        assertEquals(productSpec.getName(), productSpec.getName());
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProduct_whenCreate_thenStatusChangeAdded() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertNotNull(productEntity.getStatusChange());
        assertNotNull(productEntity.getOperationalStatusChange());
        Assertions.assertEquals(1, productEntity.getStatusChange().size());
        Assertions.assertEquals(1, productEntity.getOperationalStatusChange().size());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }


    @Test
    void givenProductWithValidDateTimeCharacteristics_whenCreate_thenAdded() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .productCharacteristic(
                                List.of(
                                        ObjectCharacteristic.builder().id("id").valueType("DateTime").name("ANY_NAME").value("2024-04-30T10:19:06Z").build()
                                ))
                        .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertEquals(1, productEntity.getProductCharacteristic().size());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithInValidDateTimeCharacteristics_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productCharacteristic(List.of(
                        ObjectCharacteristic.builder().id("id").valueType("DateTime").atType("ObjectCharacteristic").name("ANY_NAME").value("ANY_VALUE").build()

                ))
                .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), INVALID_FORMAT_FOR_DATE_TIME_PRODUCT_CHARACTERISTIC, INVALID_INPUT.getStatus());
    }


    @Test
    void givenProductWithMultipleValidityCharacteristics_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productCharacteristic(List.of(
                        ValidityCharacteristic.builder().id("123").value(Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).build()).atType("ValidityCharacteristic").build(),
                        ValidityCharacteristic.builder().id("456").value(Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).build()).atType("ValidityCharacteristic").build()
                ))
                .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), ONLY_ONE_OCCURRENCE_IS_ACCEPTED_FOR_VALIDITY_CHARACTERISTIC, INVALID_INPUT.getStatus());
    }

    @Test
    void givenSlowCatalogUrl_whenCreate_thenTimeout() {
        final Integer webClientMaxRetryAttempts = applicationConfigProperties.getWebClientMaxRetryAttempts();
        applicationConfigProperties.setWebClientMaxRetryAttempts(0);
        wireMock.stubFor(get(urlPathEqualTo(PRODUCT_SPECIFICATION_URL))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
        wireMock.stubFor(get(urlPathEqualTo(PRODUCT_OFFERING_URL)).willReturn(aResponse().withFixedDelay(30_000).withStatus(HttpStatus.OK.value())));
        await().atMost(Duration.of(5, ChronoUnit.SECONDS)).until(() -> {
            Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
            ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
            resultActions.andExpect(status().is5xxServerError());
            return true;
        });
        applicationConfigProperties.setWebClientMaxRetryAttempts(webClientMaxRetryAttempts);

    }

    @Test
    void givenCatalogUrl_whenFail_thenRetry() throws Exception {
        String body = Files.readString(Paths.get(PRODUCT_OFFERING_JSON));

        wireMock.stubFor(get(urlPathEqualTo(PRODUCT_OFFERING_URL))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())
                )
                .willSetStateTo("First Failure"));
        wireMock.stubFor(get(urlPathEqualTo(PRODUCT_OFFERING_URL))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("First Failure")
                .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())
                )
                .willSetStateTo("Second Failure"));

        wireMock.stubFor(get(urlPathEqualTo(PRODUCT_OFFERING_URL))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Second Failure")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody(body)
                )
                .willSetStateTo("Completed"));

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void givenProductWithEmptyBillingAccount_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).billingAccount(BillingAccountRef.builder().id("").build()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), getNotEmptyMessage("billingAccount.id"), INVALID_INPUT.getStatus());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenProductWithNonNullProductOfferingAndNonNullProductSpecification_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void givenProductWithNonNullProductOfferingAndNullProductSpecification_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        Product atomic = ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct());
        atomic.setProductRelationship(new ArrayList<>());
        atomic.setProductSpecification(null);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isCreated());
        consumeAndAssertEqualityForCreateProductEvent(productResponseContent);

    }

    @Test
    void givenProductWithNullProductOfferingAndNonNullProductSpecification_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSellsRelationshipWithSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        resultActions.andExpect(status().isCreated());
        consumeAndAssertEqualityForCreateProductEvent(productResponseContent);
    }

    @Test
    void givenProductWithNullProductOfferingAndNullProductSpecification_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createProductBuilderInnerRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED, CONTRACT.getValue()).build();
        Product atomic = (Product) product.getProductRelationship().get(0).getProduct();
        atomic.setProductRelationship(new ArrayList<>());
        atomic.setProductOffering(null);
        atomic.setProductSpecification(null);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), NULL_PRODUCT_OFFERING_NULL_PRODUCT_SPECIFICATION_CANNOT_ACCEPTED, INVALID_INPUT.getStatus());


    }

    @Test
    void givenProductWithoutProductRelationship_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        ProductEntity productEntity = createProductWithoutRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, CONTRACT.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productEntity), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        assertNull(productEntity.getProductRelationship());
    }

    @Test
    void givenProductWithProductPriceWithRecurringChargePeriodNullAndPriceTypeEqualRecurring_whenCreate_thenBadRequest() {
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(EMPTY_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productPrice(List.of(createBuilderProductPriceWithInvalidRecurringChargePeriodValidationBecauseOfNullValue(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_NULL, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithProductPriceWithRecurringChargePeriodEmptyAndPriceTypeEqualRecurring_whenCreate_thenBadRequest() {
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(EMPTY_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productPrice(List.of(createBuilderProductPriceWithRecurringChargePeriodValidationBecauseOfEmptyValue(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_EMPTY, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithProductPriceWithInvalidRecurringChargePeriodAndPriceTypeEqualRecurring_whenCreate_thenBadRequest() {
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(EMPTY_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productPrice(List.of(createBuilderProductPriceWithRecurringChargePeriodValidationBecauseOfBadValue(VALID_PRODUCT_OFFERING_PRICE_ID).build())).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_EMPTY, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithValidityCharacteristicsWithCheckFields_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productCharacteristic(List.of(
                        ValidityCharacteristic.builder().id("123").value(Value.builder().value(30).unitOfMeasure(UnitEnum.DAY.getValue()).validTo(OffsetDateTime.now()).build()).atType("ValidityCharacteristic").build()
                ))
                .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), EXCLUSION_FOR_VALUE_UNITE_OF_MEASURE_VALID_TO, INVALID_INPUT.getStatus());
    }

    @Test
    @Disabled("Test is skipped due to an ongoing issue with unit of measure validation")
    void givenProductWithValidityCharacteristicsWithInvalidUnitOfMeasure_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .productCharacteristic(List.of(
                        ValidityCharacteristic.builder().id("123").value(Value.builder().value(30).unitOfMeasure("worng_value").build()).atType("ValidityCharacteristic").build()
                ))
                .build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), UNITE_OF_MEASURE_MUST_BE_ONE_OF + "[Days, Month, Hour]", INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithMigrateFromRelationship_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product contract1 = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();
        Product contract2 = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(contract1), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productFromAPIResponse = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductRelationship newRelationship = ProductRelationship
                .builder()
                .relationshipType(MIGRATEFROM.getValue())
                .product(ProductRef.builder().id(productFromAPIResponse.getId()).atType(ProductTypeEnum.PRODUCT_REF.getValue()).build())
                .build();

        contract2.setProductRelationship(
                Stream.concat(
                        contract2.getProductRelationship() == null ? Stream.empty() : contract2.getProductRelationship().stream(),
                        Stream.of(newRelationship)
                ).toList()
        );
        ResultActions resultActions1 = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(contract2), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions1.andExpect(status().isCreated());
        Product productFromAPIResponse2 = readJsonFromAPIResponse(resultActions1, new TypeReference<>() {
        });
        assertNotNull(productFromAPIResponse2.getProductRelationship());
        assertTrue(productFromAPIResponse2.getProductRelationship().stream().anyMatch(productRelationship -> MIGRATEFROM.getValue().equals(productRelationship.getRelationshipType())));
    }

    @Test
    void givenProductWithRandomRelationshipType_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product contract1 = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();
        Product contract2 = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(contract1), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productFromAPIResponse = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductRelationship newRelationship = ProductRelationship
                .builder()
                .relationshipType("RANDOM_RELATIONSHIP_TYPE")
                .product(ProductRef.builder().id(productFromAPIResponse.getId()).atType(ProductTypeEnum.PRODUCT_REF.getValue()).build())
                .build();

        contract2.setProductRelationship(
                Stream.concat(
                        contract2.getProductRelationship() == null ? Stream.empty() : contract2.getProductRelationship().stream(),
                        Stream.of(newRelationship)
                ).toList()
        );
        ResultActions resultActions1 = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(contract2), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions1.andExpect(status().isCreated());
        Product productFromAPIResponse2 = readJsonFromAPIResponse(resultActions1, new TypeReference<>() {
        });
        assertNotNull(productFromAPIResponse2.getProductRelationship());
        assertTrue(productFromAPIResponse2.getProductRelationship().stream().anyMatch(productRelationship -> "RANDOM_RELATIONSHIP_TYPE".equals(productRelationship.getRelationshipType())));
    }

    @Test
    void givenProductWithAddressCharacteristics_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        AddressCharacteristic addressCharacteristic = AddressCharacteristic.builder().id("123").name("Address").addressId("112233").country("India").city("Guru gram").atType("AddressCharacteristic").build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of(addressCharacteristic))).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<Characteristic> characteristics = ((Product) ((Product) ((Product) productResponseContent.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductCharacteristic();
        assertEquals(1, characteristics.size());
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenValidProductsWithoutPriceTypeInnerPriceAlteration_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        Product product = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED)
                .productPrice(List.of(ProductPrice.builder()
                        .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                        .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                        .priceType(PRICE_TYPE_RECURRING).recurringChargePeriod(MeasuredValue.builder().amount(AMOUNT).units("day").build())
                        .productPriceAlteration(List.of(PriceAlteration.builder().applicationDuration(
                                        Quantity.builder().amount(AMOUNT).units("day").build())
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                                .build()
                        ))
                        .build()
                ))
                .build();


        ResultActions resultAcquisitionAction = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultAcquisitionAction.andExpect(status().isCreated());
    }



    @Test
    void givenProductWithFloatDurationAmount_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product validProduct = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED).build();
        String validJson = objectMapper.writeValueAsString(validProduct);
        String invalidJson = validJson.replaceFirst("\"amount\":\\s*\\d+", "\"amount\": 10.7");
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, invalidJson, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), "Invalid Input", INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithDateCharacteristics_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        OffsetDateTime expectedDate = OffsetDateTime.now().minusDays(3);
        DateCharacteristic dateCharacteristic = DateCharacteristic.builder().id("456").name("ActivationDate").value(expectedDate).atType("DateCharacteristic").build();
        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of(dateCharacteristic))).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        List<Characteristic> characteristics = ((Product) ((Product) ((Product) productResponseContent
                        .getProductRelationship().get(0).getProduct())
                        .getProductRelationship().get(0).getProduct())
                        .getProductRelationship().get(0).getProduct())
                        .getProductCharacteristic();

        assertEquals(1, characteristics.size());
        DateCharacteristic returned = (DateCharacteristic) characteristics.get(0);
        assertEquals("DateCharacteristic", returned.getAtType());
        assertEquals("ActivationDate", returned.getName());
        assertThat(returned.getValue().toInstant().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(expectedDate.toInstant().truncatedTo(ChronoUnit.SECONDS));
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithInstallmentCharge_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        InstallmentCharge installmentCharge = InstallmentCharge.builder().atType("InstallmentCharge").priceType("installment")
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                .interestRate(0.03F).downPayment(200F).partner("Orange").externalId("EXT-INST-001").productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                .build();
        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(installmentCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        List<ProductPrice> productPrices = productResponseContent.getProductPrice();
        assertEquals(1, productPrices.size());
        InstallmentCharge returned = (InstallmentCharge) productPrices.get(0);
        assertEquals("InstallmentCharge", returned.getAtType());
        assertEquals("installment", returned.getPriceType());
        assertEquals(0.03F, returned.getInterestRate());
        assertEquals(200F, returned.getDownPayment());
        assertEquals("Orange", returned.getPartner());

    }
    @Test
    void givenProductWithInstallmentChargeWithoutPriceType_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        InstallmentCharge installmentCharge = InstallmentCharge.builder().atType("InstallmentCharge")
                .priceType(null) // NOT mandatory
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build()).interestRate(0.03F).downPayment(200F).partner("Orange").externalId("EXT-INST-001")
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                .build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(installmentCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
    }
    @Test
    void givenProductWithNonInstallmentChargeWithoutPriceType_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());


        ProductPrice recurringCharge = ProductPrice.builder().atType("recurringCharge")
                .priceType(null) // INVALID
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                .productOfferingPrice(ProductOfferingPriceRef.builder()
                        .id(VALID_PRODUCT_OFFERING_PRICE_ID)
                        .build())
                .build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(recurringCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PRICE_TYPE_IS_MANDATORY_EXCEPT_FOR_NON_INSTALLMENT_CHARGE, INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithNonInstallmentChargeWithPriceType_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());


        ProductPrice recurringCharge = ProductPrice.builder().atType("recurringCharge")
                .priceType(PRICE_TYPE_RECURRING).recurringChargePeriod(MeasuredValue.builder().amount(AMOUNT).units("day").build())
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                .productOfferingPrice(ProductOfferingPriceRef.builder()
                        .id(VALID_PRODUCT_OFFERING_PRICE_ID)
                        .build())
                .build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(recurringCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);

    }
    @Test
    void givenValidProductsWithApplicationOffset_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        Product product = createProductBuilderInnerRelationShipValidRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES, CREATED)
                .productPrice(List.of(ProductPrice.builder()
                        .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                        .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                        .priceType(PRICE_TYPE_RECURRING).recurringChargePeriod(MeasuredValue.builder().amount(AMOUNT).units("day").build())
                        .productPriceAlteration(List.of(PriceAlteration.builder().applicationDuration(
                                        Quantity.builder().amount(AMOUNT).units("day").build()).applicationOffset(3)
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build())
                                .build()
                        ))
                        .build()
                ))
                .build();

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<ProductPrice> productPrices = productResponseContent.getProductPrice();
        assertEquals(1, productPrices.size());
        ProductPrice returnedPrice = productPrices.get(0);
        assertNotNull(returnedPrice.getProductPriceAlteration());
        assertEquals(1, returnedPrice.getProductPriceAlteration().size());
        PriceAlteration returnedAlteration = returnedPrice.getProductPriceAlteration().get(0);
        assertEquals(3, returnedAlteration.getApplicationOffset());
    }
    @Test
    void givenProductWithPrice_whenPost_thenMoneyAmountsAreRoundedByCurrency() throws Exception {

        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        Price price = Price.builder()
                .dutyFreeAmount(new Money("EUR", 11.004F))
                .taxIncludedAmount(new Money("EUR", 13.095F))
                .taxRate(0.19F)
                .build();

        InstallmentCharge installmentCharge = InstallmentCharge.builder().atType("InstallmentCharge")
                .priceType("installment")
                .price(price).interestRate(0.03F).downPayment(200F)
                .partner("Orange").externalId("EXT-INST-003")
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                .build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID,  VALID_PRODUCT_SPECIFICATION_ID, SELLS,  List.of())).productPrice(List.of(installmentCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product response = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        InstallmentCharge returned = (InstallmentCharge) response.getProductPrice().get(0);
        Price returnedPrice = returned.getPrice();
        assertEquals(11.00F, returnedPrice.getDutyFreeAmount().getValue());
        assertEquals(13.10F, returnedPrice.getTaxIncludedAmount().getValue());
        assertEquals("EUR", returnedPrice.getDutyFreeAmount().getUnit());
    }

    @Test
    void givenProductWithInstallmentCharge_whenPost_thendownPaymentAreRounded() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        InstallmentCharge installmentCharge = InstallmentCharge.builder().atType("InstallmentCharge")
                .priceType("installment")
                .interestRate(0.25F).downPayment(200.987F)
                .partner("Orange").externalId("EXT-INST-002")
                .price(Price.builder().taxRate(0.19F).build())
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build()).build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(installmentCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product response = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        InstallmentCharge returned = (InstallmentCharge) response.getProductPrice().get(0);
        assertEquals(200.99F, returned.getDownPayment());
    }

    @Test
    void givenProductWithPriceInMGA_whenPost_thenMoneyAmountsAreRoundedByCurrency() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        Price price = Price.builder()
                .dutyFreeAmount(new Money("MGA", 11004.755F))
                .taxIncludedAmount(new Money("MGA", 13095.450F))
                .taxRate(0.20F)
                .build();

        InstallmentCharge installmentCharge = InstallmentCharge.builder()
                .atType("InstallmentCharge")
                .priceType("installment")
                .price(price)
                .interestRate(0.03F)
                .downPayment(200F)
                .partner("Orange")
                .externalId("EXT-INST-004")
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                .build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(installmentCharge)).build();

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product response = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        InstallmentCharge returned = (InstallmentCharge) response.getProductPrice().get(0);
        Price returnedPrice = returned.getPrice();
        assertEquals(11005F, returnedPrice.getDutyFreeAmount().getValue());
        assertEquals(13095F, returnedPrice.getTaxIncludedAmount().getValue());
        assertEquals("MGA", returnedPrice.getDutyFreeAmount().getUnit());
    }

    @Test
    void givenProductWithDownPaymentAndPriceCurrency_whenPost_thenDownPaymentRoundedByCurrency() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());

        Price price = Price.builder()
                .taxIncludedAmount(new Money("MGA", 10000F))
                .taxRate(0.20F)
                .build();

        InstallmentCharge installmentCharge = InstallmentCharge.builder()
                .atType("InstallmentCharge")
                .priceType("installment")
                .price(price)
                .interestRate(0.03F)
                .downPayment(200.987F)
                .partner("Orange")
                .externalId("EXT-INST-008")
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(VALID_PRODUCT_OFFERING_PRICE_ID).build())
                .build();

        Product product = createProductBuilderInnerRelation(VALID_PRODUCT_OFFERING_CONTRACT_ID, BUNDLES, CONTRACT.getValue(), createProductWithCharacteristics(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, SELLS, List.of())).productPrice(List.of(installmentCharge)).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product response = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        InstallmentCharge returned = (InstallmentCharge) response.getProductPrice().get(0);
        assertEquals(201F, returned.getDownPayment());
    }

    // ========== IDOR PROTECTION TESTS FOR PRODUCT CREATION ==========

    @Test
    void givenNonAdminUser_whenCreateProductForSelf_thenSuccess() throws Exception {
        // Given: User creates a product for themselves
        String userRelatedPartyId = "106";
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID,
                VALID_PRODUCT_SPECIFICATION_ID,
                VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .relatedParty(List.of(createRelatedPartyRefOrPartyRoleRef(userRelatedPartyId, "User Party")))
                .build();

        // When: User creates the product (with CREATE permission x500, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x500", userRelatedPartyId,
                contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Product is created successfully
        resultActions.andExpect(status().isCreated());
        Product createdProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
    }

    @Test
    void givenNonAdminUser_whenCreateProductForAnotherUser_thenAccessDenied() throws Exception {
        // Given: User tries to create a product for another user
        String userRelatedPartyId = "106";
        String otherRelatedPartyId = "107";

        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID,
                VALID_PRODUCT_SPECIFICATION_ID,
                VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .relatedParty(List.of(createRelatedPartyRefOrPartyRoleRef(otherRelatedPartyId, "Other Party")))
                .build();

        // When: User attempts to create the product for another user (with CREATE permission x500, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x500", userRelatedPartyId,
                contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenNonAdminUser_whenCreateProductWithoutRelatedParty_thenSuccess() throws Exception {
        // Given: User creates a product without specifying relatedParty (service may assign it)
        String userRelatedPartyId = "106";

        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID,
                VALID_PRODUCT_SPECIFICATION_ID,
                VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .relatedParty(null) // No related party specified
                .build();

        // When: User creates the product (with CREATE permission x500, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x500", userRelatedPartyId,
                contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Product creation is allowed (service layer handles it)
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void givenAdminUser_whenCreateProductForAnyUser_thenSuccess() throws Exception {
        // Given: Admin creates a product for any user
        String anyRelatedPartyId = "999";

        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID,
                VALID_PRODUCT_SPECIFICATION_ID,
                VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .relatedParty(List.of(createRelatedPartyRefOrPartyRoleRef(anyRelatedPartyId, "Any Party")))
                .build();

        // When: Admin creates the product (using default admin auth which includes admin role)
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT,
                contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Product is created successfully
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void givenUserWithoutRelatedPartyIdInToken_whenCreateProduct_thenAccessDenied() throws Exception {
        // Given: User without relatedPartyId in token tries to create a product
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID,
                VALID_PRODUCT_SPECIFICATION_ID,
                VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .relatedParty(List.of(createRelatedPartyRefOrPartyRoleRef("106", "Some Party")))
                .build();

        // When: User without relatedPartyId tries to create (with CREATE permission x500, without admin role, without relatedPartyId)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x500", null,
                contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenNonAdminUser_whenCreateProductWithMultipleRelatedPartiesIncludingOthers_thenAccessDenied() throws Exception {
        // Given: User tries to create a product with multiple related parties including another user
        String userRelatedPartyId = "106";
        String otherRelatedPartyId = "107";

        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID,
                VALID_PRODUCT_SPECIFICATION_ID,
                VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .relatedParty(List.of(
                        createRelatedPartyRefOrPartyRoleRef(userRelatedPartyId, "User Party"),
                        createRelatedPartyRefOrPartyRoleRef(otherRelatedPartyId, "Other Party")
                ))
                .build();

        // When: User attempts to create the product (with CREATE permission x500, without admin role)
        ResultActions resultActions = callRestfulEndpointAsNonAdminUser(mockMvc, POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x500", userRelatedPartyId,
                contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        // Then: Access is denied
        resultActions.andExpect(status().isForbidden());
    }

    /**
     * Helper method to create RelatedPartyOrPartyRole for test payloads
     */
    private RelatedPartyOrPartyRole createRelatedPartyRefOrPartyRoleRef(String id, String name) {
        PartyRef partyRef = new PartyRef();
        partyRef.setId(id);
        partyRef.setName(name);
        partyRef.setAtType("PartyRef");
        partyRef.setAtReferredType("individual");

        RelatedPartyOrPartyRole relatedParty = new RelatedPartyOrPartyRole();
        relatedParty.setPartyOrPartyRole(partyRef);
        relatedParty.setRole("customer");
        relatedParty.setAtType("RelatedPartyRefOrPartyRoleRef");

        return relatedParty;
    }
}


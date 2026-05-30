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
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.enumerate.ResourceEntityType;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.creator.PhysicalProductCreator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.BUNDLES;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForCreateProductEvent;
import static com.orange.discobole.productinventory.util.creator.PhysicalProductCreator.*;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createProductBuilderInnerRelationShip;
import static com.orange.discobole.productinventory.util.creator.ShipmentProductCreator.createShipmentProductBuilderWithRelation;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateTangibleProductTest extends AbstractTest {


    @Test
    void givenProductWithProductAtTypeValue_whenCreate_thenAtTypeIsProduct() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_BUNDLE_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createProductBuilderInnerRelationShip(VALID_BUNDLE_PRODUCT_OFFERING_ID,
                VALID_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, BUNDLE_PRODUCT_OFFERING.getValue()).build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        assertEquals(ProductTypeEnum.PRODUCT.getValue(), productEntity.getAtType());
        consumeAndAssertEqualityForCreateProductEvent(productResponseContent);
    }

    @Test
    void givenPhysicalProductWithProductSpecificationHasNotStockItem_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_WITHOUT_STOCK_ITEM_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_BUNDLE_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        Product product = createPhysicalProductInnerRelationShip(VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, BUNDLE_PRODUCT_OFFERING.getValue(), VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id(VALID_REALIZING_RESOURCE_ID).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_SPECIFICATION_HAS_NOT_STOCK_ITEM_TYPE_IN_SUPPORT_ENTITY, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID), INVALID_INPUT.getStatus());
    }

    @Test
    void givenTangibleProductWithRealizingResource_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID).build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id(VALID_REALIZING_RESOURCE_ID).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        consumeAndAssertEqualityForCreateProductEvent(productResponseContent);

    }

    @Test
    void givenTangibleProductWithRealizingResourceAndNullRealizingResourceId_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createPhysicalProductInnerRelationShip(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue(), VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id(NULL_ID).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), TANGIBLE_PRODUCT_REALIZING_RESOURCE_ID_CANNOT_BE_NULL, INVALID_INPUT.getStatus());
    }

    @Test
    void givenTangibleProductWithRealizingResourceAndWrongSerialNumber_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        Product product = createPhysicalProductInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue(), "WRONG_VALUE").build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id(VALID_REALIZING_RESOURCE_ID).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());

        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(RESOURCE_SERIAL_NUMBER_DIFFERENT, VALID_REALIZING_RESOURCE_ID, VALID_REALIZING_RESOURCE_SERIAL_NUMBER, "WRONG_VALUE"), INVALID_INPUT.getStatus());
    }

    @Test
    void givenTangibleProductWithUnreservedRealizingResource_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, REALIZING_RESOURCE_ID_WITH_INVALID_STATUS, RESOURCE_INVENTORY_MANAGEMENT_INVALID_RESOURCE_STATUS_JSON_FILE_PATH, HttpStatus.OK.value());
        Product product = createPhysicalProductInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue(), REALIZING_RESOURCE_ID_WITH_INVALID_STATUS).build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id(REALIZING_RESOURCE_ID_WITH_INVALID_STATUS).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductSpecification().setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);

        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(RESOURCE_NOT_RESERVED, REALIZING_RESOURCE_ID_WITH_INVALID_STATUS), INVALID_INPUT.getStatus());
    }

    @Test
    void givenTangibleProductWithNonExistentRealizingResource_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, "REALIZING_RESOURCE_NONEXISTENT_ID", null, HttpStatus.NOT_FOUND.value());
        Product product = createPhysicalProductInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue(), VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id("REALIZING_RESOURCE_NONEXISTENT_ID").atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductSpecification().setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);

        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotFoundErrorExists(error, RESOURCE_NOT_FOUND.getCode(), String.format(RESOURCE_DOESN_T_EXIST, "REALIZING_RESOURCE_NONEXISTENT_ID"), RESOURCE_NOT_FOUND.getStatus());

    }

    @Test
    void givenTangibleProductWithNonTangibleRealizingResource_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, NON_TANGIBLE_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_INVALID_TYPE_JSON_FILE_PATH, HttpStatus.OK.value());
        Product product = createPhysicalProductInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue(), VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build();
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getRealizingResource().get(0).id(NON_TANGIBLE_REALIZING_RESOURCE_ID).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue());
        ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct()).getProductSpecification().setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);

        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(RESOURCE_NOT_TANGIBLE, NON_TANGIBLE_REALIZING_RESOURCE_ID), INVALID_INPUT.getStatus());
    }

    @Test
    void givenProductWithPhysicalProductAtTypeValueAndNullSerialNumber_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(TANGIBLE_PRODUCT_SPECIFICATION_JSON, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createPhysicalProductInnerRelationShip(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue(), null).build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), EMPTY_PRODUCT_SERIAL_NUMBER, MISSING_INPUT.getStatus());
    }

    @Test
    void givenProductContractWithPhysicalProductAtTypeValue_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = PhysicalProductCreator.createContractPhysicalProduct(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue()).build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(TANGIBLE_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL, CONTRACT.getValue()), INVALID_INPUT.getStatus());
    }


    @Test
    void givenTangibleProductWithLogicalAndPhysicalRealizingResource_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_BUNDLE_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID).build();
        Product productSpecification = ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct());

        productSpecification
                .realizingResource(createRealizingResourceListOneIsPhysical())
                .getProductSpecification();

        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
    }

    @Test
    void givenTangibleProductWithoutRootProduct_whenCreate_thenAtomicProductIsRootProduct() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        PhysicalProduct product = ((PhysicalProduct) createAtomicPhysicalProduct(VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build());

        PhysicalProduct productSpecification = ((PhysicalProduct) product.getProductRelationship().get(0).getProduct());
        productSpecification
                .realizingResource(createRealizingResourceListOneIsPhysical())
                .getProductSpecification();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        Assertions.assertNotNull(productEntity);
        Assertions.assertNotEquals(0, productResponseContent.getProductRelationship().size());
        ProductEntity product1 = mongoTemplate.findById(productEntity.getProductRelationship().get(0).getProduct().getId(), ProductEntity.class);
        Assertions.assertNotNull(product1);
        Assertions.assertNotEquals(0, product1.getProductRelationship().size());
        Assertions.assertEquals(ProductRelationshipType.ROOTPRODUCT.getValue(), product1.getProductRelationship().get(0).getRelationshipType());
        Assertions.assertEquals(new ObjectId(productResponseContent.getId()), (product1.getProductRelationship().get(0).getProduct().getId()));
    }

    @Test
    void givenShipmentProductWithoutRootProduct_whenCreate_thenAtomicProductIsRootProduct() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, SHIPMENT_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_SHIPMENT_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        ShipmentProduct product = (ShipmentProduct)
                createShipmentProductBuilderWithRelation(
                        CREATED,
                        randomAlphabetic(STRING_SIZE),
                        randomAlphabetic(STRING_SIZE),
                        VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID,
                        VALID_SHIPMENT_ATOMIC_PRODUCT_OFFERING_ID,
                        ATOMIC_PRODUCT_OFFERING.getValue(),
                        ProductRelationshipType.SELLS
                ).build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assert productEntity != null;
        Assertions.assertNotEquals(0, productResponseContent.getProductRelationship().size());
        ProductEntity product1 = mongoTemplate.findById(productEntity.getProductRelationship().get(0).getProduct().getId(), ProductEntity.class);
        Assertions.assertNotEquals(0, product1.getProductRelationship().size());
        Assertions.assertEquals(ProductRelationshipType.ROOTPRODUCT.getValue(), product1.getProductRelationship().get(0).getRelationshipType());
        Assertions.assertEquals(new ObjectId(productResponseContent.getId()), (product1.getProductRelationship().get(0).getProduct().getId()));
    }

    @Test
    void givenContract_whenAttachingAtomicPhysicalProduct_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());

        Product productContract = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions productContractResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productContract), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        productContractResultActions.andExpect(status().isCreated());
        Product productContractResponseContent = readJsonFromAPIResponse(productContractResultActions, new TypeReference<>() {
        });
        PhysicalProduct productAtomic = ((PhysicalProduct) createAtomicPhysicalProduct(VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build());
        PhysicalProduct productSpecification = ((PhysicalProduct) productAtomic.getProductRelationship().get(0).getProduct());
        productSpecification
                .realizingResource(createRealizingResourceListOneIsPhysical())
                .getProductSpecification()
                .setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);
        productAtomic.setProductRelationship(new ArrayList<>(productAtomic.getProductRelationship()));
        productAtomic.getProductRelationship().add(
                ProductRelationship.builder()
                        .relationshipType(ProductRelationshipType.HASPARENT.getValue())
                        .product(ProductRef.builder().id(productContractResponseContent.getId()).build()).build());
        ResultActions productAtomicResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(toJsonString((productAtomic))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        productAtomicResultActions.andExpect(status().isCreated());
        Product atomicProductResponseContent = readJsonFromAPIResponse(productAtomicResultActions, new TypeReference<>() {
        });
        ResultActions atomicProductResultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productContractResponseContent.getId()));
        atomicProductResultActions.andExpect(status().isOk());
        Product actualAtomicProduct = readJsonFromAPIResponse(atomicProductResultActions, new TypeReference<>() {
        });
        Assertions.assertTrue(actualAtomicProduct.getProductRelationship().stream().anyMatch(productRelationship -> (
                productRelationship.getRelationshipType().equals(BUNDLES.getValue()) &&
                        ((ProductRef) productRelationship.getProduct()).getId().equals(atomicProductResponseContent.getId()))));

    }

    @Test
    void givenContract_whenAttachingNonexistentAtomicPhysicalProduct_thenNotFound() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        PhysicalProduct productAtomic = ((PhysicalProduct) createAtomicPhysicalProduct(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build());
        PhysicalProduct productSpecification = ((PhysicalProduct) productAtomic.getProductRelationship().get(0).getProduct());
        productSpecification
                .realizingResource(createRealizingResourceListOneIsPhysical())
                .getProductSpecification()
                .setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);
        productAtomic.setProductRelationship(new ArrayList<>(productAtomic.getProductRelationship()));
        String hasParentId = ObjectId.get().toString();
        productAtomic.getProductRelationship().add(
                ProductRelationship.builder()
                        .relationshipType(ProductRelationshipType.HASPARENT.getValue())
                        .product(ProductRef.builder().id(hasParentId).build()).build());
        ResultActions productAtomicResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(toJsonString((productAtomic))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(productAtomicResultActions, new TypeReference<>() {
        });
        productAtomicResultActions.andExpect(status().isNotFound());
        assertNotFoundErrorExists(error, RESOURCE_NOT_FOUND.getCode(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, hasParentId), RESOURCE_NOT_FOUND.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aborted", "Cancelled", "Terminated", "Sold"})
    void givenContract_whenAttachingAtomicPhysicalProductWithParent_HavingInvalidStatus_thenBadRequest(String status) throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());

        Product productContract = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions productContractResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productContract), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        productContractResultActions.andExpect(status().isCreated());
        Product productContractResponseContent = readJsonFromAPIResponse(productContractResultActions, new TypeReference<>() {
        });
        ProductEntity actualProductEntity = mongoTemplate.findById(productContractResponseContent.getId(), ProductEntity.class);
        assertNotNull(actualProductEntity);
        actualProductEntity.setStatus(ProductStatusType.fromValue(status));
        mongoTemplate.save(actualProductEntity);
        PhysicalProduct productAtomic = ((PhysicalProduct) createAtomicPhysicalProduct(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build());
        PhysicalProduct productSpecification = ((PhysicalProduct) productAtomic.getProductRelationship().get(0).getProduct());
        productSpecification
                .realizingResource(createRealizingResourceListOneIsPhysical())
                .getProductSpecification()
                .setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);
        productAtomic.setProductRelationship(new ArrayList<>(productAtomic.getProductRelationship()));
        productAtomic.getProductRelationship().add(
                ProductRelationship.builder()
                        .relationshipType(ProductRelationshipType.HASPARENT.getValue())
                        .product(ProductRef.builder().id(productContractResponseContent.getId()).build()).build());
        ResultActions productAtomicResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(toJsonString((productAtomic))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        productAtomicResultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(productAtomicResultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID, INVALID_INPUT.getStatus());

    }

    @ParameterizedTest
    @ValueSource(strings = {"PendingCancel", "PendingTerminate"})
    void givenContract_whenAttachingAtomicPhysicalProductWithParent_HavingInvalidOperationalStatus_thenBadRequest(String status) throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_PRICE_URL, VALID_PRODUCT_OFFERING_PRICE_ID, PRODUCT_OFFERING_PRICE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        mockInventoryUrl(RESOURCE_INVENTORY_MANAGEMENT, VALID_REALIZING_RESOURCE_ID, RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID, TANGIBLE_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());

        Product productContract = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions productContractResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(productContract), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        productContractResultActions.andExpect(status().isCreated());
        Product productContractResponseContent = readJsonFromAPIResponse(productContractResultActions, new TypeReference<>() {
        });
        ProductEntity actualProductEntity = mongoTemplate.findById(productContractResponseContent.getId(), ProductEntity.class);
        assertNotNull(actualProductEntity);
        actualProductEntity.setOperationalStatus(ProductOperationalStatusType.fromValue(status));
        mongoTemplate.save(actualProductEntity);
        PhysicalProduct productAtomic = ((PhysicalProduct) createAtomicPhysicalProduct(VALID_ATOMIC_PRODUCT_OFFERING_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_REALIZING_RESOURCE_SERIAL_NUMBER).build());
        PhysicalProduct productSpecification = ((PhysicalProduct) productAtomic.getProductRelationship().get(0).getProduct());
        productSpecification
                .realizingResource(createRealizingResourceListOneIsPhysical())
                .getProductSpecification()
                .setId(VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID);
        productAtomic.setProductRelationship(new ArrayList<>(productAtomic.getProductRelationship()));
        productAtomic.getProductRelationship().add(
                ProductRelationship.builder()
                        .relationshipType(ProductRelationshipType.HASPARENT.getValue())
                        .product(ProductRef.builder().id(productContractResponseContent.getId()).build()).build());
        ResultActions productAtomicResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(toJsonString((productAtomic))), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        productAtomicResultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(productAtomicResultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_OPERATIONAL_STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID, INVALID_INPUT.getStatus());

    }
}


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
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.CONTRACT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.PRODUCT_SPECIFICATION_HAS_NOT_CFS_SPEC_OR_SHIPMENT_PRODUCT_SPECIFICATION;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.SHIPMENT_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForCreateProductEvent;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships;
import static com.orange.discobole.productinventory.util.creator.ShipmentProductCreator.createContractShipmentProduct;
import static com.orange.discobole.productinventory.util.creator.ShipmentProductCreator.createShipmentProductInnerRelationShip;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateShipmentProductTest extends AbstractTest {


    @Test
    void givenShipmentProductWithProductSpecificationHasNotCFSSpecOrShipmentProductSpecification_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, SHIPMENT_PRODUCT_SPECIFICATION_WITHOUT_AT_BASE_TYPE_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createShipmentProductInnerRelationShip(VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, BUNDLE_PRODUCT_OFFERING.getValue()).build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(PRODUCT_SPECIFICATION_HAS_NOT_CFS_SPEC_OR_SHIPMENT_PRODUCT_SPECIFICATION, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID), INVALID_INPUT.getStatus());
    }


   @Test
   @Disabled("Test is skipped")
    void givenProductWithShipmentProductAtTypeValue_whenCreate_thenAtTypeIsShipmentProduct() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, SHIPMENT_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_SHIPMENT_ATOMIC_PRODUCT_OFFERING_ID).build();
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
    void givenProductContractWithShipmentProductAtTypeValue_whenCreate_thenBadRequest() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, SHIPMENT_PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractShipmentProduct(VALID_SHIPMENT_ATOMIC_PRODUCT_OFFERING_ID, VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID, ProductRelationshipType.BUNDLES, CREATED, CONTRACT.getValue()).build();
        final String productJson = toJsonString((product));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(SHIPMENT_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL, CONTRACT.getValue()), INVALID_INPUT.getStatus());
    }


}


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
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.BUNDLES;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.*;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "config.enable.productCatalog=false"
})
class CatalogValidationDisabledApiTest extends AbstractTest {
    @Test
    void givenProductWithAtTypeSimCard_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        SimCard product = (SimCard) createProductSimCardBuilderWithProductOfferingAndRelationship(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES).atType(ProductTypeEnum.SIM_CARD.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertEquals(productEntity.getAtType(), ProductTypeEnum.SIM_CARD.getValue());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }
    @Test
    void givenProductWithAtTypeOffer_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Offer product = (Offer) createProductOfferBuilderWithProductOfferingAndRelationship(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES).atType(ProductTypeEnum.OFFER.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertEquals(productEntity.getAtType(), ProductTypeEnum.OFFER.getValue());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithAtTypeMobileLine_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        MobileLine product = (MobileLine) createProductMobileLineBuilderWithProductOfferingAndRelationship(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES).atType(ProductTypeEnum.MOBILE_LINE.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertEquals(productEntity.getAtType(), ProductTypeEnum.MOBILE_LINE.getValue());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }

    @Test
    void givenProductWithAtTypeService_whenCreate_thenCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Service product = (Service) createProductServiceBuilderWithProductOfferingAndRelationship(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, BUNDLES).atType(ProductTypeEnum.SERVICE.getValue()).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        ProductEntity productEntity = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        assertNotNull(productEntity);
        assertEquals(productEntity.getAtType(), ProductTypeEnum.SERVICE.getValue());
        assertProductDtoEqualsToProductEntity(productResponseContent, productEntity, true);
    }
}


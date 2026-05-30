// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.security;

import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductSpecificationEntityBuilder;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductApiAuthorityTest extends AbstractTest {
    @Test
    void givenProductCreationRequest_withInvalidAuth_thenForbidden() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product =
                createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                        .build();
        Product atomic = ((Product) ((Product) product.getProductRelationship().get(0).getProduct()).getProductRelationship().get(0).getProduct());
        atomic.setProductRelationship(new ArrayList<>());
        atomic.setProductSpecification(null);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x88", contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isForbidden());
        assertThat(mongoTemplate.count(new Query(), ProductEntity.class)).isZero();
    }

    @Test
    void givenEmptyDatabase_whenGetProductsWithNoFieldsDefined_thenEmptyListRetrieved() throws Exception {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, "x88");
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenValidProductIdOfProductSpec_whenPatchWithValidStatus_thenSucceed() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        expectedProductEntity.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        mongoTemplate.save(expectedProductEntity);
        Product productPatch = Product.builder().status(ProductStatusType.ACTIVE).build();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId), "x88", contentBody(productPatch),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isForbidden());
    }
}


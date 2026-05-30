// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.config.ApplicationConfigProperties;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderDeliveryScenarioTest extends AbstractTest {

    @Autowired
    private ApplicationConfigProperties applicationConfigProperties;

    @Test
    void givenMobileBasicOffer_whenActivatingAllProducts_thenSuccess() throws Exception {
        executeOfferTest(OffersData.BASIC_OFFER_PATH, OffersData.BASIC_OFFER_RELIES_FROM_RELATIONS);
    }

    @Test
    void givenMobileMaxPlusOffer_whenActivatingAllProducts_thenSuccess() throws Exception {
        executeOfferTest(OffersData.MAX_PLUS_PATH, OffersData.MAX_PLUS_OFFER_RELIES_FROM_RELATIONS);
    }

    private void executeOfferTest(String offerPath, Map<String, List<String>> offerRelations) throws Exception {
        Product addedProducts = addMobileOffer(offerPath);
        confirmProductsIfNeeded(addedProducts);
        Map<String, String> productsMap = new HashMap<>();
        extractPSProducts(addedProducts, productsMap);
        addPSRelations(productsMap, offerRelations);
        activateProductsInCorrectOrder(productsMap, offerRelations);
    }

    private Product addMobileOffer(String filePath) throws Exception {
        final boolean enabledCatalog = applicationConfigProperties.isEnableProductCatalogCheck();
        final boolean enabledResourceInventory = applicationConfigProperties.isEnableResourceInventoryManagementCheck();
        applicationConfigProperties.setEnableProductCatalogCheck(false);
        applicationConfigProperties.setEnableResourceInventoryManagementCheck(false);
        String body = Files.readString(Paths.get(filePath));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBodyJson(body), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        Product productResponse = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        applicationConfigProperties.setEnableProductCatalogCheck(enabledCatalog);
        applicationConfigProperties.setEnableResourceInventoryManagementCheck(enabledResourceInventory);
        return productResponse;
    }

    private void confirmProductsIfNeeded(Product addedProducts) {
        List<String> productsToConfirm = new ArrayList<>();
        extractProductsToConfirm(addedProducts, productsToConfirm);
        List<ProductPatch> patchOperations = new ArrayList<>();
        productsToConfirm.forEach(productId ->
                patchOperations.add(
                        ProductPatch.builder().op(PatchOperationType.REPLACE).path("/productInventoryManagement/v1/product/%s/operationalStatus".formatted(productId)).value("Confirmed")
                                .build()
                )
        );
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH),
                contentBody(patchOperations));
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    private void extractProductsToConfirm(Product product, List<String> productsToConfirm) {
        if (product.getOperationalStatus().equals(ProductOperationalStatusType.CREATED) && product.getAtType().equals(ProductTypeEnum.PRODUCT.getValue())) {
            productsToConfirm.add(product.getId());
        }
        product.getProductRelationship().forEach(productRelationship -> {
            if (productRelationship.getProduct() instanceof Product p) {
                extractProductsToConfirm(p, productsToConfirm);
            }
        });
    }

    private void extractPSProducts(Product product, Map<String, String> productsMap) {
        if (Objects.nonNull(product.getProductSpecification())) {
            productsMap.put(product.getName(), product.getId());
        }
        if (Objects.nonNull(product.getProductRelationship())) {
            product.getProductRelationship().forEach(productRelationship -> {
                if (productRelationship.getProduct() instanceof Product p) {
                    extractPSProducts(p, productsMap);
                }
            });
        }
    }

    private void addPSRelations(Map<String, String> productsMap, Map<String, List<String>> relations) {
        List<ProductPatch> patchOperations = new ArrayList<>();
        relations.keySet().forEach(source ->
                relations.get(source).forEach(destination -> {
                    patchOperations.add(getProductPatchRelation(ProductRelationshipType.RELIESFROM, productsMap.get(source), productsMap.get(destination)));
                    patchOperations.add(getProductPatchRelation(ProductRelationshipType.RELIESON, productsMap.get(destination), productsMap.get(source)));
                })
        );
        ResultActions resultActions = callRestfulEndpoint(mockMvc, PATCH, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_PATCH_PATCH),
                contentBody(patchOperations));
        assertThat(readStatusFromAPIResponse(resultActions)).isEqualTo(HttpStatus.OK.value());
    }

    private ProductPatch getProductPatchRelation(ProductRelationshipType productRelationshipType, String sourceProductId, String destinationProductId) {
        return ProductPatch.builder().op(PatchOperationType.ADD).path("/productInventoryManagement/v1/product/%s/productRelationship/-".formatted(sourceProductId))
                .value(
                        ProductRelationship.builder().relationshipType(productRelationshipType.getValue()).product(
                                ProductRef.builder().id(destinationProductId).atType("ProductRef").build()
                        ).build()
                ).build();
    }

    private void activateProductsInCorrectOrder(Map<String, String> productsMap, Map<String, List<String>> reliesFromRelations) {
        Map<String, Integer> relationsCount = new HashMap<>();
        for (var entry : reliesFromRelations.entrySet()) {
            relationsCount.putIfAbsent(entry.getKey(), 0);
            for (String destination : entry.getValue()) {
                relationsCount.putIfAbsent(destination, 0);
                relationsCount.put(destination, relationsCount.get(destination) + 1);
            }
        }
        LinkedList<String> readyToActivateProducts = new LinkedList<>(
                relationsCount.keySet().stream().filter(key -> relationsCount.get(key) == 0).toList()
        );
        while (!readyToActivateProducts.isEmpty()) {
            activateProduct(productsMap.get(readyToActivateProducts.removeFirst()));
            for (var entry : reliesFromRelations.entrySet()) {
                for (String destination : entry.getValue()) {
                    relationsCount.put(destination, relationsCount.get(destination) - 1);
                    if (relationsCount.get(destination) == 0) {
                        readyToActivateProducts.push(destination);
                    }
                }
            }
        }
    }

    private void activateProduct(String productId)  {
        ResultActions getResultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId));
        Product product = readJsonFromAPIResponse(getResultActions, new TypeReference<>() {
        });
        if (product.getAtType().equals(ProductTypeEnum.PRODUCT.getValue())) {
            product.setStatus(ProductStatusType.ACTIVE);
            product.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        } else {
            product.setStatus(ProductStatusType.SOLD);
            product.setOperationalStatus(ProductOperationalStatusType.SOLD);
        }
        product.setHref(null);
        ResultActions patchResultActions = callRestfulEndpoint(mockMvc, PATCH, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, productId), contentBody(product),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Product activatedProduct = readJsonFromAPIResponse(patchResultActions, new TypeReference<>() {
        });
        Assertions.assertTrue(
                ProductStatusType.ACTIVE.getValue().equals(activatedProduct.getStatus().getValue()) ||
                        ProductStatusType.SOLD.getValue().equals(activatedProduct.getStatus().getValue())
        );
    }
}

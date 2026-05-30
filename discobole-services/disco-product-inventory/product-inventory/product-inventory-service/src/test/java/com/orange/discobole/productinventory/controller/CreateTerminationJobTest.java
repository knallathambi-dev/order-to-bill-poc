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
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.service.impl.JobSchedulerServiceImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createProductBuilderWithProductSpecification;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateTerminationJobTest extends AbstractTest {
    @Autowired
    private JobSchedulerServiceImpl jobSchedulerService;

    @Test
    void givenProductHierarchyWithMissingProduct_whenCreateTerminationJobSpecification_thenRestOfProductsAreTerminated() throws Exception {
        // Mock product catalog URLs
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());

        // Create termination job specification
        TerminationJobSpecification terminationJobSpecification = createTerminationJobSpecificationWithImmediateJobScheduler();
        List<JobEntity> jobEntities = assertAndGetTerminationJobCreatedFromSpec(terminationJobSpecification.getId());
        final JobEntity jobEntity = jobEntities.get(0);
        // Create a product hierarchy with relationships
        Product rootProduct = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(
                VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                .build();

        // Navigate to the deeply nested product
        Product nestedProduct = (Product) ((Product) rootProduct.getProductRelationship().get(0).getProduct())
                .getProductRelationship().get(0).getProduct();

        // Create a product specification for testing
        Product productSpec = createProductBuilderWithProductSpecification(
                ProductStatusType.CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID)
                .build();

        // Keep this part unchanged
        // -------------------------------------------------
        nestedProduct.setProductSpecification(null);
        nestedProduct.setProductRelationship(List.of(
                ProductRelationship.builder()
                        .relationshipType(ProductRelationshipType.SELLS.getValue())
                        .product(productSpec)
                        .build()
        ));
        // -------------------------------------------------

        // Convert product to JSON and send API request
        final String productJson = toJsonString(rootProduct);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT,
                contentBodyJson(productJson), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());

        // Parse API response
        Product productResponseContent = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        // Extract deeply nested product from response
        Product responseNestedProduct = (Product) ((Product) ((Product) productResponseContent.getProductRelationship().get(0).getProduct())
                .getProductRelationship().get(0).getProduct())
                .getProductRelationship().get(0).getProduct();

        // Retrieve the product entity from MongoDB
        ProductEntity productEntity = mongoTemplate.findById(responseNestedProduct.getId(), ProductEntity.class);
        Assertions.assertNotNull(productEntity, "Product entity should exist in the database");
        Query query = new Query().addCriteria(Criteria.where(
                        ProductEntity.Fields.id)
                .is(responseNestedProduct.getId()));
        // Remove test data from MongoDB
        mongoTemplate.remove(query, ProductEntity.class);

        // Retrieve contract entity
        ProductEntity contract = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        Assertions.assertNotNull(contract, "Contract entity should exist in the database");

        // Prepare contract for termination
        contract.setStatus(ProductStatusType.ACTIVE);
        contract.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
        contract.setTerminationDate(OffsetDateTime.now().minusDays(1));
        mongoTemplate.save(contract);

        // Query all products related to this contract
        Query relatedProductsQuery = new Query().addCriteria(Criteria.where(
                        ProductEntity.Fields.productRelationship + "." + ProductRelationshipEntity.Fields.product + "." + ProductEntity.Fields.id)
                .is(contract.getId()));

        List<ProductEntity> relatedProducts = mongoTemplate.find(relatedProductsQuery, ProductEntity.class);

        // Activate related products
        relatedProducts.forEach(product -> {
            product.setStatus(ProductStatusType.ACTIVE);
            product.setOperationalStatus(ProductOperationalStatusType.ACTIVE);
            mongoTemplate.save(product);
        });


        // Execute scheduled termination jobs
        jobSchedulerService.executeScheduledJobs();

        // Validate that contract is terminated
        contract = mongoTemplate.findById(productResponseContent.getId(), ProductEntity.class);
        JobEntity byId = mongoTemplate.findById(jobEntity.getId(), JobEntity.class);

        Assertions.assertNotNull(byId, "job should exist");
        Assertions.assertNotNull(contract, "Contract should exist after job execution");
        Assertions.assertEquals(ProductStatusType.TERMINATED, contract.getStatus(), "Contract should be terminated");
        Assertions.assertEquals(JobStatusType.SUCCEEDED, byId.getStatus(), "Job should be Succeeded");

        // Validate that all related products are terminated
        relatedProducts = mongoTemplate.find(relatedProductsQuery, ProductEntity.class);
        relatedProducts.forEach(product -> {
            Assertions.assertEquals(ProductStatusType.TERMINATED, product.getStatus(), "All related products should be terminated");
        });
    }
}

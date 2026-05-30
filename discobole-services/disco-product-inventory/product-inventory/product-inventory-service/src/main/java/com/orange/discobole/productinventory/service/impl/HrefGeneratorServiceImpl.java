// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.controller.impl.JobApiImpl;
import com.orange.discobole.productinventory.controller.impl.JobSpecificationApiImpl;
import com.orange.discobole.productinventory.controller.impl.ProductApiImpl;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.service.HrefGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.*;

@Service
@Slf4j
public class HrefGeneratorServiceImpl implements HrefGeneratorService {

    @Override
    public String getBaseUrlProductApi() {
        return WebMvcLinkBuilder
                .linkTo(ProductApiImpl.class)
                .slash(PRODUCT_INVENTORY_MANAGEMENT_BASE_URL)
                .slash(PRODUCT_SUB_PATH)
                .withSelfRel().getHref();
    }

    @Override
    public String getProductHref(String productId, String baseUrl) {
        return String.format("%s/%s", baseUrl, productId);
    }

    @Override
    public void generateHrefProductRelationships(Collection<? extends ProductRefOrValue> products) {
        String baseUrl = getBaseUrlProductApi();
        generateHrefProductRelationships(products, baseUrl);
    }

    private void generateHrefProductRelationships(Collection<? extends ProductRefOrValue> products, String baseUrl) {
        products.forEach(product -> {
            generateHref(product, baseUrl);
            if (product instanceof Product p && !CollectionUtils.isEmpty(p.getProductRelationship())) {
                List<ProductRefOrValue> relationshipProducts = p.getProductRelationship().stream().map(ProductRelationship::getProduct).toList();
                relationshipProducts.forEach(relatedProduct -> generateHref(relatedProduct, baseUrl));
                generateHrefProductRelationships(relationshipProducts);
            }
        });
    }

    private void generateHref(ProductRefOrValue productDTO, String baseUrl) {
        if (productDTO instanceof Product product) {
            String productId = product.getId(); // Assuming the ProductDTO has an ID field
            String selfLink = getProductHref(productId, baseUrl);
            product.setHref(selfLink);
        } else if (productDTO instanceof ProductRef productRef) {
            String productId = productRef.getId(); // Assuming the ProductDTO has an ID field
            String selfLink = getProductHref(productId, baseUrl);
            productRef.setHref(selfLink);
        }
    }

    public String getBaseUrlJobSpecificationApi() {
        return WebMvcLinkBuilder
                .linkTo(JobSpecificationApiImpl.class)
                .slash(PRODUCT_INVENTORY_MANAGEMENT_BASE_URL)
                .slash(JOB_SPECIFICATION_SUB_PATH)
                .withSelfRel().getHref();
    }

    public String getJobSpecificationHref(String jobSpecificationId, String baseUrl) {
        return String.format("%s/%s", baseUrl, jobSpecificationId);
    }

    @Override
    public void generateHrefJobSpecification(Collection<JobSpecification> jobSpecifications) {
        String baseUrl = getBaseUrlJobSpecificationApi();

        jobSpecifications.forEach(jobSpecification -> {
            String selfLink = getJobSpecificationHref(jobSpecification.getId(), baseUrl);
            jobSpecification.setHref(selfLink);

        });
    }
    public String getBaseUrlJobApi() {
        return WebMvcLinkBuilder
                .linkTo(JobApiImpl.class)
                .slash(PRODUCT_INVENTORY_MANAGEMENT_BASE_URL)
                .slash(JOB_SUB_PATH)
                .withSelfRel().getHref();
    }


    @Override
    public void generateHrefJob(Collection<Job> jobs) {
        String baseUrl = getBaseUrlJobApi();
        jobs.forEach(jobSpecification -> {
            String selfLink = getJobSpecificationHref(jobSpecification.getId(), baseUrl);
            jobSpecification.setHref(selfLink);
        });
    }
}

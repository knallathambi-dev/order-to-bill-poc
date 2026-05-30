// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;

import springfox.documentation.annotations.ApiIgnore;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * CategoryController defines the endpoint to fetch category
 * based on different params or specifically by id.
 *
 * @author Varshika Choudhary
 */
@Tag(name="category")
@RestController
@RequestMapping(value = "/productCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    @Resource
    CategoryService categoryService;
    @Resource
    private CategoryEntityRelationshipService categoryEntityService;
    @Resource
    ProductOfferingService productOfferingService;

    /**
     * Find list of Category
     *
     * @param id              the Id
     * @param name            the name
     * @param startDateTime   the start date time
     * @param endDateTime     the end date time
     * @param offset
     * @param limit
     * @return the response entity of category
     */

    @GetMapping("/category")
    public ResponseEntity<List<Category>> findCategory(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "subCategory.id", required = false) String subCategoryId,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "sort", required = false) final String sort,
            @RequestParam(name = "productOffering.id", required = false) String productOfferingId,
            @RequestParam(name = "productOfferingPrice.id", required = false) String productOfferingPriceId,
            @RequestParam(name = "productSpecification.id", required = false) String productSpecificationId,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "validFor.startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final OffsetDateTime startDateTime,
            @RequestParam(name = "validFor.endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final OffsetDateTime endDateTime,
            @RequestParam(name = "lastUpdate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final OffsetDateTime lastUpdate,

            @RequestParam(name = "lastUpdateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final OffsetDateTime lastUpdateFrom,

            @RequestParam(name = "lastUpdateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final OffsetDateTime lastUpdateTo,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "isRoot", required = false) Boolean isRoot,
            @RequestParam(name = "orConditions", required = false) String orConditions)
            throws UnsupportedEncodingException {


        Map<String, Object> requestParams = new HashMap<>();

        requestParams.put("_id", id);
        requestParams.put("name", name);
        requestParams.put("subCategory._id", subCategoryId);
        requestParams.put("type", type);
        requestParams.put("sort", sort);
        requestParams.put("productSpecification._id", productSpecificationId);
        requestParams.put("productOfferingPrice._id", productOfferingPriceId);
        requestParams.put("productOffering._id", productOfferingId);
        requestParams.put("validFor.startDateTime", startDateTime);
        requestParams.put("validFor.endDateTime", endDateTime);
        requestParams.put("orConditions", orConditions);
        requestParams.put("isRoot", isRoot);
        if (lastUpdate != null) {
            requestParams.put("lastUpdate", Date.from(lastUpdate.toInstant()));
        }

        if (lastUpdateFrom != null) {
            requestParams.put("lastUpdateFrom", Date.from(lastUpdateFrom.toInstant()));
        }
        if (lastUpdateTo != null) {
            requestParams.put("lastUpdateTo", Date.from(lastUpdateTo.toInstant()));
        }
        //long totalRecords = categoryService.countCategory(requestParams);
        //List<Category> categories = categoryService.fetchCategory(requestParams,offset, limit, fields);
        
        Map<String, Object> categoriesWithCunt = categoryService.fetchCategoryWithCount(requestParams,offset, limit, fields) ;
        
        long totalRecords = (long) categoriesWithCunt.get("count");
        @SuppressWarnings("unchecked")
		List<Category> categories = (List<Category>) categoriesWithCunt.get("data");
        
        if (null == categories) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Set<String> categoryIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
        CompletableFuture<Map<String, CategoryEntityRelationship>> categoryEntityFuture =
                CompletableFuture.supplyAsync(() -> categoryEntityService.fetchEntitiesByIds(categoryIds));
        CompletableFuture<Map<String, String>> productOfferingNamesFuture =
                categoryEntityFuture.thenApply(categoryEntityMap ->
                        fetchProductOfferingNames(categoryEntityMap)
                );
        // Wait for all async tasks to complete
        CompletableFuture.allOf(categoryEntityFuture, productOfferingNamesFuture).join();
        // Get results
        Map<String, CategoryEntityRelationship> categoryEntityMap = categoryEntityFuture.join();
        Map<String, String> productOfferingNames = productOfferingNamesFuture.join();
        categories.forEach(category -> {
            CategoryEntityRelationship categoryEntity = categoryEntityMap.get(category.getId());
            if (categoryEntity != null) {
                Set<ProductOfferingRef> productOfferings = categoryEntity.getProductOfferings();
                if (!productOfferings.isEmpty()) {
                    updateProductOfferingNames(productOfferings, productOfferingNames);
                }
                if(fields == null || fields.contains("productOffering")){
                    category.setProductOffering(productOfferings);
                }
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(categories);
    }

    private void updateProductOfferingNames(Set<ProductOfferingRef> productOfferings, Map<String, String> productOfferingNames) {
        productOfferings.forEach(productOffering ->
                productOffering.setName(productOfferingNames.getOrDefault(productOffering.getId(), "Unknown"))
        );
    }

    private Map<String, String> fetchProductOfferingNames(Map<String, CategoryEntityRelationship> categoryEntityMap) {
        Set<String> productOfferingIds = categoryEntityMap.values().stream()
                .flatMap(categoryEntity -> categoryEntity.getProductOfferings().stream())
                .map(ProductOfferingRef::getId)
                .collect(Collectors.toSet());
        return productOfferingService.fetchProductOfferingNamesByIds(productOfferingIds);
    }

    private void updateProductOfferings(Set<ProductOfferingRef> productOfferings) {
        if (productOfferings == null || productOfferings.isEmpty()) {
            return;
        }
        Map<String, String> productOfferingNames = productOfferingService.fetchProductOfferingNamesByIds(
                productOfferings.stream().map(ProductOfferingRef::getId).collect(Collectors.toSet())
        );
        productOfferings.forEach(productOffering ->
                productOffering.setName(productOfferingNames.get(productOffering.getId()))
        );
    }

    /**
     * Find category by id response entity.
     *
     * @param id     the id
     * @param fields to filter the fields from category
     * @return the response entity of category
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<Category> findCategoryById(@PathVariable final String id,
                                                     @RequestParam(name = "fields", required = false) String fields) {
        Category category;
        if (null == fields)
            category = categoryService.fetchCategoryById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            category = categoryService.fetchCategoryByIdAndFieldList(id, fieldList);
        }
        CategoryEntityRelationship categoryEntity = categoryEntityService.fetchEntityById(id);
        if (null == category) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        if (null != categoryEntity) {
            Set<ProductOfferingRef> productOfferings = categoryEntity.getProductOfferings();
            if (!productOfferings.isEmpty()) {
                updateProductOfferings(productOfferings);
            }
            category.setProductOffering(productOfferings);
        } else {
            category.setProductOffering(new HashSet<>());
        }
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @ApiIgnore
    @GetMapping("/categoryEntity/{id}")
    public ResponseEntity<CategoryEntityRelationship> findCategoryEntityById(@PathVariable final String id,
                                                                             @RequestParam(name = "fields", required = false) String fields) {
        CategoryEntityRelationship categoryEntity;
        if (null == fields)
            categoryEntity = categoryEntityService.fetchEntityById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            categoryEntity = categoryEntityService.fetchCategoryEntityByIdAndFieldList(id, fieldList);
        }

        if (null == categoryEntity) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(categoryEntity);
    }


}

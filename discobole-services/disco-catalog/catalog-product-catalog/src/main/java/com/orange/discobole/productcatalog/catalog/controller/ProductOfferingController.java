// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;

/**
 * ProductOfferingController defines the endpoint to fetch product offering
 * based on different params or specifically by id.
 *
 * @author Ankur Singh
 * @author Prateek Gupta
 * @since 1.0
 */

@RestController
@Tag(name="productOffering")
@RequestMapping(value = "/productCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductOfferingController {
    private static final Logger LOGGER = LogManager.getLogger(ProductOfferingController.class);
    @Resource
    ProductOfferingService productOfferingService;
    @Resource
    private CategoryEntityRelationshipService categoryEntityService;
    @Resource
    private CategoryService categoryService;

    /**
     * Find list of product offering based on different criteria.
     *
     * @param lifecycleStatus                       the lifecycleStatus
     * @param name                                  the name
     * @param brand                                 the brand
     * @param type                                  the type
     * @param version                               the version
     * @param billingType                           the billingType
     * @param startDateTime                         the start date time
     * @param endDateTime                           the end date time
     * @param categoryRefName                       the category ref name
     * @param channelRefName                        the channel ref name
     * @param marketSegmentRefName                  the market segment ref name
     * @param productOfferingPriceType              the product offering price type
     * @param productOfferingPriceId                the product offering price id
     * @param productOfferingPriceName              the product offering price name
     * @param productSpecificationId                the product specification id
     * @param productSpecificationName              the product specification name
     * @param productSpecificationVersion           the product specification
     *                                              version
     * @param bundledProductOfferingId              the bundled product offering id
     * @param bundledProductOfferingName            the bundled product offering
     *                                              name
     * @param bundledProductOfferingValidFor        the bundled product offering
     *                                              valid for
     * @param bundledProductOfferingLifecycleStatus the bundled product offering
     *                                              lifecycle status
     * @param sort                                  the sorted product offering
     * @param relatedPartyId                        the related party id
     * @param relatedPartyName                      the related party name
     * @return the response entity of product offerings
     */
    @GetMapping("/productOffering")
    public ResponseEntity<List<ProductOffering>> findProductOfferings(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "lifecycleStatus", required = false) String lifecycleStatus,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "@type", required = false) String type,
            @RequestParam(name = "version", required = false) String version,
            @RequestParam(name = "billingType", required = false) String billingType,
            @RequestParam(name = "validFor.startDateTime", required = false) OffsetDateTime startDateTime,
            @RequestParam(name = "validFor.endDateTime", required = false) OffsetDateTime endDateTime,
            @RequestParam(name = "CategoryRef.name", required = false) String categoryRefName,
            @RequestParam(name = "ChannelRef.name", required = false) String channelRefName,
            @RequestParam(name = "ChannelRef.id", required = false) String channelRefId,
            @RequestParam(name = "MarketSegmentRef.id", required = false) String marketSegmentRefId,
            @RequestParam(name = "MarketSegmentRef.name", required = false) String marketSegmentRefName,
            @RequestParam(name = "policyRuleRef.id", required = false) String policyRuleRefId,
            @RequestParam(name = "ProductOfferingPrice.@type", required = false) String productOfferingPriceType,
            @RequestParam(name = "ProductOfferingPrice.id", required = false) String productOfferingPriceId,
            @RequestParam(name = "ProductOfferingPrice.name", required = false) String productOfferingPriceName,
            @RequestParam(name = "ProductSpecification.id", required = false) String productSpecificationId,
            @RequestParam(name = "ProductSpecification.name", required = false) String productSpecificationName,
            @RequestParam(name = "ProductSpecification.version", required = false) String productSpecificationVersion,
            @RequestParam(name = "BundledProductOffering.id", required = false) String bundledProductOfferingId,
            @RequestParam(name = "BundledProductOffering.name", required = false) String bundledProductOfferingName,
            @RequestParam(name = "BundledProductOffering.validFor", required = false) String bundledProductOfferingValidFor,
            @RequestParam(name = "BundledProductOffering.lifecycleStatus", required = false) String bundledProductOfferingLifecycleStatus,
            @RequestParam(name = "sort", required = false) final String sort,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "ProductOfferingRelationship.id", required = false) String productOfferingRelationshipId,
            @RequestParam(name = "ProductOfferingRelationship.relationshipType", required = false) String productOfferingRelationshipType,
            @RequestParam(name = "RelatedParty.id", required = false) String relatedPartyId,
            @RequestParam(name = "RelatedParty.name", required = false) String relatedPartyName,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "orConditions", required = false) String orConditions,
    @RequestParam(name = "allowedAction.Action.id", required = false) String  allowedActionId,
            @RequestParam(name = "allowedAction.Action.name", required = false) String  allowedActionName,
    @RequestParam(name = "allowedAction.ChannelRef.id", required = false) String allowedActionchannelRefId)
            throws UnsupportedEncodingException {

        Map<String, Object> requestParams = new HashMap<>();
        if (null != lifecycleStatus) {
            List<String> stateUpperCase = Arrays.asList(lifecycleStatus.split(","));
            lifecycleStatus = stateUpperCase.stream().map(String::toUpperCase).collect(Collectors.joining(","));
        }
        requestParams.put("_id", id);
        requestParams.put("lifecycleStatus", lifecycleStatus);
        requestParams.put("name", name);
        requestParams.put("brand", brand);
        requestParams.put("type", type);
        requestParams.put("version", version);
        requestParams.put("billingType", billingType);
        requestParams.put("validFor.startDateTime", startDateTime);
        requestParams.put("validFor.endDateTime", endDateTime);
        requestParams.put("categoryRef.name", categoryRefName);
        requestParams.put("channel.name", channelRefName);
        requestParams.put("channel._id", channelRefId);
        requestParams.put("marketSegment._id", marketSegmentRefId);
        requestParams.put("marketSegment.name", marketSegmentRefName);
        requestParams.put("policyRuleRef._id", policyRuleRefId);
        requestParams.put("productOfferingPrice.type", productOfferingPriceType);
        requestParams.put("commercialOperation.carries._id", productOfferingPriceId);
        requestParams.put("productOfferingPrice.name", productOfferingPriceName);
        requestParams.put("productSpecification._id", productSpecificationId);
        requestParams.put("productSpecification.name", productSpecificationName);
        requestParams.put("productSpecification.version", productSpecificationVersion);
        requestParams.put("bundledProductOffering._id", bundledProductOfferingId);
        requestParams.put("bundledProductOffering.name", bundledProductOfferingName);
        requestParams.put("bundledProductOffering.validFor", bundledProductOfferingValidFor);
        requestParams.put("bundledProductOffering.lifecycleStatus", bundledProductOfferingLifecycleStatus);
        requestParams.put("productOfferingRelationship._id", productOfferingRelationshipId);
        requestParams.put("productOfferingRelationship.relationshipType", productOfferingRelationshipType);
        requestParams.put("sort", sort);
        requestParams.put("relatedParty._id", relatedPartyId);
        requestParams.put("relatedParty.name", relatedPartyName);
        requestParams.put("orConditions", orConditions);
        requestParams.put("allowedAction.action._id", allowedActionId);
        requestParams.put("allowedAction.action.name", allowedActionName);
        requestParams.put("allowedAction.channelRef._id", allowedActionchannelRefId);
        Map<String, Object> productOfferingsWithCunt = productOfferingService.fetchProductOfferingWithCount(requestParams,offset, limit, fields) ;
        
        long totalRecords = productOfferingsWithCunt.get("count") != null ? (long) productOfferingsWithCunt.get("count"): 0l;
        @SuppressWarnings("unchecked")
		List<ProductOffering> productOfferings = (List<ProductOffering>) productOfferingsWithCunt.get("data");
        
        if (null == productOfferings) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        if (productOfferings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        for (ProductOffering productOffering : productOfferings) {
            CategoryEntityRelationship categoryEntity = categoryEntityService.fetchEntityById(productOffering.getId());
            if (null != categoryEntity) {
                Set<CategoryRef> categories = categoryEntity.getCategories();
                if (!categories.isEmpty()) {
                    updateCategories(categories);
                }
                productOffering.setCategory(categories);
            }
        }
        LOGGER.info("list of product offerings-{}", productOfferings.size());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(productOfferings);
    }

    private void updateCategories(Set<CategoryRef> categories) {
        for (CategoryRef category : categories) {
             Category cat = categoryService.fetchCategoryById(category.getId());
            if(cat!=null)
            category.setName(cat.getName());
        }
    }

    /**
     * Product offering by id response entity.
     *
     * @param id     the id
     * @param fields to filter the fields from productOffering
     * @return the response entity of product offering
     */
    @GetMapping("/productOffering/{id}")
    public ResponseEntity<ProductOffering> productOfferingById(@PathVariable String id,
                                                               @RequestParam(name = "fields", required = false) String fields) {
        ProductOffering productOffering;
        if (null == fields)
            productOffering = productOfferingService.fetchProductOfferingById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            productOffering = productOfferingService.fetchProductOfferingById(id, fieldList);
        }

        if (null == productOffering) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        CategoryEntityRelationship categoryEntity = categoryEntityService.fetchEntityById(id);
        if (null != categoryEntity) {
            Set<CategoryRef> categories = categoryEntity.getCategories();
            if (!categories.isEmpty()) {
                updateCategories(categories);
            }
            productOffering.setCategory(categories);
        } else {
            productOffering.setCategory(new HashSet<>());
        }
        return ResponseEntity.status(HttpStatus.OK).body(productOffering);
    }
}

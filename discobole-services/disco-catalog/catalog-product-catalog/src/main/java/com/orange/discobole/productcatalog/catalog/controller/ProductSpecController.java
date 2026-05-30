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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationDto;
import com.orange.discobole.productcatalog.catalog.service.ProductSpecService;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductSpecController defines the endpoint to fetch product specification
 * based on different params or specifically by id.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Tag(name="productSpecification")
@RequestMapping(value = "/productCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
//@PreAuthorize("hasAnyRole(@securityConfiguration.getRoles())")
public class ProductSpecController {
	private static final Logger LOGGER = LogManager.getLogger(ProductSpecController.class);
	@Resource
	ProductSpecService productSpecService;

	/**
	 * Find list of product specification.
	 *
	 * @param lifecycleStatus    the lifecycleStatus
	 * @param name               the name
	 * @param brand              the brand
	 * @param supportEntity      the support entity
	 * @param lastUpdate         the last update
	 * @param serviceSpecId      the service spec id
	 * @param serviceSpecName    the service spec name
	 * @param serviceSpecVersion the service spec version
	 * @param stockItemTypeId    the stock item type id
	 * @param stockItemTypeName  the stock item type name
	 * @param relatedPartyId     the related party id
	 * @param relatedPartyRole   the related party role
	 * @param relatedPartyName   the related party name
	 * @param relationshipId     the relationship id
	 * @param sort               the sort
	 * @param offset             the offset
	 * @param limit              the limit
	 * @param startDateTime      the start date time
	 * @param endDateTime        the end date time
	 * @param productSpecIds     the product spec ids
	 * @return the response entity of product specification
	 */
	@GetMapping("/productSpecification")
	public ResponseEntity<List<ProductSpecificationDto>> findProductSpecification(
			@RequestParam(name = "aggregateId", required = false) String aggregateId,
			@RequestParam(name = "lifecycleStatus", required = false) String lifecycleStatus,
			@RequestParam(name = "name", required = false) final String name,
			@RequestParam(name = "brand", required = false) final String brand,
			@RequestParam(name = "supportEntity", required = false) String supportEntity,
			@RequestParam(name = "lastUpdate", required = false) final String lastUpdate,
			@RequestParam(name = "ServiceSpecification.id", required = false) final String serviceSpecId,
			@RequestParam(name = "ServiceSpecification.name", required = false) final String serviceSpecName,
			@RequestParam(name = "ServiceSpecification.version", required = false) final String serviceSpecVersion,
			@RequestParam(name = "StockItemType.id", required = false) final String stockItemTypeId,
			@RequestParam(name = "StockItemType.name", required = false) final String stockItemTypeName,
			@RequestParam(name = "relatedParty.id", required = false) final String relatedPartyId,
			@RequestParam(name = "relatedParty.role", required = false) final String relatedPartyRole,
			@RequestParam(name = "relatedParty.name", required = false) final String relatedPartyName,
			@RequestParam(name = "ProductSpecificationRelationship.id", required = false) String relationshipId,
			@RequestParam(name = "policyRuleRef.id", required = false) String policyRuleRefId,
			@RequestParam(name = "sort", required = false) final String sort,
			@RequestParam(name = "offset", required = false) Long offset,
			@RequestParam(name = "limit", required = false) Long limit,
			@RequestParam(name = "validFor.startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final OffsetDateTime startDateTime,
			@RequestParam(name = "validFor.endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final OffsetDateTime endDateTime,
			@RequestParam(name = "id", required = false) String productSpecIds,
			@RequestParam(name = "@type", required = false) String type,
			@RequestParam(name = "@baseType", required = false) String baseType,
			@RequestParam(name = "fields", required = false) String fields,
	        @RequestParam(name = "orConditions", required = false) String orConditions)
			throws UnsupportedEncodingException

	{

		Map<String, Object> requestParams = new HashMap<>();
		if (null != lifecycleStatus) {
			List<String> stateUpperCase = Arrays.asList(lifecycleStatus.split(","));
			lifecycleStatus = stateUpperCase.stream().map(String::toUpperCase).collect(Collectors.joining(","));
		}
		if (null != supportEntity) {
			List<String> supportEntityUpperCase = Arrays.asList(supportEntity.split(","));
			supportEntity = supportEntityUpperCase.stream().map(String::toUpperCase).collect(Collectors.joining(","));
		}

		requestParams.put("aggregateId", aggregateId);
		requestParams.put("lifecycleStatus", lifecycleStatus);
		requestParams.put("name", name);
		requestParams.put("brand", brand);
		requestParams.put("supportEntity", supportEntity);
		requestParams.put("lastUpdate", lastUpdate);
		requestParams.put("serviceSpecification._id", serviceSpecId);
		requestParams.put("serviceSpecification.name", serviceSpecName);
		requestParams.put("serviceSpecification.version", serviceSpecVersion);
		requestParams.put("stockItemType._id", stockItemTypeId);
		requestParams.put("stockItemType.name", stockItemTypeName);
		requestParams.put("relatedParty._id", relatedPartyId);
		requestParams.put("relatedParty.role", relatedPartyRole);
		requestParams.put("relatedParty.name", relatedPartyName);
		requestParams.put("productSpecificationRelationship._id", relationshipId);
		requestParams.put("policyRuleRef._id", policyRuleRefId);
		requestParams.put("sort", sort);
		requestParams.put("validFor.startDateTime", startDateTime);
        requestParams.put("validFor.endDateTime", endDateTime);
		requestParams.put("_id", productSpecIds);
		requestParams.put("type", type);
		requestParams.put("baseType", baseType);
		requestParams.put("orConditions", orConditions);
		//long totalRecords = productSpecService.countProductSpecification(requestParams);

		//List<ProductSpecification> productSpecifications = productSpecService.fetchProductSpecification(requestParams,offset, limit,fields);
		
		Map<String, Object> productSpecWithCunt = productSpecService.fetchProductSpecificationWithCount(requestParams,offset, limit, fields) ;
        
        long totalRecords = (long) productSpecWithCunt.get("count");
        @SuppressWarnings("unchecked")
		List<ProductSpecification> productSpecifications = (List<ProductSpecification>) productSpecWithCunt.get("data");
        
		LOGGER.info("number of Product Specs-{}",productSpecifications.size());
		if (productSpecifications.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", String.valueOf(totalRecords));
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(convert(productSpecifications));

	}

	/**
	 * Find product specification by id response entity.
	 *
	 * @param id the id
	 * @param fields to filter the fields from productSpecification
	 * @return the response entity of product specification
	 */
	@GetMapping("/productSpecification/{id}")
	public ResponseEntity<ProductSpecificationDto> findProductSpecificationById(@PathVariable final String id,
			@RequestParam(name = "fields", required = false) String fields) {
		ProductSpecification productSpecification;
		if (null == fields)
			productSpecification = productSpecService.fetchProductSpecificationById(id);
		else {
			List<String> fieldList = Arrays.asList(fields.split(","));
			productSpecification = productSpecService.fetchProductSpecificationById(id, fieldList);
		}
		if (null == productSpecification) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(convert(productSpecification));
	}


	private List<ProductSpecificationDto> convert(List<ProductSpecification> productSpecifications) {
		List<ProductSpecificationDto> productSpecificationDtos = new ArrayList<>();
		for(ProductSpecification productSpecification : productSpecifications) {
			productSpecificationDtos.add(convert(productSpecification));
		}
		return productSpecificationDtos;
	}

	private ProductSpecificationDto convert(ProductSpecification productSpecification) {
		ProductSpecificationDto productSpecificationDto = new ProductSpecificationDto();
		productSpecificationDto.id(productSpecification.getId()).href(productSpecification.getHref())
				.brand(productSpecification.getBrand()).description(productSpecification.getDescription())
				.isBundle(productSpecification.isIsBundle()).lastUpdate(productSpecification.getLastUpdate())
				.lifecycleStatus(productSpecification.getLifecycleStatus()).name(productSpecification.getName())
				.productNumber(productSpecification.getProductNumber()).version(productSpecification.getVersion())
				.supportEntity(productSpecification.getSupportEntity())
				.productSpecCharacteristic(productSpecification.getProductSpecCharacteristic())
				.productSpecificationRelationship(productSpecification.getProductSpecificationRelationship())
				.policyRuleRef(productSpecification.getPolicyRuleRef())
				.relatedParty(productSpecification.getRelatedParty())
				.resourceSpecification(productSpecification.getResourceSpecification())
				.serviceSpecification(productSpecification.getServiceSpecification())
				.validFor(productSpecification.getValidFor())
				.productUsageSpecification(productSpecification.getProductUsageSpecification())
				.relatedResource(productSpecification.getRelatedResource())
				.operationSpecification(productSpecification.getOperationSpecification())
				.stockItemType(productSpecification.getStockItemType())
				.productConfiguration(productSpecification.getProductConfiguration())
				.baseType(productSpecification.getBaseType())
				.type(productSpecification.getType())
				.setUsageSpecification(productSpecification.getUsageSpecification());
		return productSpecificationDto;
	}

}

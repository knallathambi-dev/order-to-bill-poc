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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingPriceService;

import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@Tag(name="productOfferingPrice")
@RequestMapping(value = "/productCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductOfferingPriceController {

	@Resource
	ProductOfferingPriceService productOfferingPriceService;

	/**
	 * Find list of product offering Price based on different criteria.
	 *
	 * @param name
	 * @param type
	 * @param version
	 * @param priceType
	 * @param prorationType
	 * @param immediatePayment
	 * @param lastUpdate
	 * @param lifecycleStatus
	 * @param startDateTime
	 * @param endDateTime
	 * @param sort
	 * @param offset
	 * @param limit
	 * @param productOfferingPriceRelationshipId
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping(value = "/productOfferingPrice")
	public ResponseEntity<List<ProductOfferingPrice>> findProductOfferingPrices(
			@RequestParam(name = "id", required = false) String id,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "@type", required = false) String type,
			@RequestParam(name = "version", required = false) String version,
			@RequestParam(name = "priceType", required = false) String priceType,
			@RequestParam(name = "prorationType", required = false) String prorationType,
			@RequestParam(name = "immediatePayment", required = false) String immediatePayment,
			@RequestParam(name = "lastUpdate", required = false) final String lastUpdate,
			@RequestParam(name = "lifecycleStatus", required = false) String lifecycleStatus,
			@RequestParam(name = "validFor.startDateTime", required = false) OffsetDateTime startDateTime,
			@RequestParam(name = "validFor.endDateTime", required = false) OffsetDateTime endDateTime,
			@RequestParam(name = "sort", required = false) final String sort,
			@RequestParam(name = "offset", required = false) Long offset,
			@RequestParam(name = "limit", required = false) Long limit,
			@RequestParam(name = "productOfferingPriceRelationship._id", required = false) String productOfferingPriceRelationshipId,
			@RequestParam(name = "fields", required = false) String fields,
			@RequestParam(name = "orConditions", required = false) String orConditions,
            @RequestParam(name = "applicationOffset", required = false) String applicationOffset,
	@RequestParam(name = "partner", required = false) String partner,
	@RequestParam(name = "externalId", required = false) String externalId,
	@RequestParam(name = "downPayment", required = false) Double downPayment)
			throws UnsupportedEncodingException {

		Map<String, Object> requestParams = new HashMap<>();
		if (null != lifecycleStatus) {
			List<String> stateUpperCase = Arrays.asList(lifecycleStatus.split(","));
			lifecycleStatus = stateUpperCase.stream().map(String::toUpperCase).collect(Collectors.joining(","));
		}
		requestParams.put("_id", id);
		requestParams.put("name", name);
		requestParams.put("type", type == null ? type : type.toUpperCase());
		requestParams.put("version", version);
		requestParams.put("priceType", priceType);
		requestParams.put("prorationType", prorationType);
		requestParams.put("immediatePayment", immediatePayment);
		requestParams.put("lastUpdate", lastUpdate);
		requestParams.put("lifecycleStatus", lifecycleStatus);
		requestParams.put("validFor.startDateTime", startDateTime);
		requestParams.put("validFor.endDateTime", endDateTime);
		requestParams.put("sort", sort);
		requestParams.put("popRelationship._id", productOfferingPriceRelationshipId);
		requestParams.put("orConditions", orConditions);
		requestParams.put("applicationOffset", applicationOffset);
		requestParams.put("partner", partner);
		requestParams.put("externalId", externalId);
		requestParams.put("downPayment", downPayment);

		//long totalRecords = productOfferingPriceService.countProductOfferingPrice(requestParams);
		//List<ProductOfferingPrice> productOfferingPrices = productOfferingPriceService.getProductOfferingPrices(requestParams, offset, limit, fields);
		
		Map<String, Object> productOfferingPriceWithCunt = productOfferingPriceService.fetchProductOfferingPriceWithCount(requestParams,offset, limit, fields) ;
        
        long totalRecords = (long) productOfferingPriceWithCunt.get("count");
        @SuppressWarnings("unchecked")
		List<ProductOfferingPrice> productOfferingPrices = (List<ProductOfferingPrice>) productOfferingPriceWithCunt.get("data");
        
		if (null == productOfferingPrices) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", String.valueOf(totalRecords));

		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(productOfferingPrices);
	}

	/**
	 * Find product offering price by id response entity.
	 *
	 * @param id     the id
	 * @param fields to filter the fields from productOfferingPrice
	 * @return the response entity of product Offering Price
	 */
//	@GetMapping(value = "/productOfferingPrice/{id}")
//	public ResponseEntity<ProductOfferingPrice> findProductOfferingPricetest(@PathVariable String id,
//			@RequestParam(name = "fields", required = false) String fields) {
//		ProductOfferingPrice productOfferingPrice ;
//
//
//
//
//		if (null == fields) {
//			productOfferingPrice = productOfferingPriceService.getProductOfferingPriceById(id);
//
//		}else {
//			List<String> fieldList = Arrays.asList(fields.split(","));
//			productOfferingPrice = productOfferingPriceService.getProductOfferingPriceById(id, fieldList);
//		}
//		if (null == productOfferingPrice) {
//			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//		}
//		return ResponseEntity.status(HttpStatus.OK).body(productOfferingPrice);
//	}


	@GetMapping(value = "/productOfferingPrice/{id}")
	public ResponseEntity<ProductOfferingPrice> findProductOfferingPrice(@PathVariable String id,
																		 @RequestParam(name = "fields", required = false) String fields) {
		ProductOfferingPrice  productOfferingPrice;

		if (fields == null) {
			productOfferingPrice = (ProductOfferingPrice)productOfferingPriceService.getProductOfferingPriceById(id);
		} else {
			List<String> fieldList = Arrays.asList(fields.split(","));
			productOfferingPrice = (ProductOfferingPrice)productOfferingPriceService.getProductOfferingPriceById(id, fieldList);
		}

		if (productOfferingPrice == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(productOfferingPrice);
	//	return ResponseEntity.ok(productOfferingPrice);
	}



}


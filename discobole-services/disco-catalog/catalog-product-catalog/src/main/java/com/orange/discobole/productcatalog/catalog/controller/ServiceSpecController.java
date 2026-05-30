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


import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.service.ServiceSpecService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ProductOfferingController defines the endpoint to fetch product offering
 * based on different params or specifically by id.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Tag(name="serviceSpecification")
@RequestMapping(value = "/serviceCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
//@PreAuthorize("hasAnyRole(@securityConfiguration.getRoles())")
public class ServiceSpecController {

	@Resource
	ServiceSpecService serviceSpecService;

	/**
	 * Find list of service specifications.
	 *
	 * @param lifecycleStatus the lifecycle status
	 * @param name            the name
	 * @param version         the version
	 * @param startDateTime   the start date time
	 * @param endDateTime     the end date time
	 * @return the response entity of service specifications
	 */
	@GetMapping("/serviceSpecification")
	public ResponseEntity<List<ServiceSpecification>> findServiceSpecifications(
			@RequestParam(name = "id", required = false) String id,
			@RequestParam(name = "lifecycleStatus", required = false) String lifecycleStatus,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "version", required = false) String version,
			@RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit,
			@RequestParam(name = "validFor.startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDateTime,
			@RequestParam(name = "validFor.endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDateTime,
			@RequestParam(name = "fields", required = false) String fields,
			@RequestParam(name = "orConditions", required = false) String orConditions,
			@RequestParam(name = "sort", required = false) String sort)
			throws UnsupportedEncodingException {

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("_id", id);
		requestParams.put("lifecycleStatus", lifecycleStatus);
		requestParams.put("name", name);
		requestParams.put("version", version);
		requestParams.put("validFor.startDateTime", startDateTime);
        requestParams.put("validFor.endDateTime", endDateTime);
		requestParams.put("orConditions", orConditions);
		requestParams.put("sort", sort);
//		long totalRecords = serviceSpecService.countServiceSpecification(requestParams);
//		List<ServiceSpecification> serviceSpecifications = serviceSpecService.fetchServiceSpecifications(requestParams,offset, limit);
		

		Map<String, Object> serviceSpecWithCunt = serviceSpecService.fetchServiceSpecificationWithCount(requestParams,offset, limit, fields) ;
        
		long totalRecords = serviceSpecWithCunt.get("count") != null ? (long) serviceSpecWithCunt.get("count"): 0l;
        @SuppressWarnings("unchecked")
		List<ServiceSpecification> serviceSpecifications = (List<ServiceSpecification>) serviceSpecWithCunt.get("data");
        
		
		if (null == serviceSpecifications) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", String.valueOf(totalRecords));

		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(serviceSpecifications);
	}

	/**
	 * Find service specification by id response entity.
	 *
	 * @param id the id
	 * @param fields to filter the fields from serviceSpecification
	 * @return the response entity of service specification
	 */
	@GetMapping("/serviceSpecification/{id}")
	public ResponseEntity<ServiceSpecification> findServiceSpecificationById(@PathVariable final String id,
			@RequestParam(name = "fields", required = false) String fields) {
		ServiceSpecification serviceSpecification;
		if (null == fields)
			serviceSpecification = serviceSpecService.fetchServiceSpecificationById(id);
		else {
			List<String> fieldList = Arrays.asList(fields.split(","));
			serviceSpecification = serviceSpecService.fetchServiceSpecificationById(id, fieldList);
		}
		if (null == serviceSpecification) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(serviceSpecification);
	}

}

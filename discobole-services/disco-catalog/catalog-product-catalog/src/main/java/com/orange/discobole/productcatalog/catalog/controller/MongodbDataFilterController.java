// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;


import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name="cleanUp")
@RequestMapping(value = "/productCatalogManagement/v1/dataClean", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole(@securityConfiguration.getRoles())")
public class MongodbDataFilterController {

	@Resource
	private MongodbDataFilterService mongodbDataFilterService;
	
	  @DeleteMapping(value = "/productSpecification") public ResponseEntity<Long>
	  productSpecDataClean(@RequestParam(name = "days", required = true) Long days)
	  {
	  
	  Long deletedCount =
	  mongodbDataFilterService.filterProductSpecificationData(days); return
	  ResponseEntity.status(HttpStatus.OK).body(deletedCount); }
	 

	@DeleteMapping(value = "/productOffering")
	public ResponseEntity<Long> productOffDataClean(@RequestParam(name = "days", required = true) Long days) {
		Long deletedCount = mongodbDataFilterService.filterProductOfferingData(days);
		return ResponseEntity.status(HttpStatus.OK).body(deletedCount);
	}
}
